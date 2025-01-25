package com.evilcorp.anguish
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.evilcorp.anguish.BallsActivity.PrintControlPont

class RatingAdapter(private val ratingList: List<PrintControlPont>, private val isSpecial: Boolean) :
    RecyclerView.Adapter<RatingAdapter.RatingViewHolder>() {

    class RatingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.rating_subtitle)
        private val balls: TextView = itemView.findViewById(R.id.balls)

        @SuppressLint("DefaultLocale")
        fun formatNumbers(first: Double, second: Double): String {
            val ball = if (first % 1 == 0.0) {
                first.toInt().toString()
            } else {
                String.format("%.1f", first)
            }

            val maxBall = if (second % 1 == 0.0) {
                second.toInt().toString()
            } else {
                String.format("%.1f", second)
            }

            return "$ball | $maxBall"
        }

        fun bind(rating: PrintControlPont, isSpecial: Boolean) {


            title.text = rating.name ?: "Артем съел название"
            balls.text = formatNumbers(rating.ball, rating.maxBall)

            if (isSpecial) {
                title.setTextColor(ContextCompat.getColor(itemView.context, R.color.lightGreen))
                balls.setTextColor(ContextCompat.getColor(itemView.context, R.color.lightGreen))
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rating, parent, false)
        return RatingViewHolder(view)
    }

    override fun onBindViewHolder(holder: RatingViewHolder, position: Int) {
        holder.bind(ratingList[position], isSpecial)
    }

    override fun getItemCount(): Int {
        return ratingList.size
    }
}

