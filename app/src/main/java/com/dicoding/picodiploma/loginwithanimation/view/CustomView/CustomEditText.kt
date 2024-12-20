package com.dicoding.picodiploma.loginwithanimation.view.CustomView

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import com.google.android.material.textfield.TextInputEditText
import com.dicoding.picodiploma.loginwithanimation.R

class CustomEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : TextInputEditText(context, attrs, defStyleAttr) {
    init {
        setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                Log.d("CustomEditText", "Field mendapatkan fokus")
            } else {
                validatePassword()
            }
        }
    }

    override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
        super.onTextChanged(text, start, before, count)

        if (text != null && text.length < 8) {
            error = context.getString(R.string.password_too_short)
        } else {
            error = null
        }
    }

    private fun validatePassword() {
        val text = text?.toString() ?: ""
        if (text.length < 8) {
            error = context.getString(R.string.password_too_short)
        } else {
            error = null
        }
    }
}
