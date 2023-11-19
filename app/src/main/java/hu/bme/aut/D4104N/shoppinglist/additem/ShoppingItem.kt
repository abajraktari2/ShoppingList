package hu.bme.aut.D4104N.shoppinglist.additem

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_items")
data class ShoppingItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val category: String,
    val name: String,
    val description: String,
    val estimatedPriceHUF: Int,
    val isBought: Boolean
)
