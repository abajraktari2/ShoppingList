package hu.bme.aut.D4104N.shoppinglist.additem

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingItemDao {
    @Insert
    suspend fun insertItem(item: ShoppingItem)

    @Query("SELECT * FROM shopping_items")
    fun getAllItems(): Flow<List<ShoppingItem>>

    @Update
    suspend fun updateItem(item: ShoppingItem)

    @Delete
    suspend fun deleteItem(item: ShoppingItem)

    @Query("SELECT * FROM shopping_items WHERE id = :itemId")
    suspend fun getItemById(itemId: Int): ShoppingItem?
    @Query("DELETE FROM shopping_items")
    suspend fun deleteAllItems()

}