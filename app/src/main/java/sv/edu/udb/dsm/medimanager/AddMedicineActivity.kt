package sv.edu.udb.dsm.medimanager

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import sv.edu.udb.dsm.medimanager.db.HelperDB
import sv.edu.udb.dsm.medimanager.model.IntervalModel
import sv.edu.udb.dsm.medimanager.model.MedicineModel
import sv.edu.udb.dsm.medimanager.service.AlarmService
import sv.edu.udb.dsm.medimanager.utils.DateTimePickerMethods
import sv.edu.udb.dsm.medimanager.utils.DateTimeUtils

class AddMedicineActivity : AppCompatActivity(), DateTimePickerMethods {

    private var managerMedicine: MedicineModel? = null
    private var managerInterval: IntervalModel? = null
    private var cursor: Cursor? = null

    private var action: String = ""
    private var actualId: String = ""
    private var edtName: EditText? = null
    private var edtDose: EditText? = null
    private var edtDoctor: EditText? = null
    private var spiIntervals: Spinner? = null
    private var edtInterval: EditText? = null
    private var edtTime: EditText? = null
    private var dateStart: EditText? = null
    private var dateEnd: EditText? = null

    private var datePickerSelected: Int = 0
    private var dateTimeUtils: DateTimeUtils? = null
    private lateinit var alarmService: AlarmService
    private var dbHelper: HelperDB? = null
    private var db: SQLiteDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_medicine)

        dateTimeUtils = DateTimeUtils(this)
        alarmService = AlarmService(this)

        edtName = findViewById(R.id.edtName)
        edtDose = findViewById(R.id.edtDose)
        edtDoctor = findViewById(R.id.edtDoctor)
        spiIntervals = findViewById(R.id.spiIntervalos)
        edtInterval = findViewById(R.id.edtInterval)
        edtTime = findViewById(R.id.edtTime)
        dateStart = findViewById(R.id.edtStartDate)
        dateEnd = findViewById(R.id.edtFinishDate)

        dbHelper = HelperDB(this)
        db = dbHelper!!. writableDatabase

        startingComponents()
        val data: Bundle? = intent.getExtras()
        if (data != null) {
            action = data.getString("action").toString()
            actualId = data.getString("id").toString()
            Log.i("MEDIMANAGER","actualId: $actualId, idInterval: ${data.getInt("idInterval")}")
            edtName!!.setText(data.getString("name").toString())
            edtDose!!.setText(data.getString("dose").toString())
            edtDoctor!!.setText(data.getString("doctor").toString())
            spiIntervals!!.setSelection(data.getInt("idInterval")-1)
            edtInterval!!.setText(data.getString("interval").toString())
            edtTime!!.setText(data.getString("time").toString())
            dateStart!!.setText(data.getString("dateStart").toString())
            dateEnd!!.setText(data.getString("dateEnd").toString())
        }
        if (action == "new") {
            supportActionBar!!.title = "Nuevo Medicamento"
        } else if (action == "edit") {
            supportActionBar!!.title = "Edición Medicamento"
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_save_delete, menu)
        Log.i("MEDIMANAGER", "action: $action")
        //val action = intent.getBooleanExtra("action", false)
        val menuItem = menu?.findItem(R.id.action_delete)
        menuItem?.isVisible = action != "new"
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //Log.i("MEDIMANAGER","action: $action, id: $actualId")
        return when (item.itemId) {
            16908332 -> {
                val i = Intent(this, MainActivity::class.java)
                this.startActivity(i)
                return true
            }
            R.id.action_new -> {
                save()
                true
            }
            R.id.action_delete -> {
                delete()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun save() {
        managerMedicine = MedicineModel(this)
        val name: String = edtName!!.text.toString().trim()
        val dose: String = edtDose!!.text.toString().trim()
        val doctor: String = edtDoctor!!.text.toString().trim()
            val intervalName: String = spiIntervals!!.selectedItem.toString().trim()
        val intervalId = managerInterval!!.searchID(intervalName)
        //Log.i("MEDIMANAGER", "intervalName: "$intervalName", intervalId: $intervalId")
        var interval = edtInterval!!.text.toString().trim()
        val time: String = edtTime!!.text.toString().trim()
        val start: String = dateStart!!.text.toString().trim()
        var end: String = dateEnd!!.text.toString().trim()

        val response = validateForm(name,doctor,intervalId,interval,time,start,end)
        // Si se seleccionó "una sola vez" el intervalo, se setean valores por defecto
        if (spiIntervals!!.selectedItemPosition == 0) {
            interval = "0"
            end = start
        }
        //Log.i("MEDIMANAGER","name: $name, dose: '$dose', doctor: $doctor, invervalId: $intervalId," +
        //        "interval: $interval, time: $time, start: $start, end: $end")

        if (db != null && response) {
            //Log.i("MEDIMANAGER", "Me guardaré")
            if (action == "new") {
                managerMedicine!!.addNewMedicine (
                    name,
                    dose,
                    doctor,
                    intervalId,
                    interval.toInt(),
                    start,
                    end,
                    time
                )
            } else {
                managerMedicine!!.updateMedicine (
                    actualId.toInt(),
                    name,
                    dose,
                    doctor,
                    intervalId,
                    interval.toInt(),
                    start,
                    end,
                    time
                )
            }
            setReminder(spiIntervals!!.selectedItemPosition, start, end, time)
            Toast.makeText(this, "Recordatorio guardado con éxito", Toast.LENGTH_LONG).show()
            val i = Intent(this, MainActivity::class.java)
            this.startActivity(i)
        } else {
            if (!response) {
                Toast.makeText(
                    this, "Favor llenar campos obligatorios", Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    this, "Ocurrió un error", Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun delete() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmación")
        builder.setMessage("¿Está seguro que desea remover este medicamento junto con su recordatorio?")

        builder.setPositiveButton("Eliminar") { dialog, which ->
            managerMedicine = MedicineModel(this)
            managerMedicine!!.deleteMedicine(actualId.toInt())
            Toast.makeText(this, "Recordatorio eliminado con éxito", Toast.LENGTH_SHORT).show()
            val i = Intent(this, MainActivity::class.java)
            this.startActivity(i)

        }
        builder.setNegativeButton("Cancelar") { dialog, which ->
            //Toast.makeText(applicationContext,"", Toast.LENGTH_SHORT).show()
        }
        /*builder.setNeutralButton("Maybe") { dialog, which ->
            Toast.makeText(applicationContext,
                "Maybe", Toast.LENGTH_SHORT).show()
        }*/
        builder.show()
    }

    private fun validateForm(name: String, doctor: String, intervalId: Int, interval: String,
        time: String, start: String, end: String): Boolean {

        var response = true
        if (name.isEmpty()) {
            edtName!!.error = "Ingrese el nombre del medicamento"
            edtName!!.requestFocus()
            response = false
        }
        if (doctor.isEmpty()) {
            edtDoctor!!.error = "Ingrese el nombre del médico"
            edtDoctor!!.requestFocus()
            response = false
        }
        //Log.i("MEDIMANAGER","intervalId: $intervalId, interval: '$interval'")
        if (intervalId != 1 && interval == "") {
            edtInterval!!.error = "Ingrese el número del intervalo para la solución"
            edtInterval!!.requestFocus()
            response = false
        }
        if (time.isEmpty()) {
            edtTime!!.error = "Ingrese la hora para el recordatorio"
            edtTime!!.requestFocus()
            response = false
        }
        if (start.isEmpty()) {
            dateStart!!.error = "Ingrese la fecha de inicio"
            dateStart!!.requestFocus()
            response = false
        }
        if (end.isEmpty() && spiIntervals!!.selectedItemPosition != 0) {
            dateEnd!!.error = "Ingrese la fecha fin"
            dateEnd!!.requestFocus()
            response = false
        }

        return response
    }

    private fun startingComponents() {
        setSpinnerInterval()
        spiIntervals?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Log.i("MEDIMANAGER", "position: $position")
                edtInterval?.isEnabled = position != 0
                dateEnd?.isEnabled = position != 0
            }
        }
        edtTime?.setOnClickListener{
            dateTimeUtils?.pickTime(this, edtTime?.text.toString())
        }
        dateStart?.setOnClickListener{
            datePickerSelected = 0
            dateTimeUtils?.pickDate(this, dateStart?.text.toString())
        }
        dateEnd?.setOnClickListener{
            datePickerSelected = 1
            dateTimeUtils?.pickDate(this, dateEnd?.text.toString())
        }

    }

    private fun setSpinnerInterval() {
        managerInterval = IntervalModel(this)
        managerInterval!!.insertValuesDefault()
        cursor = managerInterval!!.showAllIntervals()
        val cat = ArrayList<String>()
        if (cursor != null && cursor!!.count > 0) {
            cursor!!.moveToFirst()
            do {
                cat.add(cursor!!.getString(1))
            } while (cursor!!.moveToNext())
        }
        edtInterval?.isEnabled = false

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cat)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spiIntervals!!.adapter = adapter
    }

    override fun onDateSelected(year: Int, month: Int, day: Int) {
        val newMonth = month + 1
        val monthString = if (newMonth < 10) "0$newMonth" else newMonth.toString()
        val dayString = if (day < 10) "0$day" else day.toString()

        if (datePickerSelected == 0) {
            val dateString = "$dayString/$monthString/$year"
            val dateFinishString = dateEnd?.text.toString()
            if (dateFinishString != "" && spiIntervals?.selectedItemPosition != 0) {
                if (dateTimeUtils?.verifyDates(dateString, dateFinishString)!!) {
                    dateStart?.setText(dateString)
                } else {
                    Toast.makeText(this,"La fecha inicial no puede ser mayor a la final",Toast.LENGTH_SHORT).show()
                }
            } else {
                dateStart?.setText(dateString)
            }
        } else {
            val dateString = "$dayString/$monthString/$year"
            val dateStartString = dateStart?.text.toString()
            if (dateStartString != "") {
                if (dateTimeUtils?.verifyDates(dateStartString, dateString)!!) {
                    dateEnd?.setText(dateString)
                } else {
                    Toast.makeText(this,"La fecha final no puede ser menor a la inicial",Toast.LENGTH_SHORT).show()
                }
            } else {
                dateEnd?.setText(dateString)
            }
        }
    }
    override fun onTimeSelected(hour: Int, minutes: Int) {
        val hourString = if (hour < 10) "0$hour" else hour.toString()
        val minutesString = if (minutes < 10) "0$minutes" else minutes.toString()

        edtTime?.setText("$hourString:$minutesString")
    }

    private fun setReminder(position: Int, start: String, end: String, time: String) {

        // Convirtiendo la fecha y hora a milisegundos para setear la alarma
        val timeInMillis = dateTimeUtils?.stringDateToMillis(start, time)
        when (position) {
            0 -> {
                // Ya funciona solo falta el código en el AlarmReceiver, para generar una Push Notification
                if (timeInMillis != null) {
                    alarmService.setExactAlarm(timeInMillis)
                }
            }
            1 -> {
                // Aquí se ejecutaría la lógica para setear la alarma repetitiva cada x minutos
            }
            2 -> {
                // Aquí se ejecutaría la lógica para setear la alarma repetitiva cada x horas
            }
        }
    }

}