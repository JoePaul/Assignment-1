package com.example.assignment1.database.income

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface IncomeDao {


    @Query("SELECT * FROM incomes order by date desc, id desc")
    fun getAllIncomes(): Flow<List<Income>>

    @Query("SELECT IFNULL(SUM(amount), 0) FROM incomes")
    fun getTotalIncome(): Flow<Double>

    @Query("SELECT * FROM incomes where :start <= date  and date <= :end order by date desc, id desc")
    fun getIncomesBetweenDates(start: String, end: String): Flow<List<Income>>

    @Insert
    suspend fun addIncome(vararg income: Income)

    @Delete
    suspend fun removeIncome(income: Income)

    @Query("SELECT category, sum(amount) as amount FROM incomes GROUP BY (category)")
    fun getAmountPerCategory(): Flow<List<IncomePerCategory>>

    @Query("SELECT category, sum(amount) as amount FROM incomes where :start <= date  and date <= :end GROUP BY (category)")
    fun getAmountPerCategoryBetweenDates(start: String, end: String): Flow<List<IncomePerCategory>>

    @Query("SELECT IFNULL(SUM(amount), 0) - (SELECT IFNULL(SUM(amount), 0) FROM expenses)  FROM incomes")
    fun getBalance(): Flow<Double>

    @Query("SELECT IFNULL(SUM(amount), 0) - (SELECT IFNULL(SUM(amount), 0) FROM expenses where :start <= date  and date <= :end)  FROM incomes where :start <= date  and date <= :end")
    fun getBalanceBetweenDates(start: String, end: String): Flow<Double>

}