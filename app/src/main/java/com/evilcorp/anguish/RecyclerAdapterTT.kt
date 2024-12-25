package com.evilcorp.anguish
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.evilcorp.anguish.TimeTableWeekActivity.PrintTimeTableClass

class TimeTableAdapter(private val timeTableList: List<PrintTimeTableClass>) :
    RecyclerView.Adapter<TimeTableAdapter.TimeTableViewHolder>() {

    class TimeTableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val auditoryTextView: TextView = itemView.findViewById(R.id.auditoryTextView)
        private val numberTextView: TextView = itemView.findViewById(R.id.numberTextView)
        private val teacherTextView: TextView = itemView.findViewById(R.id.teacherTextView)

        fun bind(timeTableClass: PrintTimeTableClass) {
            titleTextView.text = timeTableClass.title ?: ""
            auditoryTextView.text = "${ timeTableClass.auditNumber } (${ timeTableClass.campusTitle })" ?: ""
            numberTextView.text = timeTableClass.number?.toString() ?: ""
            teacherTextView.text = timeTableClass.teacher?.replace(" ", "\n") ?: ""
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeTableViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_lesson, parent, false)
        return TimeTableViewHolder(view)
    }

    override fun onBindViewHolder(holder: TimeTableViewHolder, position: Int) {
        holder.bind(timeTableList[position])
    }

    override fun getItemCount(): Int {
        return timeTableList.size
    }
}

