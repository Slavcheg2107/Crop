package com.example.crop

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.Navigation.findNavController

class MainActivity : AppCompatActivity(), NavHost {
    lateinit var controller: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        controller = findNavController(this, R.id.nav_host_fragment)
    }

    override fun getNavController(): NavController {
        return controller
    }

    override fun onSupportNavigateUp(): Boolean = findNavController(this, R.id.nav_host_fragment).navigateUp()

}