package com.example.quiztap.ui.game.game

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quiztap.data.TimeSetting
import com.example.quiztap.data.TimeSetting.*
import com.example.quiztap.model.PlayerModel
import com.example.quiztap.model.QuestionDataSetModel
import com.example.quiztap.network.model.response.QuestionSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameViewModel: ViewModel() {
    private var gameData: QuestionDataSetModel? = null
    private val players: ArrayList<PlayerModel> = arrayListOf()
    private var currentUserIndex: Int = 0

    private val _currentQuestion = MutableLiveData<QuestionSet?>()
    val currentQuestion = _currentQuestion

    private val _gameState = MutableLiveData<GameState>()
    val gameState = _gameState

    private var _totalTime = MutableLiveData<Int>()
    val totalTime = _totalTime
    var runTotalTime = true

    private var questionIndex = 0

    fun setGameData(gameData: QuestionDataSetModel?, playersData: List<PlayerModel>) {
        this.gameData = gameData
        players.clear()
        players.addAll(playersData)
        currentUserIndex = 0
        if (players.isNotEmpty()) {
            _gameState.postValue(GameState.PlayerChanged(players[currentUserIndex].userName))
        } else {
            _gameState.postValue(GameState.GameFinished(gameData, playersData))
        }
    }

    fun startGame() {
        _totalTime.value = 0
        questionIndex = 0
        loadQuestion()
        startTime()
    }

    fun getTimeSettings() = gameData?.timeSetting ?: NO_TIME

    fun getTotalQuestionCount(): Int {
        return gameData?.questionCount ?: 0
    }

    fun startTime() {
        runTotalTime = true
        viewModelScope.launch (Dispatchers.Main){
            while (runTotalTime) {
                delay(1000)
                val time = (totalTime.value ?: 0) + 1
                _totalTime.postValue(time)

            }
        }
    }


    fun loadQuestion() {
        gameData?.let { data ->
            if (questionIndex <= data.questionCount) {
                val question = data.questions.getOrNull(questionIndex)
                question?.questionIndex = questionIndex
                _currentQuestion.postValue(question)
            }

        }
    }

    fun loadNextQuestion() {
        questionIndex ++
        if (questionIndex in 0..<(gameData?.questionCount?:0)) {
            loadQuestion()
        } else {
            //Question set completed
            runTotalTime = false // stop timer
            players[currentUserIndex].totalTimeTaken = totalTime.value?:0

            currentUserIndex ++
            if (currentUserIndex < players.size) {
                // move to next player
                _gameState.postValue(GameState.PlayerChanged(players[currentUserIndex].userName))
            } else {
                _gameState.postValue(GameState.GameFinished(
                    gameData ?: return,
                    players
                ))
            }
        }
    }

    fun endCurrentUserSession() {
        questionIndex = gameData?.questionCount?:0
        loadNextQuestion()
    }

    private fun getCurrentQuestion() : QuestionSet? {
        return currentQuestion.value
    }
    fun submitAnswer(answer: String) {
        players.getOrNull(currentUserIndex)?.answers?.set(getCurrentQuestion()?.id ?: -1, answer)
        loadNextQuestion()
    }

    fun onQuizComplete() {

    }

    fun getTimePerQuestion() : Int {
        return with(gameData ?: return -1) {
            if (timeSetting == TIME_PER_QUESTION) {
               timeInSec
            } else {
                -1
            }
        }
    }

    fun getTotalTime() : Int {
        return with(gameData ?: return -1) {
            if (timeSetting == TOTAL_TIME) {
               timeInSec
            } else {
                -1
            }
        }
    }

    sealed class GameState() {
        data object GameReadyToStart: GameState()
        data class PlayerChanged(val name: String): GameState()
        data class GameFinished(val questionSet: QuestionDataSetModel?, val playersData: List<PlayerModel>): GameState()
    }
}