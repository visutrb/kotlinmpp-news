package com.example.visutrb.news.shared.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal actual object PlatformDispatchers {

    actual val Main: CoroutineDispatcher = Dispatchers.Main
}
