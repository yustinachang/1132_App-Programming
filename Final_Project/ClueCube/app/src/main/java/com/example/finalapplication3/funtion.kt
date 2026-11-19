package com.example.finalapplication3

import android.content.Context
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.android.material.bottomsheet.BottomSheetDialog

fun Context.showCubeSizePicker(onSelect: (index: Int) -> Unit) {
    val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_picker, null)
    val listView = dialogView.findViewById<ListView>(R.id.list_option)
    val options = resources.getStringArray(R.array.cube_sizes)
    val dialog = BottomSheetDialog(this)
    val adapter = ArrayAdapter(this, R.layout.item_option, R.id.text_option, options)
    listView.adapter = adapter
    listView.setOnItemClickListener { _, _, position, _ ->
        onSelect(position)
        dialog.dismiss()
    }
    dialog.setContentView(dialogView)
    dialog.show()
}