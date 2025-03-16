package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class SearchActivity : AppCompatActivity() {

    private var searchText: String = ""

    @SuppressLint("MissingInflatedId", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString("searchText", "")
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar_search)
        setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener {
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
            finish()
        }

        val editText: EditText = findViewById(R.id.editText)
        editText.setText(searchText)
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
                searchText = s?.toString() ?: ""
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
                        searchText = ""

                        val imm =
                            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(editText.windowToken, 0)

                        return@setOnTouchListener true
                    }
                }
            }
            false
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("searchText", searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString("searchText", "")
        val editText: EditText = findViewById(R.id.editText)
        editText.setText(searchText)
    }
}