package com.evilcorp.anguish

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.evilcorp.anguish.databinding.ActivityBallsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BallsActivity : AppCompatActivity() {

    private lateinit var ratingAdapterP: RatingAdapterP
    private lateinit var ratingSQLite: RatingSQLite
    private lateinit var binding: ActivityBallsBinding

    data class PrintControlPont(
        val name: String?,
        val ball: Double,
        val maxBall: Double
    )

    data class Rating(
        val title: String,
        val controlPoints: List<PrintControlPont>
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ratingSQLite = RatingSQLite(this)
        binding = ActivityBallsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val subjectId = intent.getIntExtra("discipline_id", 0)

        CoroutineScope(Dispatchers.IO).launch {
            val rating = ratingSQLite.dbExtractAllRating(subjectId)
            var printRating = emptyList<Rating>()

            if (rating.isNotEmpty()) {

                val exam = rating.last()
                val ballsSummary = rating.sumOf { parent ->
                    parent.controlPoints.sumOf { it.ball }
                }

                val summary = Rating(
                    title = "",
                    controlPoints = listOf(
                        PrintControlPont(
                            name = "Summary",
                            ball = ballsSummary - exam.controlPoints.sumOf { it.ball },
                            maxBall = 70.0
                        )
                    )
                )
                val total = Rating(
                    title = "",
                    controlPoints = listOf(
                        PrintControlPont(
                            name = "Total",
                            ball = ballsSummary,
                            maxBall = 100.0
                        )
                    )
                )

                printRating = rating.dropLast(1) + summary + exam + total
            } else {
                printRating = listOf(
                    Rating(
                        title = "Увы",
                        controlPoints =  listOf(
                            PrintControlPont(
                                name = "Но рейтинга плана нету",
                                ball = 6.0,
                                maxBall = 9.0
                            )
                        )
                    )
                )
            }

            withContext(Dispatchers.Main) {
                ratingAdapterP = RatingAdapterP(printRating)
                binding.recyclerView.layoutManager = LinearLayoutManager(this@BallsActivity)
                binding.recyclerView.adapter = ratingAdapterP
            }
        }
        binding.backButton.setOnClickListener{
            this.finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        finish()
    }
}