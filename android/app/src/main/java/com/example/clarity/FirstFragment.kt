package com.example.clarity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.clarity.databinding.FragmentFirstBinding
import android.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.example.clarity.sdk.ClaritySDK
import com.example.clarity.sdk.LoginRequest
import com.example.clarity.sdk.LoginResponse
import com.example.clarity.sdk.StatusResponse
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Response


class FirstFragment : Fragment() {

    private val api = ClaritySDK().apiService

    private var _binding: FragmentFirstBinding? = null

    private val sessionManager: SessionManager by lazy { SessionManager(requireContext()) }

    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)


        binding.buttonLogin.setOnClickListener {
            val username = binding.editTextUsername.text.toString()
            val password = binding.editTextPassword.text.toString()

            //check if the username/password is valid
            val req = LoginRequest(username, password)
            val response : Response<LoginResponse> = runBlocking {
                return@runBlocking api.login(req)
            }
            println(response.body())

            var valid = false

            if (response.body()?.response == StatusResponse.Success) {
                valid = true
            }
            if (valid) {
                lifecycleScope.launch {
                    sessionManager.setUserName(username)
                    sessionManager.setUserId(response.body()!!.user!!.user_id)
                }

                val intent = Intent(activity, IndexActivity::class.java)
                startActivity(intent)
            } else {
                binding.editTextUsername.setText("")
                binding.editTextPassword.setText("")
                wrongUserPassAlert()
            }

        }
            //need to call api

        binding.signupLink.setOnClickListener {
            findNavController().navigate(R.id.SecondFragment)
        }
    }
    private fun wrongUserPassAlert() {
        val alertDialog = AlertDialog.Builder(requireContext()).create() //the method to get context might be incorrect
        alertDialog.setMessage("Wrong Username or Password. Please enter correct credentials")
        alertDialog.setTitle("Incorrect Username/Password")
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK") { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}