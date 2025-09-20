package com.example.quiztap.adapter

import android.content.res.ColorStateList
import android.graphics.Paint
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.quiztap.R
import com.example.quiztap.databinding.AdapterAnswerReviewListBinding
import com.example.quiztap.utils.decodeBase64
import com.example.quiztap.utils.gone
import com.example.quiztap.utils.show
import kotlinx.parcelize.Parcelize

class AnswerReviewListAdapter(
    private val answers :  ArrayList<AnswerReviewModel>
) : RecyclerView.Adapter<AnswerReviewListAdapter.ViewHolder>() {

    class ViewHolder(val binding: AdapterAnswerReviewListBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.tvUserAnswerValue.paintFlags = binding.tvUserAnswerValue.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AdapterAnswerReviewListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }



    override fun getItemCount(): Int {
        return answers.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            val item = answers[position]
            tvQuestion.text = item.question.decodeBase64()
            if (item.isCorrect()) {
                tvUserAnswerValue.gone()
                tvUserAnswerValue.text = ""
                ivQuestion.setImageResource( R.drawable.ic_correct)
            } else {
                tvUserAnswerValue.show()
                tvUserAnswerValue.text = item.userAnswer.decodeBase64()
                ivQuestion.setImageResource(R.drawable.ic_wrong)
            }
            tvCorrectAnswerValue.text = item.correctAnswer.decodeBase64()
        }
    }



    @Parcelize
    data class AnswerReviewModel(
        val question: String,
        val correctAnswer: String,
        val userAnswer: String
    ): Parcelable {

        fun isCorrect():Boolean {
            return correctAnswer == userAnswer
        }
    }

}