package com.adewale.niplofy.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.adewale.niplofy.R
import kotlinx.android.synthetic.main.activity_product_details.*
import kotlinx.android.synthetic.main.activity_safety_tips.*

class SafetyTipsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_safety_tips)

        setupActionBar()
    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_safety_tip_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        toolbar_safety_tip_activity.setNavigationOnClickListener { onBackPressed() }
    }
}