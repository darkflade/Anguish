package com.evilcorp.anguish

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.evilcorp.anguish.databinding.ActivityProfileBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var network: GetRequestAndEtc
    private val client = HttpClient(CIO)




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        network = GetRequestAndEtc(this)
        val navView: BottomNavigationView = binding.navView

        val navController : NavController = findNavController(R.id.nav_host_fragment_activity_profile)

        navView.setupWithNavController(navController)

        val currentDestination = navController.currentDestination

        network.needUpdateTimeTable()

        if (currentDestination != null) {
            val currentFragmentId = currentDestination.id
/*
            when (currentFragmentId) {
                R.id.navigation_home -> {
                    Toast.makeText(
                        this, "Home",
                        Toast.LENGTH_LONG
                    ).show()
                }
                R.id.navigation_dashboard -> {
                    Toast.makeText(
                        this, "Nav Block",
                        Toast.LENGTH_LONG
                    ).show()
                }
                R.id.navigation_notifications -> {
                    Toast.makeText(
                        this, "Notification",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }*/
        }

    }



    override fun onDestroy() {
        client.close()
        super.onDestroy()
    }
}