package com.teamfopo.fopo.fragments


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.teamfopo.fopo.FriendInformActivity
import com.teamfopo.fopo.MainActivity
import com.teamfopo.fopo.R
import com.teamfopo.fopo.ViewActivity
import com.teamfopo.fopo.databinding.ItemMemberBinding
import com.teamfopo.fopo.module.FriendsVO
import com.teamfopo.fopo.module.modFriendProcess

class FriendListActivity : Fragment() {
    lateinit var memberlist: List<FriendsVO>

    lateinit var viewRoot: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewRoot =  inflater.inflate(R.layout.content_friend_list, container, false)

        initFragment()

        return viewRoot
    }

    private fun initFragment(){
        var mFriendAdd = MainActivity.mMenu.findItem(R.id.action_friend_add).also {
            it.setVisible(true)
        }

        var getFriendList = modFriendProcess().getFriends()
        memberlist = getFriendList.execute().get()

        var txtFriendCount: TextView = viewRoot.findViewById(R.id.txtFriendCount)
        txtFriendCount.setText("친구 " + memberlist.size + "명")

         var mRecyclerView: RecyclerView = viewRoot.findViewById(R.id.recycler_view)
                mRecyclerView.apply {
                    layoutManager = LinearLayoutManager(activity)
                    adapter = MemberAdapter(memberlist) { member ->
                        Toast.makeText(context,"$member", Toast.LENGTH_SHORT).show()
                        val i = Intent(super.getContext(), FriendInformActivity::class.java)
                        i.putExtra("m_select", member.mem_no)
                        startActivityForResult(i, 1)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        var mFriendAdd = MainActivity.mMenu.findItem(R.id.action_friend_add)
        mFriendAdd.setVisible(false)
    }
}

class MemberAdapter(var items: List<FriendsVO>,
                    private val clickListener: (member: FriendsVO) -> Unit) :
    RecyclerView.Adapter<MemberAdapter.MemberViewHolder>() {

    class MemberViewHolder(val binding: ItemMemberBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberAdapter.MemberViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_member, parent, false)
        val btnRemoveFriend: Button = view.findViewById(R.id.btnRemoveFriend)
        val viewHolder = MemberViewHolder(ItemMemberBinding.bind(view))

        view.setOnClickListener{
            clickListener.invoke(items[viewHolder.adapterPosition])
        }

        btnRemoveFriend.setOnClickListener {
            var fri_id: String = "${items[viewHolder.adapterPosition].mem_no}"
            var removeFriend = modFriendProcess().removeFriend()
            var result = removeFriend.execute("$fri_id").get()

            if (result.equals("success")) {
                Toast.makeText(MainActivity.mContext,"${items[viewHolder.adapterPosition].mem_nick} 언팔로우", Toast.LENGTH_SHORT).show()

                var getFriendList = modFriendProcess().getFriends()
                items = getFriendList.execute().get()

                this.notifyDataSetChanged()
            }
        }

        return viewHolder
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: MemberAdapter.MemberViewHolder, position: Int) {
        holder.binding.member = items[position]
    }
}