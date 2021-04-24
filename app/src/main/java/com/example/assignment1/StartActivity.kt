package com.example.assignment1

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import com.example.assignment1.databinding.ActivityStartBinding
import com.google.android.material.snackbar.Snackbar

class StartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val firstname = prefs.getString("firstname", "")
        //val lastname = prefs.getString("lastname", "")
        val currency = prefs.getString("currency", "")

        if(firstname != "" && currency != "") {//&& lastname != "") {
            moveToApplication()
        }

        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val currencyItems = listOf("Swedish Krona (kr)", "US Dollar ($)", "British Pound (£)", "Euro (€)")
        val adapter = ArrayAdapter(this, R.layout.list_item, currencyItems)
        (binding.selectionCurrency.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        binding.buttonEnterApp.setOnClickListener {
            if (onSubmitName()) {
                moveToApplication()
            }
        }

    }



    private fun moveToApplication() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun onSubmitName(): Boolean {

        val firstname = binding.firstname.editText
        val currency  = binding.selectionCurrency.editText
        if(firstname?.text.toString().isNotEmpty() && currency?.text.toString().isNotEmpty()) {
            val prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE) ?: return false
            with(prefs.edit()) {
                putString("firstname", firstname?.text.toString())
                val currencyShort = currency?.text.toString().substringAfter("(").substringBefore(")")
                putString("currency", currencyShort)
                apply()
            }
            return true
        }
        if (firstname?.text.toString().isEmpty())
            firstname?.error = "You need to enter a name"
        if (currency?.text.toString().isEmpty())
            currency?.error = "Please enter a preferred currency"
        return false

    }


}