package com.bibitdev.storyapps.ui.authentication.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bibitdev.storyapps.R
import com.bibitdev.storyapps.api.ApiConfig
import com.bibitdev.storyapps.databinding.ActivityLoginBinding
import com.bibitdev.storyapps.model.LoginResponse
import com.bibitdev.storyapps.repository.UserRepository
import com.bibitdev.storyapps.ui.ViewModelFactory
import com.bibitdev.storyapps.ui.authentication.register.RegisterActivity
import com.bibitdev.storyapps.ui.home.HomeActivity
import com.bibitdev.storyapps.utils.PreferencesHelper
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {
    private lateinit var preferencesHelper: PreferencesHelper
    private lateinit var binding: ActivityLoginBinding

    private val loginViewModel: LoginViewModel by viewModels {
        ViewModelFactory(UserRepository(ApiConfig.apiService))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferencesHelper = PreferencesHelper(this)

        setupView()
        observeViewModel()
        setupAction()
    }

    private fun setupView() {
        animationLogin()
    }

    private fun loginClick() {
        val email = binding.edtLoginEmail.text.toString().trim()
        val password = binding.edtLoginPassword.text.toString().trim()

        if (validateInput(email, password)) {
            executeLogin(email, password)
        }
    }

    private fun signUpClick() {
        val intent = Intent(this, RegisterActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun setupAction() {
        binding.btnLogin.setOnClickListener { loginClick() }
        binding.tvRegisterLogin.setOnClickListener { signUpClick() }
    }

    private fun validateInput(email: String, password: String): Boolean {
        return when {
            email.isEmpty() -> {
                binding.edtLoginEmail.error = getString(R.string.emailrequired)
                false
            }

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.edtLoginEmail.error = getString(R.string.invalidemail)
                false
            }

            password.isEmpty() -> {
                binding.edtLoginPassword.error = getString(R.string.password_too_short)
                false
            }

            else -> true
        }
    }

    private fun executeLogin(email: String, password: String) {
        setLoad(true)
        loginViewModel.login(email, password)
    }

    private fun observeViewModel() {
        loginViewModel.isFetching.observe(this) { setLoad(it) }
        loginViewModel.loginResult.observe(this) { processLoginResponse(it) }
        loginViewModel.error.observe(this) { showError(it) }
    }

    private fun processLoginResponse(response: LoginResponse?) {
        setLoad(false)
        response?.let {
            if (it.error) {
                Snackbar.make(binding.root, it.message, Snackbar.LENGTH_LONG).show()
            } else {
                preferencesHelper.saveUser(it.loginResult)
                goToHome()
            }
        }
    }

    private fun goToHome() {
        val intent = Intent(this, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    }

    private fun setLoad(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        val isEnabled = !isLoading
        binding.btnLogin.isEnabled = isEnabled
        binding.tvRegisterLogin.isEnabled = isEnabled
        binding.edtLoginEmail.isEnabled = isEnabled
        binding.edtLoginPassword.isEnabled = isEnabled
    }

    private fun showError(errorMessage: String) {
        setLoad(false)
        Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG).show()
    }

    private fun animationLogin() {
        ObjectAnimator.ofFloat(binding.ivLogin, View.TRANSLATION_X, -50f, 50f).apply {
            duration = 2000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val hello = ObjectAnimator.ofFloat(binding.tvHello, View.ALPHA, 1f).setDuration(300)
        val emailtext = ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(300)
        val emaillayout = ObjectAnimator.ofFloat(binding.tfEmail, View.ALPHA, 1f).setDuration(300)
        val passtext = ObjectAnimator.ofFloat(binding.tvPassword, View.ALPHA, 1f).setDuration(300)
        val passlayout = ObjectAnimator.ofFloat(binding.tfPassword, View.ALPHA, 1f).setDuration(300)
        val login = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(300)
        val ask = ObjectAnimator.ofFloat(binding.tvAsk, View.ALPHA, 1f).setDuration(300)
        val regist = ObjectAnimator.ofFloat(binding.tvRegisterLogin, View.ALPHA, 1f).setDuration(300)


        AnimatorSet().apply {
            playSequentially(
                hello,
                emailtext,
                emaillayout,
                passtext,
                passlayout,
                login,
                ask,
                regist
            )
            start()
        }
    }
}
