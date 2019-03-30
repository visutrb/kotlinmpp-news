package com.example.visutrb.news.shared.util

import kotlinx.coroutines.CoroutineDispatcher

internal expect object PlatformDispatchers {

    val Main: CoroutineDispatcher
}
