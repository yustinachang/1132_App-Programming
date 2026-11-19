package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_main2)

        //Toast
        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener{
            Toast.makeText(this, "Toast提示訊息", Toast.LENGTH_SHORT).show()
        }

        //Snackbar
        val button2 = findViewById<Button>(R.id.button2)
        button2.setOnClickListener{
            Snackbar.make(it, "Snackbar提示訊息", Snackbar.LENGTH_INDEFINITE).setAction("按鈕"){
                val intent = Intent(android.content.Intent.ACTION_VIEW)
                intent.data = Uri.parse("https://www.google.com/")
                startActivity(intent)
            }.show()
        }

        //AlertDialog
        val button3 = findViewById<Button>(R.id.button3)
        button3.setOnClickListener {
            Toast.makeText(this, "Toast提示訊息", Toast.LENGTH_LONG).show()
            AlertDialog.Builder(this).setTitle("按鈕的AlertDialog").setMessage("AlertDialog內容")
                .setNeutralButton("左按鈕") { dialog, which ->
                    Toast.makeText(this, "左按鈕", Toast.LENGTH_LONG).show()
                }
                .setNegativeButton("中按鈕") { dialog, which ->
                    Toast.makeText(this, "中按鈕", Toast.LENGTH_LONG).show()
                }
                .setPositiveButton("右按鈕") { dialog, which ->
                    Toast.makeText(this, "右按鈕", Toast.LENGTH_LONG).show()
                }.show()
        }

        //AlertDialog2
        val item = resources.getStringArray(R.array.itemList)
        //val item = arrayOf("item 1", "item 2", "item 3", "item 4", "item 5")
        val button4 = findViewById<Button>(R.id.button4)
        button4.setOnClickListener {
            AlertDialog.Builder(this).setTitle("列表式的AlertDialog")
                .setItems(item){ dialogInterface, i->
                    Toast.makeText(this, "選擇${item[i]}", Toast.LENGTH_LONG).show()
                }.show()
        }

        //AlertDialog3
        val position = 0
        val button5 = findViewById<Button>(R.id.button5)
        button5.setOnClickListener {
            AlertDialog.Builder(this).setTitle("單選式的AlertDialog")
                .setSingleChoiceItems(item, 0){ dialogInterface, i->
                    Toast.makeText(this, "選擇${item[position]}", Toast.LENGTH_LONG).show()
                }.show()
        }

        /*
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
         */
    }
}