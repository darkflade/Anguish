package com.evilcorp.anguish.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.evilcorp.anguish.FacultyAdapter
import com.evilcorp.anguish.FacultyAdapterP
import com.evilcorp.anguish.RatingAdapterP
import com.evilcorp.anguish.RatingSQLite
import com.evilcorp.anguish.TimeTableAdapterP
import com.evilcorp.anguish.TimeTableSQLite
import com.evilcorp.anguish.databinding.FragmentNotificationsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotificationsFragment : Fragment() {

    private lateinit var facultyAdapterP: FacultyAdapterP
    private lateinit var ratingSQLite: RatingSQLite


    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    data class PrintDiscipline(
        val name: String
    )

    data class FacultyDiscipline(
        val faculty: String,
        val disciplines: List<PrintDiscipline>
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        ratingSQLite = RatingSQLite(requireContext())

        CoroutineScope(Dispatchers.IO).launch {

            val subjects = ratingSQLite.dbExtractAllFaculty()

            withContext(Dispatchers.Main) {
                facultyAdapterP = FacultyAdapterP(subjects)
                binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
                binding.recyclerView.adapter = facultyAdapterP
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}