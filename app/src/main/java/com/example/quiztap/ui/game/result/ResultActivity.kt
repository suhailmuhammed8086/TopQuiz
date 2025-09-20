package com.example.quiztap.ui.game.result

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.addListener
import androidx.core.content.FileProvider
import androidx.core.graphics.BitmapCompat
import com.example.quiztap.R
import com.example.quiztap.adapter.AnswerReviewListAdapter
import com.example.quiztap.adapter.ResultInfoListAdapter
import com.example.quiztap.data.Grade
import com.example.quiztap.databinding.ActivityResultBinding
import com.example.quiztap.model.PlayerModel
import com.example.quiztap.model.ProgressReportModel
import com.example.quiztap.model.QuestionDataSetModel
import com.example.quiztap.ui.game.result.answer.AnswerListActivity
import com.example.quiztap.utils.Utils
import com.example.quiztap.utils.gone
import com.example.quiztap.utils.show
import java.io.File
import java.util.ArrayList

class ResultActivity : AppCompatActivity() ,View.OnClickListener{
    private val viewModel: ResultViewModel by viewModels()
    private lateinit var binding: ActivityResultBinding
    private lateinit var resultInfoAdapter: ResultInfoListAdapter
    private val resultInfo = arrayListOf<ResultInfoListAdapter.ResultInfo>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        getIntentData()
        observeViewModel()
    }

    private fun observeViewModel() {
        with(viewModel) {
            progressReportState.observe(this@ResultActivity) {
                when(it) {
                    is ResultViewModel.ProgressCalculationState.Calculated -> {
                        setAnswerResult(it)
                    }
                    ResultViewModel.ProgressCalculationState.Started -> {}
                }
            }
        }
    }

    private fun initView() {
        resultInfoAdapter = ResultInfoListAdapter(resultInfo)
        binding.rvInfo.adapter = resultInfoAdapter

        binding.tvCheckAnswers.setOnClickListener(this)
        binding.btnShare.setOnClickListener(this)
        binding.btnHome.setOnClickListener(this)
    }

    private fun setAnswerResult(progressCalculationState: ResultViewModel.ProgressCalculationState.Calculated) {

        binding.tvGrade.alpha = 0f
        binding.tvGrade.paint?.apply {
            style = Paint.Style.FILL_AND_STROKE
            strokeWidth = 5f
        }
        binding.tvPercentage.text = ""

        @SuppressLint("SetTextI18n")
        fun animatePercentage(percentage: Int, onEnd: () -> Unit) {
            val animator = ValueAnimator.ofInt(0, percentage)
            animator.addUpdateListener {
                val value = it.animatedValue as Int
                binding.tvPercentage.text = value.toString().plus("%")
            }
            animator.duration = 1000L
            animator.addListener(onEnd = {onEnd()})
            animator.start()
        }

        fun animateGrade(grade: Grade, onEnd: ()-> Unit) {
            val initialScale = 100f
            binding.tvGrade.apply {
                scaleX = 100f
                scaleY = 100f
                alpha = 0f
                text = grade.toText()
                setTextColor(Color.parseColor(grade.getColor()))
            }

            val animator = ValueAnimator.ofFloat(initialScale, 1f)
            animator.addUpdateListener {
                val value = it.animatedValue as Float
                val percentage = 1- (value/initialScale)
                binding.tvGrade.apply {
                    scaleX = value
                    scaleY = value
                    alpha = percentage
                }
            }
            animator.duration = 500L
            animator.addListener(onEnd = {onEnd()})
             animator.start()
        }

        fun loadInfo(firstPlayerData: ProgressReportModel) {
            val gameInfos = arrayListOf<ResultInfoListAdapter.ResultInfo>()

            viewModel.gameData?.let {
                gameInfos.add(ResultInfoListAdapter.ResultInfo(R.drawable.ic_question, "Questions" , it.questionCount.toString()))
                gameInfos.add(ResultInfoListAdapter.ResultInfo(it.category?.icon ?: R.drawable.ic_category, "Category" , it.category?.getDisplayName()?:"Mixed"))
                gameInfos.add(ResultInfoListAdapter.ResultInfo(R.drawable.ic_difficulty, "Difficulty" , it.difficulty.getDisplayName()))
                gameInfos.add(ResultInfoListAdapter.ResultInfo(R.drawable.ic_correct, "Correct" , firstPlayerData.score.toString()))
                gameInfos.add(ResultInfoListAdapter.ResultInfo(R.drawable.ic_wrong, "Wrong" , (it.questionCount - firstPlayerData.score).toString()))
                gameInfos.add(ResultInfoListAdapter.ResultInfo(R.drawable.ic_time_settings, "Time Mode" ,it.timeSetting.getDisplayName()))
                if (it.timeInSec > 0) {
                    gameInfos.add(ResultInfoListAdapter.ResultInfo(R.drawable.ic_time_limit, "Time Limit" ,Utils.getFormattedSeconds(it.timeInSec)))
                }

                val timeTaken = viewModel.playerData.firstOrNull()?.totalTimeTaken ?:0
                if (timeTaken>0) {
                    gameInfos.add(ResultInfoListAdapter.ResultInfo(R.drawable.ic_time_taken, "Time Taken" ,Utils.getFormattedSeconds(timeTaken)))
                }
            }

            this.resultInfo.addAll(gameInfos)
            resultInfoAdapter.notifyDataSetChanged()
        }

        val firstPlayerData = progressCalculationState.progressReports.first()
        animatePercentage(firstPlayerData.percentage) {
            animateGrade(firstPlayerData.grade) {
                loadInfo(firstPlayerData)

                binding.tvCheckAnswers.show()
                binding.btnHome.show()
                binding.btnShare.show()
            }
        }

        // Preparing answer data
        viewModel.gameData?.let { questionData ->
            viewModel.playerData?.firstOrNull()?.let { answerData ->
                prepareAnswerData(firstPlayerData, questionData, answerData)
            }

        }
    }

    private fun prepareAnswerData(
        progressData: ProgressReportModel,
        questionData: QuestionDataSetModel,
        answerData: PlayerModel
    ) {
        val correctAnswerCount = progressData.score
        val wrongAnswerCount = questionData.questionCount - correctAnswerCount
        val answerReviewList = questionData.questions.map {
            AnswerReviewListAdapter.AnswerReviewModel(
                question = it.question,
                correctAnswer = it.correctAnswer,
                userAnswer = answerData.answers.get(it.id) ?: ""
            )
        }
        viewModel.answerReviewData = AnswerListActivity.AnswerData(correctAnswerCount, wrongAnswerCount, answerReviewList)
    }

    private fun getIntentData() {
        val questionData = intent?.getParcelableExtra<QuestionDataSetModel>(ARG_QUESTION_DATA_SET)
        val players = intent?.getParcelableArrayListExtra<PlayerModel>(ARG_PLAYERS_DATA) ?: emptyList()
        if (questionData != null) {
            viewModel.setGameData(questionData, players)
        }
        Log.e("TAG", "getIntentData: $players")
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvCheckAnswers -> {
                viewModel.answerReviewData?.let { AnswerListActivity.start(this, it) }
            }
            R.id.btnShare -> {
                shareResult()
            }
            R.id.btnHome -> {
                finish()
            }
        }
    }



    private fun getShareContent(): String {

        fun getDialog(percentage: Int, category: String) : String{
            val percentageTag = "P!*"
            val categoryTag = "C%^"
            val template = listOf(
                "Hey, look at this! I scored $percentageTag in the $categoryTag quiz.",
                "Check it out — I just got $percentageTag on the $categoryTag quiz!",
                "I scored $percentageTag in the $categoryTag quiz, thought you’d like to see this!",
                "Look! I managed to get $percentageTag on the $categoryTag quiz.",
                "Just finished the $categoryTag quiz — got $percentageTag!",
                "Proud moment: I scored $percentageTag in the $categoryTag quiz \uD83D\uDE03",
            ).random()
            return template
                .replace(percentageTag, percentage.toString())
                .replace(categoryTag, category)
        }


        val progressData = viewModel.progressReportState.value
        if (progressData is ResultViewModel.ProgressCalculationState.Calculated) {
            viewModel.gameData?.let {
                return getDialog(progressData.progressReports.first().percentage, it.category?.getDisplayName() ?: "Mixed")
            }
        }

        return ""
    }


    private fun shareResult() {
        with(binding) {
            val extraViews = listOf(tvCheckAnswers,btnShare,btnHome)
            extraViews.forEach { it.gone() }
            val bitmap = Bitmap.createBitmap(scrollView.width, scrollView.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            binding.scrollView.draw(canvas)
            extraViews.forEach { it.show() }
            val file = File.createTempFile("Result",".jpg")
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,file.outputStream())

             val uri = FileProvider.getUriForFile(
                this@ResultActivity,
                "${packageName}.fileprovider", // defined in manifest
                file
            )



            val content = getShareContent()

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, uri)   // image
                putExtra(Intent.EXTRA_TEXT, content)         // text
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(shareIntent, "Share via"))
        }
    }

    companion object {

        private const val ARG_QUESTION_DATA_SET = "ARG_QUESTION_DATA_SET"
        private const val ARG_PLAYERS_DATA = "ARG_PLAYERS_DATA"
        fun start(context: Context, gameData: QuestionDataSetModel?, playersData: List<PlayerModel>) {
            val starter = Intent(context, ResultActivity::class.java)
                .putExtra(ARG_QUESTION_DATA_SET, gameData)
                .putParcelableArrayListExtra(ARG_PLAYERS_DATA, ArrayList(playersData))
            context.startActivity(starter)

        }
    }
}