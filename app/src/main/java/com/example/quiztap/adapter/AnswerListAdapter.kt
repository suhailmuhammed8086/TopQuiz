package com.example.quiztap.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.quiztap.databinding.AdapterAnswerBinding
import com.example.quiztap.utils.decodeBase64

class AnswerListAdapter(
    private val onAnswerSelected: (String) -> Unit
) : RecyclerView.Adapter<AnswerListAdapter.ViewHolder>() {
    private val answers =  ArrayList<String>()
    class ViewHolder(val binding: AdapterAnswerBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AdapterAnswerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    fun loadAnswers(answers: List<String>) {
        this.answers.clear()
        this.answers.addAll(answers)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return answers.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            tvAnswer.text = answers[position].decodeBase64()
            root.setOnClickListener {
                onAnswerSelected.invoke(answers[holder.adapterPosition])
            }

        }
    }



}