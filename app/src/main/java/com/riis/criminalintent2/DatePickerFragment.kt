package com.riis.criminalintent2

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import java.util.*
import java.util.Calendar.*

private const val TAG = "TimePickerDialog"

//DatePickerFragment is a fragment class that displays a dialog, particularly a date picker
//At first, the dialog will appear as a calendar with the current data. However, the user can then interact with the calendar and change its values
class DatePickerFragment : DialogFragment() {

    private val safeArgs: DatePickerFragmentArgs by navArgs() //gaining type-safe access to the arguments sent to DatePickerFragment.kt

    //CREATING THE DIALOG
    //---------------------
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val crimeDate = Date(safeArgs.date) //retrieving the crime date from the fragment's (safeArg) arguments,
        // ... and converting it from a Long to java.util.Date

        val calendar = getInstance() //creates a Calendar object with the default (current) date
        calendar.time = crimeDate //setting the Calendar object's time and date to the crime date

        //Since only the calendar values will be changed by this dialog, we will be returning the time values
        // ... from the original crime date passed into the fragment
        val prevHour = calendar.get(HOUR_OF_DAY)
        val prevMinute = calendar.get(MINUTE)

        //Listener for when the user has finished selecting a date
        val dateListener = DatePickerDialog.OnDateSetListener {
                _: DatePicker, year: Int, month: Int, day: Int ->
            //Creating a new Date object from the date values provided by the DatePicker and the time values from original crime date
            //Gregorian calendar is the most widely used calendar around the world
            val resultDate : Long = GregorianCalendar(year, month, day, prevHour, prevMinute).timeInMillis

            //send the crime date result as a key-value pair argument to the bundle of the previous destination in the BackStackEntry (CrimeFragment.kt)
            findNavController().previousBackStackEntry?.savedStateHandle?.set("resultDate", resultDate)

        }

        //When the dialog clock appears, it will be set to the calendar values from the original crime date passed into the fragment
        val initialYear = calendar.get(YEAR)
        val initialMonth = calendar.get(MONTH)
        val initialDay = calendar.get(DAY_OF_MONTH)

        //Displaying the dialog on screen with provided values
        return DatePickerDialog(
            requireContext(), //context fragment is currently using
            dateListener, //date listener
            initialYear,
            initialMonth,
            initialDay
        )
    }
}


