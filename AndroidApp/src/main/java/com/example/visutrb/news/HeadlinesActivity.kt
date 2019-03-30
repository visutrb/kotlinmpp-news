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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_headlines)

        headlinesRecyclerView.apply {
            adapter = headlinesAdapter
            layoutManager = LinearLayoutManager(this@HeadlinesActivity)
            addItemDecoration(
                DividerItemDecoration(
                    this@HeadlinesActivity,
                    LinearLayoutManager.VERTICAL
                )
            )
        }

        headlinesSwipeRefreshLayout.setOnRefreshListener { headlinesPresenter.loadHeadlineAsync() }

        headlinesPresenter.apply {
            view = this@HeadlinesActivity
            loadHeadlineAsync()
        }
    }

    override fun onLoad() {
        headlinesSwipeRefreshLayout.isRefreshing = true
        Log.d(TAG, "Loading articles.")
    }

    override fun onResponse(response: ArticleListResponse) {
        Log.d(TAG, "Headlines loaded.")
        response.articles?.let { headlinesAdapter.articles = it }
        headlinesSwipeRefreshLayout.isRefreshing = false
    }

    override fun onError(e: Exception) {
        Log.e(TAG, "Failed to load articles.", e)
        Toast.makeText(this, "Failed to load articles", Toast.LENGTH_SHORT).show()
        headlinesSwipeRefreshLayout.isRefreshing = false
    }

    companion object {
        private const val TAG = "HeadlinesActivity"
    }
}
