package dev.mobile.medicalink.utils

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import dev.mobile.medicalink.R

class GoTo {
    companion object {
        fun fragment(destinationFragment: Fragment, parentFragmentManager: FragmentManager) {
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, destinationFragment)
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }
    }
}