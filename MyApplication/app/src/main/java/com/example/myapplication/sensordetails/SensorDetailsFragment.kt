package com.example.myapplication.sensordetails

import android.app.Service.SENSOR_SERVICE
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class SensorDetailsFragment : Fragment(), SensorEventListener {
    @Inject
    lateinit var sensorManager : SensorManager
    lateinit var mSensorName : TextView
    lateinit var mSensorData : TextView

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //todo
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            mSensorName.text = it.sensor?.name
            it.values?.let {
                var sb = StringBuilder()
                var floatInteractor = it.iterator()
                while(floatInteractor.hasNext()){
                    sb.append(floatInteractor.nextFloat())
                    sb.append(",")
                }
                mSensorData.text = sb.toString()
            }
            arguments?.getInt(SensorDetailsActivity.SENSOR_TYPE)?.apply {
                when(this){
                    Sensor.TYPE_LIGHT -> {
                        var lightCode = event?.values[0].toInt()
                        var colorCode = 0xff shl(24)
                        colorCode = colorCode or (0xf0 shl(16))
                        colorCode = colorCode or 0xf0 shl(8)
                        colorCode = colorCode or (lightCode and 0xff)
                        activity?.window?.decorView?.setBackgroundColor(colorCode)
                    }
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //sensorManager = activity?.getSystemService(SENSOR_SERVICE) as SensorManager
    }

    override fun onStart() {
        super.onStart()
        arguments?.getInt(SensorDetailsActivity.SENSOR_TYPE)?.also {
            sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(it),
                SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onStop() {
        super.onStop()
        sensorManager.unregisterListener(this)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sensor_details, container, false).also {
            mSensorName = it.findViewById(R.id.sensor_name)
            mSensorData = it.findViewById(R.id.sensor_data)
        }
    }

    companion object {
        fun newInstance(sensorType : Int) : SensorDetailsFragment {
            return SensorDetailsFragment().let {
                var bundle = Bundle()
                bundle.putInt(SensorDetailsActivity.SENSOR_TYPE, sensorType)
                it.arguments = bundle
                it
            }
        }
    }
}