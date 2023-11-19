package hu.bme.aut.D4104N.shoppinglist.additem

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch



class AddItemViewModel(
    private val itemDao: ShoppingItemDao
) : ViewModel() {

    fun addItem(name: String, description: String, price: Int, category: String, isBought: Boolean) {
        viewModelScope.launch {
            val newItem = ShoppingItem(
                name = name,
                description = description,
                estimatedPriceHUF = price,
                category = category,
                isBought = isBought
            )
            itemDao.insertItem(newItem)
        }
    }
}
