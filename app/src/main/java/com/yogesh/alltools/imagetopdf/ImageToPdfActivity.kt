package com.yogesh.alltools.imagetopdf

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.yogesh.alltools.R
import com.yogesh.alltools.databinding.ActivityImagetopdfBinding

class ImageToPdfActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImagetopdfBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImagetopdfBinding.inflate(layoutInflater)

        setSupportActionBar(binding.toolbar)

        setContentView(binding.root)
    }
}