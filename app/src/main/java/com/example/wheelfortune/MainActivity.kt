package com.example.wheelfortune

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var wheelView: WheelView
    private lateinit var btnSpin: Button
    private lateinit var tvResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        wheelView = findViewById(R.id.wheelView)
        btnSpin = findViewById(R.id.btnSpin)
        tvResult = findViewById(R.id.tvResult)

        wheelView.setOnSpinEndListener { place ->
            tvResult.text = "Выбор: $place!"
            btnSpin.text = "Крутить снова"
            btnSpin.isEnabled = true
        }

        btnSpin.setOnClickListener {
            btnSpin.isEnabled = false
            tvResult.text = ""
            wheelView.spin()
        }
    }
}