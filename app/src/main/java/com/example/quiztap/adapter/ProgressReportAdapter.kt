package com.example.quiztap.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.quiztap.databinding.AdapterProgressReportBinding
import com.example.quiztap.model.ProgressReportModel
import com.example.quiztap.utils.Utils

class ProgressReportAdapter(
    private val onProgressReportClick: (ProgressReportModel) -> Unit
) : RecyclerView.Adapter<ProgressReportAdapter.ViewHolder>() {
    private val progressReports =  ArrayList<ProgressReportModel>()
    private var totalQuestionCount = 0
    class ViewHolder(val binding: AdapterProgressReportBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AdapterProgressReportBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    fun loadProgressReports(progressReports: List<ProgressReportModel>, totalQuestions: Int) {
        this.progressReports.clear()
        this.progressReports.addAll(progressReports)
        totalQuestionCount = totalQuestions
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return progressReports.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            val item = progressReports[position]
            tvPlayerName.text = item.player.userName
            tvPlayerPosition.text ="#".plus(item.position)
            tvAnswerCount.text = item.score.toString().plus("/").plus(totalQuestionCount)
            tvPercentage.text = item.percentage.toString().plus("%")
            tvTotalTimeTakenValue.text = Utils.getFormattedSeconds(item.player.totalTimeTaken)
            tvGrade.text = item.grade.toText()
            root.setOnClickListener {
                onProgressReportClick.invoke(item)
            }

        }
    }
}