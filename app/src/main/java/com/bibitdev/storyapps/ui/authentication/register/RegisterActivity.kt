package com.bibitdev.storyapps.ui.authentication.register

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bibitdev.storyapps.R
import com.bibitdev.storyapps.api.ApiConfig
import com.bibitdev.storyapps.databinding.ActivityRegisterBinding
import com.bibitdev.storyapps.model.Response
import com.bibitdev.storyapps.repository.UserRepository
import com.bibitdev.storyapps.ui.ViewModelFactory
import com.bibitdev.storyapps.ui.authentication.login.LoginActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private val registerViewModel: RegisterViewModel by viewModels {
        ViewModelFactory(UserRepository(ApiConfig.apiService))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        fadeInAnimation()
        observeRegistrable()
        animateRegister()
    }

    private fun setupListeners() {
        binding.apply {
            tvLoginLink.setOnClickListener { fadeOutAndNavigateToLogin() }
            btnRegister.setOnClickListener { registerbuttonclick() }
        }
    }

    private fun validateInput(name: String, email: String, password: String): Boolean {
        return when {
            name.isEmpty() -> {
                binding.edtRegisterName.error = getString(R.string.namerequired)
                false
            }

            email.isEmpty() -> {
                binding.edtRegisterEmail.error = getString(R.string.emailrequired)
                false
            }

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.edtRegisterEmail.error = getString(R.string.invalidemail)
                false
            }

            password.isEmpty() -> {
                binding.edtRegisterPassword.error = getString(R.string.password_too_short)
                false
            }

            password.length < 8 -> {
                binding.edtRegisterPassword.error = getString(R.string.password_too_short)
                false
            }

            else -> true
        }
    }

    private fun registerbuttonclick() {
        val name = binding.edtRegisterName.text.toString().trim()
        val email = binding.edtRegisterEmail.text.toString().trim()
        val password = binding.edtRegisterPassword.text.toString().trim()

        if (!validateInput(name, email, password)) {
            return
        }

        updateLoadingState(true)
        registerViewModel.register(name, email, password)
    }

    private fun observeRegistrable() {
        registerViewModel.apply {
            registerResult.observe(this@RegisterActivity) { processRegistration(it) }
            error.observe(this@RegisterActivity) { handleError(it) }
        }
    }

    private fun processRegistration(response: Response) {
        updateLoadingState(false)
        val message = if (response.error) response.message else getString(R.string.registersuccess)
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()

        if (!response.error) fadeOutAndNavigateToLogin()
    }

    private fun handleError(errorMessage: String) {
        updateLoadingState(false)
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }

    private fun fadeInAnimation() {
        ObjectAnimator.ofFloat(binding.root, "alpha", 0f, 1f).apply {
            duration = 300
            start()
        }
    }

    private fun fadeOutAndNavigateToLogin() {
        ObjectAnimator.ofFloat(binding.root, "alpha", 1f, 0f).apply {
            duration = 300
            start()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    startActivity(intent)
                    finish()
                }
            })
        }
    }

    private fun updateLoadingState(isLoading: Boolean) {
        binding.apply {
            progressOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            if (isLoading) disableInput() else enableInput()
        }
    }

    private fun enableInput() {
        binding.apply {
            edtRegisterName.isEnabled = true
            edtRegisterEmail.isEnabled = true
            edtRegisterPassword.isEnabled = true
            btnRegister.isEnabled = true
            tvRegister.isEnabled = true
        }
    }

    private fun disableInput() {
        binding.apply {
            edtRegisterName.isEnabled = false
            edtRegisterEmail.isEnabled = false
            edtRegisterPassword.isEnabled = false
            btnRegister.isEnabled = false
            tvRegister.isEnabled = false
        }
    }

    private fun animateRegister() {
        ObjectAnimator.ofFloat(binding.ivRegister, View.TRANSLATION_X, -50f, 50f).apply {
            duration = 2000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val regist = ObjectAnimator.ofFloat(binding.tvRegister, View.ALPHA, 1f).setDuration(300)
        val name = ObjectAnimator.ofFloat(binding.tvName, View.ALPHA, 1f).setDuration(300)
        val namelayout = ObjectAnimator.ofFloat(binding.tfName, View.ALPHA, 1f).setDuration(300)
        val emailtext = ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(300)
        val emaillayout = ObjectAnimator.ofFloat(binding.tfEmail, View.ALPHA, 1f).setDuration(300)
        val passtext = ObjectAnimator.ofFloat(binding.tvPassword, View.ALPHA, 1f).setDuration(300)
        val passlayout = ObjectAnimator.ofFloat(binding.tfPassword, View.ALPHA, 1f).setDuration(300)
        val btnregister = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(300)
        val ask = ObjectAnimator.ofFloat(binding.tvAskAccount, View.ALPHA, 1f).setDuration(300)
        val link = ObjectAnimator.ofFloat(binding.tvLoginLink, View.ALPHA, 1f).setDuration(300)

        AnimatorSet().apply {
            playSequentially(
                regist,
                name,
                namelayout,
                emailtext,
                emaillayout,
                passtext,
                passlayout,
                btnregister,
                ask,
                link
            )
            start()
        }
    }
}
