package com.example.visutrb.news.shared.api

import com.example.visutrb.news.shared.entity.ArticleListResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url

class NewsApi(private val client: HttpClient) {

    suspend fun getTopHeadlines(page: Int, pageSize: Int): ArticleListResponse = client.get {
        url("https://newsapi.org/v2/top-headlines")
        parameter("apiKey", "ca80497618ff4bd4af54c673db01ece8")
        parameter("country", "us")
        parameter("category", "technology")
        parameter("pageSize", pageSize.toString())
        parameter("page", page.toString())
    }
}
