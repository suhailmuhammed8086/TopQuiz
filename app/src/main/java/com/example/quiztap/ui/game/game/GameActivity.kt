package com.example.quiztap.ui.game.game

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.health.connect.datatypes.units.Percentage
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.ContentInfoCompat.Flags
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quiztap.R
import com.example.quiztap.adapter.AnswerListAdapter
import com.example.quiztap.base.QuizTapBaseActivity
import com.example.quiztap.data.TimeSetting
import com.example.quiztap.data.TimeSetting.TOTAL_TIME
import com.example.quiztap.databinding.ActivityGameBinding
import com.example.quiztap.model.PlayerModel
import com.example.quiztap.model.QuestionDataSetModel
import com.example.quiztap.ui.game.result.ResultActivity
import com.example.quiztap.utils.decodeBase64
import com.example.quiztap.utils.getParcelableExtraCompact
import com.example.quiztap.utils.getParcelableListExtraCompact
import com.example.quiztap.utils.gone
import com.example.quiztap.utils.show
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList
import kotlin.math.roundToInt

@AndroidEntryPoint
class GameActivity : QuizTapBaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityGameBinding
    private val viewModel : GameViewModel by viewModels()

    private var countDownTimer : CountDownTimer? = null

    private lateinit var answersAdapter: AnswerListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initView()
        getIntentData()
        observerViewModel()

    }

    @SuppressLint("SetTextI18n")
    private fun observerViewModel() {
        with(viewModel) {
            currentQuestion.observe(this@GameActivity){
                if (it != null) {
                    binding.tvQuestion.text = it.question.decodeBase64()
                    binding.tvQuestionCount.text = (it.questionIndex + 1).toString().plus("/").plus(viewModel.getTotalQuestionCount())
                    val answers = mutableListOf<String>()
                    answers.add(it.correctAnswer)
                    answers.addAll(it.incorrectAnswers)
                    answers.shuffle()
                    answersAdapter.loadAnswers(answers)
                    resetScreenAlpha()

                    val timePerQuestion = getTimePerQuestion()
                    if (timePerQuestion > 0) {
                        startTimer(timePerQuestion)
                        binding.timeIndicator.show()
                    } else {
                        binding.timeIndicator.gone()
                    }
                }
            }
            totalTime.observe(this@GameActivity) {
                if (getTimeSettings() == TOTAL_TIME) {
                    val totalTime = getTotalTime()
                    val remainingTime = totalTime - it
                    val timePercentage = (it.toFloat() / getTotalTime().toFloat())
                    binding.tvTotalTime.backgroundTintList = ColorStateList.valueOf(getProgressColor(timePercentage * 100))
                    binding.tvTotalTime.text = getFormattedSeconds(remainingTime)
                    if (remainingTime<0) {
                        endCurrentUserSession()
                    }
                } else {
                    binding.tvTotalTime.text = getFormattedSeconds(it)
                }

            }

            gameState.observe(this@GameActivity) {
                when(it) {
                    is GameViewModel.GameState.GameFinished -> {
                        stopTimer()
                        ResultActivity.start(this@GameActivity,it.questionSet, it.playersData)
                        finish()
                    }
                    GameViewModel.GameState.GameReadyToStart -> {

                    }
                    is GameViewModel.GameState.PlayerChanged -> {
                        viewModel.startGame()
                    }
                }
            }
        }
    }

    private fun getIntentData() {
        val questionData = intent?.getParcelableExtraCompact<QuestionDataSetModel>(ARG_QUESTION_DATA_SET,QuestionDataSetModel::class.java)
        val players = intent?.getParcelableListExtraCompact<PlayerModel>(ARG_PLAYERS_DATA,PlayerModel::class.java) ?: emptyList()
        viewModel.setGameData(questionData, players)
    }

    private fun initView() {
        answersAdapter = AnswerListAdapter(::onAnswerSelected)
        binding.rvAnswers.apply {
            layoutManager = LinearLayoutManager(this@GameActivity)
            adapter = answersAdapter
        }

        binding.bgView.animateShapes()
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun onAnswerSelected(answer: String) {
        viewModel.submitAnswer(answer)
    }

    private fun setAlphaOfScreen(alpha: Float) {
        binding.tvQuestion.alpha = alpha
    }

    private fun resetScreenAlpha() {
        val animator = ValueAnimator.ofFloat(0f,1f)
        animator.duration = 300L
        animator.addUpdateListener {
            val alpha = it.animatedValue as Float
            binding.tvQuestion.alpha = alpha
            binding.rvAnswers.alpha = alpha
            binding.timeIndicator.alpha = alpha
        }
        animator.start()
    }

    private fun updateProgressIndicator(percentage: Float) {
        val totalWidth = binding.tvQuestion.width
        val percentageWidth = totalWidth * percentage
        val lp = binding.timeIndicator.layoutParams
        lp.width = percentageWidth.roundToInt()
        binding.timeIndicator.layoutParams = lp
        binding.timeIndicator.backgroundTintList = ColorStateList.valueOf(getProgressColor(100 - percentage * 100))
    }

    private fun getProgressColor(percentage: Float) : Int{
        fun blendColor(from:Int, to: Int, ratio: Float): Int {
            val inverseR = 1 - ratio
            val r = Color.red(from) * inverseR + Color.red(to) * ratio
            val g = Color.green(from) * inverseR + Color.green(to) * ratio
            val b = Color.blue(from) * inverseR + Color.blue(to) * ratio
            return Color.rgb(r.toInt(),g.toInt(),b.toInt())
        }

        fun getColor(id: Int): Int {
            return ContextCompat.getColor(this, id)
        }
       return if (percentage < 50) {
           val ratio = percentage/50f
            blendColor(getColor(R.color.progress_one), getColor(R.color.progress_two), ratio)
        } else {
           val ratio = (percentage-50f)/50f
           blendColor(getColor(R.color.progress_two), getColor(R.color.progress_three), ratio)
        }
    }

    override fun onClick(v: View?) {

    }

    private fun startTimer(timeInSec: Int) {
        if (countDownTimer!=null) {
            countDownTimer?.cancel()
        }
        val time = timeInSec* 1000L
        countDownTimer = object : CountDownTimer(time, 100L) {
            var totalTime = time
            override fun onTick(millisUntilFinished: Long) {
                val finishedTime = totalTime - millisUntilFinished
                val percentage = 1-finishedTime.toFloat()/totalTime.toFloat()
                setAlphaOfScreen(percentage)
                updateProgressIndicator(percentage)
            }

            override fun onFinish() {
                viewModel.loadNextQuestion()
            }
        }
        countDownTimer?.start()
    }

    private fun stopTimer() {
        countDownTimer?.cancel()
    }

    companion object {
        private const val ARG_QUESTION_DATA_SET = "ARG_QUESTION_DATA_SET"
        private const val ARG_PLAYERS_DATA = "ARG_PLAYERS_DATA"
        
        @JvmStatic
        fun start(context: Context, questionDataSet: QuestionDataSetModel, players: List<PlayerModel>) {
            val starter = Intent(context, GameActivity::class.java)
                .putExtra(ARG_QUESTION_DATA_SET, questionDataSet)
                .putParcelableArrayListExtra(ARG_PLAYERS_DATA, ArrayList(players))
            context.startActivity(starter)
        }
    }
}