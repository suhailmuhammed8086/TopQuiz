package com.example.quiztap.ui.game.result.answer

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.quiztap.adapter.AnswerReviewListAdapter
import com.example.quiztap.components.MultiOptionView
import com.example.quiztap.data.TimeSetting
import com.example.quiztap.databinding.ActivityAnswerListBinding
import com.example.quiztap.ui.game.result.answer.AnswerListActivity.AnswerListType.*
import com.example.quiztap.utils.getParcelableExtraCompact
import kotlinx.parcelize.Parcelize

class AnswerListActivity : AppCompatActivity() {
    companion object {

        private const val ANSWER_DATA = "answer_data"

        @JvmStatic
        fun start(context: Context, answerData: AnswerData) {
            val starter = Intent(context, AnswerListActivity::class.java)
            starter.putExtra(ANSWER_DATA, answerData)
            context.startActivity(starter)
        }
    }

    private lateinit var binding: ActivityAnswerListBinding
    private val viewModel: AnswerListViewModel by viewModels()
    private var answerReviewListAdapter : AnswerReviewListAdapter? = null
    private var answerReviewList = arrayListOf<AnswerReviewListAdapter.AnswerReviewModel>()

    enum class AnswerListType(val id: Int) {
        ALL(1), WRONG_ONLY(2);

        companion object {
            fun parseFromId(id: Int): AnswerListType {
                return when (id) {
                    ALL.id -> ALL
                    WRONG_ONLY.id -> WRONG_ONLY
                    else -> ALL
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAnswerListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getIntentData()
        initView()
        observeViewModel()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeViewModel() {
        with(viewModel) {
            answerListType.observe(this@AnswerListActivity) {
                binding.answerListType.setSelectedOption(it.id)
               val answers = getAnswers(it)
                answerReviewList.clear();answerReviewList.addAll(answers)
                answerReviewListAdapter?.notifyDataSetChanged()
            }
        }
    }

    private fun getIntentData() {
        viewModel.answerListData = intent?.getParcelableExtraCompact(ANSWER_DATA, AnswerData::class.java)
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        answerReviewListAdapter = AnswerReviewListAdapter(answerReviewList)
        binding.rvAnswers.adapter = answerReviewListAdapter

        binding.answerListType.loadOptions(
            MultiOptionView.Option(ALL.id,"All"),
            MultiOptionView.Option(WRONG_ONLY.id,"Wrong"),
        ).setOnOptionSelectedListener { option ->
            val selectedOption = AnswerListType.parseFromId(option.id)
            viewModel.setAnswerListType(selectedOption)
        }

        viewModel.answerListData?.let {
            binding.tvCorrectAnswerCount.text = it.correctAnswerCount.toString()
            binding.tvWrongAnswerCount.text = it.wrongAnswerCount.toString()
        }

        binding.btnBack.setOnClickListener { finish() }
    }


    @Parcelize
    data class AnswerData(
        val correctAnswerCount: Int,
        val wrongAnswerCount: Int,
        val answerData: List<AnswerReviewListAdapter.AnswerReviewModel>
    ): Parcelable {}

}