package com.example.visutrb.news

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import com.example.visutrb.news.shared.entity.ArticleListResponse
import com.example.visutrb.news.shared.presentation.PresenterFactory
import com.example.visutrb.news.shared.presentation.headline.HeadlinesView
import kotlinx.android.synthetic.main.activity_headlines.*

class HeadlinesActivity : AppCompatActivity(), HeadlinesView {

    private val headlinesAdapter by lazy { HeadlinesRvAdapter() }
    private val headlinesPresenter by lazy { PresenterFactory.createHeadlinesPresenter() }

    private val statusBarOffset: Int
        get() {
            val scale = resources.displayMetrics.density
            val statusBarHeight = resources.getDimensionPixelSize(
                resources.getIdentifier(
                    "status_bar_height",
                    "dimen",
                    "android"
                )
            )
            // 16dp offset
            return (16 * scale).toInt() + statusBarHeight
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_headlines)

        headlinesAdapter.apply {
            onScrollEnded = { headlinesPresenter.loadHeadlineAsync() }
        }

        headlinesRecyclerView.apply {
            adapter = headlinesAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        }

        headlinesSwipeRefreshLayout.apply {
            setProgressViewOffset(true, 0, statusBarOffset)
            setOnRefreshListener { headlinesPresenter.loadHeadlineAsync(true) }
        }

        headlinesPresenter.apply {
            view = this@HeadlinesActivity
            loadHeadlineAsync()
        }
    }

    override fun onLoad() {
        if (headlinesPresenter.currentPage == 1) headlinesSwipeRefreshLayout.isRefreshing = true
        else headlinesAdapter.isLoadingNextPage = true
        Log.d(TAG, "Loading articles.")
    }

    override fun onResponse(response: ArticleListResponse) {
        val articles = response.articles
        if (headlinesPresenter.currentPage == 1) {
            articles?.let { headlinesAdapter.replaceAll(it) }
            headlinesSwipeRefreshLayout.isRefreshing = false
        } else {
            articles?.let { headlinesAdapter.addAll(it) }
            headlinesAdapter.isLoadingNextPage = false
        }
        Log.d(TAG, "Headlines loaded.")
    }

    override fun onError(e: Exception) {
        Toast.makeText(this, "Failed to load articles", Toast.LENGTH_SHORT).show()
        headlinesSwipeRefreshLayout.isRefreshing = false
        headlinesAdapter.isLoadingNextPage = false
        Log.e(TAG, "Failed to load articles.", e)
    }

    companion object {
        private const val TAG = "HeadlinesActivity"
    }
}
