package com.uyghar.torapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.GsonBuilder
import com.google.gson.internal.GsonBuildConfig
import com.uyghar.torapp.databinding.FragmentHomeBinding
import com.uyghar.torapp.model.Category
import okhttp3.*
import java.io.IOException
import java.net.URL


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        getRequest()
        return root
    }

    fun getRequest() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(URL("http://uyghurapp.com/wp/wp-json/wp/v2/categories?_fields=id,name"))
            .build()
        client.newCall(request).enqueue(
            object: Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    val json_str = response.body?.string()
                    val gson = GsonBuilder().create()
                    val cats_array = gson.fromJson(json_str, Array<Category>::class.java)
                    activity?.runOnUiThread {
                        binding.viewPager.adapter = CatAdapter(cats_array, activity?.supportFragmentManager!!, lifecycle)
                        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
                            tab.text = cats_array.get(position).name
                        }.attach()
                    }
                }

            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}