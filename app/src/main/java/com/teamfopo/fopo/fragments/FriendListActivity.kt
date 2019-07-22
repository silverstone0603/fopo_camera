package com.teamfopo.fopo


import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.teamfopo.fopo.databinding.ItemMemberBinding
import com.teamfopo.fopo.module.FriendsVO
import com.teamfopo.fopo.module.modBoardProcess
import com.teamfopo.fopo.module.modFriendProcess
import com.teamfopo.fopo.nodes.Member
import kotlinx.android.synthetic.main.content_friend_list.*
import kotlinx.android.synthetic.main.item_member.*
import java.util.*

class FriendListActivity : Fragment() {

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
        var mFriendAdd = MainActivity.mMenu.findItem(R.id.action_friend_add)
        mFriendAdd.setVisible(true)

        var getFriendList = modFriendProcess().getFriends()
        var memberlist = getFriendList.execute().get()

        //"친구 " + memberlist.size + "명"
        var txtFriendCount: TextView = viewRoot.findViewById(R.id.txtFriendCount)
        txtFriendCount.setText("친구 " + memberlist.size + "명")

        var mRecyclerView: RecyclerView = viewRoot.findViewById(R.id.recycler_view)
        mRecyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = MemberAdapter(memberlist) { member ->

                //btnRemoveFriend.setOnClickListener {
                //    Toast.makeText(context,"${member.mem_no}", Toast.LENGTH_SHORT).show()
                //}

                Toast.makeText(context,"$member", Toast.LENGTH_SHORT).show()

            }



            //members.add(Member(1,"ddddd",5))
            //mRecyclerView.adapter!!.notifyDataSetChanged()


        }

    }

    override fun onStop() {
        super.onStop()
        var mFriendAdd = MainActivity.mMenu.findItem(R.id.action_friend_add)
        mFriendAdd.setVisible(false)
    }
}

class MemberAdapter(val items: List<FriendsVO>,
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

        //btnRemoveFriend.setOnClickListener{
        //    clickListener.invoke(items[viewHolder.adapterPosition])
        //}


        //btnRemoveFriend.setOnClickListener {
           // clickListener.invoke(items[viewHolder.adapterPosition])

            /*var fri_id: String = "${items[viewHolder.adapterPosition].mem_no}"

            var removeFriend = modFriendProcess().removeFriend()
            var result = removeFriend.execute("$fri_id").get()

            if (result.equals("success")) {
//
            }
            */
        //}

        return viewHolder
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: MemberAdapter.MemberViewHolder, position: Int) {
        holder.binding.member = items[position]
    }
}