package com.example.myapplication.basic_01_02_firstApp

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe

import com.example.myapplication.R
import javax.inject.Inject

class homeWorkFragment : Fragment() {
    lateinit var mZeroButton : Button
    lateinit var mCountTextView : TextView
    lateinit var mCountButton : Button

    companion object {
        fun newInstance() = homeWorkFragment()
    }

    /*@Inject*/
    lateinit var viewModel: CountViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_first_app_home_work, container, false).also{
            it.findViewById<Button>(R.id.showToastButton).also { _button ->
                _button.setOnClickListener() {
                    Toast.makeText(this.context, R.string.home_work_toast, Toast.LENGTH_LONG)
                        .also { _toast ->
                            _toast.setGravity(Gravity.LEFT or Gravity.BOTTOM, 0, 0)
                            _toast.show()
                        }
                }
            }
            mZeroButton = it.findViewById<Button>(R.id.zeroButton).also {_zeroBtn ->
                _zeroBtn.setOnClickListener({_view ->
                    viewModel.getCountViewModel().postValue(0)
                })
            }

            mCountTextView = it.findViewById(R.id.countTextView)

            mCountButton = it.findViewById<Button>(R.id.countButton).also {_countBtn ->
                _countBtn.setOnClickListener(View.OnClickListener {
                    var nextValue = mCountTextView.text.toString().toInt() + 1
                    viewModel.getCountViewModel().postValue(nextValue)
                })
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CountViewModel::class.java).also {_viewMode ->
            _viewMode.getCountViewModel().observe(this, Observer {_countValue ->
                mCountTextView.text = _countValue.toString()
                if(_countValue > 0){
                    mZeroButton.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                }
                else {
                    mZeroButton.setBackgroundColor(resources.getColor(R.color.colorGray))
                }
                mCountButton.setBackgroundColor(resources.getColor(if (_countValue % 2 == 1)  R.color.colorOdd else R.color.colorPrimary))
            })
        }
    }
}
