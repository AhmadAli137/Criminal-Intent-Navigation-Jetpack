package com.riis.criminalintent2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(){

    //CREATING THE ACTIVITY
    //---------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) //creating a new activity using whatever data is stored in savedInstanceState
        setContentView(R.layout.activity_main) //setting content to be displayed in activity_main.xml

    }


}