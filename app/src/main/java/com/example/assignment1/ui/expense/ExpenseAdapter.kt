package com.example.assignment1.ui.expense

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment1.R
import com.example.assignment1.database.DatabaseViewModel
import com.example.assignment1.database.enums.ExpenseCategory
import com.example.assignment1.database.enums.IncomeCategory
import com.example.assignment1.database.expense.Expense
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class ExpenseAdapter(private val databaseViewModel: DatabaseViewModel): ListAdapter<Expense, ExpenseAdapter.ViewHolder>(ExpenseAdapter.EXPENSE_COMPARATOR) {
    private lateinit var context: Context
    private var currency: String = ""

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.title)
        val amount: TextView = view.findViewById(R.id.amount)
        val category: View =  view.findViewById(R.id.item_category)
        val date: TextView = view.findViewById(R.id.date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_list_item, parent, false)
        return ViewHolder(view)
    }




    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = getItem(position)
        val amount = when (currency) {
            "$"-> "$currency${ String.format("%.2f",current.amount)}"
            "£" -> "$currency${String.format("%.2f",current.amount)}"
            else -> "${String.format("%.2f",current.amount)}$currency"
        }
        holder.amount.text = amount
        holder.title.text = current.title
        holder.date.text = current.date
        setVectorImageOfItem(holder.category, current.category)
        holder.itemView.setOnClickListener {
            val items = "Title: ${current.title} \nDate: ${current.date}\nAmount: $amount \nCategory: ${current.category}"
            MaterialAlertDialogBuilder(context)
                .setTitle("Expense")
                .setMessage(items)
                .setNeutralButton("Close") {dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton("Delete") { dialog, _ ->
                    deleteDialog(current)
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun deleteDialog(expense: Expense) {
        MaterialAlertDialogBuilder(context)
            .setTitle("Delete")
            .setMessage("Are you sure you want to delete this income?")
            .setNeutralButton("Close") {dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Delete") {dialog, _ ->
                databaseViewModel.deleteExpense(expense)
                dialog.dismiss()
            }
            .show()
    }
    private fun setVectorImageOfItem(view: View, category: String) {
        val id: Int = when (category) {
            ExpenseCategory.GIFT.category -> R.drawable.ic_gift_24
            ExpenseCategory.FOOD.category -> R.drawable.ic_food_24
            ExpenseCategory.OTHER.category -> R.drawable.ic_other_24
            ExpenseCategory.TRAVEL.category -> R.drawable.ic_travel_24
            ExpenseCategory.ACCOMMODATION.category -> R.drawable.ic_apartment_24
            ExpenseCategory.LEISURE.category -> R.drawable.ic_leisure_24
            else -> return
        }
        view.setBackgroundResource(id)
    }

    fun setCurrency(currency: String) {
        this.currency = currency
    }

    fun setContext(context: Context) {
        context.also { this.context = it }
    }

    companion object {
        private val EXPENSE_COMPARATOR = object : DiffUtil.ItemCallback<Expense>() {
            override fun areItemsTheSame(oldItem: Expense, newItem: Expense): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Expense, newItem: Expense): Boolean {
                return  oldItem.id == newItem.id &&
                        oldItem.date == newItem.date &&
                        oldItem.category == newItem.category &&
                        oldItem.amount == newItem.amount &&
                        oldItem.title == newItem.title
            }
        }
    }


}