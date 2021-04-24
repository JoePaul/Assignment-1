package com.example.assignment1.database.income

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.assignment1.database.enums.IncomeCategory


@Entity(tableName = "incomes")
data class Income(
        val date: String,
        val title: String,
        val category: String,
        val amount: Double,
        @PrimaryKey(autoGenerate = true) val id: Int,
)