package com.example.assignment1.ui.income

import android.content.Context
import android.view.Menu
import androidx.core.util.Pair
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assignment1.R
import com.example.assignment1.database.DatabaseViewModel
import com.example.assignment1.databinding.FragmentIncomeBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat

class IncomeController(private val fragment: IncomeFragment, private val binding: FragmentIncomeBinding, private val viewModel: DatabaseViewModel) {
    init {
        val recyclerView = binding.incomeList
        val adapter = IncomeAdapter(viewModel)
        val prefs = fragment.requireActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val currency = prefs?.getString("currency", "")

        adapter.setCurrency(currency ?: "")
        adapter.setContext(fragment.requireContext())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(fragment.context)

        viewModel.incomesBetweenDates.observe(fragment.viewLifecycleOwner) { incomes ->
            incomes.let {adapter.submitList(it)}
        }
    }


    fun setUpFilterButton(menu: Menu) {
        val constraint = CalendarConstraints.Builder().setValidator(DateValidatorPointBackward.now())
        val dateRangePicker =
            MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Filter Incomes by date")
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
            viewModel.incomeDates.value = kotlin.Pair(start, end)
            menu.findItem(R.id.remove_filter).isVisible = true
            val filterDatesText = "$start - $end"
            binding.filterDates.text = filterDatesText
        }
        dateRangePicker.show(fragment.parentFragmentManager, null)
    }

    fun setUpRemoveFilterButton(menu: Menu) {
        viewModel.incomeDates.value = kotlin.Pair("", "")
        menu.findItem(R.id.remove_filter).isVisible = false
        binding.filterDates.text = "-"

    }

    fun prepareMenuOption(menu: Menu) {
        val dates = viewModel.incomeDates.value
        if (!dates?.first.isNullOrEmpty()) {
            binding.filterDates.text = "${dates?.first} - ${dates?.second}"
        } else {
            menu.findItem(R.id.remove_filter).isVisible = false
        }
        fragment.requireActivity().invalidateOptionsMenu()
    }
}