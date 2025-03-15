package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class SearchActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val toolbar: Toolbar = findViewById(R.id.toolbar_search)
        setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener {
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
        }

        val editText: EditText = findViewById(R.id.editText)
        editText.setCompoundDrawablesRelativeWithIntrinsicBounds(
            editText.compoundDrawablesRelative[0],
            null,
            null,
            null
        )

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            @SuppressLint("UseCompatLoadingForDrawables")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    editText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        editText.compoundDrawablesRelative[0],
                        null,
                        null,
                        null
                    )
                } else {
                    editText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        editText.compoundDrawablesRelative[0],
                        null,
                        resources.getDrawable(R.drawable.clear_button_icon, theme),
                        null
                    )
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        editText.setOnTouchListener { v, event ->
            if (event.action == android.view.MotionEvent.ACTION_UP) {
                val drawableEnd = editText.compoundDrawablesRelative[2]
                if (drawableEnd != null) {
                    val isClearButtonClicked =
                        event.x >= (editText.width - editText.paddingEnd - drawableEnd.bounds.width())
                    if (isClearButtonClicked) {
                        editText.text.clear()
                        return@setOnTouchListener true
                    }
                }
            }
            false
        }
    }
}