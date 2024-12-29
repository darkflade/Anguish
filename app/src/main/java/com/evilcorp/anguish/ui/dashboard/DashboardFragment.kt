package com.evilcorp.anguish.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.evilcorp.anguish.TimeTableAdapterP
import com.evilcorp.anguish.TimeTableSQLite
import com.evilcorp.anguish.TimeTableWeekActivity
import com.evilcorp.anguish.databinding.FragmentDashboardBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private lateinit var timeTableAdapterP: TimeTableAdapterP
    private lateinit var timeTableSQLite: TimeTableSQLite


    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        timeTableSQLite = TimeTableSQLite(requireContext())

        CoroutineScope(Dispatchers.IO).launch {

            val twoDay = timeTableSQLite.dbExtractTwoDays()

            withContext(Dispatchers.Main) {
                timeTableAdapterP = TimeTableAdapterP(twoDay)
                binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
                binding.recyclerView.adapter = timeTableAdapterP
            }
        }

        binding.weekTtButton.setOnClickListener{
            startActivity(Intent(requireContext(), TimeTableWeekActivity::class.java))
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}