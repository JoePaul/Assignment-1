package com.example.assignment1.ui.summary

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.Menu
import androidx.core.util.Pair
import androidx.fragment.app.viewModels
import com.example.assignment1.FinanceApplication
import com.example.assignment1.R
import com.example.assignment1.database.DatabaseViewModel
import com.example.assignment1.database.DatabaseViewModelFactory
import com.example.assignment1.databinding.FragmentSummaryBinding
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat

class SummaryController(private val fragment: SummaryFragment, private val binding: FragmentSummaryBinding, private val viewModel: DatabaseViewModel) {

    private var currency: String = ""

    init {
        val prefs = fragment.activity?.getSharedPreferences( "prefs", Context.MODE_PRIVATE)
        currency = prefs?.getString("currency", "") ?: ""
        viewModel.balanceBetweenDates.observe(fragment.viewLifecycleOwner) {
            val amountView = binding.balanceAmount
            val amount = it ?: 0.00
            if (amount > 0.00) amountView.setTextColor(Color.GREEN)
            else if (amount < 0.00) amountView.setTextColor(Color.RED)
            amountView.text = addCurrency(amount)
        }
        val greeting = "Welcome to your financial summary ${prefs?.getString("firstname", "") ?: ""}"
        binding.greeting.text = greeting
        setUpIncomePieChart()
        setUpExpensePieChart()
    }

    private fun addCurrency(amount: Double): String {
        val parsedAmount = String.format("%.2f", amount)
        return when (currency) {
            "$"-> "$currency$parsedAmount"
            "£" -> "$currency$parsedAmount"
            else -> "$parsedAmount$currency"
        }
    }

    private fun setUpIncomePieChart() {
        val chart = binding.pieChartIncome
        val amount = addCurrency(0.00)
        chart.centerText = "Income\n $amount"
        viewModel.incomeCategoryBetweenDates.observe(fragment.viewLifecycleOwner) { list ->
            val income = list.sumOf { it.amount }
            val updatedAmount = addCurrency(income)
            chart.centerText = "Income\n $updatedAmount"
            val dataEntries = list.map { PieEntry(it.amount.toFloat(), it.category) }
            setUpPieChartObserver(chart, dataEntries)
        }
    }


    private fun setUpExpensePieChart() {
        val chart = binding.pieChartExpense
        val amount = when (currency) {
            "$"-> "${currency}0"
            "£" -> "${currency}0"
            else -> "0$currency"
        }
        chart.centerText = "Expense\n $amount"
        viewModel.expenseCategoryBetweenDates.observe(fragment.viewLifecycleOwner) { list ->
            val expense = list.sumOf { it.amount }
            val updatedAmount = addCurrency(expense)
            chart.centerText = "Expense\n $updatedAmount"
            val dataEntries = list.map { PieEntry(it.amount.toFloat(), it.category) }
            setUpPieChartObserver(chart, dataEntries)
        }
    }




    private fun setUpPieChartObserver(chart: PieChart, entries: List<PieEntry>) {

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = ColorTemplate.createColors(ColorTemplate.COLORFUL_COLORS)

        dataSet.valueTextSize = 16f
        dataSet.selectionShift = 10f
        dataSet.sliceSpace = 2.5f

        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter(chart))
        chart.data = data
        data.setValueTextSize(13f)
        chart.setOnChartValueSelectedListener(fragment)
        chart.invalidate()
    }

    fun setUpFilterButton(menu: Menu) {
        val constraint = CalendarConstraints.Builder().setValidator(DateValidatorPointBackward.now())
        val dateRangePicker =
            MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Filter between dates")
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
            val filterDatesText = "Showing $start - $end"
            setFilterText(filterDatesText, 15f)
            menu.findItem(R.id.remove_filter).isVisible = true

            viewModel.incomeDates.value = kotlin.Pair(start, end)
        }
        dateRangePicker.show(fragment.parentFragmentManager, null)
    }

    private fun setFilterText(text: String, size: Float) {
        binding.filterText.setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
        binding.filterText.text = text
    }

    fun setUpRemoveFilterButton(menu: Menu) {
        setFilterText("", 0f)
        viewModel.expenseDates.value = kotlin.Pair("", "")
        viewModel.incomeDates.value = kotlin.Pair("", "")
        menu.findItem(R.id.remove_filter).isVisible = false
    }

    fun onChartValueSelected(entry: Entry?) {
        val value = (entry as PieEntry).value
        val label = entry.label
        MaterialAlertDialogBuilder(fragment.requireContext())
            .setTitle(label)
            .setMessage("Amount: ${addCurrency(value.toDouble())}")
            .setOnDismissListener {
                binding.pieChartIncome.highlightValue(null, false)
                binding.pieChartExpense.highlightValue(null, false)
            }
            .setNeutralButton("Close") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    fun prepareMenuOptions(menu: Menu) {
        val dates = viewModel.incomeDates.value
        if (!dates?.first.isNullOrEmpty()) {
            setFilterText("Showing ${dates?.first} - ${dates?.second}", 15f)
        } else {
            menu.findItem(R.id.remove_filter).isVisible = false
        }
        fragment.requireActivity().invalidateOptionsMenu()
    }

}