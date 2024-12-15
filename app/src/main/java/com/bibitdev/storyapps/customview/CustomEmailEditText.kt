package com.bibitdev.storyapps.customview

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.bibitdev.storyapps.R

class CustomEmailEditText : AppCompatEditText, View.OnFocusChangeListener {

    var isEmailValid = false
    private var isEmailHasTaken = false
    private lateinit var emailSame: String

    constructor(context: Context) : super(context) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initialize()
    }

    private fun initialize() {
        background = ContextCompat.getDrawable(context, R.drawable.input_desc)

        inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

        onFocusChangeListener = this

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                emailValidation()
                if (isEmailHasTaken) {
                    emailValidateTake()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (!hasFocus) {
            emailValidation()
            if (isEmailHasTaken) {
                emailValidateTake()
            }
        }
    }

    private fun emailValidation() {
        isEmailValid = Patterns.EMAIL_ADDRESS.matcher(text.toString().trim()).matches()
        error = if (!isEmailValid) {
            resources.getString(R.string.invalidemail)
        } else {
            null
        }
    }

    private fun emailValidateTake() {
        error = if (isEmailHasTaken && text.toString().trim() == emailSame) {
            resources.getString(R.string.emailalready)
        } else {
            null
        }
    }
}
