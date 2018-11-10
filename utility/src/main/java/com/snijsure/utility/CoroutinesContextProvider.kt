package com.snijsure.utility

import kotlin.coroutines.CoroutineContext

data class CoroutinesContextProvider(val main: CoroutineContext,
                                     val io: CoroutineContext,
                                     val computation: CoroutineContext)
