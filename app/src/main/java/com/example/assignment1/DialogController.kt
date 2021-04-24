package com.example.assignment1

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.util.Pair
import androidx.core.widget.addTextChangedListener
import com.example.assignment1.database.DatabaseViewModel
import com.example.assignment1.database.enums.ExpenseCategory
import com.example.assignment1.database.enums.IncomeCategory
import com.example.assignment1.database.expense.Expense
import com.example.assignment1.database.income.Income
import com.example.assignment1.databinding.DialogAddBinding
import com.example.assignment1.databinding.DialogChooseBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.text.SimpleDateFormat
import java.util.*


class DialogController(private val activity: MainActivity, private val snackbar: Snackbar, private val databaseViewModel: DatabaseViewModel) {

    private var alertDialogBuilder: MaterialAlertDialogBuilder = MaterialAlertDialogBuilder(activity)
    var dialog: AlertDialog? = null
    private val dialogAddBinding: DialogAddBinding = DialogAddBinding.inflate(activity.layoutInflater, null, false)
    private val dialogChooseBinding: DialogChooseBinding = DialogChooseBinding.inflate(activity.layoutInflater, null, false)

    fun onClickListener() {
        val items = listOf("Income", "Expense")
        val adapter = ArrayAdapter(activity, R.layout.list_item, items)
        val textView = (dialogAddBinding.selectionType.editText as? AutoCompleteTextView)
        addCurrencyToAmount(dialogChooseBinding.textCost)
        textView?.setAdapter(adapter)
        textView?.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> incomeForm()
                1 -> expenseForm()
            }
        }
        launchAlertDialog()
    }

    private fun incomeForm() {
        val adapter = ArrayAdapter(activity,
            R.layout.list_item,
            IncomeCategory.values().map { it.category })
        form(adapter)
    }

    private fun expenseForm() {
        val adapter = ArrayAdapter(activity,
            R.layout.list_item,
            ExpenseCategory.values().map { it.category })
        form(adapter)
    }

    private fun form(adapter: ArrayAdapter<String>) {
        val textView = (dialogChooseBinding.selectionCategory.editText as? AutoCompleteTextView)
        textView?.setText("", false)
        textView?.setAdapter(adapter)
        val parent = dialogAddBinding.parentLinearLayout
        parent.removeAllViewsInLayout()
        parent.addView(dialogChooseBinding.root)
        dialogChooseBinding.textDate.editText?.setOnClickListener {
            val constraint = CalendarConstraints.Builder().setValidator(DateValidatorPointBackward.now()).build()
            val dialog = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Filter Incomes by date")
                    .setSelection(
                        MaterialDatePicker.todayInUtcMilliseconds()
                    )
                    .setCalendarConstraints(constraint)
                    .build()
            dialog.addOnPositiveButtonClickListener {
                dialogChooseBinding.textDate.editText?.setText(SimpleDateFormat("yyyy-MM-dd").format(Date(it)))
            }
            dialog.show(activity.supportFragmentManager, null)
        }
    }

    private fun launchAlertDialog() {
        val prefs = activity.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val firstname = prefs.getString("firstname", "")

        dialog = alertDialogBuilder.setView(dialogAddBinding.root)
            .setTitle("Add")
            .setPositiveButton("Add") { dialog, _ ->
                addToDatabase()
                snackbar.setText("Good job $firstname! It's added")
                snackbar.show()
                dialog.dismiss()

            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setOnDismissListener {
                removeViews()
            }
            .show()
        dialog!!.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)

        val positiveButton = dialog!!.getButton(Dialog.BUTTON_POSITIVE)
        setPositiveButtonConstraints(positiveButton!!)


    }

    private fun setPositiveButtonConstraints(positiveButton: Button) {
        positiveButton.isEnabled = false
        val selectCategory = dialogChooseBinding.selectionCategory.editText
        val amount = dialogChooseBinding.textCost.editText
        val title = dialogChooseBinding.title.editText

        selectCategory?.addTextChangedListener {
            val tempAmount = amount!!.text.toString()
            positiveButton.isEnabled = tempAmount.isNotEmpty() && tempAmount.toDouble() == 0.0 &&
                    title!!.text.isNotEmpty()

        }
        amount?.addTextChangedListener {
            positiveButton.isEnabled =
                selectCategory!!.text.isNotEmpty() &&
                        it.toString() != "" &&
                        it.toString().toDouble() != 0.0 &&
                        title!!.text.isNotEmpty()
        }
        title?.addTextChangedListener {

            val tempAmount = amount!!.text.toString()
            positiveButton.isEnabled =
                selectCategory!!.text.isNotEmpty() &&
                        tempAmount != "" &&
                        tempAmount.toDouble() != 0.0  &&
                        it.toString().isNotEmpty()
        }
    }

    private fun removeViews() {
        val type = (dialogAddBinding.selectionType.editText as? AutoCompleteTextView)
        dialogChooseBinding.title.editText?.setText("")
        dialogChooseBinding.textCost.editText?.setText("")
        (dialogChooseBinding.selectionCategory.editText as? AutoCompleteTextView)?.setText("")
        dialogChooseBinding.textDate.editText?.setText("")
        type?.setText("")
        (dialogAddBinding.root.parent as? ViewGroup)?.removeAllViews()
        (dialogChooseBinding.root.parent as? ViewGroup)?.removeAllViews()

    }

    private fun addCurrencyToAmount(inputLayout: TextInputLayout) {
        val currency = activity.getSharedPreferences("prefs", Context.MODE_PRIVATE).getString("currency", "")
        if (currency == "$" || currency == "£") inputLayout.prefixText = currency
        else inputLayout.suffixText = currency
    }

    private fun addToDatabase() {
        val title = dialogChooseBinding.title.editText!!.text.toString()
        val category = dialogChooseBinding.selectionCategory.editText!!.text.toString()
        val date = dialogChooseBinding.textDate.editText!!.text.toString().ifEmpty { getDate() }
        val amount = dialogChooseBinding.textCost.editText!!.text.toString().toDouble()
        when ((dialogAddBinding.selectionType.editText as? AutoCompleteTextView)?.text.toString()) {
            "Income" -> databaseViewModel.addIncome(Income(date,
                title,
                category,
                amount,
                0))
            "Expense" -> databaseViewModel.addExpense(Expense(date,
                title,
                category,
                amount,
                0))
        }
    }

    private fun getDate(format: String = "yyyy-MM-dd", locale: Locale = Locale.getDefault()): String {
        return SimpleDateFormat(format, locale).format(Calendar.getInstance().time)
    }

    fun setPersistentData(dialogPersistent: ArrayList<String>) {
        onClickListener()
        val type = dialogPersistent.removeFirst()
        (dialogAddBinding.selectionType.editText as? AutoCompleteTextView)?.setText(type, false)
        when (type) {
            "Income" -> incomeForm()
            "Expense" -> expenseForm()
        }
        (dialogChooseBinding.selectionCategory.editText as? AutoCompleteTextView)?.setText(dialogPersistent.removeFirst(), false)
        dialogChooseBinding.title.editText?.setText(dialogPersistent.removeFirst())
        dialogChooseBinding.textCost.editText?.setText(dialogPersistent.removeFirst())
        dialogChooseBinding.textDate.editText?.setText(dialogPersistent.removeFirst())
    }

    fun getPersistentData(): ArrayList<String> {
        val type = (dialogAddBinding.selectionType.editText as? AutoCompleteTextView)?.text.toString()
        val title = dialogChooseBinding.title.editText?.text.toString()
        val category = dialogChooseBinding.selectionCategory.editText?.text.toString()
        val amount = dialogChooseBinding.textCost.editText?.text.toString()
        val date = dialogChooseBinding.textDate.editText?.text.toString()
        return arrayListOf(type, category, title, amount, date)
    }


}