package com.uyghar.torapp.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.GsonBuilder
import com.uyghar.torapp.R
import com.uyghar.torapp.databinding.FragmentCategoryBinding
import com.uyghar.torapp.databinding.FragmentHomeBinding
import com.uyghar.torapp.model.Category
import com.uyghar.torapp.model.Post
import okhttp3.*
import java.io.IOException
import java.net.URL


class CategoryFragment : Fragment() {
    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!
    var cat: Category? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    fun getRequest() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(URL("http://uyghurapp.com/wp/wp-json/wp/v2/posts?categories=${cat?.id}&_fields=id,title"))
            .build()
        client.newCall(request).enqueue(
            object: Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    val json_str = response.body?.string()
                    val gson = GsonBuilder().create()
                    val post_array = gson.fromJson(json_str, Array<Post>::class.java)
                    binding.listView.setOnItemClickListener { adapterView, view, i, l ->
                        val post = post_array.get(i)
                        val bundle = Bundle()
                        bundle.putParcelable("post", post)
                        findNavController().navigate(R.id.contentFragment, bundle)
                    }
                    activity?.runOnUiThread {
                        binding.listView.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, post_array)
                    }
                }

            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        getRequest()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}