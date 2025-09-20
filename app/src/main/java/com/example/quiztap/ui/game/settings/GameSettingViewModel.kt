package com.example.quiztap.ui.game.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quiztap.data.AnswerType
import com.example.quiztap.data.Difficulty
import com.example.quiztap.data.ResponseState
import com.example.quiztap.data.TimeSetting
import com.example.quiztap.model.QuestionDataSetModel
import com.example.quiztap.network.model.request.QuestionSetRequest
import com.example.quiztap.network.model.response.CategoryModel
import com.example.quiztap.network.model.response.QuestionSetResponse
import com.example.quiztap.network.utils.OperationsStateHandler
import com.example.quiztap.repository.QuizTapRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GameSettingViewModel @Inject constructor(
    private val quizTapRepository: QuizTapRepository
): ViewModel() {
    private val _questionCount = MutableLiveData(QuestionCountData(DEFAULT_QUESTION_COUNT))
    val questionCount = _questionCount

    fun addQuestionCount() {
        val currentValue = _questionCount.value?.count ?: DEFAULT_QUESTION_COUNT
        val newValue = currentValue + QUESTION_COUNT_STEP
        if (newValue <= MAXIMUM_QUESTION_COUNT) {
            _questionCount.postValue(QuestionCountData(newValue))
        } else {
            _questionCount.postValue(QuestionCountData(currentValue))
        }
    }
    fun subtractQuestionCount() {
        val currentValue = _questionCount.value?.count ?: DEFAULT_QUESTION_COUNT
        val newValue = currentValue - QUESTION_COUNT_STEP
        if (newValue >= MINIMUM_QUESTION_COUNT) {
            _questionCount.postValue(QuestionCountData(newValue))
        } else {
            _questionCount.postValue(QuestionCountData(currentValue))
        }
    }
    data class QuestionCountData(var count: Int) {
        fun isAddEnabled():Boolean {
            return count+ QUESTION_COUNT_STEP <= MAXIMUM_QUESTION_COUNT
        }
        fun isSubEnabled():Boolean {
            return count- QUESTION_COUNT_STEP >= MINIMUM_QUESTION_COUNT
        }
    }

    private val _difficulty = MutableLiveData(Difficulty.MEDIUM)
    val difficulty = _difficulty

    fun setDifficulty(difficulty: Difficulty) {
        _difficulty.value = difficulty
    }
    fun getDifficulty(): Difficulty {
        return difficulty.value ?: Difficulty.MEDIUM
    }

    private val _timeSettings = MutableLiveData(TimeSetting.NO_TIME)
    val timeSetting = _timeSettings

    fun setTimeSettings(timeSetting: TimeSetting) {
        _timeSettings.value = timeSetting
    }
    fun getTimeSettings(): TimeSetting {
        return _timeSettings.value?: TimeSetting.NO_TIME
    }

    private val _selectedTime = MutableLiveData<TimerSettingsData>()
    val selectedTime = _selectedTime
    private var lastTimePerQuestion = DEFAULT_TIME_PER_QUESTION
    private var lastTimePerSession = DEFAULT_TIME_PER_SESSION
    data class TimerSettingsData(var timeInSec : Int, val isAddEnabled: Boolean, val isSubEnabled: Boolean)

    fun loadTime() {
        when (timeSetting.value) {
            TimeSetting.NO_TIME, null -> {
                selectedTime.value =
                    TimerSettingsData(0, isAddEnabled = false, isSubEnabled = false)
            }

            TimeSetting.TIME_PER_QUESTION -> {
                selectedTime.value = TimerSettingsData(
                    lastTimePerQuestion,
                    isAddEnabled = true,
                    isSubEnabled = lastTimePerQuestion > MINIMUM_TIME_PER_QUESTION
                )
            }

            TimeSetting.TOTAL_TIME -> {
                selectedTime.value = TimerSettingsData(
                    lastTimePerSession,
                    isAddEnabled = true,
                    isSubEnabled = lastTimePerSession > MINIMUM_TIME_PER_SESSION
                )
            }
        }
    }

    fun addTime() {
        when (timeSetting.value) {
            TimeSetting.TIME_PER_QUESTION -> {
                val lastTime = lastTimePerQuestion
                val newTime = if (lastTime < 30) {
                    lastTime + 5
                } else {
                    lastTime + 30
                }
                selectedTime.value = TimerSettingsData(newTime,true, isSubEnabled = newTime> MINIMUM_TIME_PER_QUESTION)
                lastTimePerQuestion = newTime
            }
            TimeSetting.TOTAL_TIME -> {
                val lastTime = lastTimePerSession
                val newTime = lastTime + 30
                selectedTime.value = TimerSettingsData(newTime,true, isSubEnabled = newTime> MINIMUM_TIME_PER_SESSION)
                lastTimePerSession = newTime
            }
            else -> Unit
        }
    }

    fun subTime() {
        when (timeSetting.value) {
            TimeSetting.TIME_PER_QUESTION -> {
                val lastTime = lastTimePerQuestion
                val newTime = if (lastTime <= 30) {
                    lastTime - 5
                } else {
                    lastTime - 30
                }
                selectedTime.value = TimerSettingsData(newTime,true, isSubEnabled = newTime> MINIMUM_TIME_PER_QUESTION)
                lastTimePerQuestion = newTime
            }
            TimeSetting.TOTAL_TIME -> {
                val lastTime = lastTimePerSession
                val newTime = lastTime - 30
                selectedTime.value = TimerSettingsData(newTime,true, isSubEnabled = newTime> MINIMUM_TIME_PER_SESSION)
                lastTimePerSession = newTime
            }
            else -> Unit
        }
    }


    private val _answerType = MutableLiveData(AnswerType.ALL)
    val answerType = _answerType

    fun setAnswerType(answerType: AnswerType) {
        _answerType.value = answerType
    }

    private val _category = MutableLiveData<CategoryModel?>(null)
    val category = _category

    fun setCategory(categoryModel: CategoryModel?) {
        _category.value = categoryModel
    }
    fun getCategory(): CategoryModel? {
       return _category.value
    }

    private val questionListApiCall = OperationsStateHandler<QuestionSetResponse> (viewModelScope){
        when (it) {
            ResponseState.Cancelled -> _questionSetState.postValue(ResponseState.Cancelled)
            is ResponseState.Failed ->  _questionSetState.postValue(it)
            ResponseState.Idle -> _questionSetState.postValue(ResponseState.Idle)
            ResponseState.Loading -> _questionSetState.postValue(ResponseState.Loading)
            is ResponseState.Success -> {
                if (it.response?.responseCode == 0 ) {
                    val questions = it.response.results
                    questions.forEachIndexed { index, questionSet ->
                        questionSet.id = index
                    }
                    val questionDataSet = QuestionDataSetModel(
                        it.response.results,
                        category = getCategory(),
                        questionCount = questionCount.value?.count ?: DEFAULT_QUESTION_COUNT,
                        answerType = answerType.value?: AnswerType.ALL,
                        difficulty = difficulty.value ?: Difficulty.MEDIUM,
                        timeSetting = timeSetting.value ?: TimeSetting.NO_TIME,
                        timeInSec = selectedTime.value?.timeInSec ?: 0
                    )
                    _questionSetState.postValue(ResponseState.Success(questionDataSet))
                } else {
                    _questionSetState.postValue(ResponseState.Failed("Failed to load",it.response?.responseCode ?:-1))
                }
            }
            is ResponseState.ValidationError -> _questionSetState.postValue(it)
        }

    }

    private val _questionSetState = MutableLiveData<ResponseState<QuestionDataSetModel>>()
    val questionSetState = _questionSetState

    fun loadQuestions() {
        val request = QuestionSetRequest().apply {
            questionCount = this@GameSettingViewModel.questionCount.value?.count ?: DEFAULT_QUESTION_COUNT
            difficulty = this@GameSettingViewModel.difficulty.value ?: Difficulty.MEDIUM
            categoryId = getCategory()?.id ?: -1
            answerType = this@GameSettingViewModel.answerType.value ?: AnswerType.ALL
        }

        questionListApiCall.load {
            quizTapRepository.getQuestionSet(request)
        }
    }





    companion object {
        const val DEFAULT_QUESTION_COUNT = 10
        const val MINIMUM_QUESTION_COUNT = 5
        const val MAXIMUM_QUESTION_COUNT = 30
        const val QUESTION_COUNT_STEP = 5


        const val DEFAULT_TIME_PER_QUESTION = 30 //seconds
        const val MINIMUM_TIME_PER_QUESTION = 10 //seconds
        const val DEFAULT_TIME_PER_SESSION = 60 * 5 // 5 minutes
        const val MINIMUM_TIME_PER_SESSION = 30 // 5 minutes
    }
}