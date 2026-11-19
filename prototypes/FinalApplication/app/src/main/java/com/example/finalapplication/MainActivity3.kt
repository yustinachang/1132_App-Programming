package com.example.finalapplication

import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class MainActivity3 : AppCompatActivity() {

    private lateinit var container: FrameLayout
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity3", "onCreate called")
        setContentView(R.layout.activity_main3)

        container = findViewById(R.id.frameContainer)

        val shapesNum = intent.getIntExtra("shapes_num", 0)
        Log.d("MainActivity3", "Received shapes_num: $shapesNum")

        val shapesData = intent.getSerializableExtra("shapes_data") as? ArrayList<ArrayList<List<Double>>>
        Log.d("MainActivity3", "Received shapes_data: $shapesData")

        // 合併訊息，一次顯示
        val msg = "Received shapes_num: $shapesNum\nReceived shapes_data size: ${shapesData?.size ?: 0}"

        // 確保 UI 繪製完成後顯示Dialog
        container.post {
            showLongMessageDialog("Received Data", msg)
        }

        if (shapesData == null) {
            Log.e("MainActivity3", "shapes_data is null!")
            Toast.makeText(this, "shapes_data is null!", Toast.LENGTH_LONG).show()
            return
        }

        // 把嵌套 List 轉成 JSONArray 結構
        val shapeArray = JSONArray()
        for ((shapeIndex, shape) in shapesData.withIndex()) {
            val pointArray = JSONArray()
            for ((pointIndex, point) in shape.withIndex()) {
                val xy = JSONArray()
                xy.put(point[0])
                xy.put(point[1])
                pointArray.put(xy)
                Log.d("MainActivity3", "Shape $shapeIndex Point $pointIndex: ${point[0]}, ${point[1]}")
            }
            shapeArray.put(pointArray)
        }

        val json = JSONObject().apply {
            put("shapes_num", shapesNum)
            put("shapes_data", shapeArray)
        }
        Log.d("MainActivity3", "JSON to send: $json")

        val mediaType = "application/json".toMediaType()
        val requestBody = json.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url("https://a794-34-122-238-127.ngrok-free.app/predict") // 請替換成你的 ngrok URL
            .post(requestBody)
            .build()

        Log.d("MainActivity3", "Sending request to server...")

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("MainActivity3", "API call failed: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this@MainActivity3, "API call failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    Log.e("MainActivity3", "API returned error code: ${response.code}")
                    runOnUiThread {
                        Toast.makeText(this@MainActivity3, "API error: ${response.code}", Toast.LENGTH_LONG).show()
                    }
                    return
                }

                val jsonString = response.body?.string()
                Log.d("MainActivity3", "API response JSON: $jsonString")

                if (jsonString == null) {
                    Log.e("MainActivity3", "Response body is null")
                    runOnUiThread {
                        Toast.makeText(this@MainActivity3, "Response body is null", Toast.LENGTH_LONG).show()
                    }
                    return
                }

                try {
                    val jsonObject = JSONObject(jsonString)
                    val best = jsonObject.getJSONObject("best")
                    val placementArray = best.getJSONArray("placement")
                    Log.d("MainActivity3", "Parsed 'best' and 'placement' from response")

                    runOnUiThread {
                        container.removeAllViews()
                        for (i in 0 until placementArray.length()) {
                            val pointArray = placementArray.getJSONArray(i)
                            val points = mutableListOf<List<Double>>()
                            for (j in 0 until pointArray.length()) {
                                val point = pointArray.getJSONArray(j)
                                val x = point.getDouble(0)
                                val y = point.getDouble(1)
                                points.add(listOf(x, y))
                                Log.d("MainActivity3", "Polygon $i Point $j: ($x, $y)")
                            }
                            val polyView = PolygonView(this@MainActivity3, points)
                            container.addView(polyView)
                        }
                        Toast.makeText(this@MainActivity3, "Shapes drawn successfully!", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("MainActivity3", "JSON parsing error: ${e.message}")
                    runOnUiThread {
                        Toast.makeText(this@MainActivity3, "JSON parsing error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }

    // 自訂的顯示長訊息 Dialog (可捲動)
    fun showLongMessageDialog(title: String, message: String) {
        val scrollView = ScrollView(this)
        val textView = TextView(this).apply {
            text = message
            setPadding(40, 40, 40, 40)
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        }
        scrollView.addView(textView)

        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(scrollView)
            .setPositiveButton("OK", null)
            .show()
    }
}