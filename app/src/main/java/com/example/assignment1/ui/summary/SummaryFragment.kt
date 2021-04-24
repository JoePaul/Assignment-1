package com.example.assignment1.ui.summary

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
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
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat


class SummaryFragment : Fragment(), OnChartValueSelectedListener {

    private lateinit var binding: FragmentSummaryBinding
    private lateinit var menu: Menu
    private lateinit var summaryController: SummaryController
    private val viewModel: DatabaseViewModel by viewModels {
        DatabaseViewModelFactory((activity?.application as FinanceApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSummaryBinding.inflate(layoutInflater, container, false)
        val root = binding.root
        summaryController = SummaryController(this, binding, viewModel)
        return root
    }

    override fun onPrepareOptionsMenu(menu: Menu){
        super.onPrepareOptionsMenu(menu)
        summaryController.prepareMenuOptions(menu)
        this.menu = menu
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_filter -> {
                summaryController.setUpFilterButton(menu)
                true
            }
            R.id.remove_filter -> {
                summaryController.setUpRemoveFilterButton(menu)
                true
            }
            else ->  super.onOptionsItemSelected(item)
        }

    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        summaryController.onChartValueSelected(e)
    }

    override fun onNothingSelected() {  }

}