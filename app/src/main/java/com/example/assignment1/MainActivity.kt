package com.example.assignment1

import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.assignment1.database.DatabaseViewModel
import com.example.assignment1.database.DatabaseViewModelFactory
import com.example.assignment1.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private lateinit var mainBinding: ActivityMainBinding
    private var dialog: DialogController? = null
    private lateinit var snackbar: Snackbar
    private val viewModel: DatabaseViewModel by viewModels {
        DatabaseViewModelFactory((application as FinanceApplication).repository)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        val navView: BottomNavigationView = mainBinding.navView

        snackbar = Snackbar.make(mainBinding.root, "", Snackbar.LENGTH_SHORT)


        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_summary, R.id.navigation_expense, R.id.navigation_income))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        dialog = DialogController(this, snackbar, viewModel)
        val dialogPersistent = savedInstanceState?.getStringArrayList("dialog")
        if(!dialogPersistent.isNullOrEmpty() && dialogPersistent.joinToString("").isNotEmpty()) {
            dialog!!.setPersistentData(dialogPersistent)
        }
        mainBinding.addButton.setOnClickListener{
            dialog?.onClickListener()
        }

    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val data = dialog?.getPersistentData()
        outState.putStringArrayList("dialog", data)

    }

    override fun onDestroy() {
        dialog?.dialog?.cancel()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_menu, menu)
        return true
    }

}