package com.teamfopo.fopo

import android.databinding.BindingAdapter
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.teamfopo.fopo.databinding.ItemFriendArticleBinding
import com.teamfopo.fopo.nodes.FriendArticle

class FriendInformActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_inform)

        var i = getIntent()
        var mem_no = i.getIntExtra("m_select", 0)

        var articleList = arrayListOf<FriendArticle>()

        //articleList.add(FriendArticle(1, R.drawable.img_logo))
        //articleList.add(FriendArticle(2, R.drawable.img_marker))
        //articleList.add(FriendArticle(3, R.drawable.img_logo))
        //articleList.add(FriendArticle(4, R.drawable.img_marker))
        articleList.add(FriendArticle(4, BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.img_marker)))
        articleList.add(FriendArticle(4, BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.img_marker)))
        articleList.add(FriendArticle(4, BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.img_marker)))
        articleList.add(FriendArticle(4, BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.img_marker)))
        articleList.add(FriendArticle(4, BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.img_marker)))

        var mRecyclerView: RecyclerView = findViewById(R.id.rcvFriendInform)
        mRecyclerView.apply {
            layoutManager = GridLayoutManager(this@FriendInformActivity,3)
            //layoutManager = LinearLayoutManager(this@FriendInformActivity)
            adapter = FriendArticleAdapter(articleList) { member ->
                Toast.makeText(context,"$member", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

class FriendArticleAdapter(var items: List<FriendArticle>,
                    private val clickListener: (member: FriendArticle) -> Unit) :
    RecyclerView.Adapter<FriendArticleAdapter.FriendArticleViewHolder>() {

    companion object {
        /*
        @JvmStatic
        @BindingAdapter("imgRes")
        fun imgload(imageView: ImageView, resid: Int) {
            imageView.setImageResource(resid)
        }
        */
    }

    class FriendArticleViewHolder(val binding: ItemFriendArticleBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendArticleAdapter.FriendArticleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_friend_article, parent, false)
        val viewHolder = FriendArticleAdapter.FriendArticleViewHolder(ItemFriendArticleBinding.bind(view))

        return viewHolder
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: FriendArticleAdapter.FriendArticleViewHolder, position: Int) {
        holder.binding.friend = items[position]
    }
}