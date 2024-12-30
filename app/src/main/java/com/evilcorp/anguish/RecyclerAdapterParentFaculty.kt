package com.evilcorp.anguish

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.evilcorp.anguish.FacultyAdapterP.FacultyViewHolderP
import com.evilcorp.anguish.ui.notifications.NotificationsFragment.FacultyDiscipline
import com.evilcorp.anguish.ui.notifications.NotificationsFragment.PrintDiscipline


class FacultyAdapterP(private val parent: List<FacultyDiscipline>) :
RecyclerView.Adapter<FacultyViewHolderP>() {

    class FacultyViewHolderP(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.faculty_title)
        private val childRecyclerView: RecyclerView = itemView.findViewById(R.id.child_faculty)

        fun bind(parent: FacultyDiscipline) {
            title.text = parent.faculty
            setupChildRecyclerView(parent.disciplines)
        }
        private fun setupChildRecyclerView(discipline: List<PrintDiscipline>) {
            val childAdapter = FacultyAdapter(discipline)
            childRecyclerView.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)
            childRecyclerView.adapter = childAdapter
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacultyViewHolderP {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_faculty_parent, parent, false)
        return FacultyViewHolderP(view)
    }

    override fun onBindViewHolder(holder: FacultyViewHolderP, position: Int) {
        holder.bind(parent[position])
    }

    override fun getItemCount(): Int {
        return parent.size
    }
}