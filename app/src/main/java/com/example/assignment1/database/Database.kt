package com.example.assignment1.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.assignment1.database.expense.Expense
import com.example.assignment1.database.expense.ExpenseDao
import com.example.assignment1.database.income.Income
import com.example.assignment1.database.income.IncomeDao

@Database(entities = [Income::class, Expense::class], version = 2)
abstract class AppDatabase: RoomDatabase() {

    abstract fun incomeDao(): IncomeDao

    abstract fun expenseDao(): ExpenseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context):AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
