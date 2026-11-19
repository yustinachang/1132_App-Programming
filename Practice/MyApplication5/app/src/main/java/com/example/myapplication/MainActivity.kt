package com.example.myapplication

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.TextView
import java.util.Calendar
import android.app.DatePickerDialog

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val btnOpenDatePicker = findViewById<Button>(R.id.btnOpenDatePicker)
        val tvSelectedDate = findViewById<TextView>(R.id.tvSelectedDate)

        ViewCompat.setOnApplyWindowInsetsListener(btnOpenDatePicker) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 取得今天的日期作為預設
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) // 注意：從 0 開始（0=1月）
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        btnOpenDatePicker.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    // 使用者按下確定後回傳的年月日
                    val dateString = "$selectedYear 年 ${selectedMonth + 1} 月 $selectedDay 日"
                    tvSelectedDate.text = "你選擇的日期是：$dateString"
                },
                year, month, day
            )
            // 可選擇是否設最小/最大日期（這裡不設）
            // datePickerDialog.datePicker.minDate = calendar.timeInMillis
            datePickerDialog.show()
        }
    }
}