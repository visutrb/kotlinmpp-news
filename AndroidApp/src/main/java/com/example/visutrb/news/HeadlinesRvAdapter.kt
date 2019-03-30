package com.example.visutrb.news

import android.support.v7.widget.RecyclerView
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.visutrb.news.shared.entity.Article
import java.text.SimpleDateFormat
import java.util.*

class HeadlinesRvAdapter : RecyclerView.Adapter<HeadlinesRvAdapter.HeadlineItemViewHolder>() {

    var articles = listOf<Article>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int {
        return articles.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_ENLARGED else VIEW_TYPE_NORMAL
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeadlineItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val layoutId =
            if (viewType == VIEW_TYPE_ENLARGED) R.layout.item_enlarged_headline
            else R.layout.item_normal_headline
        val itemView = inflater.inflate(layoutId, parent, false)
        return HeadlineItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HeadlineItemViewHolder, position: Int) {
        val article = articles[position]
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

    class HeadlineItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    companion object {
        private const val VIEW_TYPE_ENLARGED = 0
        private const val VIEW_TYPE_NORMAL = 1
    }
}
