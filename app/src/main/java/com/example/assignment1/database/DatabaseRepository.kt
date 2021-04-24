package com.example.assignment1.database

import androidx.annotation.WorkerThread
import com.example.assignment1.database.expense.Expense
import com.example.assignment1.database.expense.ExpenseDao
import com.example.assignment1.database.expense.ExpensePerCategory
import com.example.assignment1.database.income.Income
import com.example.assignment1.database.income.IncomeDao
import com.example.assignment1.database.income.IncomePerCategory
import kotlinx.coroutines.flow.Flow

class DatabaseRepository(private val incomeDao: IncomeDao, private val expenseDao: ExpenseDao) {

    @WorkerThread
    suspend fun addExpense(expense: Expense) {
        return expenseDao.addExpense(expense)
    }

    fun getBalanceBetweenDates(start: String, end: String): Flow<Double> {
        return if (start.isEmpty() && end.isEmpty()) {
            incomeDao.getBalance()
        } else incomeDao.getBalanceBetweenDates(start, end)
    }

    @WorkerThread
    suspend fun addIncome(income: Income) {
        return incomeDao.addIncome(income)
    }

    fun getExpensesPerCategoryBetweenDates(start: String, end: String): Flow<List<ExpensePerCategory>> {
        return if (start.isEmpty() && end.isEmpty()) {
            expenseDao.getAmountPerCategory()
        } else expenseDao.getAmountPerCategoryBetweenDates(start, end)
    }

    fun getIncomesPerCategoryBetweenDates(start: String, end: String): Flow<List<IncomePerCategory>> {
        return if (start.isEmpty() && end.isEmpty()) {
            incomeDao.getAmountPerCategory()
        } else incomeDao.getAmountPerCategoryBetweenDates(start, end)
    }


    fun getExpensesBetweenDates(start: String, end: String): Flow<List<Expense>> {
        return if (start.isEmpty() && end.isEmpty()) {
            expenseDao.getAllExpenses()
        } else expenseDao.getExpensesBetweenDates(start, end)
    }

    fun getIncomesBetweenDates(start: String, end: String): Flow<List<Income>> {
        return if (start.isEmpty() && end.isEmpty()) {
            incomeDao.getAllIncomes()
        } else incomeDao.getIncomesBetweenDates(start, end)
    }

    @WorkerThread
    suspend fun deleteExpense(expense: Expense) {
        expenseDao.removeExpense(expense)
    }
    
    @WorkerThread
    suspend fun deleteIncome(income: Income) {
        incomeDao.removeIncome(income)
    }
}