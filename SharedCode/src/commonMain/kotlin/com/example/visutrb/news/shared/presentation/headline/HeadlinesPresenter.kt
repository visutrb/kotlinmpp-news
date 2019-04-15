package com.example.visutrb.news.shared.presentation.headline

import com.example.visutrb.news.shared.api.NewsApi
import com.example.visutrb.news.shared.presentation.BasePresenter
import kotlinx.coroutines.CoroutineDispatcher
import kotlin.math.ceil

class HeadlinesPresenter(
    dispatcher: CoroutineDispatcher,
    private val newsApi: NewsApi
) : BasePresenter<HeadlinesView>(dispatcher) {

    private val pageSize = 20

    var currentPage = 0
        private set

    var totalPages = 0
        private set

    val isFirstPage: Boolean
        get() = currentPage == 1

    val isLastPage: Boolean
        get() = currentPage == totalPages

    fun reloadHeadlineAsync() = dispatchAsync {
        currentPage = 0
        totalPages = 0
        loadHeadlineAsync()
    }

    fun loadHeadlineAsync() = dispatchAsync {
        val view = view ?: return@dispatchAsync
        currentPage++
        view.onLoad()
        val response = newsApi.getTopHeadlines(currentPage, pageSize)
        if (totalPages == 0) {
            totalPages = response.totalResults?.let { ceil(it / pageSize.toDouble()).toInt() } ?: 0
        }
        view.onResponse(response)
        if (isLastPage) view.onLastPageLoaded()
    }
}
