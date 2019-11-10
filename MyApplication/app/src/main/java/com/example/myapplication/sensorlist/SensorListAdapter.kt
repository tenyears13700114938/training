package com.example.myapplication.sensorlist

import android.hardware.Sensor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class SensorListAdapter @Inject constructor(val layoutInflater: LayoutInflater): RecyclerView.Adapter<SensorListAdapter.SensorItem>(){
    val sensorList = mutableListOf<Sensor>()
    val clickPublishSubject : PublishSubject<Sensor> = PublishSubject.create()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SensorItem {
        return SensorItem(layoutInflater.inflate(R.layout.sensor_item, parent, false))
    }

    override fun getItemCount(): Int {
        return sensorList.size
    }

    override fun onBindViewHolder(holder: SensorItem, position: Int) {
        holder.bind(sensorList[position])
        holder.itemView.setOnClickListener {
            clickPublishSubject.onNext(sensorList[position])
        }
    }

    override fun setHasStableIds(hasStableIds: Boolean) {
        super.setHasStableIds(hasStableIds)
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    fun setSensors(sensors : List<Sensor>) {
        sensorList.clear()
        sensorList.addAll(sensors)
        notifyDataSetChanged()
    }

    class SensorItem(item : View) : RecyclerView.ViewHolder(item){
        var name : TextView = itemView.findViewById(R.id.sensor_name)
        var description : TextView = itemView.findViewById(R.id.sensor_description)

        fun bind(sensor : Sensor){
            name.text = sensor.name
            description.text = sensor.toString()
        }
    }
}