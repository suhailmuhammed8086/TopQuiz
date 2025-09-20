package com.example.quiztap.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.quiztap.databinding.AdapterCategoryListBinding
import com.example.quiztap.databinding.AdapterResultInfoBinding
import com.example.quiztap.network.model.response.CategoryModel

class ResultInfoListAdapter(
    private val info: List<ResultInfo>,
) : RecyclerView.Adapter<ResultInfoListAdapter.ViewHolder>() {


    class ViewHolder(val binding: AdapterResultInfoBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AdapterResultInfoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return info.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            val item = info[position]
            ivIcon.setImageResource(item.icon)
            tvTitle.text = item.title
            tvValue.text = item.value
        }
    }


    data class ResultInfo(
        val icon: Int,
        val title: String,
        val value: String
    )

}