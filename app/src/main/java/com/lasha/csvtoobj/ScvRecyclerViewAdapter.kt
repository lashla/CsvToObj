package com.lasha.csvtoobj

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ScvRecyclerViewAdapter: RecyclerView.Adapter<ScvRecyclerViewAdapter.ViewHolder>() {

    private var dbList = ArrayList<CsvData>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.scv_recycler, parent, false)
        return ViewHolder(view)
    }

    fun updateDbInfo(newInfo: ArrayList<CsvData>){
        dbList = newInfo
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val items = dbList[position]
        holder.etBillFromDb.text = items.billFromDb
        holder.etBillFromAiS.text = items.billFromAiS
        holder.etMobileNumber.text = items.mobileNumber
        holder.etHomeNumber.text = items.homeNumber
        holder.etLastVisit.text = items.lastVisit
        holder.etImprovementFromDb.text = items.improvementFromDb
        holder.etFullNameFromDb.text = items.fullNameFromDb
    }

    override fun getItemCount(): Int {
        return dbList.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val etBillFromDb: TextView = itemView.findViewById(R.id.billFromDb)
        val etBillFromAiS: TextView = itemView.findViewById(R.id.billFromAiS)
        val etMobileNumber: TextView = itemView.findViewById(R.id.mobileNumberFromDb)
        val etHomeNumber: TextView = itemView.findViewById(R.id.homeNumberFromDb)
        val etLastVisit: TextView = itemView.findViewById(R.id.lastVisitFromDb)
        val etImprovementFromDb: TextView = itemView.findViewById(R.id.improvementFromDb)
        val etFullNameFromDb: TextView = itemView.findViewById(R.id.fullNameFromDb)
    }


}