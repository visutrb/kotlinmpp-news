package com.example.visutrb.news.shared.presentation

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

abstract class BasePresenter<View : BaseView>(private val dispatcher: CoroutineDispatcher) :
    CoroutineScope {

    private val job = Job()

    var view: View? = null

    override val coroutineContext: CoroutineContext
        get() = dispatcher + job

    protected fun dispatchAsync(block: suspend () -> Unit) {
        launch {
            try {
                block()
            } catch (e: Exception) {
                view?.onError(e)
            }
        }
    }

    fun finish() {
        job.cancel()
    }
}
