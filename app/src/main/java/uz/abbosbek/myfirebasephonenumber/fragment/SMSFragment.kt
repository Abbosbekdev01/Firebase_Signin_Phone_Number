package uz.abbosbek.myfirebasephonenumber.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import uz.abbosbek.myfirebasephonenumber.GetConstants
import uz.abbosbek.myfirebasephonenumber.R
import uz.abbosbek.myfirebasephonenumber.databinding.FragmentNumberBinding
import uz.abbosbek.myfirebasephonenumber.databinding.FragmentSMSBinding
import java.util.concurrent.TimeUnit

private const val TAG = "SMSFragment"

class SMSFragment : Fragment() {

    private val binding by lazy { FragmentSMSBinding.inflate(LayoutInflater.from(requireContext())) }
    private var number: String? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var storedVerificationId: String
    private lateinit var resentToken: PhoneAuthProvider.ForceResendingToken

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        auth = Firebase.auth

        number = arguments?.getString(GetConstants.KEY_NUMBER).toString()

        Toast.makeText(requireContext(), number, Toast.LENGTH_SHORT).show()

        openScreen()
        binding.progressBar.visibility = View.VISIBLE
        return binding.root
    }

    // todo: keyingi oynaga otadigan funksiya
    private fun openScreen() {
        sendVerificationCode(number.toString())

        binding.verificationCode.addTextChangedListener {
            val verificationCode = it.toString()
            if (verificationCode.length == 6) {
                val credential =
                    PhoneAuthProvider.getCredential(storedVerificationId ?: "", verificationCode)
                signInWithPhoneAuthCredential(credential)
            }
        }

        binding.resendBtn.setOnClickListener {
            binding.progressBar.isSelected = true
            reSendVerificationCode(number.toString())
        }
    }

    private fun reSendVerificationCode(phoneNumber: String) {
        val option = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(callbacks)
            .setForceResendingToken(resentToken)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(option)
    }

    private fun sendVerificationCode(phoneNumber: String) {
        val option = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(option)
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Log.d(TAG, "onVerificationCompleted: $credential")
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.d(TAG, "onVerificationFailed:", e)
            if (e is FirebaseAuthInvalidCredentialsException) {
                //todo: Invalid request
            } else if (e is FirebaseAuthInvalidCredentialsException) {
                //todo: The SMS quota for the project has been exceeded
            }
            //todo: Show a message and update the UI
        }

        override fun onCodeSent(
            verficationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            Log.d(TAG, "onCodeSent: $verficationId")
            storedVerificationId = verficationId
            resentToken = token
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithPhoneAuthCredential: ---< Success >---")
                    val user = task.result?.user
                    binding.progressBar.isSelected = false
                    findNavController().navigate(
                        R.id.openFragment,
                        bundleOf(GetConstants.KEY_NUMBER to number)
                    )
                } else {

                    binding.progressBar.isSelected = false
                    Log.d(TAG, "signInWithPhoneAuthCredential: failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {

                    }
                    //todo: Update UI
                }
            }
    }
}