package com.example.assignment1

import android.app.Application
import com.example.assignment1.database.AppDatabase
import com.example.assignment1.database.DatabaseRepository

class FinanceApplication: Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { DatabaseRepository(database.incomeDao(), database.expenseDao()) }
}