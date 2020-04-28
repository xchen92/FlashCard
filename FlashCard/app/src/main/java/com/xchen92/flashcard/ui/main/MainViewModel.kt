package com.xchen92.flashcard.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.xchen92.flashcard.database.Item
import com.xchen92.flashcard.database.ItemRepository
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {

    init {
        ItemRepository.initialize(application)
    }

    private val itemRepository = ItemRepository.get()
    val cardLiveData = itemRepository.getAllItems()
    val partialCardLiveData = itemRepository.getIncompleteItems()
    var  currentProduct = itemRepository.getLastItem() //initialize

    fun addItem(newItem: Item) {
       itemRepository.addItem(newItem)
    }
    fun getItemWithTitle(title:String){
        currentProduct = itemRepository.getItemWithTitle(title)
    }

    fun deleteItem(item: Item) {
        itemRepository.deleteItem(item)
    }
    fun deleteAll(items:List<Item>){
        itemRepository.deleteAll(items)
    }
    fun updateItem(theID: UUID?, status:Boolean){
        itemRepository.updateStatus(theID,status)
    }

}

