package com.example.myapplicationhw2

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val client = OkHttpClient()

    private lateinit var button: Button
    private lateinit var tvCountry1: EditText
    private lateinit var tvCountry2: TextView
    private lateinit var tvCountry3: TextView
    private lateinit var svCountry1: Spinner
    private lateinit var svCountry2: Spinner
    private lateinit var svCountry3: Spinner
    private lateinit var ivCountry1: ImageView
    private lateinit var ivCountry2: ImageView
    private lateinit var ivCountry3: ImageView

    private var ratesObject: JSONObject? = null  // 儲存匯率資料

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 綁定 UI 元件
        button = findViewById(R.id.button)
        tvCountry1 = findViewById(R.id.tvCountry1)
        tvCountry2 = findViewById(R.id.tvCountry2)
        tvCountry3 = findViewById(R.id.tvCountry3)
        svCountry1 = findViewById(R.id.svCountry1)
        svCountry2 = findViewById(R.id.svCountry2)
        svCountry3 = findViewById(R.id.svCountry3)
        ivCountry1 = findViewById(R.id.ivCountry1)
        ivCountry2 = findViewById(R.id.ivCountry2)
        ivCountry3 = findViewById(R.id.ivCountry3)

        button.setOnClickListener {
            val svCountry1Text = svCountry1.selectedItem.toString()
            fetchCountryData(svCountry1Text)
        }

        // Spinner 2 監聽
        svCountry2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                updateConversion(svCountry2, tvCountry2, ivCountry2)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Spinner 3 監聽
        svCountry3.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                updateConversion(svCountry3, tvCountry3, ivCountry3)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun fetchCountryData(baseCurrency: String) {
        val request = Request.Builder()
            .url("https://v6.exchangerate-api.com/v6/e4420e0b76437bc31bc4f3b7/latest/$baseCurrency")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    button.setBackgroundColor(ContextCompat.getColor(this@MainActivity, R.color.red))
                    button.text = "請求失敗：${e.message}"
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful || bodyString.isEmpty()) {
                    runOnUiThread {
                        button.setBackgroundColor(ContextCompat.getColor(this@MainActivity, R.color.red))
                        button.text = "回應失敗或無資料"
                    }
                    return
                }

                try {
                    val jsonObject = JSONObject(bodyString)
                    val result = jsonObject.getString("result")
                    if (result != "success") {
                        runOnUiThread {
                            button.setBackgroundColor(ContextCompat.getColor(this@MainActivity, R.color.red))
                            button.text = "API 回傳失敗"
                        }
                        return
                    }

                    ratesObject = jsonObject.getJSONObject("conversion_rates")

                    runOnUiThread {
                        // 更新旗幟圖片
                        ivCountry1.setImageResource(getImageResource(svCountry1.selectedItem.toString()))

                        // 重新觸發 Spinner 更新顯示
                        updateConversion(svCountry2, tvCountry2, ivCountry2)
                        updateConversion(svCountry3, tvCountry3, ivCountry3)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    runOnUiThread {
                        button.setBackgroundColor(ContextCompat.getColor(this@MainActivity, R.color.red))
                        button.text = "JSON 解析失敗：${e.message}"
                    }
                }
            }
        })
    }

    private fun updateConversion(spinner: Spinner, targetTextView: TextView, imageView: ImageView) {
        val amount = tvCountry1.text.toString().toFloatOrNull() ?: 1f
        val currency = spinner.selectedItem.toString()

        try {
            ratesObject?.let {
                val rate = it.getDouble(currency)
                targetTextView.text = (amount * rate).toString()
                imageView.setImageResource(getImageResource(currency))
            }
        } catch (e: Exception) {
            targetTextView.text = "錯誤"
        }
    }

    // 動態找 drawable 圖片，找不到就回傳 ic_unknown
    private fun getImageResource(resourceName: String): Int {
        val resId = resources.getIdentifier(resourceName.lowercase(), "drawable", packageName)
        return if (resId == 0) R.drawable.ic_unknown else resId
    }
}