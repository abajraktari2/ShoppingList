package hu.bme.aut.D4104N.shoppinglist.viewitems

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import hu.bme.aut.D4104N.shoppinglist.R
import hu.bme.aut.D4104N.shoppinglist.additem.ShoppingItem
import hu.bme.aut.D4104N.shoppinglist.additem.ShoppingItemDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ShoppingListScreen(itemDao: ShoppingItemDao, onItemClick: (Int) -> Unit) {
    val scope = rememberCoroutineScope()
    val itemsFlow = remember { itemDao.getAllItems() }
    val items by itemsFlow.collectAsState(initial = listOf())
    val context = LocalContext.current
    Column {
        Spacer(modifier = Modifier.height(85.dp)) // Space between the list and the header

        LazyColumn {
            items(items) { item ->
                ShoppingListItem(
                    item = item,
                    onItemCheckedChanged = { shoppingItem, isChecked ->
                        scope.launch {
                            itemDao.updateItem(shoppingItem.copy(isBought = isChecked))
                        }
                    },
                    onDeleteConfirmed = { shoppingItem ->
                        scope.launch {
                            itemDao.deleteItem(shoppingItem)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    "Item deleted successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    ,
                    onItemClick = onItemClick
                )
            }
        }
    }
}




@Composable
fun ShoppingListItem(
    item: ShoppingItem,
    onItemCheckedChanged: (ShoppingItem, Boolean) -> Unit,
    onDeleteConfirmed: (ShoppingItem) -> Unit,
    onItemClick: (Int) -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(item.id) }
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val iconResource = when (item.category) {
                "Food" -> R.drawable.ic_food
                "Electronic" -> R.drawable.ic_electronics
                "Book" -> R.drawable.ic_books
                else -> R.drawable.ic_unknown
            }
            Icon(
                painter = painterResource(id = iconResource),
                contentDescription = "Item Icon",
                modifier = Modifier.size(48.dp),
                tint = Color.Unspecified
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Text(text = item.name, style = MaterialTheme.typography.titleMedium)
                Text(text = item.description, style = MaterialTheme.typography.bodyMedium)
                Text(text = "Price: ${item.estimatedPriceHUF} HUF", style = MaterialTheme.typography.labelSmall)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Purchased", style = MaterialTheme.typography.bodySmall)
                // Use Box to create a clickable area around the Checkbox
                Box(
                    modifier = Modifier

                        .padding(12.dp)

                        .clickable {

                            onItemCheckedChanged(item, !item.isBought)
                        }
                ) {
                    Checkbox(
                        checked = item.isBought,
                        onCheckedChange = null // Disables the default listener
                    )
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Delete", style = MaterialTheme.typography.bodySmall)
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }

            // Show confirmation dialog when requested
            if (showDeleteDialog) {
                ConfirmDeleteDialog(
                    item = item,
                    onDismiss = { showDeleteDialog = false },
                    onDeleteConfirmed = {
                        onDeleteConfirmed(item)
                        showDeleteDialog = false
                    }
                )
            }
        }
        }
    }


@Composable
fun ConfirmDeleteDialog(
    item: ShoppingItem,
    onDismiss: () -> Unit,
    onDeleteConfirmed: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirm Deletion") },
        text = { Text("Are you sure you want to delete '${item.name}'?") },
        confirmButton = {
            TextButton(onClick = onDeleteConfirmed) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

