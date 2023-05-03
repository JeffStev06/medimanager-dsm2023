package sv.edu.udb.dsm.medimanager.model

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import sv.edu.udb.dsm.medimanager.db.HelperDB

class IntervalModel (context: Context?){

    private var helper: HelperDB? = null
    private var db: SQLiteDatabase? = null
    init {
        helper = HelperDB(context)
        db = helper!!.getWritableDatabase()
    }
    companion object {
        //TABLA INTERVALOS
        val TABLE_NAME_INTERVAL = "interval"
        val COL_ID              = "_id"
        val COL_NAME            = "name"
        val COL_TEXT            = "text"

        val CREATE_TABLE_INTERVAL = (
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_INTERVAL + "("
                    + COL_ID + " integer primary key autoincrement,"
                    + COL_NAME + " varchar(50) NOT NULL,"
                    + COL_TEXT + " varchar(50));"
            )
    }

    fun generarContentValues(
        name: String?
    ): ContentValues {
        val values = ContentValues()
        values.put(COL_NAME, name)
        return values
    }
    fun insertValuesDefault() {
        val intervals = arrayOf(
            "Una sola vez",
            "Cada x minutos",
            "Cada x horas"
        )
        // Verificacion si existen registros precargados
        val columns = arrayOf(COL_ID, COL_NAME)
        var cursor: Cursor? =
            db!!.query(TABLE_NAME_INTERVAL, columns, null, null, null, null, null)

        if (cursor == null || cursor.count <= 0) {
            // Registrando Intervalos por defecto
            for (item in intervals) {
                db!!.insert(TABLE_NAME_INTERVAL, null, generarContentValues(item))
            }
        }
    }

    fun showAllIntervals(): Cursor? {
        val columns = arrayOf(COL_ID, COL_NAME, COL_TEXT)
        return db!!.query(
            TABLE_NAME_INTERVAL, columns,
            null, null, null, null, "$COL_ID ASC"
        )
    }

    fun searchID(name: String): Int {
        val columns = arrayOf(COL_ID, COL_NAME)
        val cursor: Cursor? = db!!.query(
            TABLE_NAME_INTERVAL, columns,
            "$COL_NAME=?", arrayOf(name.toString()), null, null, null
        )
        cursor!!.moveToFirst()
        return cursor.getInt(0)
    }
}