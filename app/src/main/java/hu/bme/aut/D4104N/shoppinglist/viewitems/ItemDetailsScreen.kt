package hu.bme.aut.D4104N.shoppinglist.viewitems


import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import hu.bme.aut.D4104N.shoppinglist.additem.ShoppingItemDao
import hu.bme.aut.D4104N.shoppinglist.additem.ShoppingItem

@ExperimentalMaterial3Api
@Composable

fun ItemDetailsScreen(itemId: Int, itemDao: ShoppingItemDao, navController: NavController) {
    val scope = rememberCoroutineScope()
    var item by remember { mutableStateOf<ShoppingItem?>(null) }
    var currencyRates by remember { mutableStateOf<Map<String, Double>?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(itemId) {
        isLoading = true
        errorMessage = ""

        // Fetch the item details from the database
        scope.launch {
            Log.d("ItemDetailsScreen", "Fetching item details from database for itemId: $itemId")
            item = itemDao.getItemById(itemId)
            Log.d("ItemDetailsScreen", "Fetched item: $item")
        }

        // Fetch currency rates
        scope.launch {
            try {
                Log.d("ItemDetailsScreen", "Fetching currency rates")
                val response = RetrofitInstance.apiService.getRates(fromCurrency = "HUF", toCurrencies = "USD,EUR,GBP")
                if (response.isSuccessful) {
                    currencyRates = response.body()?.rates
                    Log.d("ItemDetailsScreen", "Fetched currency rates: $currencyRates")
                } else {
                    errorMessage = "Error fetching currency rates: ${response.errorBody()?.string()}"
                    Log.e("ItemDetailsScreen", errorMessage)
                }
            } catch (e: Exception) {
                errorMessage = "Error fetching currency rates: ${e.message}"
                Log.e("ItemDetailsScreen", errorMessage)
            }
            isLoading = false
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Item Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else if (errorMessage.isNotEmpty()) {
                Text("Error: $errorMessage")
            } else if (item != null && currencyRates != null) {
                ItemDetailsContent(item = item!!, currencyRates = currencyRates!!)
            }
        }
    }
}

@Composable
fun ItemDetailsContent(item: ShoppingItem, currencyRates: Map<String, Double>) {
    Column {
        DetailCard("Name", item.name, MaterialTheme.typography.titleLarge)
        DetailCard("Description", item.description, MaterialTheme.typography.bodyMedium)
        DetailCard("Category", item.category, MaterialTheme.typography.bodyMedium)
        DetailCard("Price in HUF", "${item.estimatedPriceHUF}", MaterialTheme.typography.bodyMedium)
        DetailCard("Purchased", if (item.isBought) "Yes" else "No", MaterialTheme.typography.bodyMedium)

        currencyRates.let { rates ->
            DetailCard("Price in USD", "${"%.2f".format(item.estimatedPriceHUF * (rates["USD"] ?: 1.0))}", MaterialTheme.typography.bodyMedium)
            DetailCard("Price in EUR", "${"%.2f".format(item.estimatedPriceHUF * (rates["EUR"] ?: 1.0))}", MaterialTheme.typography.bodyMedium)
            DetailCard("Price in GBP", "${"%.2f".format(item.estimatedPriceHUF * (rates["GBP"] ?: 1.0))}", MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun DetailCard(label: String, value: String, textStyle: TextStyle) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp).padding(end = 8.dp)
            )
            Column {
                Text(
                    text = label.uppercase(),
                    style = MaterialTheme.typography.labelMedium.copy(color = Color.Black)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = value,
                    style = textStyle.copy(color = Color.Black)
                )
            }

        }
    }
}

