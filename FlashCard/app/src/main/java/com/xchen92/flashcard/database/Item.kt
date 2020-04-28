package com.xchen92.flashcard.database


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.xchen92.flashcard.R
import java.util.*

@Entity(tableName = "items_table")
data class Item(

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,
    var title: String = "",
    var completed: Boolean = false,
    var type: Int = 0,
    var uuid: UUID = UUID.randomUUID()
) {
    val photoFileName
        get() = "IMG_$uuid.jpg"

    val typeImage
        get() = when (type) {
            0 -> R.drawable.work //work
            1 -> R.drawable.lifestyle //lifestyle
            else -> R.drawable.personal_growth //personal growth
        }
    val typeName: String
        get() = when (type) {
            0 -> "Work" //work
            1 -> "Lifestyle"//lifestyle
            else -> "Personal Growth" //personal growth
        }
}