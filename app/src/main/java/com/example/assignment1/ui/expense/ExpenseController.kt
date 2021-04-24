package com.example.assignment1.ui.expense

import android.content.Context
import android.view.Menu
import androidx.core.util.Pair
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assignment1.R
import com.example.assignment1.database.DatabaseViewModel
import com.example.assignment1.databinding.FragmentExpenseBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat

class ExpenseController(private val fragment: ExpenseFragment, private val binding: FragmentExpenseBinding, private val viewModel: DatabaseViewModel) {

    init {
        val recyclerView = binding.expensesList
        val adapter = ExpenseAdapter(viewModel)
        val prefs = fragment.requireActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val currency = prefs?.getString("currency", "")

        adapter.setCurrency(currency ?: "")
        adapter.setContext(fragment.requireContext())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(fragment.context)

        viewModel.expensesBetweenDates.observe(fragment.viewLifecycleOwner) { expenses ->
            expenses.let {adapter.submitList(it)}
        }
    }


    fun setUpFilterButton(menu: Menu) {
        val constraint = CalendarConstraints.Builder().setValidator(DateValidatorPointBackward.now())
        val dateRangePicker =
            MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Filter Expenses by date")
                .setCalendarConstraints(constraint.build())
                .setSelection(Pair(
                    MaterialDatePicker.todayInUtcMilliseconds(),
                    MaterialDatePicker.todayInUtcMilliseconds()
                ))
                .build()

        dateRangePicker.addOnPositiveButtonClickListener { pair ->
            val sdf =  SimpleDateFormat("yyyy-MM-dd")
            val start = sdf.format(pair.first)
            val end = sdf.format(pair.second)
            viewModel.expenseDates.value = kotlin.Pair(start, end)
            menu.findItem(R.id.remove_filter).isVisible = true
            val filterDatesText = "$start - $end"
            binding.filterDates.text = filterDatesText
        }
        dateRangePicker.show(fragment.parentFragmentManager, null)
    }

    fun setUpRemoveFilterButton(menu: Menu) {
        viewModel.expenseDates.value = kotlin.Pair("", "")
        menu.findItem(R.id.remove_filter).isVisible = false
        binding.filterDates.text = "-"

    }

    fun prepareMenuOption(menu: Menu) {
        val dates = viewModel.expenseDates.value
        if (!dates?.first.isNullOrEmpty()) {
            binding.filterDates.text = "${dates?.first} - ${dates?.second}"
        } else {
            menu.findItem(R.id.remove_filter).isVisible = false
        }
        fragment.requireActivity().invalidateOptionsMenu()
    }
}