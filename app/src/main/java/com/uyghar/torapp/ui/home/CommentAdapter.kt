package com.uyghar.torapp.ui.home

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import com.uyghar.torapp.MainActivity
import com.uyghar.torapp.databinding.CommentItemBinding
import com.uyghar.torapp.model.Comment

class CommentAdapter(val context: Context, val comments: Array<Comment>): BaseAdapter() {
    override fun getCount(): Int {
        return comments.size
    }

    override fun getItem(p0: Int): Any {
        return comments.get(p0)
    }

    override fun getItemId(p0: Int): Long {
        return comments.get(p0)?.id?.toLong() ?: 0
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val binding = CommentItemBinding.inflate( (context as MainActivity).layoutInflater )
        val item = getItem(p0) as Comment
        binding.textName.setText(item.author_name)
        binding.textDate.setText(item.date)
        binding.textComment.setText(item.content?.rendered ?: "")
        return binding.root
    }





}