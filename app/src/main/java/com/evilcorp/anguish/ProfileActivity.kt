package com.evilcorp.anguish

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.evilcorp.anguish.databinding.ActivityProfileBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var network: GetRequestAndEtc




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
        }

    }



    override fun onDestroy() {
        super.onDestroy()
    }
}