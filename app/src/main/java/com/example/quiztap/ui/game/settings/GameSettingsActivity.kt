package com.example.quiztap.ui.game.settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.quiztap.R
import com.example.quiztap.base.QuizTapBaseActivity
import com.example.quiztap.components.LeftRightOptionVIew
import com.example.quiztap.components.MultiOptionView
import com.example.quiztap.data.AnswerType
import com.example.quiztap.data.Difficulty
import com.example.quiztap.data.ResponseState
import com.example.quiztap.data.TimeSetting
import com.example.quiztap.databinding.ActivityGameSettingsBinding
import com.example.quiztap.model.PlayerModel
import com.example.quiztap.network.model.response.CategoryModel
import com.example.quiztap.ui.game.category.CategoryListActivity
import com.example.quiztap.ui.game.game.GameActivity
import com.example.quiztap.utils.setEnableWithAlpha
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameSettingsActivity : QuizTapBaseActivity(), View.OnClickListener {
    private val viewModel: GameSettingViewModel by viewModels()
    private lateinit var binding: ActivityGameSettingsBinding

    private var categoryResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(),::onCategorySelected)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityGameSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initView()
        observeViewModel()
    }

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    private fun observeViewModel() {
        with(viewModel) {
            questionCount.observe(this@GameSettingsActivity) {
//                binding.tvQuestionInput.text = it.count.toString()
//                binding.btnAddNum.setEnableWithAlpha(it.isAddEnabled())
//                binding.btnSubNum.setEnableWithAlpha(it.isSubEnabled())
                binding.questionCountView.apply {
                    setValue(it.count.toString())
                    enableLeftButton(it.isSubEnabled())
                    enableRightButton(it.isAddEnabled())
                }
            }

            difficulty.observe(this@GameSettingsActivity) { value ->
                binding.multiOptionDifficulty.setSelectedOption(value.id)
            }
            timeSetting.observe(this@GameSettingsActivity) { value ->
                binding.multiOptionTimeSetting.setSelectedOption(value.id)
                if (value == TimeSetting.NO_TIME) {
                    binding.timeSettingsView.setOnTouchListener { v, event -> true }
                    binding.timeSettingsView.alpha = 0.4f
                } else {
                    binding.timeSettingsView.setOnTouchListener (null)
                    binding.timeSettingsView.alpha = 1f
                }
                viewModel.loadTime()
            }
            selectedTime.observe(this@GameSettingsActivity) { timeData ->
                Log.e("TAG", "observeViewModel: $timeData", )
                binding.timeSettingsView.apply {
                    setValue(getFormattedSeconds(timeData.timeInSec))
                    enableLeftButton(timeData.isSubEnabled)
                    enableRightButton(timeData.isAddEnabled)
                }
            }
            answerType.observe(this@GameSettingsActivity) { answerType ->
                binding.answerTypeOptionView.setSelectedOption(answerType.id)
//                binding.tvMultipleChoice.isChecked = answerType == AnswerType.ALL || answerType == AnswerType.MULTIPLE_CHOICE
//                binding.tvYesOrNo.isChecked = answerType == AnswerType.ALL || answerType == AnswerType.YES_OR_NO
            }

            category.observe(this@GameSettingsActivity) {
              if (it != null) {
                  binding.tvCategoryInput.text = it.getDisplayName()
                  if (it.icon != -1) {
                      binding.ivCategory.setImageResource(it.icon)
                  }
              } else {
                  binding.tvCategoryInput.text = "Mixed"
                  binding.ivCategory.setImageResource(R.drawable.img_category)

              }
            }

            questionSetState.observe(this@GameSettingsActivity) {
                when (it) {
                    ResponseState.Cancelled -> {}
                    is ResponseState.Failed -> {}
                    ResponseState.Idle -> {}
                    ResponseState.Loading -> {}
                    is ResponseState.Success -> {
                        Log.e("TAG", "observeViewModel: ${  it.response}", )
                        if (it.response != null) {
                            val player = listOf(PlayerModel("Asta"))
                            GameActivity.start(this@GameSettingsActivity, it.response, player)
                        }
                    }
                    is ResponseState.ValidationError -> {}
                }
            }
        }

    }

    private fun initView() {
//        binding.btnAddNum.setOnClickListener(this)
//        binding.btnSubNum.setOnClickListener(this)

//        binding.btnEasy.tag = Difficulty.EASY
//        binding.btnMedium.tag = Difficulty.MEDIUM
//        binding.btnHard.tag = Difficulty.HARD

//        binding.btnEasy.setOnClickListener(this)
//        binding.btnMedium.setOnClickListener(this)
//        binding.btnHard.setOnClickListener(this)
        binding.tvCategoryInput.setOnClickListener(this)


//        binding.cbNoTime.tag = TimeSetting.NO_TIME
//        binding.cbTimePerQs.tag = TimeSetting.TIME_PER_QUESTION
//        binding.cbTimePerSession.tag = TimeSetting.TOTAL_TIME
//        binding.cbNoTime.setOnClickListener(this)
//        binding.cbTimePerQs.setOnClickListener(this)
//        binding.cbTimePerSession.setOnClickListener(this)

//        binding.btnAddTime.setOnClickListener(this)
//        binding.btnSubTime.setOnClickListener(this)
//        binding.tvMultipleChoice.setOnClickListener(this)
//        binding.tvYesOrNo.setOnClickListener(this)
        binding.btStart.setOnClickListener(this)


        binding.multiOptionDifficulty.loadOptions(
            MultiOptionView.Option(Difficulty.EASY.id,"Easy"),
            MultiOptionView.Option(Difficulty.MEDIUM.id,"Medium"),
            MultiOptionView.Option(Difficulty.HARD.id,"Hard"),
        ).setOnOptionSelectedListener { option ->
            val selectedOption = Difficulty.parseFromId(option.id)
            viewModel.setDifficulty(selectedOption)
        }.setSelectedOption(viewModel.getDifficulty().id)

        binding.multiOptionTimeSetting.loadOptions(
            MultiOptionView.Option(TimeSetting.NO_TIME.id,"No time"),
            MultiOptionView.Option(TimeSetting.TIME_PER_QUESTION.id,"Time per question"),
            MultiOptionView.Option(TimeSetting.TOTAL_TIME.id,"Time per session"),
        ).setOnOptionSelectedListener { option ->
            val selectedOption = TimeSetting.parseFromId(option.id)
            viewModel.setTimeSettings(selectedOption)
        }


        binding.answerTypeOptionView.loadOptions(
            MultiOptionView.Option(AnswerType.ALL.id,"Mixed"),
            MultiOptionView.Option(AnswerType.YES_OR_NO.id,"Yer Or No"),
            MultiOptionView.Option(AnswerType.MULTIPLE_CHOICE.id,"Choice"),
        ).setOnOptionSelectedListener { option ->
            val selectedOption = AnswerType.parseFromId(option.id)
            viewModel.setAnswerType(selectedOption)
        }


        binding.questionCountView.setListener(object : LeftRightOptionVIew.Listener {
            override fun onLeftButtonClick() {
                viewModel.subtractQuestionCount()
            }

            override fun onRightButtonClick() {
                viewModel.addQuestionCount()
            }
        })
        binding.timeSettingsView.setListener(object : LeftRightOptionVIew.Listener {
            override fun onLeftButtonClick() {
                viewModel.subTime()
            }

            override fun onRightButtonClick() {
                viewModel.addTime()
            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvCategoryInput -> {
                CategoryListActivity.startForSelection(this, viewModel.getCategory()?.id ?: -1, categoryResultLauncher)
            }
            R.id.btStart -> {
                viewModel.loadQuestions()
            }
        }
    }

    private fun onCategorySelected(result: ActivityResult) {
        val data = result.data
        if (result.resultCode == RESULT_OK && data != null) {
            val categoryId = result.data?.getIntExtra(CategoryListActivity.ARG_CATEGORY_ID, -1) ?: -1
            val categoryName = result.data?.getStringExtra(CategoryListActivity.ARG_CATEGORY_NAME)
            val categoryIcon = result.data?.getIntExtra(CategoryListActivity.ARG_CATEGORY_ICON, -1) ?: -1
            if (categoryId != -1 && !categoryName.isNullOrEmpty()) {
                viewModel.setCategory(CategoryModel(categoryId, categoryName, categoryIcon))
            }

        }

    }

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, GameSettingsActivity::class.java)
            context.startActivity(starter)
        }

    }
}