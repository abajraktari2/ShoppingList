@file:OptIn(ExperimentalMaterial3Api::class)

package hu.bme.aut.D4104N.shoppinglist.additem


import androidx.compose.foundation.background

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem



@Composable
fun CategoryDropdown(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    categories: List<String> = listOf("Food", "Electronic", "Book")
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .wrapContentSize(Alignment.TopStart)
    ) {
        Text(
            text = selectedCategory,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .background(Color.LightGray)
                .padding(16.dp),
            color = Color.Black
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth() // Adjust this as needed
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category) },
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemScreen(
    viewModel: AddItemViewModel,
    navController: NavController
) {
    // State variables
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var estimatedPrice by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(false) }
    var category by remember { mutableStateOf("Food") }
    var showMenu by remember { mutableStateOf(false) }

    // Snackbar state
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title for the screen
            Text(
                text = "Create a New Item",
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(24.dp))
            // Name TextField
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Item Name") },
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Description TextField
            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Item Description") },
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Estimated Price TextField
            TextField(
                value = estimatedPrice,
                onValueChange = { estimatedPrice = it },
                label = { Text("Estimated Price (HUF)") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Category Dropdown
            ExposedDropdownMenuBox(
                expanded = showMenu,
                onExpandedChange = { showMenu = !showMenu }
            ) {
                TextField(
                    readOnly = true,
                    value = category.ifEmpty { "Choose a category" },
                    onValueChange = { },
                    label = { if (category.isEmpty()) Text("Choose a category") else Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showMenu) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showMenu = true }
                )
                CategoryDropdown(
                    selectedCategory = category,
                    onCategorySelected = { newCategory ->
                        category = newCategory
                    },
                    categories = listOf("Food", "Electronic", "Book") // Passing the list of categories
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Bought Checkbox
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Purchased: ")
                Checkbox(
                    checked = status,
                    onCheckedChange = { status = it }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Add Item Button
            Button(
                onClick = {
                    coroutineScope.launch {
                        val price = estimatedPrice.toIntOrNull()
                        if (name.isNotBlank() && description.isNotBlank() && price != null) {
                            viewModel.addItem(name, description, price, category, status)
                            snackbarHostState.showSnackbar("Item added successfully")

                            // Clearing the input fields after successful addition
                            name = ""
                            description = ""
                            estimatedPrice = ""
                            status = false
                            category = "Food"

                            navController.popBackStack()
                        } else {
                            snackbarHostState.showSnackbar("Please enter valid details")
                        }
                    }
                }
            ) {
                Text("Add Item")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Go Back Button
            Button(
                onClick = { navController.popBackStack() }
            ) {
                Text("Go Back")
            }
        }
    }
}








