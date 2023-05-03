package sv.edu.udb.dsm.medimanager

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import android.widget.SimpleCursorAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import sv.edu.udb.dsm.medimanager.db.HelperDB
import sv.edu.udb.dsm.medimanager.db.Medicine
import sv.edu.udb.dsm.medimanager.model.IntervalModel
import sv.edu.udb.dsm.medimanager.model.MedicineModel


class MainActivity : AppCompatActivity() {

    private var managerMedicine: MedicineModel? = null
    private var managerInterval: IntervalModel? = null

    private var dbHelper: HelperDB? = null
    private var db: SQLiteDatabase? = null

    private var medicines: MutableList<Medicine>? = null
    private var lvMedicine: ListView? = null

    private val from = arrayOf(
        MedicineModel.COL_ID,
        MedicineModel.COL_NAME,
        MedicineModel.COL_TIME,
        MedicineModel.COL_DOSE,
        MedicineModel.COL_DOCTOR,
        MedicineModel.COL_START_DATE
    )
    private val to = intArrayOf(R.id.tvIdView,R.id.tvNameView, R.id.tvTimeView, R.id.tvDoseView, R.id.tvDoctorView, R.id.tvDateView)

    override fun onCreate(savedInstanceState: Bundle?) {
        Thread.sleep(1000)
        setTheme(R.style.Theme_MediManager)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Base
        dbHelper = HelperDB(this)
        db = dbHelper!!. writableDatabase
        managerMedicine = MedicineModel(this)

        // Componentes de layout
        lvMedicine = findViewById(R.id.lvMedi)

        medicines = ArrayList()
        val medicinesCursor: Cursor? = findAllMedicines()
        if (medicinesCursor != null) {
            val adapter = SimpleCursorAdapter(this, R.layout.medicine_layout, medicinesCursor, from, to, 0)
            adapter.notifyDataSetChanged();
            lvMedicine!!.adapter = adapter
        }
        // OnCLickListiner For List Items
        lvMedicine!!.setOnItemClickListener { parent, view, position, viewId ->
            val idMedicine = view.findViewById<TextView>(R.id.tvIdView)

            val medicine = managerMedicine?.searchMedicine(idMedicine.text.toString().toInt())

            val intent = Intent(applicationContext, AddMedicineActivity::class.java)
            intent.putExtra("action", "edit")
            intent.putExtra("id", medicine?.id.toString())
            intent.putExtra("name", medicine?.name)
            intent.putExtra("dose", medicine?.dose)
            intent.putExtra("doctor", medicine?.doctor)
            intent.putExtra("idInterval", medicine?.idInterval)
            //Log.i("MEDIMANAGER", "idInterval: ${medicine?.idInterval} interval: ${medicine?.interval}")
            intent.putExtra(
                "interval",
                if (medicine?.interval == 0) "" else medicine?.interval.toString()
            )
            intent.putExtra("time", medicine?.time)
            intent.putExtra("dateStart", medicine?.dateStart)
            intent.putExtra("dateEnd", medicine?.dateEnd)
            startActivity(intent)
        }
    }

    private fun findAllMedicines(): Cursor? {
        return managerMedicine?.searchMedicineAll()
    }

    fun onClickAddMedicine(view: View) {
        val intent = Intent(baseContext, AddMedicineActivity::class.java)
        intent.putExtra("action", "new")
        intent.putExtra("id","")
        intent.putExtra("name", "")
        intent.putExtra("dose", "")
        intent.putExtra("doctor", "")
        intent.putExtra("idInterval", 0)
        intent.putExtra("interval", "")
        intent.putExtra("time", "")
        intent.putExtra("dateStart", "")
        intent.putExtra("dateEnd", "")
        startActivity(intent)
        //finish()
    }
}