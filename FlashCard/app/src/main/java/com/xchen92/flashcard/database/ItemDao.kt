/*
 * Copyright (c) Arthur Roolfs 2020. All rights reserved.
 * Last modified 3/18/20 8:16 PM
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IT IS MEANT TO BE USED IN AN EDUCATIONAL CONTEXT TO ELUCIDATE NARROW ASPECTS OF SPECIFIC NATURE ONLY. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.xchen92.flashcard.database
import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.*

@Dao
interface ItemDao {

    @Insert
    fun addItem(item: Item)

    @Update
    fun updateItem(item: Item)

    @Delete
    fun deleteItem(item: Item)
    @Delete
    fun deleteAll(items: List<Item>)

    @Query("SELECT * FROM items_table WHERE id=(:id)")
    fun getItem(id: Long): LiveData<Item?>

    @Query("SELECT * FROM items_table WHERE title=(:x)")
    fun getItemWithTitle(x:String): LiveData<Item>


    @Query("SELECT * FROM items_table ORDER BY id DESC")
    fun getAllItems(): LiveData<List<Item>>

    //SQL doesn't has boolean type; 1 is true 0 is false
    @Query("SELECT * FROM items_table WHERE completed= 0")
    fun getIncompleteItems(): LiveData<List<Item>>

    @Query("SELECT * FROM items_table ORDER BY id DESC LIMIT 1")
    fun getLastItem(): LiveData<Item>

   @Query("UPDATE items_table SET completed =:status WHERE uuid =:theID")
   fun updateStatus(theID: UUID?, status:Boolean)

}
