package com.example.myapplication.basic_02_Activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View

class IntentReceiveActivity : ActivityIntentsCommon() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mMessageTextView.text = intent?.getStringExtra("MESSAGE_PARAM") ?: ""

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun TAG(): String {
        return IntentReceiveActivity::class.java.simpleName
    }

    override fun actionName(): String {
        return "ReplyMessage"
    }

    override fun actionHandler(): View.OnClickListener {
        return View.OnClickListener {_view ->
            Intent(this, IntentSendActivity::class.java).let { _intent ->
                _intent.putExtra("MESSAGE_PARAM", mEditText.text.toString())
                setResult(Activity.RESULT_OK, _intent)
                finish()
                Log.d(TAG(), "finish mySelf")
            }
        }
    }
}