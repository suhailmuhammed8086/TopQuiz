package com.example.quiztap.data


sealed class ResponseState<out T> {
    data object Idle: ResponseState<Nothing>()
    data object Loading: ResponseState<Nothing>()
    class Success<R>(val response: R?) : ResponseState<R>()
    class ValidationError(val errorCode: Int,val error: String) : ResponseState<Nothing>()
    class Failed(val error: String, val errorCode: Int) : ResponseState<Nothing>()
    data object Cancelled : ResponseState<Nothing>()

    open fun isSuccess(): Boolean{
        return this is Success
    }

    open fun getSuccessResponse() : T? {
        if (this is Success){
            return response
        }
        return null
    }

    companion object {

    }
}