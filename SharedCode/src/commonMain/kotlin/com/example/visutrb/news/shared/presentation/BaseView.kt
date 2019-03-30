package com.example.visutrb.news.shared.presentation

interface BaseView {

    fun onLoad()

    fun onError(e: Exception)
}
