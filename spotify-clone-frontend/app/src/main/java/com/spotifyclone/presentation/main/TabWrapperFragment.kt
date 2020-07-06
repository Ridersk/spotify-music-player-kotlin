package com.spotifyclone.presentation.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

class TabWrapperFragment(private val fragment: Fragment) : Fragment(), IWrapperFragment {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val frameLayout = FrameLayout(activity!!.applicationContext)
        frameLayout.id = ID
        val initialFragment = fragment
        val args = Bundle()
        initialFragment.arguments = args

        val fragmentManager = childFragmentManager
        fragmentManager.popBackStack(
            BACK_STACK_ROOT_TAG,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )

        fragmentManager.beginTransaction()
            .replace(ID, initialFragment)
            .addToBackStack(BACK_STACK_ROOT_TAG)
            .commit()
        return frameLayout
    }

    override fun onReplace(fragment: Fragment, args: Bundle) {
        fragment.arguments = args

        childFragmentManager
            .beginTransaction()
            .replace(ID, fragment)
            .addToBackStack(BACK_STACK_ROOT_TAG)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

    override fun onBackPressed(): Boolean {
        val size = childFragmentManager.backStackEntryCount
        if (size > 1) {
            childFragmentManager.popBackStackImmediate()
            return true
        }
        return false
    }

    companion object {
        private const val ID = 10000
        private const val BACK_STACK_ROOT_TAG = "ROOT_FRAGMENT"

        fun getInstance(fragment: Fragment): TabWrapperFragment {
            val activityTabFragment = TabWrapperFragment(fragment)
            val args = Bundle()
            activityTabFragment.arguments = args
            return activityTabFragment
        }
    }
}