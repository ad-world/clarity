package com.example.clarity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.TextView
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.clarity.databinding.FragmentFirstBinding
import android.app.AlertDialog


class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
<<<<<<< HEAD

        super.onViewCreated(view, savedInstanceState)

=======

        super.onViewCreated(view, savedInstanceState)
        super.onCreate(savedInstanceState)
>>>>>>> e447f3838fff6eb13007107376fdf8ab08935c85

        binding.buttonLogin.setOnClickListener {
            val username = binding.editTextUsername.text.toString()
            val password = binding.editTextPassword.text.toString()

<<<<<<< HEAD
            //check if the username/password is valid

            val valid = true
            if(valid) {
                val intent = Intent(activity, IndexActivity::class.java)
                startActivity(intent)
            } else {
                binding.editTextUsername.setText("")
                binding.editTextPassword.setText("")
                wrongUserPassAlert()
            }

=======
            //need to call api
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
>>>>>>> e447f3838fff6eb13007107376fdf8ab08935c85
        }

        binding.signupLink.setOnClickListener {
            findNavController().navigate(R.id.SecondFragment)
        }

<<<<<<< HEAD
    }
    private fun wrongUserPassAlert() {
        val alertDialog = AlertDialog.Builder(requireContext()).create() //the method to get context might be incorrect
        alertDialog.setMessage("Wrong Username or Password. Please enter correct credentials")
        alertDialog.setTitle("Incorrect Username/Password")
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK") { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.show()
=======

>>>>>>> e447f3838fff6eb13007107376fdf8ab08935c85
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}