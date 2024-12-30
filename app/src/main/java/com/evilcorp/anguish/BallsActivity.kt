package com.evilcorp.anguish

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.evilcorp.anguish.TimeTableWeekActivity
import com.evilcorp.anguish.databinding.ActivityTimeTableWeekBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BallsActivity : AppCompatActivity() {

    private lateinit var ratingAdapterP: RatingAdapterP
    private lateinit var ratingSQLite: RatingSQLite
    private lateinit var binding: ActivityTimeTableWeekBinding

    data class PrintControlPont(
        val name: String? = "Artem eat title",
        val ball: Int,
        val maxBall: Int
    )

    data class Rating(
        val title: String,
        val controlPoints: List<PrintControlPont>
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ratingSQLite = RatingSQLite(this)
        binding = ActivityTimeTableWeekBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CoroutineScope(Dispatchers.IO).launch {
            val ratingAll = ratingSQLite.dbExtractAllRating()

            withContext(Dispatchers.Main) {
                ratingAdapterP = RatingAdapterP(ratingAll)
                binding.recyclerView.layoutManager = LinearLayoutManager(this@BallsActivity)
                binding.recyclerView.adapter = ratingAdapterP
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        finish()
    }
}