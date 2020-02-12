package com.dongnh.autosoundmode.ultil.binding

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView

@BindingAdapter("adapterRecyclerView")
fun setAdapterRecyclerView(view: RecyclerView, adapter: RecyclerView.Adapter<*>) {
    if (view.visibility == View.VISIBLE) {
        view.adapter = adapter
    }
}