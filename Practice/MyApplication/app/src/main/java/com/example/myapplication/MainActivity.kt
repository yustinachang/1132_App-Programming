package com.example.myapplication
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.SeekBar


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val nameEditText = findViewById<EditText>(R.id.nameEditText)
        val nameTextView = findViewById<TextView>(R.id.nameTextView)
        val send = findViewById<Button>(R.id.send)
        val seekBar = findViewById<SeekBar>(R.id.seekBar)
        //範例一:輸入姓名
        /*
        //監聽輸入文字
        send.setOnClickListener {
            val name = nameEditText.text.toString()
            nameTextView.text = name
        }
        */
        //拖曳改變文字大小
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                nameTextView.textSize = p1.toFloat()
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {
                //自定義動作
            }
            override fun onStopTrackingTouch(p0: SeekBar?) {
                //自定義動作
            }
        })

        //作業一:分數級距
        send.setOnClickListener {
            val levelNumber = nameEditText.text.toString().toInt()
            nameTextView.text = FunctionDB().getLevelName(levelNumber)
        }
    }
}