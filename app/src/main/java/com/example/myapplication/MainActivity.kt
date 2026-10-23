package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.example.myapplication.ui.theme.InspectionScreen // 修正：根据左侧目录，InspectionScreen 在 ui.theme 包下

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                var result by remember { mutableStateOf("等待请求") }
                var busy by remember { mutableStateOf(false) }

                InspectionScreen("何志恒", "A203", 10, 3, result, busy) { room ->
                    busy = true
                    result = "请求中：$room"
                    Thread {
                        val text = fetch("https://httpbin.org/get?room=$room")
                        runOnUiThread {
                            result = text
                            busy = false
                        }
                    }.start()
                }
            }
        }
    }

    // 补充 fetch 函数的实现，用于解决 Unresolved reference 'fetch' 报错
    private fun fetch(urlString: String): String {
        return try {
            java.net.URL(urlString).readText()
        } catch (e: Exception) {
            "请求失败: ${e.message}"
        }
    }
}