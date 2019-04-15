package com.example.visutrb.news.shared.presentation.headline

import com.example.visutrb.news.shared.api.NewsApi
import com.example.visutrb.news.shared.presentation.BasePresenter
import kotlinx.coroutines.CoroutineDispatcher
import kotlin.math.ceil

class HeadlinesPresenter(
    dispatcher: CoroutineDispatcher,
    private val newsApi: NewsApi
) : BasePresenter<HeadlinesView>(dispatcher) {

    var currentPage = 0
        private set

    private var totalPages = 0

    private val pageSize = 20

    fun loadHeadlineAsync(reload: Boolean = false) = dispatchAsync {
        if (reload) {
            currentPage = 0
            totalPages = 0
        } else if (currentPage >= (100 / pageSize) || (currentPage >= totalPages && totalPages != 0)) {
            return@dispatchAsync
        }
        currentPage++
        view?.onLoad()
        val response = newsApi.getTopHeadlines(currentPage, pageSize)
        if (totalPages == 0) {
            totalPages = response.totalResults?.div(pageSize.toDouble())
                ?.let { ceil(it).toInt() } ?: 0
        }
        view?.onResponse(response)
    }
}
