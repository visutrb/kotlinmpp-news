package com.example.visutrb.news.shared.presentation.headline

import com.example.visutrb.news.shared.api.NewsApi
import com.example.visutrb.news.shared.presentation.BasePresenter
import kotlinx.coroutines.CoroutineDispatcher

class HeadlinesPresenter(
    dispatcher: CoroutineDispatcher,
    private val newsApi: NewsApi
) : BasePresenter<HeadlinesView>(dispatcher) {

    fun loadHeadlineAsync() = dispatchAsync {
        view?.onLoad()
        val articles = newsApi.getTopHeadlines()
        view?.onResponse(articles)
    }
}
