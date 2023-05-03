package sv.edu.udb.dsm.medimanager.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import sv.edu.udb.dsm.medimanager.model.IntervalModel
import sv.edu.udb.dsm.medimanager.model.MedicineModel

class HelperDB(context: Context?) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private const val DB_NAME = "medimanager.sqlite"
        private const val DB_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(IntervalModel.CREATE_TABLE_INTERVAL)
        db?.execSQL(MedicineModel.CREATE_TABLE_MEDICINE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) { }
}