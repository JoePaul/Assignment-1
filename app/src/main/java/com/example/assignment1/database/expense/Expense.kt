package com.example.assignment1.database.expense

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.assignment1.database.enums.ExpenseCategory


@Entity(tableName = "expenses")
data class Expense (
    val date: String,
    val title: String,
    val category: String,
    val amount: Double,
    @PrimaryKey(autoGenerate = true) val id: Int,
)