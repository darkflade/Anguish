package com.evilcorp.anguish.ui.faculty

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.evilcorp.anguish.BallsActivity
import com.evilcorp.anguish.FacultyAdapterP
import com.evilcorp.anguish.RatingSQLite
import com.evilcorp.anguish.databinding.FragmentFacultyBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FacultyFragment : Fragment() {

    private lateinit var facultyAdapterP: FacultyAdapterP
    private lateinit var ratingSQLite: RatingSQLite


    private var _binding: FragmentFacultyBinding? = null
    private val binding get() = _binding!!

    data class PrintDiscipline(
        val id: Int,
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

        _binding = FragmentFacultyBinding.inflate(inflater, container, false)
        val root: View = binding.root
        ratingSQLite = RatingSQLite(requireContext())

        CoroutineScope(Dispatchers.IO).launch {

            val subjects = ratingSQLite.dbExtractAllFaculty()

            withContext(Dispatchers.Main) {
                facultyAdapterP = FacultyAdapterP(subjects){ disciplineId ->
                    Log.e("id", disciplineId.toString())
                    val intent = Intent(requireContext(), BallsActivity::class.java)
                    intent.putExtra("discipline_id", disciplineId)
                    startActivity(intent)
                }
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