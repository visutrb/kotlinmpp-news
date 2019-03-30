package com.example.visutrb.news.shared.presentation

import com.example.visutrb.news.shared.api.NewsApi
import com.example.visutrb.news.shared.entity.Article
import com.example.visutrb.news.shared.entity.ArticleListResponse
import com.example.visutrb.news.shared.entity.Source
import com.example.visutrb.news.shared.presentation.headline.HeadlinesPresenter
import com.example.visutrb.news.shared.util.PlatformDispatchers
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import kotlinx.serialization.list

object PresenterFactory {

    private val kotlinSerializer
        get() = KotlinxSerializer().apply {
            register(Article.serializer())
            register(Article.serializer().list)
            register(ArticleListResponse.serializer())
            register(Source.serializer())
        }

    private val httpClient
        get() = HttpClient {
            install(JsonFeature) {
                serializer = kotlinSerializer
            }
        }

    fun createHeadlinesPresenter() =
        HeadlinesPresenter(PlatformDispatchers.Main, NewsApi(httpClient))
}

