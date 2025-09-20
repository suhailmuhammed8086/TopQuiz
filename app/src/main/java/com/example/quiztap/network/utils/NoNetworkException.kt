package com.example.quiztap.network.utils

import java.io.IOException

class NoNetworkException: IOException() {
    companion object {
        const val NO_NETWORK_CONNECTION_ERROR_CODE = -500
    }
}