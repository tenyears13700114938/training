package com.example.myapplication.basic_02_Activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.myapplication.R

class IntentSendActivity : ActivityIntentsCommon() {
    private val MESSAGE_SAVE_KEY = "MESSAGE_SAVE_KEY"
    companion object {
        val TEXT_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.getString("MESSAGE_SAVE_KEY", resources.getString(R.string.no_message))
            .also { _message ->
                mMessageTextView.text = _message
            }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(MESSAGE_SAVE_KEY, mMessageTextView.text.toString())
    }

    override fun actionName(): String {
        return "SendMessage"
    }

    override fun TAG(): String {
        return IntentSendActivity::class.java.simpleName
    }

    override fun actionHandler(): View.OnClickListener {
        return View.OnClickListener {_view ->
            Intent(this, IntentReceiveActivity::class.java).let { _intent ->
                _intent.putExtra("MESSAGE_PARAM", mEditText.text.toString())
                startActivityForResult(_intent, TEXT_REQUEST)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == TEXT_REQUEST && resultCode == Activity.RESULT_OK){
               mMessageTextView.text = data?.getStringExtra("MESSAGE_PARAM")?: ""
        }
    }
}