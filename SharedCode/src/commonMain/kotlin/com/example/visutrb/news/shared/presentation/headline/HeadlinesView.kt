package com.example.visutrb.news.shared.presentation.headline

import com.example.visutrb.news.shared.entity.ArticleListResponse
import com.example.visutrb.news.shared.presentation.BaseView

interface HeadlinesView : BaseView {

    fun onResponse(response: ArticleListResponse)

    fun onLastPageLoaded()
}
