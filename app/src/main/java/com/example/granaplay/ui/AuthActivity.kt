package com.example.granaplay.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.granaplay.GranaPlayApplication
import com.example.granaplay.databinding.ActivityAuthBinding
import com.example.granaplay.MainActivity

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding





    // Inicializando o ViewModel usando a Factory que criamos na Application
    private val viewModel: AuthViewModel by viewModels {
        GameViewModelFactory((application as GranaPlayApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val senha = binding.etSenha.text.toString()

            if (email.isNotBlank() && senha.isNotBlank()) {
                viewModel.login(email, senha)
            } else {
                Toast.makeText(this, "Preencha email e senha", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnCadastrar.setOnClickListener {
            val nome = binding.etNome.text.toString()
            val email = binding.etEmail.text.toString()
            val senha = binding.etSenha.text.toString()

            if (nome.isNotBlank() && email.isNotBlank() && senha.isNotBlank()) {
                viewModel.cadastrar(nome, email, senha)
            } else {
                Toast.makeText(this, "Preencha todos os campos para cadastrar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupObservers() {
        // Observa Sucesso no Login
        viewModel.loginResult.observe(this) { sucesso ->
            if (sucesso) {
                Toast.makeText(this, "Login realizado!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        // Observa Sucesso no Cadastro
        viewModel.cadastroResult.observe(this) { sucesso ->
            if (sucesso) {
                Toast.makeText(this, "Cadastro realizado! Faça login.", Toast.LENGTH_SHORT).show()
                // Limpar campos ou já logar direto se quiser
                binding.etNome.text.clear()
            }
        }

        // Observa Erros
        viewModel.errorMessage.observe(this) { erro ->
            erro?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
    }
}
