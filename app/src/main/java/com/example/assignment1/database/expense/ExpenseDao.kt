package com.example.assignment1.database.expense

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface ExpenseDao {


    @Query("SELECT * FROM expenses order by date desc, id desc")
    fun getAllExpenses(): Flow<List<Expense>>

    @Query("SELECT SUM(amount) FROM expenses")
    fun getTotalExpense(): Flow<Double>

    @Insert
    suspend fun addExpense(vararg expense: Expense)

    @Delete
    suspend fun removeExpense(expense: Expense)

    @Query("SELECT category, sum(amount) as amount FROM expenses GROUP BY (category)")
    fun getAmountPerCategory(): Flow<List<ExpensePerCategory>>

    @Query("SELECT category, sum(amount) as amount FROM expenses where :start <= date  and date <= :end GROUP BY (category)")
    fun getAmountPerCategoryBetweenDates(start: String, end: String): Flow<List<ExpensePerCategory>>

    @Query("SELECT * FROM expenses where :start <= date  and date <= :end order by date desc, id desc ")
    fun getExpensesBetweenDates(start: String?, end: String?): Flow<List<Expense>>

}