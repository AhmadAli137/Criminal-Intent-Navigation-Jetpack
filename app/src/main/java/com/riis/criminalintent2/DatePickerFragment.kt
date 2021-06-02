package com.riis.criminalintent2

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import java.sql.Date
import java.util.*

//DatePickerFragment is a fragment class that displays a dialog, particularly a date picker
//At first, the dialog will appear as a calendar with the current data and time. However, the user can then interact with the calendar and change its values
class DatePickerFragment : DialogFragment() {

    private val safeArgs: DatePickerFragmentArgs by navArgs() //gaining type-safe access to the arguments sent to DatePickerFragment.kt

    //CREATING THE DIALOG
    //---------------------
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        //Listener for when the user has finished selecting a date
        val dateListener = DatePickerDialog.OnDateSetListener {
                _: DatePicker, year: Int, month: Int, day: Int ->
            //Creating a Date object from the values provided by the DatePicker
            val resultDate : java.util.Date = GregorianCalendar(year, month, day).time
            //send the crime date result as a key-value pair argument to the bundle of the previous destination in the BackStackEntry (CrimeFragment.kt)
            findNavController().previousBackStackEntry?.savedStateHandle?.set("resultDate", resultDate.toString())

        }

        val date = Date(safeArgs.date) //retrieving the crime date from the fragment's (safeArg) arguments,
                                       // ... and converting it from a Long to java.sql.Date

        val crimeDate: Date = Date.valueOf(date.toString()) //Converting java.sql.Date --> String --> java.util.Date

        val calendar = Calendar.getInstance() //returns a Calendar object with the default (current) date
        calendar.time = crimeDate //the Calendar object is now set to the crime date from the bundle stored in the fragment's arguments
        val initialYear = calendar.get(Calendar.YEAR)
        val initialMonth = calendar.get(Calendar.MONTH)
        val initialDay = calendar.get(Calendar.DAY_OF_MONTH)
        return DatePickerDialog(
            requireContext(), //context fragment is currently using
            dateListener, //date listener
            initialYear,
            initialMonth,
            initialDay
        )
    }
}