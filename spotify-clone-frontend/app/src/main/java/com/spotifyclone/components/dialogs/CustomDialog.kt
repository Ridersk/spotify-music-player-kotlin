package com.spotifyclone.components.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import com.spotifyclone.R
import kotlinx.android.synthetic.main.dialog_bottom.*

class CustomDialog(
    private val contextActivity: Context
) : Dialog(contextActivity), View.OnClickListener {

    private lateinit var add: Button
    private lateinit var remove: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.setContentView(R.layout.dialog_bottom)
        super.onCreate(savedInstanceState)

        initComponents()
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btnAdd -> contextActivity
            R.id.btnRemove -> contextActivity
            else -> contextActivity
        }
    }

    private fun initComponents() {
        add = btnAdd
        remove = btnRemove

        add.text = contextActivity.getString(R.string.music_queue_dialog_add_music)
        add.setOnClickListener(this)

        remove.text = contextActivity.getString(R.string.music_queue_dialog_remove_music)
        remove.setOnClickListener(this)
    }
}
