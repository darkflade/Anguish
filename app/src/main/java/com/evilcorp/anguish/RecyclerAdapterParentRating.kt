package com.evilcorp.anguish

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.evilcorp.anguish.BallsActivity.PrintControlPont
import com.evilcorp.anguish.BallsActivity.Rating
import com.evilcorp.anguish.RatingAdapterP.RatingViewHolderP


class RatingAdapterP(private val parent: List<Rating>) :
RecyclerView.Adapter<RatingViewHolderP>() {

    class RatingViewHolderP(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.rating_title)
        private val childRecyclerView: RecyclerView = itemView.findViewById(R.id.child_rating)

        fun bind(parent: Rating, isLast: Boolean) {
            title.text = parent.title

            if (isLast) {
                title.setTextColor(ContextCompat.getColor(itemView.context, R.color.gigaGreen))
            } else {
                title.setTextColor(ContextCompat.getColor(itemView.context, R.color.cornflower_blue))
            }
            setupChildRecyclerView(parent.controlPoints, isLast)
        }
        private fun setupChildRecyclerView(points: List<PrintControlPont>, isLast: Boolean) {
            val childAdapter = RatingAdapter(points, isLast)
            childRecyclerView.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)
            childRecyclerView.adapter = childAdapter
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatingViewHolderP {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rating_parent, parent, false)
        return RatingViewHolderP(view)
    }

    override fun onBindViewHolder(holder: RatingViewHolderP, position: Int) {
        val isLast = position >= parent.size - 3
        holder.bind(parent[position], isLast)

    }

    override fun getItemCount(): Int {
        return parent.size
    }
}