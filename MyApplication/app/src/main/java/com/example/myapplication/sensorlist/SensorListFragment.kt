package com.example.myapplication.sensorlist

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.sensordetails.SensorDetailsActivity
import com.example.myapplication.sensorlist.model.sensorsViewModel
import dagger.android.support.AndroidSupportInjection
import io.reactivex.functions.Consumer
import javax.inject.Inject

class SensorListFragment : Fragment(){
    @Inject
    lateinit var listAdapter: SensorListAdapter
    @Inject
    lateinit var viewModels : sensorsViewModel
    lateinit var sensorListView : RecyclerView
    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sensorlist, container, false).apply {
            sensorListView = findViewById(R.id.sensor_list)
            sensorListView.apply {
                layoutManager = LinearLayoutManager(activity).run {
                    orientation = LinearLayoutManager.VERTICAL
                    this
                }
                adapter = listAdapter
                listAdapter.clickPublishSubject.subscribe(Consumer {
                    Intent(activity, SensorDetailsActivity::class.java).apply {
                        putExtra(SensorDetailsActivity.SENSOR_TYPE, it.type)
                        startActivity(this)
                    }
                })
            }
            activity?.run{
                viewModels.readSensors().observe(this, Observer<List<Sensor>>{ _sensorsList ->
                    listAdapter.setSensors(_sensorsList)
                })
            }
        }
    }

    companion object{
        fun newInstance() : SensorListFragment {
            return SensorListFragment().let {
                val bundle = Bundle()
                it.arguments = bundle;
                it
            }
        }
    }
}