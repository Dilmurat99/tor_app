package com.uyghar.torapp.ui.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.uyghar.torapp.model.Category

class CatAdapter(val cats: Array<Category>, fragmentManager: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return cats.size
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = CategoryFragment()
        fragment.cat = cats.get(position)
        return fragment
    }

}