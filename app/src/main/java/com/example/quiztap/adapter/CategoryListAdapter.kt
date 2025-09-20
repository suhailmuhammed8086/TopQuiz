package com.example.quiztap.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.quiztap.databinding.AdapterCategoryListBinding
import com.example.quiztap.network.model.response.CategoryModel

class CategoryListAdapter(
    private val categories: List<CategoryModel>,
    private val onCategorySelected: (CategoryModel) -> Unit
) : RecyclerView.Adapter<CategoryListAdapter.ViewHolder>() {


    class ViewHolder(val binding: AdapterCategoryListBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AdapterCategoryListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            tvCategoryName.text = categories[position].getDisplayName()
            ivCategoryLogo.setImageResource(categories[position].icon)
            root.setOnClickListener {
                onCategorySelected.invoke(categories[holder.adapterPosition])
            }
        }
    }


 }