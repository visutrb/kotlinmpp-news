package com.example.visutrb.news.shared.entity

import kotlinx.serialization.Serializable

@Serializable
data class ArticleListResponse(
    var status: String?,
    var totalResults: Int?,
    var articles: List<Article>?
)
