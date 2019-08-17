package com.teamfopo.fopo

import android.content.Intent
import android.databinding.BindingAdapter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.teamfopo.fopo.databinding.ItemFriendArticleBinding
import com.teamfopo.fopo.module.modBoardProcess
import com.teamfopo.fopo.module.modFriendProcess
import com.teamfopo.fopo.nodes.FriendArticle
import kotlinx.android.synthetic.main.activity_friend_inform.*

class FriendInformActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_inform)

        var i = getIntent()
        var mem_no = i.getIntExtra("m_select", 0)
        var mem_nick = i.getStringExtra("mem_nick")

        var articleList = arrayListOf<FriendArticle>()

        var bm: Bitmap
        var getFriendArticles = modBoardProcess().getFriendArticles()
        var temp = getFriendArticles.execute("${mem_no.toString()}").get()

        for (i in 0..temp.size - 1) {
            var getBoardImage = modBoardProcess().GetImage()
            bm = getBoardImage.execute(temp.get(i).file_no).get()

            articleList.add(FriendArticle(temp.get(i).brd_no, bm))
        }

        txtFriendInformTitle.text = mem_nick + "님의 포토존"

        var mRecyclerView: RecyclerView = findViewById(R.id.rcvFriendInform)
        mRecyclerView.apply {
            layoutManager = GridLayoutManager(this@FriendInformActivity,3)
            adapter = FriendArticleAdapter(articleList) { member ->
                val i = Intent(context, ViewActivity::class.java)
                i.putExtra("m_select", member.art_no)
                startActivityForResult(i, 1)
            }
        }
    }
}

class FriendArticleAdapter(var items: List<FriendArticle>,
                    private val clickListener: (member: FriendArticle) -> Unit) :
    RecyclerView.Adapter<FriendArticleAdapter.FriendArticleViewHolder>() {

    class FriendArticleViewHolder(val binding: ItemFriendArticleBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendArticleAdapter.FriendArticleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_friend_article, parent, false)
        val viewHolder = FriendArticleAdapter.FriendArticleViewHolder(ItemFriendArticleBinding.bind(view))

        view.setOnClickListener{
            clickListener.invoke(items[viewHolder.adapterPosition])
        }

        return viewHolder
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: FriendArticleAdapter.FriendArticleViewHolder, position: Int) {
        holder.binding.friend = items[position]
    }
}