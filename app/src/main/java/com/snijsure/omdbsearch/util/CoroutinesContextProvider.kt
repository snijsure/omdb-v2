package com.snijsure.omdbsearch.util

import kotlin.coroutines.CoroutineContext

data class CoroutinesContextProvider(val main: CoroutineContext, val io: CoroutineContext)
