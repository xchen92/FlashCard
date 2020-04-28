/*
 * Copyright (c) Arthur Roolfs 2020. All rights reserved.
 * Last modified 3/17/20 8:51 PM
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IT IS MEANT TO BE USED IN AN EDUCATIONAL CONTEXT TO ELUCIDATE NARROW ASPECTS OF SPECIFIC NATURE ONLY. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.xchen92.flashcard.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import java.util.*
import java.util.concurrent.Executors

class ItemRepository private constructor(context: Context) {

    private val database: ItemDatabase = Room.databaseBuilder(
        context.applicationContext,
        ItemDatabase::class.java,
        "flashcard_database"
    )
        .addMigrations(migration_1_2)
        .build()

    private val itemDao = database.itemDao()
    private val executor = Executors.newSingleThreadExecutor()

    fun addItem(item: Item) {
        executor.execute {
            itemDao.addItem(item)
        }
    }

    fun updateItem(item: Item) {
        executor.execute {
            itemDao.updateItem(item)
        }
    }

    fun deleteItem(item: Item) {
        executor.execute {
            itemDao.deleteItem(item)
        }
    }

    fun deleteAll(items:List<Item>){
        executor.execute {
            itemDao.deleteAll(items)
        }
    }
    fun updateStatus(theID: UUID?, status:Boolean){
        executor.execute {
            itemDao.updateStatus(theID,status)
        }
    }

    fun getItem(id: Long): LiveData<Item?> = itemDao.getItem(id)
    fun getItemWithTitle(title:String):LiveData<Item> = itemDao.getItemWithTitle(title)
    fun getAllItems(): LiveData<List<Item>> = itemDao.getAllItems()
    fun getIncompleteItems(): LiveData<List<Item>> = itemDao.getIncompleteItems()
    fun getLastItem():LiveData<Item> = itemDao.getLastItem()

    companion object {

        private var INSTANCE: ItemRepository? = null
        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = ItemRepository(context)
            }
        }

        fun get(): ItemRepository {
            return INSTANCE
                ?: throw IllegalStateException("ItemRepository must be initialized.")
        }
    }
}
