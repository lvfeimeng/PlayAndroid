package zqx.rj.com.playandroid.article.data.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import zqx.rj.com.playandroid.R
import zqx.rj.com.playandroid.article.data.bean.Article

/**
 * author：  HyZhan
 * created： 2018/10/22 20:39
 * desc：    TODO
 */
class ArticleAdapter(layoutId: Int, listData: List<Article>?)
    : BaseQuickAdapter<Article, BaseViewHolder>(layoutId, listData) {

    override fun convert(viewHolder: BaseViewHolder?, article: Article?) {
        viewHolder?.let {
            it.setText(R.id.mTvAuthor, article?.author)
            it.setText(R.id.mTvTitle, article?.title)
            it.setText(R.id.mTvCategory, "${article?.superChapterName}/${article?.chapterName}")
            it.setText(R.id.mTvTime, article?.niceDate)
            it.setImageResource(R.id.mIvLove, isCollect(article))
            it.addOnClickListener(R.id.mIvLove)
        }
    }

    private fun isCollect(article: Article?): Int {
        return article?.let {
            if (it.collect) R.drawable.ic_collected else R.drawable.ic_collect
        } ?: R.drawable.ic_collect
    }
}