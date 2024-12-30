package com.evilcorp.anguish

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.evilcorp.anguish.TimeTableAdapterP.TimeTableViewHolderP
import com.evilcorp.anguish.TimeTableWeekActivity.DaySchedule
import com.evilcorp.anguish.TimeTableWeekActivity.PrintTimeTableClass


class TimeTableAdapterP(private val dayScheduleList: List<DaySchedule>) :
RecyclerView.Adapter<TimeTableAdapterP.TimeTableViewHolderP>() {

    class TimeTableViewHolderP(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.day_title)
        private val childRecyclerView: RecyclerView = itemView.findViewById(R.id.child_recycler_view)

        fun bind(daySchedule: DaySchedule) {
            titleTextView.text = daySchedule.dayTitle
            setupChildRecyclerView(daySchedule.lessons)
        }
        private fun setupChildRecyclerView(lessons: List<PrintTimeTableClass>) {
            val childAdapter = TimeTableAdapter(lessons)
            childRecyclerView.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)
            childRecyclerView.adapter = childAdapter
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeTableViewHolderP {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_lesson_parent, parent, false)
        return TimeTableViewHolderP(view)
    }

    override fun onBindViewHolder(holder: TimeTableViewHolderP, position: Int) {
        holder.bind(dayScheduleList[position])
    }

    override fun getItemCount(): Int {
        return dayScheduleList.size
    }
}