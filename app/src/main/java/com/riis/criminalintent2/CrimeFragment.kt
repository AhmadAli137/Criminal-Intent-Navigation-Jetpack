package com.riis.criminalintent2

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import java.text.SimpleDateFormat
import java.util.*



private const val TAG = "CrimeFragment"
private const val REQUEST_DATE = 0

//CrimeFragment is a fragment which controls the detail view when hosted by MainActivity
//It is also the controller that interacts with the Crime model objects and the view objects
class CrimeFragment : Fragment() {

    //DEFINING CLASS VARIABLES
    private lateinit var crime: Crime
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var solvedCheckBox: CheckBox

    //ASSOCIATING THE FRAGMENT WITH THE viewModel CrimeDetailViewModel.kt
    private val crimeDetailViewModel: CrimeDetailViewModel by lazy {
        ViewModelProvider(this).get(CrimeDetailViewModel::class.java)
    }

    //------------------------------------------NAVIGATION NOTE-----------------------------------------------------------------
    //Due to the safe-args gradle plugin, a new class is automatically generated (FragmentNameArgs) for any fragment destination
    // ... that specifies in the navigation graph XML code that it wants to take in a certain variable as an argument. These
    // ... variables are then stored as class properties and includes getter and setter functions for them.
    // This allows for the type-safe access to the variables stored in the arguments
    //--------------------------------------------------------------------------------------------------------------------------

    private val safeArgs: CrimeFragmentArgs by navArgs() //gaining type-safe access to the arguments sent to CrimeFragment.kt

    //WHAT HAPPENS WHEN THE FRAGMENT CrimeFragment IS CREATED
    override fun onCreate(savedInstanceState: Bundle?) { //passes information saved in the bundle or null if empty
        super.onCreate(savedInstanceState) //creates the fragment using the info passed to savedInstanceState
        crime = Crime() //an empty crime object is created from the Crime.kt class

        val crimeId: UUID = UUID.fromString(safeArgs.crimeId) //retrieving the crimeId from the fragment's (safeArg) arguments
        crimeDetailViewModel.loadCrime(crimeId) //load the retrieved crimeId into the viewModel to send a query for the corresponding Crime object in the database

    }

    //WHAT HAPPENS WHEN THE FRAGMENT VIEW made using fragment_crime.xml IS CREATED
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //defining and inflating the fragment view which is then hosted in an activity's container view
        //The third parameter tells the layout inflater whether to immediately add the inflated view to the view’s parent
        val view = inflater.inflate(R.layout.fragment_crime, container, false)

        //referencing views from fragment_crime.xml using their View IDs
        titleField = view.findViewById(R.id.crime_title) as EditText
        dateButton = view.findViewById(R.id.crime_date) as Button
        solvedCheckBox = view.findViewById(R.id.crime_solved) as CheckBox

        return view
    }

    //WHEN A NEW VIEW IS CREATED, OBTAIN THE UPDATED CRIME OBJECT
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeDetailViewModel.crimeLiveData.observe(
            viewLifecycleOwner,
            Observer { crime ->
                crime?.let {
                    this.crime = crime
                    updateUI()
                }
            })


        // getting the back stack entry of the current navigation destination
        //-------------------------------------------------------------------
        val navBackStackEntry = findNavController().getBackStackEntry(R.id.crimeDetail_dest)

        // Creating and scoping an observer to the lifecycle of CrimeFragment
        //--------------------------------------------------------------------

        // NOTE: Because CrimeFragment is still visible on screen when dialogs appear,
        // ... it remains in "STARTED" mode even though it is not the current destination.
        // This results in CrimeFragment acting on received LiveData from dialogs before
        // ... the dialog is even closed and CrimeFragment becomes the current destination again.
        // As such, we must check to see if CrimeFragment is in "ON_RESUME" mode before allowing
        // ... it to act on received LiveData

        val observer = LifecycleEventObserver { _, event ->
            //If CrimeFragment is in "ON_RESUME" mode and contains an argument with key "resultDate" in its bundle...
            if (event == Lifecycle.Event.ON_RESUME
                && navBackStackEntry.savedStateHandle.contains("resultDate")) {
                val resultDate = navBackStackEntry.savedStateHandle.get<String>("resultDate") //grab the value of the key-value pair from bundle

                // Updating the crime date with new date provided by DatePickerDialog
                //-------------------------------------------------------------------
                val dateFormatter = SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy", Locale.getDefault())
                crime.date = dateFormatter.parse(resultDate as String) as Date
                updateUI()
            }
        }

        //Adding the Observer to the NavBackStackEntry's lifecycle
        //--------------------------------------------------------
        navBackStackEntry.lifecycle.addObserver(observer)

        //When the fragment's view lifecycle is destroyed, remove the observer from the NavBackStackEntry's lifecycle
        //-----------------------------------------------------------------------------------------------------------
        viewLifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                navBackStackEntry.lifecycle.removeObserver(observer)
            }
        })

    }


    //WHAT HAPPENS WHENEVER THE APP STARTS UP
    override fun onStart() {
        super.onStart()

        //EditText Listener
        val titleWatcher = object : TextWatcher { //using TextWatcher listener to react to user input
            override fun beforeTextChanged(
                sequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                // This space intentionally left blank (not interested in this function)
            }
            override fun onTextChanged(
                sequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                crime.title = sequence.toString() //setting the title attribute of the crime object to the new sequence
            }
            override fun afterTextChanged(sequence: Editable?) {
                // This one too (not interested in this function)
            }
        }
        titleField.addTextChangedListener(titleWatcher)//whenever the text of titleField changes, titleWatcher reacts accordingly

        //CheckBox Listener
        solvedCheckBox.apply {
            setOnCheckedChangeListener { _, isChecked ->
                crime.isSolved = isChecked //sets isSolved attribute of the crime object to the boolean isChecked (T/F)
            }
        }

        //dateButton Listener
        dateButton.setOnClickListener {
            //sends the current crime date to the dialog destination DatePickerFragment.kt as a
            // ... Long number (since navigation jetpack currently can't send Date objects as arguments)

            val action = CrimeFragmentDirections.showDatePickerDialog(date=crime.date.time)
            this.findNavController().navigate(action)
            }
    }

    //WHEN THE CRIME OBJECT HAS BEEN UPDATED, UPDATE THE UI VIEWS
    private fun updateUI() {
        titleField.setText(crime.title)
        dateButton.text = crime.date.toString()
        solvedCheckBox.apply {
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState() //skips the checkbox animation
        }
    }

    //WHEN THE FRAGMENT HAS BEEN STOPPED
    //happens anytime the fragment moves entirely out of view
    override fun onStop() {
        super.onStop()
        crimeDetailViewModel.saveCrime(crime) //saves the data of the current Crime object being accessed by the user before stopping the fragment
    }

}


