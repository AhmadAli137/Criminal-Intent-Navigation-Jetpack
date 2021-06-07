package com.riis.criminalintent2

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import java.sql.Date
import java.util.*

//TimePickerFragment is a fragment class that displays a dialog, particularly a time picker
//At first, the dialog will appear as a clock with the current time. However, the user can then interact with the clock and change its values
class TimePickerFragment : DialogFragment() {

    private val safeArgs: DatePickerFragmentArgs by navArgs() //gaining type-safe access to the arguments sent to TimePickerFragment.kt

    //CREATING THE DIALOG
    //---------------------
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val crimeDate = java.util.Date(safeArgs.date) //retrieving the crime date from the fragment's (safeArg) arguments,
        // ... and converting it from a Long to java.util.Date

        val calendar = Calendar.getInstance() //creates a Calendar object with the default (current) date
        calendar.time = crimeDate //setting the Calendar object's time and date to the crime date

        //Since only the time values will be changed by this dialog, we will be returning the calendar values
        // ... from the original crime date passed into the fragment
        val prevYear = calendar.get(Calendar.YEAR)
        val prevMonth = calendar.get(Calendar.MONTH)
        val prevDay = calendar.get(Calendar.DAY_OF_MONTH)

        //Listener for when the user has finished selecting a time
        val timeListener = TimePickerDialog.OnTimeSetListener {
                _: TimePicker, hourOfDay: Int, minute: Int->

            //Creating a new Date object from the time values provided by the TimePicker and the calendars values from original crime date
            //Gregorian calendar is the most widely used calendar around the world
            val resultDate : Long = GregorianCalendar(prevYear, prevMonth, prevDay, hourOfDay, minute).timeInMillis

            //send the crime date result as a key-value pair argument to the bundle of the previous destination in the BackStackEntry (CrimeFragment.kt)
            findNavController().previousBackStackEntry?.savedStateHandle?.set("resultDate", resultDate)
        }


        //When the dialog clock appears, it will be set to the time values from the original crime date passed into the fragment
        val initialHour = calendar.get(Calendar.HOUR_OF_DAY)
        val initialSecond = calendar.get(Calendar.MINUTE)

        //Displaying the dialog on screen with provided values
        return TimePickerDialog(
            requireContext(), //context fragment is currently using
            timeListener, //time listener
            initialHour,
            initialSecond,
            false
        )


    }
}