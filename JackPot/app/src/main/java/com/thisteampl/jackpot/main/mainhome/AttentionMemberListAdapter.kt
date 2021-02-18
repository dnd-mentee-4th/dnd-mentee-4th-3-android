package com.thisteampl.jackpot.main.mainhome

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.thisteampl.jackpot.R
import com.thisteampl.jackpot.main.projectdetail.ProjectViewDetail
import java.util.*


// 주목받는 멤버 어댑터(연결 구간)
class AttentionMemberListAdapter(val attentionmemberlist: ArrayList<AttentionMemberList> ?= null): RecyclerView.Adapter<AttentionMemberListAdapter.ProjectView>() {

    class ProjectView(itemview: View):RecyclerView.ViewHolder(itemview){
        val imageview = itemView.findViewById<ImageView>(R.id.main_attention_imageview)
        val member_name = itemView.findViewById<TextView>(R.id.main_attentionmember_title_textview)
        val position = itemView.findViewById<TextView>(R.id.main_attentionproject_content_textview)


    }

    // onCreateViewHolder : ViewHolder와 Layout 파일을 연결해주는 역할
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectView {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.main_attentionmember_list,parent,false)

        return ProjectView(view)
    }

    override fun getItemCount(): Int {
        if (attentionmemberlist != null) {
            return attentionmemberlist.size
        }else{
            return 0
        }
    }

    // onBindViewHolder : 생성된 ViewHolder에 바인딩 해주는 함수
    override fun onBindViewHolder(holder: ProjectView, position: Int) {
        holder.imageview.setImageResource(attentionmemberlist!!.get(position).memberiamge)
        holder.member_name.text = attentionmemberlist.get(index = position).attention_member_name
        holder.position.text = attentionmemberlist.get(index = position).attentionmember_recruitment_position



        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ProjectViewDetail::class.java)
            intent.putExtra("project", holder.member_name.text.toString())
            intent.putExtra("position",holder.position.text.toString())

            holder.itemView.context.startActivity(intent)

        }

    }
}