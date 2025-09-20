package com.example.quiztap.ui.game.result

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quiztap.data.Grade
import com.example.quiztap.model.PlayerModel
import com.example.quiztap.model.ProgressReportModel
import com.example.quiztap.model.QuestionDataSetModel
import com.example.quiztap.ui.game.result.answer.AnswerListActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class ResultViewModel: ViewModel() {

    var gameData: QuestionDataSetModel? = null
    var playerData = arrayListOf<PlayerModel>()
    var answerReviewData :AnswerListActivity.AnswerData? = null

    private val _progressReportState = MutableLiveData<ProgressCalculationState>()
    val progressReportState = _progressReportState

    fun setGameData(gameData: QuestionDataSetModel, playersData: List<PlayerModel> ) {
        this.gameData = gameData
        playerData.clear();playerData.addAll(playersData)
        viewModelScope.launch (Dispatchers.IO){
            calculateMarks(gameData, playersData)
        }

    }

    fun calculateMarks(gameData: QuestionDataSetModel, playersData: List<PlayerModel>) {
        _progressReportState.postValue(ProgressCalculationState.Started)
        gameData.questions.forEach { question ->
            playersData.forEach { player ->
                if (question.correctAnswer == player.answers[question.id]) {
                    player.mark += 1
                }
            }
        }

        val progressReports = playersData.map {
            val percentage = ((it.mark.toFloat()/ gameData.questionCount.toFloat()) * 100).roundToInt().coerceIn(0,100)
            ProgressReportModel(
                it,
                it.mark,
                percentage,
                Grade.getGrade(percentage),
                0
            )
        }
        Log.e("TAG", "calculateMarks: ${progressReports}", )
        _progressReportState.postValue(ProgressCalculationState.Calculated(progressReports, gameData.questionCount))
    }


    sealed class ProgressCalculationState {
        data object Started: ProgressCalculationState()
        data class Calculated(val progressReports: List<ProgressReportModel>, val totalQuestionCount: Int): ProgressCalculationState()
    }
}