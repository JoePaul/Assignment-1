package com.example.assignment1.ui.expense

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.core.util.Pair
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assignment1.FinanceApplication
import com.example.assignment1.R
import com.example.assignment1.database.DatabaseViewModel
import com.example.assignment1.database.DatabaseViewModelFactory
import com.example.assignment1.databinding.FragmentExpenseBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat

class ExpenseFragment : Fragment() {

    private lateinit var binding: FragmentExpenseBinding
    private lateinit var menu: Menu
    private lateinit var expenseController: ExpenseController
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
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentExpenseBinding.inflate(layoutInflater, container, false)
        expenseController = ExpenseController(this, binding, viewModel)

        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.action_filter -> {
                expenseController.setUpFilterButton(menu)
                return true
            }
            R.id.remove_filter -> {
                expenseController.setUpRemoveFilterButton(menu)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        this.menu = menu
        expenseController.prepareMenuOption(menu)
    }
}