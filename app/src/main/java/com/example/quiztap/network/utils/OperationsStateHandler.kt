package com.example.quiztap.network.utils

import com.example.quiztap.data.ResponseState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.util.concurrent.CancellationException

class OperationsStateHandler<T>(
    private val scope: CoroutineScope,
    private var stateUpdateCallback: (state: ResponseState<T>) -> Unit
) {
    private var action: (suspend () -> ResponseState<T>)? = null
    private var apiCallJob: Job? = null
    fun load(action: suspend () -> ResponseState<T>) {
        this.action = action
        apiCallJob = scope.launch(Dispatchers.IO) {
            try {
                stateUpdateCallback(ResponseState.Loading)
                val response = action()
                updateState(response)
            } catch (e: Exception) {
                e.printStackTrace()
                handleExceptionAndUpdateState(e)
            }
        }
    }

    suspend fun loadSuspend(action: suspend () -> ResponseState<T>) {
        this.action = action
        apiCallJob = Job()
        try {
            withContext(Dispatchers.IO + apiCallJob!!) {
                stateUpdateCallback(ResponseState.Loading)
                val response = action()
                updateState(response)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            handleExceptionAndUpdateState(e)
        }
    }

    private fun handleExceptionAndUpdateState(e: Exception) {
        when (e) {
            is ValidationErrorException -> updateState(
                ResponseState.ValidationError(
                    e.errorCode,
                    e.message ?: MSG_VALIDATION_ERROR
                )
            )

            is HttpException -> updateState(
                ResponseState.Failed(
                    e.message ?: MSG_SOMETHING_WENT_WRONG, e.code()
                )
            )

            is CancellationException -> updateState(ResponseState.Cancelled)
            is NoNetworkException -> updateState(
                ResponseState.Failed(
                    MSG_NO_NETWORK,
                    NoNetworkException.NO_NETWORK_CONNECTION_ERROR_CODE
                )
            )

            else -> updateState(ResponseState.Failed(e.message ?: MSG_SOMETHING_WENT_WRONG, 100))
        }
    }

    private fun updateState(state: ResponseState<T>) {
        stateUpdateCallback(state)
    }

    fun retry() {
        if (action != null) {
            load(action!!)
        }
    }

    fun cancel() {
        apiCallJob?.cancel(CancellationException())
        apiCallJob = null
    }


    companion object {
        const val MSG_SOMETHING_WENT_WRONG = "Something went wrong"
        const val MSG_VALIDATION_ERROR = "Validation error"
        const val MSG_NO_NETWORK = "No internet connection"
    }
}