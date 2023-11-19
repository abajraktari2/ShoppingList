package hu.bme.aut.D4104N.shoppinglist.list

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import hu.bme.aut.D4104N.shoppinglist.ui.theme.ShoppingListTheme
import hu.bme.aut.D4104N.shoppinglist.additem.AddItemScreen
import hu.bme.aut.D4104N.shoppinglist.additem.AddItemViewModel
import hu.bme.aut.D4104N.shoppinglist.additem.AppDatabase
import hu.bme.aut.D4104N.shoppinglist.additem.ShoppingItemDao
import hu.bme.aut.D4104N.shoppinglist.viewitems.ItemDetailsScreen
import hu.bme.aut.D4104N.shoppinglist.viewitems.ShoppingListScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShoppingListTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val appDatabase = remember { AppDatabase.getDatabase(context) }
    val dao = remember { appDatabase.shoppingItemDao() }
    var showDeleteDialog by remember { mutableStateOf(false) }

    NavHost(navController = navController, startDestination = "mainScreen") {
        composable("mainScreen") {
            ShoppingListHeader(
                onAddItem = {
                    navController.navigate("addItemScreen")
                },
                onDeleteList = {
                    showDeleteDialog = true // Triggers the delete confirmation dialog
                }
            )
            Spacer(modifier = Modifier.height(12.dp))

            ShoppingListScreen(itemDao = dao) { itemId ->
                navController.navigate("itemDetailsScreen/$itemId")
            }

            if (showDeleteDialog) {
                DeleteConfirmationDialog(
                    showDialog = showDeleteDialog,
                    onConfirm = {
                        CoroutineScope(Dispatchers.IO).launch {
                            dao.deleteAllItems()
                        }
                        showDeleteDialog = false
                    },
                    onDismiss = {
                        showDeleteDialog = false
                    }
                )
            }
        }

        composable("addItemScreen") {

            val viewModel: AddItemViewModel = viewModel(
                key = "AddItemViewModel",
                factory = AddItemViewModelFactory(dao)
            )

            // Pass the NavController and ScaffoldState to the AddItemScreen composable
            AddItemScreen(viewModel = viewModel, navController = navController)
        }
        composable("itemDetailsScreen/{itemId}") { backStackEntry ->
            val arguments = requireNotNull(backStackEntry.arguments)
            val itemIdString = arguments.getString("itemId") ?: "0"
            val itemId = itemIdString.toIntOrNull() ?: 0

            ItemDetailsScreen(itemId = itemId, itemDao = dao, navController = navController)
        }

    }
}





// ViewModel factory implementation, necessary to provide the DAO to the ViewModel
class AddItemViewModelFactory(private val dao: ShoppingItemDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddItemViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}



