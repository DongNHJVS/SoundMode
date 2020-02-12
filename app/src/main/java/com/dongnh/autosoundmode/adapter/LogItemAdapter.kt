package com.dongnh.autosoundmode.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dongnh.autosoundmode.R
import com.dongnh.autosoundmode.databinding.ItemLogBinding
import com.dongnh.autosoundmode.db.entity.LogEntity
import com.dongnh.autosoundmode.viewmodel.LogItemViewModel

class LogItemAdapter : RecyclerView.Adapter<LogItemAdapter.ViewHolder>() {

    private var dataList: ArrayList<LogEntity> = arrayListOf()

    // Create view of Adapter
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemLogBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_log, parent, false)
        return ViewHolder(binding)
    }

    // Bind data
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    // Get item count
    override fun getItemCount(): Int {
        return dataList.size
    }

    /**
     * Set new data for adapter
     */
    fun setDataForAdapter(arrayList: List<LogEntity>) {
        dataList.clear()
        dataList.addAll(arrayList)
        this@LogItemAdapter.notifyDataSetChanged()
    }

    // Class binding data
    class ViewHolder(private val binding: ItemLogBinding): RecyclerView.ViewHolder(binding.root){
        private val viewModel = LogItemViewModel()

        fun bind(logEntity: LogEntity) {
            binding.viewModel = viewModel
            viewModel.binding(logEntity)
        }
    }
}