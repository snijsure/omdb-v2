package com.snijsure.omdbsearch.data

import kotlin.coroutines.CoroutineContext

data class CoroutinesContextProvider(val main: CoroutineContext, val io: CoroutineContext)
