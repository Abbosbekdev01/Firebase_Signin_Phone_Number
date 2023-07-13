package uz.abbosbek.myfirebasephonenumber.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import uz.abbosbek.myfirebasephonenumber.GetConstants
import uz.abbosbek.myfirebasephonenumber.R
import uz.abbosbek.myfirebasephonenumber.databinding.FragmentOpenBinding

class OpenFragment : Fragment() {

    private val binding by lazy { FragmentOpenBinding.inflate(LayoutInflater.from(requireContext())) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val open = arguments?.getString(GetConstants.KEY_NUMBER).toString()
        binding.tvNamber.text = open
        Toast.makeText(requireContext(), open, Toast.LENGTH_SHORT).show()
        return binding.root
    }
}