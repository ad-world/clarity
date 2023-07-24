package com.example.clarity.profile

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.clarity.IndexActivity
import com.example.clarity.R
import com.example.clarity.SessionManager
import com.example.clarity.databinding.FragmentSettingsBinding
import com.example.clarity.sdk.ChangePasswordEntity
import com.example.clarity.sdk.ChangePasswordResponse
import com.example.clarity.sdk.ClaritySDK
import com.example.clarity.sdk.CreateUserEntity
import com.example.clarity.sdk.CreateUserResponse
import com.example.clarity.sdk.Difficulty
import com.example.clarity.sdk.EditUserEntity
import com.example.clarity.sdk.EditUserResponse
import com.example.clarity.sdk.GetUserResponse
import com.example.clarity.sdk.LoginRequest
import com.example.clarity.sdk.LoginResponse
import com.example.clarity.sdk.StatusResponse
import com.example.clarity.sdk.UpdateDifficultyEntity
import com.example.clarity.sdk.UpdateDifficultyResponse
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Response

class SettingsFragment : Fragment() {

    private val api = ClaritySDK().apiService

    private var _binding: FragmentSettingsBinding? = null

    private val sessionManager: SessionManager by lazy { SessionManager(requireContext()) }

    private val binding get() = _binding!!
    var username: String = ""
    var userId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            username = sessionManager.getUserName()
            userId = sessionManager.getUserId()
        }

        binding.back.setOnClickListener {
            findNavController().navigate(R.id.ProfileFragment)
        }
        val user = getUser()?.user

        val firstBefore = user?.firstname
        val lastBefore = user?.lastname
        val emailBefore = user?.email
        var difficulty = user?.difficulty

        binding.editTextFirst.setText(user?.firstname)
        binding.editTextLast.setText(user?.lastname)
        binding.editTextEmail.setText(user?.email)

        var level = 0
        when (difficulty) {
            Difficulty.Hard -> {
                binding.difficulty.check(R.id.hard)
                level = 2
            }
            Difficulty.Medium -> {
                binding.difficulty.check(R.id.medium)
                level = 1
            }
            else -> binding.difficulty.check(R.id.easy)
        }
        var newLevel = level
        binding.difficulty.setOnCheckedChangeListener { _, checkedId ->
            val radioButton: RadioButton = when (checkedId) {
                binding.easy.id -> binding.easy
                binding.medium.id -> binding.medium
                binding.hard.id -> binding.hard
                else -> binding.easy // Default value
            }
            difficulty = when (checkedId) {
                binding.easy.id -> Difficulty.Easy
                binding.medium.id -> Difficulty.Medium
                binding.hard.id -> Difficulty.Hard
                else -> Difficulty.Easy
            }
            newLevel = when (checkedId) {
                binding.easy.id -> 0
                binding.medium.id -> 1
                binding.hard.id -> 2
                else -> 0
            }
        }

        //binding.difficulty = user?.difficulty
        binding.buttonSavePass.setOnClickListener {
            val oldPassword = binding.editTextOldPass.text
            val newPassword = binding.editTextNewPass.text
            if(oldPassword.isNotEmpty() && newPassword.isNotEmpty()) {
                //then do api call to change password
                val changePass = ChangePasswordEntity(userId, oldPassword.toString(), newPassword.toString())
                val response: Response<ChangePasswordResponse> = runBlocking {
                    return@runBlocking api.changePassword(changePass)
                }
                if(response.isSuccessful) {
                    successfulPassChange()
                } else {
                    unsuccessfulPassChange()
                }
            } else {
                unsuccessfulPassChange()
            }
        }

        binding.buttonSave.setOnClickListener {

            var first = binding.editTextFirst.text
            var last = binding.editTextLast.text
            var email = binding.editTextEmail.text

            var notNull = false

            //need to check if all of these are not null
            if(first.isEmpty() || first.toString() == firstBefore) {
                first = null
            } else {
                notNull = true
            }
            if(last.isEmpty() || last.toString() == lastBefore) {
                last = null
            } else {
                notNull = true
            }
            if(email.isEmpty() || email.toString() == emailBefore) {
                email = null
            } else {
                notNull = true
            }


            if(notNull || newLevel != level) {
                var response: Response<EditUserResponse>? = null
                var responseDiff: Response<UpdateDifficultyResponse>? = null
                if(notNull) {
                    val user =
                        EditUserEntity(userId, first?.toString(), last?.toString(), email?.toString())
                    response = runBlocking {
                        return@runBlocking api.updateUser(user)
                    }
                }

                if(newLevel != level) {
                    println("it is different")
                    var diff = UpdateDifficultyEntity(userId, difficulty)
                    responseDiff = runBlocking {
                        return@runBlocking api.updateDifficulty(diff)
                    }
                }

                if(response?.isSuccessful == true || responseDiff?.isSuccessful == true) {
                    successfulInfoChange()
                }
            }

        }

    }
    private fun successfulInfoChange() {
        val alertDialog = AlertDialog.Builder(requireContext()).create()
        alertDialog.setMessage("You have been able to successfully update your information!")
        alertDialog.setTitle("Successful Information Change")
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK") { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.show()
    }
    private fun successfulPassChange() {
        val alertDialog = AlertDialog.Builder(requireContext()).create()
        alertDialog.setMessage("You have been able to successfully update your password!")
        alertDialog.setTitle("Successful Password Change")
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK") { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.show()
    }
    private fun unsuccessfulPassChange() {
        val alertDialog = AlertDialog.Builder(requireContext()).create() //the method to get context might be incorrect
        alertDialog.setMessage("Unsuccessful Password Change. Please make sure all fields are filled in and correct.")
        alertDialog.setTitle("Unsuccessful Password Change")
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK") { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.show()
    }
    private fun getUser(): GetUserResponse? {
        val response : Response<GetUserResponse> = runBlocking {
            return@runBlocking api.getUser(username)
        }
        return response.body()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}