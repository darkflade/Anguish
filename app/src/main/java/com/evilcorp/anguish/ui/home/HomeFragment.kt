package com.evilcorp.anguish.ui.home

import TokenManager
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.evilcorp.anguish.MainActivity
import com.evilcorp.anguish.databinding.FragmentHomeBinding
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.io.File


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var tokenManager: TokenManager
    private val client = HttpClient(CIO) {
        install(ContentNegotiation){
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

//------------------------------Load a fucking picture and txt
        try {
            val inputStream = File(requireContext().filesDir, "profile_photo.jpg").inputStream()
            val bitmap = BitmapFactory.decodeStream(inputStream)
            binding.profileImage.setImageBitmap(bitmap)
        } catch (e: Exception) {
            Log.e("ProfileImage", "Error loading profile photo: ${e.message}")
        }

        try {
            val userData = File(requireContext().filesDir, "UserData.txt").readText().split(" ")
            binding.NameProfile.text = userData[0]
            binding.LastNameProfile.text = userData[1]
            binding.ThirdNameProfile.text = userData[2]
        } catch (e: Exception) {
            Log.e("ProfileImage", "Error loading profile photo: ${e.message}")
        }


        tokenManager = TokenManager(requireContext())


        binding.SignOutButton.setOnClickListener{
            CoroutineScope(Dispatchers.IO).launch {
                tokenManager.clearCredentials()
            }
            startActivity(Intent(requireContext(), MainActivity::class.java))
            requireActivity().finish()
        }
/*
        binding.SignGetTestButton.setOnClickListener{
            tokenManager.getAccessToken()?.let { token -> getUserProfile(token) }
        }
*/

        return root
    }



    override fun onDestroyView() {
        client.close()
        super.onDestroyView()
        _binding = null
    }
}