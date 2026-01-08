package com.example.granaplay.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.granaplay.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    // Configuração padrão do ViewBinding para Fragments
    private var _binding: FragmentProfileBinding? = null

    // Essa propriedade só é válida entre onCreateView e onDestroyView
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Se você quiser criar um ViewModel para o perfil depois, descomente as linhas abaixo:
        // val profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Exemplo de como manipular a tela:
        // binding.textProfile.text = "Perfil do Marcelo"

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}