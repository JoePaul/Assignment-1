package com.example.assignment1.database

import androidx.lifecycle.*
import com.example.assignment1.database.expense.Expense
import com.example.assignment1.database.expense.ExpensePerCategory
import com.example.assignment1.database.income.Income
import com.example.assignment1.database.income.IncomePerCategory
import kotlinx.coroutines.launch

class DatabaseViewModel(private val databaseRepository: DatabaseRepository): ViewModel() {

    val incomeDates: MutableLiveData<Pair<String, String>> = MutableLiveData(Pair("", ""))
    val expenseDates: MutableLiveData<Pair<String, String>> = MutableLiveData(Pair("", ""))

    fun addIncome(income: Income) = viewModelScope.launch {
        databaseRepository.addIncome(income)
    }

    fun addExpense(expense: Expense) = viewModelScope.launch {
        databaseRepository.addExpense(expense)
    }

    val balanceBetweenDates : LiveData<Double> = Transformations.switchMap(incomeDates) {
        databaseRepository.getBalanceBetweenDates(it.first, it.second).asLiveData()
    }

    val incomesBetweenDates : LiveData<List<Income>> = Transformations.switchMap(incomeDates) {
        databaseRepository.getIncomesBetweenDates(it.first, it.second).asLiveData()
    }

    val expensesBetweenDates : LiveData<List<Expense>> = Transformations.switchMap(expenseDates) {
        databaseRepository.getExpensesBetweenDates(it.first, it.second).asLiveData()
    }

    val expenseCategoryBetweenDates : LiveData<List<ExpensePerCategory>> = Transformations.switchMap(expenseDates) {
        databaseRepository.getExpensesPerCategoryBetweenDates(it.first, it.second).asLiveData()
    }

    val incomeCategoryBetweenDates : LiveData<List<IncomePerCategory>> = Transformations.switchMap(incomeDates) {
        databaseRepository.getIncomesPerCategoryBetweenDates(it.first, it.second).asLiveData()
    }

    fun deleteIncome(income: Income) = viewModelScope.launch {
        databaseRepository.deleteIncome(income)

    }

    fun deleteExpense(expense: Expense) = viewModelScope.launch {
        databaseRepository.deleteExpense(expense)
    }
}

class DatabaseViewModelFactory(private val repository: DatabaseRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(DatabaseViewModel::class.java)) {
            return DatabaseViewModel(repository) as T
        }
        throw IllegalArgumentException("Unkown ViewModel class")
    }

}