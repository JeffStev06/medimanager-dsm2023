package sv.edu.udb.dsm.medimanager.model

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import sv.edu.udb.dsm.medimanager.db.HelperDB
import sv.edu.udb.dsm.medimanager.db.Medicine

class MedicineModel (context: Context?){

    private var helper: HelperDB? = null
    private var db: SQLiteDatabase? = null
    init {
        helper = HelperDB(context)
        db = helper!!.getWritableDatabase()
    }
    companion object {
        //TABLA MEDICAMENTOS
        val TABLE_NAME_MEDICINE = "medicine"
        val COL_ID              = "_id"
        val COL_NAME            = "name"
        val COL_DOSE            = "dose"
        val COL_DOCTOR          = "doctor"
        val COL_IDINTERVAL      = "idinterval"
        val COL_INTERVAL        = "interval"
        val COL_START_DATE      = "date_start"
        val COL_FINISH_DATE     = "date_finish"
        val COL_TIME            = "time"

        val CREATE_TABLE_MEDICINE = (
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_MEDICINE + "("
                    + COL_ID + " integer primary key autoincrement,"
                    + COL_NAME + " varchar(50) NOT NULL,"
                    + COL_DOSE + " varchar(100),"
                    + COL_DOCTOR + " varchar(50) NOT NULL,"
                    + COL_IDINTERVAL + " integer NOT NULL,"
                    + COL_INTERVAL + " integer NOT NULL,"
                    + COL_START_DATE + " varchar(10) NOT NULL,"
                    + COL_FINISH_DATE + " varchar(10) NOT NULL,"
                    + COL_TIME + " varchar(5));"
            )
    }

    private fun generateContentValues(
        name: String?,
        dose: String?,
        doctor: String?,
        idInterval: Int?,
        interval: Int?,
        dateStart: String?,
        dateFinish: String?,
        time: String?
    ): ContentValues {
        val values = ContentValues()
        values.put(COL_NAME, name)
        values.put(COL_DOSE, dose)
        values.put(COL_DOCTOR, doctor)
        values.put(COL_IDINTERVAL, idInterval)
        values.put(COL_INTERVAL, interval)
        values.put(COL_START_DATE, dateStart)
        values.put(COL_FINISH_DATE, dateFinish)
        values.put(COL_TIME, time)
        return values
    }

    fun addNewMedicine(name: String?, dose: String?, doctor: String?, idInterval: Int?,
                       interval: Int?, dateStart: String?, dateFinish: String?, time: String?) {
        db!!.insert(
            TABLE_NAME_MEDICINE,
            null,
            generateContentValues(name, dose, doctor, idInterval, interval, dateStart, dateFinish, time)
        )
    }

    //fun searchMedicineAll() : List<Medicine> {
    fun searchMedicineAll() : Cursor? {
        val columns = arrayOf(
            COL_ID,
            COL_NAME,
            COL_DOSE,
            COL_DOCTOR,
            COL_IDINTERVAL,
            COL_INTERVAL,
            COL_TIME
        )
        /*val cursor: Cursor? = db!!.query(
            TABLE_NAME_MEDICINE, columns, null, null, null, null, null
        )*/
        val cursor2: Cursor? = db!!.rawQuery(
            "SELECT $COL_ID, $COL_NAME, $COL_DOSE, $COL_DOCTOR, $COL_IDINTERVAL, $COL_INTERVAL, " +
                    "CASE $COL_START_DATE = $COL_FINISH_DATE " +
                    "   WHEN true THEN 'El ' || $COL_START_DATE " +
                    "   WHEN false THEN 'Desde '|| $COL_START_DATE || ' hasta ' || $COL_FINISH_DATE END as $COL_START_DATE," +
                    " $COL_TIME FROM medicine", arrayOf()
        )
        return cursor2
    }

    fun searchMedicine(id: Int): Medicine? {
        val columns = arrayOf(
            COL_ID,
            COL_NAME,
            COL_DOSE,
            COL_DOCTOR,
            COL_IDINTERVAL,
            COL_INTERVAL,
            COL_START_DATE,
            COL_FINISH_DATE,
            COL_TIME
        )
        val cursor = db!!.query(
            TABLE_NAME_MEDICINE, columns,
            "$COL_ID=?", arrayOf(id.toString()), null, null, null
        )
        //val medicines = mutableListOf<Medicine>()
        var medicine: Medicine? = null
        while (cursor!!.moveToNext()) {
            //val id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"))
            val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            val dose = cursor.getString(cursor.getColumnIndexOrThrow("dose"))
            val doctor = cursor.getString(cursor.getColumnIndexOrThrow("doctor"))
            val idInterval = cursor.getInt(cursor.getColumnIndexOrThrow("idinterval"))
            val interval = cursor.getInt(cursor.getColumnIndexOrThrow("interval"))
            val dateStart = cursor.getString(cursor.getColumnIndexOrThrow("date_start"))
            val dateEnd = cursor.getString(cursor.getColumnIndexOrThrow("date_finish"))
            val time = cursor.getString(cursor.getColumnIndexOrThrow("time"))

            medicine = Medicine(id, name, dose, doctor, idInterval, interval, time, dateStart, dateEnd)
            //medicines.add(medicine)
        }
        return medicine
    }

    fun updateMedicine (
        id: Int,
        name: String,
        dose: String,
        doctor: String,
        idInterval: Int,
        interval: Int,
        dateStart: String,
        dateFinish: String,
        time: String
    ) {
        db!!.update(
            TABLE_NAME_MEDICINE, generateContentValues(name, dose, doctor, idInterval, interval, dateStart, dateFinish, time),
            "$COL_ID=?", arrayOf(id.toString())
        )
    }

    fun deleteMedicine(id: Int) {
        db!!.delete(TABLE_NAME_MEDICINE, "$COL_ID=?", arrayOf(id.toString()))
    }
}