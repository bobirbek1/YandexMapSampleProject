package com.idrok.yandexmap.ui.bookmark

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.idrok.yandexmap.R
import com.idrok.yandexmap.database.MyRoomDatabase
import com.idrok.yandexmap.databinding.FragmentBookmarkBinding
import com.idrok.yandexmap.ui.Variables
import com.idrok.yandexmap.ui.bookmark.adapters.BookmarkAdapter

class BookmarkFragment : Fragment() {

    private val TAG = "BookmarkFragment"

    private lateinit var bookmarkViewModel: BookmarkViewModel
    private lateinit var binding: FragmentBookmarkBinding
    private lateinit var adapter: BookmarkAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dataSource = MyRoomDatabase.getDatabase(requireContext()).dao()
        val factory = BookmarkViewModelFactory(dataSource)
        bookmarkViewModel =
            ViewModelProvider(this, factory).get(BookmarkViewModel::class.java)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bookmark, container, false)

        setRv()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        bookmarkViewModel.getAllPlaces().observe(this) { listPlaces ->
            adapter.setData(listPlaces)
        }
    }

    private fun setRv() {
        adapter = BookmarkAdapter {
            Log.d(TAG, "setRv: $it")
            Variables.placeModel = it
            findNavController().navigate(
                R.id.navigation_map, bundleOf(
                    "lat" to it.lat,
                    "long" to it.longitude
                )
            )
        }
        binding.rvBookmark.adapter = adapter
    }
}