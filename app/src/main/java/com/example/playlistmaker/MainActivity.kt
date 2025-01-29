package com.example.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonSearch = findViewById<Button>(R.id.search)
        val buttonMedia = findViewById<Button>(R.id.media)
        val buttonSettings = findViewById<Button>(R.id.settings)

        val buttonSearchClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(
                    this@MainActivity,
                    "Страница Поиск пока в разработке",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        buttonSearch.setOnClickListener(buttonSearchClickListener)

        val buttonMediaClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(
                    this@MainActivity,
                    "Страница Медиатека пока в разработке",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        buttonMedia.setOnClickListener(buttonMediaClickListener)

        buttonSettings.setOnClickListener {
            Toast.makeText(
                this@MainActivity,
                "Страница Настройки пока в разработке",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    } */
}