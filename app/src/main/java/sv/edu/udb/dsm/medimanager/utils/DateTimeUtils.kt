package sv.edu.udb.dsm.medimanager.utils

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.text.format.DateFormat
import android.util.Log
import android.widget.DatePicker
import android.widget.TimePicker
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

interface DateTimePickerMethods {
    fun onDateSelected(year: Int, month: Int, day: Int)
    fun onTimeSelected(hour: Int, minutes: Int)
}

class DateTimeUtils(private val context: Context) : DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    var day = 0
    var month = 0
    var year = 0
    var hour = 0
    var minute = 0

    private fun getDateCalendar() {
        val cal: Calendar = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
    }
    private fun getTimeCalendar() {
        val cal: Calendar = Calendar.getInstance()
        hour = cal.get(Calendar.HOUR)
        minute = cal.get(Calendar.MINUTE)
    }

    fun pickDate(listener: DateTimePickerMethods, currentDate: String) {
        getDateCalendar()
        if (currentDate != "") {
            val variables = currentDate.split("/")
            day = variables[0].toInt()
            month = variables[1].toInt() - 1 //Ya que el de "currentDate" va desde 1, no desde 0
            year = variables[2].toInt()
        }
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                listener.onDateSelected(year, month, dayOfMonth)
            }
        val dialog = DatePickerDialog(context, dateSetListener, year, month, day)
        dialog.datePicker.minDate = System.currentTimeMillis() - 1000
        dialog.show()
    }
    fun pickTime(listener: DateTimePickerMethods, currentTime: String) {
        getTimeCalendar()
        if (currentTime != "") {
            val timeVariables = currentTime.split(":")
            hour = timeVariables[0].toInt()
            minute = timeVariables[1].toInt()
        }
        val timeSetListener =
            TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                listener.onTimeSelected(hour, minute)
            }
        val dialog = TimePickerDialog(context, timeSetListener, hour, minute, true)
        dialog.show()
    }

    // Este método no se está utilizando
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        Log.i("MEDIMANAGER", "onDataSet")
    }
    // Este método no se está utilizando
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        Log.i("MEDIMANAGER", "onDataSet")
    }

    fun verifyDates(startDateString: String, finishDateString: String): Boolean {
        val startDate = LocalDate.parse(startDateString, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        val finishDate =
            LocalDate.parse(finishDateString, DateTimeFormatter.ofPattern("dd/MM/yyyy"))

        return startDate.isBefore(finishDate) || startDate.isEqual(finishDate)
    }

    @SuppressLint("SimpleDateFormat")
    fun stringDateToMillis(date: String, time: String) : Long {
        val calendar = Calendar.getInstance()
        val currentDate: Date? = try {
            val format = "dd/MM/yyyy HH:mm"
            val dateFormat = SimpleDateFormat(format)
            dateFormat.parse("$date $time")
        } catch (e: ParseException) {
            Log.i("MEDIMANAGER", "getDateInMillisFromString error: ${e.message}")
            Date()
        }
        if (currentDate != null) {
            calendar.time = currentDate
        }
        return calendar.timeInMillis
    }

    fun millisToStringDate(timeInMillis: Long) : String {
        return DateFormat.format("dd/MM/yyyy HH:mm", timeInMillis).toString()
    }
}