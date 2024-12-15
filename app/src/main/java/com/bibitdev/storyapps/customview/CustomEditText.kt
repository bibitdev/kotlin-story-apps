package com.bibitdev.storyapps.customview

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.bibitdev.storyapps.R

class CustomEditText : AppCompatEditText, View.OnFocusChangeListener {

    private var isNameValid: Boolean = false

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        init()
    }

    private fun init() {
        background = ContextCompat.getDrawable(context, R.drawable.input_desc)
        inputType = InputType.TYPE_CLASS_TEXT
        onFocusChangeListener = this
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (!hasFocus) {
            performValidation()
        }
    }

    private fun performValidation() {
        isNameValid = isValidInput()
        showErrorMessage(isNameValid)
    }

    private fun isValidInput(): Boolean {
        return text.toString().trim().isNotEmpty()
    }

    private fun showErrorMessage(isValid: Boolean) {
        error = if (isValid) {
            null
        } else {
            context.getString(R.string.namerequired)
        }
    }
}
