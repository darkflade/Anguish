package com.evilcorp.anguish

import android.content.Intent
import android.os.Bundle
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.evilcorp.anguish.databinding.ActivityTimeTableWeekBinding

class TimeTableWeekActivity : AppCompatActivity() {

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

        binding = ActivityTimeTableWeekBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.toolbar))
        binding.toolbarLayout.title = title
        /*binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(R.id.fab).show()
        }*/
        binding.backButton.setOnClickListener{
            startActivity(Intent(this, ProfileActivity::class.java))
            this.finish()
        }
    }
}