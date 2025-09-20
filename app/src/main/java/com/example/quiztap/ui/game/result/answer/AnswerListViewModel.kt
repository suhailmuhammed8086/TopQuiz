package com.example.quiztap.ui.game.result.answer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.quiztap.adapter.AnswerReviewListAdapter
import com.example.quiztap.ui.game.result.answer.AnswerListActivity.AnswerListType.*

class AnswerListViewModel: ViewModel() {
    var answerListData: AnswerListActivity.AnswerData? = null

    private val _answerListType = MutableLiveData(ALL)
    val answerListType: LiveData<AnswerListActivity.AnswerListType> = _answerListType


    fun setAnswerListType(answerListType: AnswerListActivity.AnswerListType) {
        _answerListType.postValue(answerListType)
    }

    fun getAnswers(listType: AnswerListActivity.AnswerListType): List<AnswerReviewListAdapter.AnswerReviewModel> {
        return when (listType) {
            ALL -> answerListData?.answerData ?: emptyList()
            WRONG_ONLY -> answerListData?.answerData?.filter { it.correctAnswer != it.userAnswer } ?: emptyList()
        }
    }
}