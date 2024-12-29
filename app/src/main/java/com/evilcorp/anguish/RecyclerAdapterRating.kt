package com.evilcorp.anguish
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.evilcorp.anguish.BallsActivity.PrintControlPont
import com.evilcorp.anguish.TimeTableWeekActivity.PrintTimeTableClass

class RatingAdapter(private val ratingList: List<PrintControlPont>) :
    RecyclerView.Adapter<RatingAdapter.RatingViewHolder>() {

    class RatingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.rating_subtitle)
        private val balls: TextView = itemView.findViewById(R.id.balls)

        fun bind(rating: PrintControlPont) {

            title.text = rating.name ?: "Artem ate title"
            balls.text = "${ rating.ball }/${ rating.maxBall }" ?: ""
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rating, parent, false)
        return RatingViewHolder(view)
    }

    override fun onBindViewHolder(holder: RatingViewHolder, position: Int) {
        holder.bind(ratingList[position])
    }

    override fun getItemCount(): Int {
        return ratingList.size
    }
}

