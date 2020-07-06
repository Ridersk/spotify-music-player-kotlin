package com.spotifyclone.presentation.musicqueue

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.spotifyclone.R
import com.spotifyclone.presentation.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_modify_queue.*

class ModifyQueueFragment(
    private val callBackAdd: () -> Unit,
    private val callBackRemoce: () -> Unit
) : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_modify_queue, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        createCallbacks()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun initComponents() {
        btnAdd.text = getString(R.string.music_queue_fragment_add_music)
        btnRemove.text = getString(R.string.music_queue_fragment_remove_music)
    }

    private fun createCallbacks() {
        btnAdd.setOnClickListener {
            callBackAdd.invoke()
        }

        btnRemove.setOnClickListener {
            callBackRemoce.invoke()
        }
    }

    companion object {
        fun getInstance(
            callBackAdd: () -> Unit,
            callBackRemoce: () -> Unit
        ): ModifyQueueFragment {
            return ModifyQueueFragment(callBackAdd, callBackRemoce)
        }
    }
}