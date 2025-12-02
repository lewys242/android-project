package com.mbongo.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: String,
    val color: String = "#FFD4AF37",
    val icon: String = "💰",
    val createdAt: Long = System.currentTimeMillis()
)
