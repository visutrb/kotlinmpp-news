package com.example.visutrb.news

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.visutrb.news.shared.entity.Article
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*

class HeadlinesRvAdapter : RecyclerView.Adapter<HeadlinesRvAdapter.HeadlineItemViewHolder>() {

    private lateinit var recyclerViewRef: WeakReference<RecyclerView>

    var onScrollEnded: (() -> Unit)? = null

    var isLastPage: Boolean = false
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private val articleList = mutableListOf<Article>()

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.addOnScrollListener(OnScrollEndedListener())
        recyclerViewRef = WeakReference(recyclerView)
    }

    override fun getItemCount(): Int {
        return if (!isLastPage && articleList.size != 0) articleList.size + 1 else articleList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position == 0 -> VIEW_TYPE_ENLARGED_HEADLINE
            position == itemCount - 1 && !isLastPage -> VIEW_TYPE_PROGRESS
            else -> VIEW_TYPE_NORMAL_HEADLINE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeadlineItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val layoutId = when (viewType) {
            VIEW_TYPE_ENLARGED_HEADLINE -> R.layout.item_enlarged_headline
            VIEW_TYPE_NORMAL_HEADLINE -> R.layout.item_normal_headline
            VIEW_TYPE_PROGRESS -> R.layout.item_progress
            else -> throw Exception("Invalid view type")
        }
        val itemView = inflater.inflate(layoutId, parent, false)
        return HeadlineItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HeadlineItemViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_PROGRESS) return
        val article = articleList[position]
        val date: Date = article.publishedAt?.let {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")
            dateFormat.parse(it)
        } ?: Date()

        with(holder.itemView) {
            val imageView = findViewById<ImageView>(R.id.headlineImageView)
            val titleTv = findViewById<TextView>(R.id.headlineTitleTv)
            val sourceTv = findViewById<TextView>(R.id.headlineSourceTv)
            val timeTv = findViewById<TextView>(R.id.headlineTimeTv)

            titleTv.text = article.title
            sourceTv.text = article.author ?: article.source?.name ?: "Unknown"
            timeTv.text = DateUtils.getRelativeTimeSpanString(
                date.time,
                System.currentTimeMillis(),
                DateUtils.SECOND_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_TIME
            )

            article.urlToImage?.let {
                Glide.with(this@with)
                    .load(it)
                    .placeholder(R.drawable.image_placeholder)
                    .into(imageView)
            }
        }
    }

    fun addAll(articles: List<Article>) {
        val insertPosition = articleList.size
        articleList.addAll(articles)
        notifyItemRangeInserted(insertPosition, articles.size)
    }

    fun replaceAll(articles: List<Article>) {
        articleList.apply {
            clear()
            addAll(articles)
        }
        notifyDataSetChanged()
    }

    class HeadlineItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private inner class OnScrollEndedListener : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val lm = recyclerView.layoutManager ?: return

            val lastItemPos = itemCount - 1
            val lastVisibleItemPos = when (lm) {
                is LinearLayoutManager -> lm.findLastCompletelyVisibleItemPosition()
                else -> return
            }

            if (lastItemPos == lastVisibleItemPos) {
                onScrollEnded?.invoke()
            }
        }
    }

    companion object {
        private const val VIEW_TYPE_ENLARGED_HEADLINE = 0
        private const val VIEW_TYPE_NORMAL_HEADLINE = 1
        private const val VIEW_TYPE_PROGRESS = 2
    }
}
