package com.idrok.yandexmap.ui.search

import android.content.DialogInterface
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.idrok.yandexmap.R
import com.idrok.yandexmap.databinding.SearchFragmentBinding
import com.idrok.yandexmap.models.Place
import com.idrok.yandexmap.ui.Variables
import com.idrok.yandexmap.ui.search.adapters.Adapter
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.*
import com.yandex.runtime.Error


class SearchFragment : BottomSheetDialogFragment(),
    androidx.appcompat.widget.SearchView.OnQueryTextListener, SuggestSession.SuggestListener {

    private val TAG = "SearchFragment"


    private lateinit var viewModel: SearchViewModel
    private lateinit var binding: SearchFragmentBinding
    private lateinit var searchManager: SearchManager
    private lateinit var adapter: Adapter
    private lateinit var searchText: String
    private lateinit var suggestOption: SuggestOptions

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.search_fragment, container, false)
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
        suggestOption = SuggestOptions()
        suggestOption.userPosition = Variables.userLocation
        setAllViews()
        setRv()
        return binding.root
    }

    private fun setRv() {
        adapter = Adapter(requireContext()) { uri ->
            Log.d(TAG, "setRv: $uri")
            findNavController().previousBackStackEntry?.savedStateHandle?.set("uri", uri)
            findNavController().popBackStack()
        }
        binding.rvSearch.adapter = adapter
        binding.rvSearch.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
    }

    private fun setAllViews() {
        val background = binding.searchView.findViewById<View>(androidx.appcompat.R.id.search_plate)
        background.background = null

        binding.apply {
            searchView.requestFocus()
            val dp = resources.displayMetrics.density
            parentView.layoutParams = ViewGroup.LayoutParams(
                Resources.getSystem().displayMetrics.widthPixels,
                (Resources.getSystem().displayMetrics.heightPixels - 35 * dp).toInt()
            )

            searchView.setOnQueryTextListener(this@SearchFragment)

        }

//        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                findNavController().previousBackStackEntry?.savedStateHandle?.set("uri", "")
//                requireActivity().onBackPressedDispatcher.onBackPressed()
//            }
//        })

    }

    private fun submitQuery(query: String) {

        searchManager.createSuggestSession().suggest(
            query, BoundingBox(
                Point(40.419348, -3.700897),
                Point(35.682272, 139.753137)
            ), suggestOption, this
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
    }


    override fun onQueryTextSubmit(query: String?): Boolean {

        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {

        Log.d("SearchFragment", "onQueryTextChange: $newText")
        searchText = newText ?: ""
        newText?.let { submitQuery(it) }

        return true
    }


    override fun onResponse(suggestList: MutableList<SuggestItem>) {
        val listItems = arrayListOf<Place>()

        for (item in suggestList) {

            val distance = item.distance?.text ?: ""
            val index = item.displayText?.indexOf(searchText, ignoreCase = true)
            val spannable = SpannableString(item.displayText)
            index?.let {
                if (it != -1)
                    spannable.setSpan(
                        ForegroundColorSpan(Color.BLACK),
                        it,
                        it + searchText.length,
                        0
                    )
            }
            listItems.add(Place(spannable, distance, item.subtitle?.text ?: "", item.uri ?: ""))
        }
        adapter.submitList(listItems)
    }

    override fun onError(error: Error) {
        Log.e(TAG, "onError: $error")
        }

    override fun onPause() {
        super.onPause()
        findNavController().previousBackStackEntry?.savedStateHandle?.set("ui", "destroy")
    }
}