package com.evilcorp.anguish
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.evilcorp.anguish.ui.notifications.NotificationsFragment.PrintDiscipline

class FacultyAdapter(private val facultyList: List<PrintDiscipline>) :
    RecyclerView.Adapter<FacultyAdapter.FacultyViewHolder>() {

    class FacultyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.subject_title)

        fun bind(faculty: PrintDiscipline) {
            title.text = faculty.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacultyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_faculty, parent, false)
        return FacultyViewHolder(view)
    }

    override fun onBindViewHolder(holder: FacultyViewHolder, position: Int) {
        holder.bind(facultyList[position])
    }

    override fun getItemCount(): Int {
        return facultyList.size
    }
}

