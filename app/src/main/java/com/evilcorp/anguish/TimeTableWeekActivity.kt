package com.evilcorp.anguish

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.evilcorp.anguish.databinding.ActivityTimeTableWeekBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TimeTableWeekActivity : AppCompatActivity() {

    private lateinit var timeTableAdapterP: TimeTableAdapterP
    private lateinit var timeTableSQLite: TimeTableSQLite
    private lateinit var binding: ActivityTimeTableWeekBinding

    data class PrintTimeTableClass(
        val number: Int,
        val subgroupCount: Int,
        val title: String,
        val group: String,
        val subgroupNumber: Int,
        val teacher: String,
        val auditNumber: String,
        val campusTitle: String
    )

    data class DaySchedule(
        val dayTitle: String,
        val lessons: List<PrintTimeTableClass>
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        timeTableSQLite = TimeTableSQLite(this)
        binding = ActivityTimeTableWeekBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CoroutineScope(Dispatchers.IO).launch {
            val timeTableAll = timeTableSQLite.dbExtractAll()

            withContext(Dispatchers.Main) {
                timeTableAdapterP = TimeTableAdapterP(timeTableAll)
                binding.recyclerView.layoutManager = LinearLayoutManager(this@TimeTableWeekActivity)
                binding.recyclerView.adapter = timeTableAdapterP
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        finish()
    }
}