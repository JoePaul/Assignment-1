package com.example.assignment1.ui.income

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.assignment1.FinanceApplication
import com.example.assignment1.R
import com.example.assignment1.database.DatabaseViewModel
import com.example.assignment1.database.DatabaseViewModelFactory
import com.example.assignment1.databinding.FragmentIncomeBinding

class IncomeFragment : Fragment() {

    private lateinit var menu: Menu

    private lateinit var binding: FragmentIncomeBinding
    private lateinit var incomeController: IncomeController
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
    ): View? {
        binding = FragmentIncomeBinding.inflate(inflater, container, false)
        incomeController = IncomeController(this, binding, viewModel)
        return binding.root
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.action_filter -> {
                incomeController.setUpFilterButton(menu)
                return true
            }
            R.id.remove_filter -> {
                incomeController.setUpRemoveFilterButton(menu)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        this.menu = menu
        incomeController.prepareMenuOption(menu)
    }


}