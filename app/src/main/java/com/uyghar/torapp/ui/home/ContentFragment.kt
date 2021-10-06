package com.uyghar.torapp.ui.home

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import com.uyghar.torapp.R
import com.uyghar.torapp.databinding.FragmentCategoryBinding
import com.uyghar.torapp.databinding.FragmentContentBinding
import com.uyghar.torapp.model.Post
import android.content.Intent
import android.util.Log
import android.view.Window
import android.webkit.WebChromeClient

import android.webkit.WebView
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.GsonBuilder
import com.uyghar.torapp.databinding.DialogLayoutBinding
import com.uyghar.torapp.model.Comment
import okhttp3.*
import java.io.IOException
import java.net.URL


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
class ContentFragment : Fragment() {
    private var _binding: FragmentContentBinding? = null
    private val binding get() = _binding!!
    var post: Post? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            post = it.getParcelable("post")
            Log.i("post_id",post?.id?.toString() ?: "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentContentBinding.inflate(inflater, container, false)

        binding.buttonComment.setOnClickListener {
            val dialog = Dialog(requireContext())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            val dialogBinding = DialogLayoutBinding.inflate(layoutInflater)
            dialog.setContentView(dialogBinding.root)
            dialogBinding.buttonClear.setOnClickListener {
                dialogBinding.editName.setText("")
                dialogBinding.editEmail.setText("")
                dialogBinding.editComment.setText("")
            }
            dialogBinding.buttonSend.setOnClickListener {
                val name = dialogBinding.editName.text.toString()
                val email = dialogBinding.editEmail.text.toString()
                val content = dialogBinding.editComment.text.toString()
                val params = HashMap<String, Any>()
                params["author_name"] = name
                params["author_email"] = email
                params["content"] = content
                var formBody = MultipartBody.Builder()
                formBody.setType(MultipartBody.FORM)
                for( (key, value) in params ) {
                    formBody.addFormDataPart(key, value.toString())
                }
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url(URL("http://uyghurapp.com/wp/wp-json/wp/v2/comments?post=${post?.id}"))
                    .post(formBody.build())
                    .build()
                client.newCall(request).enqueue(object: Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val res = response.body?.string()
                        Log.i("response", res ?: "")
                    }

                })
            }
            dialog.show()
        }
        getRequest()
        return binding.root
    }

    fun getRequest() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(URL("http://uyghurapp.com/wp/wp-json/wp/v2/posts/${post?.id}?_fields=id,title,content"))
            .build()
        client.newCall(request).enqueue(
            object: Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    val json_str = response.body?.string()
                    val gson = GsonBuilder().create()
                    val post = gson.fromJson(json_str, Post::class.java)
                    activity?.runOnUiThread {
                        binding.textTitle.setText(post?.title?.rendered ?: "")
                        val content = "<span style='direction: rtl; text-align: justify;'>" + post?.content?.rendered ?: "" + "</span>"
                        binding.webView.loadDataWithBaseURL(null, content, "text/html", "utf-8", null)
                        val webViewClient = object: WebViewClient() {
                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                binding.buttonComment.visibility = View.VISIBLE
                                getComments()

                            }

                        }
                        binding.webView.webViewClient = webViewClient


                    }
                }

            }
        )
    }

    fun getComments() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(URL("http://uyghurapp.com/wp/wp-json/wp/v2/comments?post=${post?.id}&_fields=id,author_name,content,date"))
            .build()
        client.newCall(request).enqueue(
            object: Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    val json_str = response.body?.string()
                    val gson = GsonBuilder().create()
                    val comments = gson.fromJson(json_str, Array<Comment>::class.java)
                    activity?.runOnUiThread {
                        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(),1)
                        binding.recyclerView.adapter = CommentAdapter(comments)
                        binding.recyclerView.visibility = View.VISIBLE

                    }
                }

            }
        )
    }



    private class CustomWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(wv: WebView, url: String): Boolean {
            return true
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}