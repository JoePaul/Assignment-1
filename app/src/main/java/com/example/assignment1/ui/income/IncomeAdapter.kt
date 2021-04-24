package com.example.assignment1.ui.income

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment1.R
import com.example.assignment1.database.DatabaseViewModel
import com.example.assignment1.database.enums.IncomeCategory
import com.example.assignment1.database.income.Income
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class IncomeAdapter(private val databaseViewModel: DatabaseViewModel): ListAdapter<Income, IncomeAdapter.ViewHolder>(INCOME_COMPARATOR) {
    private var currency: String = ""
    private lateinit var context: Context
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.title)
        val amount: TextView = view.findViewById(R.id.amount)
        val category: View = view.findViewById(R.id.item_category)
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
            else -> "${String.format("%.2f", current.amount)}$currency"
        }
        holder.amount.text = amount
        holder.date.text = current.date
        holder.title.text = current.title
        setVectorImageOfItem(holder.category, current.category)
        holder.itemView.setOnClickListener {
            val items = "Title: ${current.title} \nDate: ${current.date}\nAmount: $amount \nCategory: ${current.category}"
            MaterialAlertDialogBuilder(context)
                .setTitle("Income")
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

    private fun deleteDialog(income: Income) {
        MaterialAlertDialogBuilder(context)
            .setTitle("Delete")
            .setMessage("Are you sure you want to delete this income?")
            .setNeutralButton("Close") {dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Delete") {dialog, _ ->
                databaseViewModel.deleteIncome(income)
                dialog.dismiss()
            }
            .show()
    }

    private fun setVectorImageOfItem(view: View, category: String) {
        val id: Int = when (category) {
            IncomeCategory.GIFT.category -> R.drawable.ic_gift_24
            IncomeCategory.SALARY.category -> R.drawable.ic_money_black_24
            IncomeCategory.OTHER.category -> R.drawable.ic_other_24
            else -> return
        }
        view.setBackgroundResource(id)
    }

    fun setCurrency(currency: String) {
        this.currency = currency
    }

    fun setContext(context: Context) {
        this.context = context
    }

    companion object {
        private val INCOME_COMPARATOR = object : DiffUtil.ItemCallback<Income>() {
            override fun areItemsTheSame(oldItem: Income, newItem: Income): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Income, newItem: Income): Boolean {
                return  oldItem.id == newItem.id &&
                        oldItem.date == newItem.date &&
                        oldItem.category == newItem.category &&
                        oldItem.amount == newItem.amount &&
                        oldItem.title == newItem.title
            }
        }
    }
}
