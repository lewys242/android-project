package com.mbongo.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "incomes")
data class Income(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val description: String?,
    val month: String, // Format: yyyy-MM
    val date: String?, // Format: yyyy-MM-dd
    val type: String = "other", // "salary" ou "other"
    val createdAt: Long = System.currentTimeMillis()
)
