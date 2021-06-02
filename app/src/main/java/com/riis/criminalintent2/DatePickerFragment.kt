package com.riis.criminalintent2

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import java.sql.Date
import java.util.*

private const val ARG_DATE = "date"

//DatePickerFragment is a fragment class that displays a dialog, particularly a date picker
//At first the dialog will appear with the current data and time, but the user can then interact with and change the dialog values
class DatePickerFragment : DialogFragment() {

    private val safeArgs: DatePickerFragmentArgs by navArgs()

    //CREATING THE DIALOG
    //---------------------
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val date = Date(safeArgs.date)
        val crimeDate: Date = Date.valueOf(date.toString())
        
        val calendar = Calendar.getInstance() //returns a Calendar object with the default (current) date
        calendar.time = crimeDate //the Calendar object is now set to the crime date from the bundle stored in the fragment's arguments
        val initialYear = calendar.get(Calendar.YEAR)
        val initialMonth = calendar.get(Calendar.MONTH)
        val initialDay = calendar.get(Calendar.DAY_OF_MONTH)
        return DatePickerDialog(
            requireContext(), //context fragment is currently using
            null, //date listener
            initialYear,
            initialMonth,
            initialDay
        )
    }
}