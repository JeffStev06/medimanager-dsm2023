package sv.edu.udb.dsm.medimanager.adapter

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import sv.edu.udb.dsm.medimanager.R
import sv.edu.udb.dsm.medimanager.db.Medicine

class MedicineAdapter(private val context: Activity, var medicines: List<Medicine>) :
    ArrayAdapter<Medicine?>(context, R.layout.medicine_layout, medicines) {

        override fun getView(position: Int, view: View?, parent: ViewGroup): View {
            val layoutInflater = context.layoutInflater

            val rowview = view ?: layoutInflater.inflate(R.layout.medicine_layout, null)
/*
            val tvName = rowview.findViewById<TextView>(R.id.tvName)
            val tvTime = rowview.findViewById<TextView>(R.id.tvTime)
            tvName.text = medicines[position].name
            tvTime.text = medicines[position].time*/
            return rowview
        }

}