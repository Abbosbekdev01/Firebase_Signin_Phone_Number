package uz.abbosbek.myfirebasephonenumber.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.FragmentResultOwner
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthActionCodeException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import uz.abbosbek.myfirebasephonenumber.GetConstants
import uz.abbosbek.myfirebasephonenumber.R
import uz.abbosbek.myfirebasephonenumber.databinding.FragmentNumberBinding
import java.util.concurrent.TimeUnit

private const val TAG = "NumberFragment"

class NumberFragment : Fragment() {

    private val binding by lazy { FragmentNumberBinding.inflate(LayoutInflater.from(requireContext())) }
    private lateinit var auth: FirebaseAuth
    private lateinit var gso: GoogleSignInOptions
    private lateinit var googleSignInClient: GoogleSignInClient
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        auth = Firebase.auth
        binding.btntoCodescreen.setOnClickListener {
            if (binding.edtNumber.text.isNotEmpty()) {
                val bundle = bundleOf(GetConstants.KEY_NUMBER to binding.edtNumber.text.toString())
                findNavController().navigate(R.id.SMSFragment, bundle)
                binding.edtNumber.text.clear()
            }
        }
        googleSingIn()
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            val signedInAccountFromIntent = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = signedInAccountFromIntent.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account?.idToken ?: "")

            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    private fun googleSingIn() {

        gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(R.string.client_id.toString())
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        binding.btnSignIn.setOnClickListener {
            val intent = googleSignInClient.signInIntent
            startActivityForResult(intent, 1)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "firebaseAuthWithGoogle: success")
                    val user = auth.currentUser

                    val budle =
                        bundleOf(GetConstants.KEY_NUMBER to user?.displayName)
                    findNavController().navigate(
                        R.id.openFragment,
                        bundleOf(GetConstants.KEY_NUMBER to budle)
                    )

                    Toast.makeText(requireContext(), "${user?.displayName}", Toast.LENGTH_SHORT)
                        .show()

//                    updateUI(user)
                } else {
                    Log.d(TAG, "firebaseAuthWithGoogle:${task.exception}")
//                    updateUI(null)
                }
            }
    }
}