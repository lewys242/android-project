package com.mbongo.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "loans")
data class Loan(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val principal: Double,
    val interestRate: Double,
    val termMonths: Int,
    val startDate: String, // Format: yyyy-MM-dd
    val lender: String?,
    val purpose: String?,
    val createdAt: Long = System.currentTimeMillis()
)
