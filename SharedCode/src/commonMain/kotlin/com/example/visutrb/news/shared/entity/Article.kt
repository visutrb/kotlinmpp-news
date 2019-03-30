package com.example.visutrb.news.shared.entity

import kotlinx.serialization.Serializable

@Serializable
data class Article(
    var author: String?,
    var title: String?,
    var description: String?,
    var url: String?,
    var urlToImage: String?,
    var publishedAt: String?,
    var content: String?,
    var source: Source?
)
