package com.idrok.yandexmap.ui.map

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.idrok.yandexmap.MainActivity
import com.idrok.yandexmap.R
import com.idrok.yandexmap.databinding.FragmentMapBinding
import com.idrok.yandexmap.hide
import com.idrok.yandexmap.models.PlaceModel
import com.idrok.yandexmap.show
import com.idrok.yandexmap.ui.Variables
import com.yandex.mapkit.Animation
import com.yandex.mapkit.GeoObject
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.GeoObjectTapListener
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.search.*
import com.yandex.mapkit.search.search_layer.SearchLayer
import com.yandex.mapkit.uri.UriObjectMetadata
import com.yandex.runtime.Error

class MapFragment : Fragment(), Session.SearchListener {

    private val TAG = "MapFragment"

    private lateinit var mapViewModel: MapViewModel
    private lateinit var binding: FragmentMapBinding
    private lateinit var searchManager: SearchManager
    private lateinit var searchLayer: SearchLayer
    private lateinit var searchOptions: SearchOptions
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var point: Point
    private lateinit var searchType: SearchType

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mapViewModel =
            ViewModelProvider(this).get(MapViewModel::class.java)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false)
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
        searchLayer = SearchFactory.getInstance().createSearchLayer(binding.mapView.mapWindow)
        searchOptions = SearchOptions()
        bottomSheetBehavior = BottomSheetBehavior.from(binding.placeInfo.root)
        setAllViews()

        return binding.root
    }

    private fun setAllViews() {

        val lat = arguments?.getDouble("lat")
        val long = arguments?.getDouble("long")
        if (lat != null && long != null) {
            moveCamera(Point(lat, long), 18f)
            Variables.placeModel?.let {
                showDialog(it)
            }
        } else {
            moveCamera(Point(41.311165, 69.279735), 15f)
        }

        binding.searchView.setOnClickListener {
            findNavController().navigate(R.id.searchFragment)
            binding.cvSearchBar.hide()
        }
        binding.searchView.setOnSearchClickListener {
            findNavController().navigate(R.id.searchFragment)
            binding.cvSearchBar.hide()
        }

        Variables.userLocation = Point(41.31149123419636, 69.28391964202837)

        binding.mapView.pointOfView = PointOfView.SCREEN_CENTER
        searchOptions.snippets = Snippet.BUSINESS_RATING1X.value
    }

    private fun setListeners() {
        binding.mapView.map.addCameraListener(cameraListener())
        bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback())
        binding.mapView.map.addTapListener(tapListener())
        binding.mapView.map.addInertiaMoveListener(inertialMoveListener())
    }

    private fun removeListeners() {
        binding.mapView.map.removeCameraListener(cameraListener())
        bottomSheetBehavior.removeBottomSheetCallback(bottomSheetCallback())
        binding.mapView.map.removeTapListener(tapListener())
        binding.mapView.map.removeInertiaMoveListener(inertialMoveListener())
    }


    override fun onStart() {
        super.onStart()
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("uri")
            ?.observe(viewLifecycleOwner) { uri ->
                Log.d(TAG, "onStart: $uri")
                if (uri.isNotEmpty()) {
                    searchType = SearchType.URISEARCH
                    requestUriData(uri)
                }
            }
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("ui")
            ?.observe(viewLifecycleOwner) {
                Log.d(TAG, "onStart: $it")
                binding.cvSearchBar.show()
                setListeners()
            }
        binding.mapView.onStart()
    }


    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: onResume")
        setListeners()
        binding.searchView.visibility = View.VISIBLE
    }


    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView: onDestroy")
        showBottomNav()
    }

    private fun moveCamera(point: Point, zoom: Float = 15f) {
        binding.mapView.map.move(
            CameraPosition(point, zoom, 0f, 0f),
            Animation(Animation.Type.SMOOTH, 1f), null
        )
    }

    private fun requestUriData(uri: String?) {

        if (!uri.isNullOrEmpty()) {
            searchManager.resolveURI(uri, searchOptions, this)
        }
    }

    private fun requestPointSearch(point: Point) {
        searchType = SearchType.POINTSEARCH
        searchManager.submit(point, 18, searchOptions, this)
            .setSortByDistance(Geometry.fromPoint(point))
    }


    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
        removeListeners()
    }

    private fun showBottomNav() {
        (requireActivity() as MainActivity).binding.navView.show()
    }

    private fun showBottomSheet() {
        hideBottomNav()
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun hideBottomSheet() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun hideBottomNav() {
        (requireActivity() as MainActivity).binding.navView.hide()
    }

    private fun handleGeoObject(geoObject: GeoObject?) {
        val placeModel = PlaceModel()

        val businessObject =
            geoObject?.metadataContainer?.getItem(BusinessObjectMetadata::class.java)
        val businessRatingObject =
            geoObject?.metadataContainer?.getItem(BusinessRating1xObjectMetadata::class.java)

        businessObject?.let {
            placeModel.displayName = it.name
            placeModel.address = it.address.formattedAddress
            placeModel.workingHours = it.workingHours?.text ?: ""
        }
        businessRatingObject?.let {
            placeModel.rating = it.score ?: -1f
            placeModel.review = "${it.reviews} reviews"
        }

        if (businessObject == null) {
            placeModel.displayName = geoObject?.name ?: ""
            placeModel.address = geoObject?.descriptionText ?: ""
        }
        placeModel.lat = geoObject?.geometry?.get(0)?.point?.latitude ?: 0.0
        placeModel.longitude = geoObject?.geometry?.get(0)?.point?.longitude ?: 0.0



        Log.d(TAG, "onSearchResponse: $placeModel")

        if (searchType != SearchType.SELECTEDSEARCH) {
            val point = geoObject?.geometry?.get(0)?.point
            point?.let {
                moveCamera(point)
            }
        }
        showDialog(placeModel)

    }

    private fun showDialog(placeModel: PlaceModel) {
        val dp = resources.displayMetrics.density
        binding.placeInfo.apply {
            if (placeModel.displayName.isNotEmpty()) {

                tvTitle.text = placeModel.displayName
                tvAddress.text = placeModel.address
                if (placeModel.rating != -1f) {

                    val layoutParams = this.root.layoutParams
                    layoutParams.height = (194*dp).toInt()

                    this.root.layoutParams = layoutParams

                    tvRating.show()
                    tvReview.show()
                    ratingBar.show()

                    tvRating.text = placeModel.rating.toString()
                    tvReview.text = placeModel.review
                    ratingBar.rating = placeModel.rating
                } else {
                    val layoutParams = this.root.layoutParams
                    layoutParams.height = (150*dp).toInt()

                    this.root.layoutParams = layoutParams
                    tvRating.hide()
                    tvReview.hide()
                    ratingBar.hide()
                }

                if (placeModel.workingHours.isNotEmpty()) {
                    tvWorkingHours.show()
                    tvWorkingHours.text = placeModel.workingHours
                } else {
                    tvWorkingHours.hide()
                }
                btnSave.setOnClickListener {
                    Variables.placeModel = placeModel
                    findNavController().navigate(R.id.saveDialog)
                }

                ivClose.setOnClickListener {
                    hideBottomSheet()
                }
                showBottomSheet()

            }
        }

    }

    private fun cameraListener(): CameraListener {
        return CameraListener { _, cameraPos, _, p0 ->
            if (p0) {
                Variables.userLocation = cameraPos.target
//                requestPointSearch(cameraPos.target)
            }
        }
    }


    private fun inertialMoveListener(): InertiaMoveListener {
        return object : InertiaMoveListener {
            override fun onStart(map: Map, pos: CameraPosition) {

            }

            override fun onCancel(map: Map, pos: CameraPosition) {

            }

            override fun onFinish(map: Map, pos: CameraPosition) {
                requestPointSearch(pos.target)
            }

        }
    }


    private fun tapListener(): GeoObjectTapListener {
        return GeoObjectTapListener { geoObject ->

            val selectedObject =
                geoObject.geoObject.metadataContainer.getItem(GeoObjectSelectionMetadata::class.java)
            val uriMetadata =
                geoObject.geoObject.metadataContainer.getItem(UriObjectMetadata::class.java)

            binding.mapView.map.selectGeoObject(selectedObject.id, selectedObject.layerId)

            if (uriMetadata != null && uriMetadata.uris[0] != null) {
                searchType = SearchType.SELECTEDSEARCH
                requestUriData(uriMetadata.uris[0].value)
            }

            selectedObject != null
        }
    }


    private fun bottomSheetCallback(): BottomSheetBehavior.BottomSheetCallback {
        return object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    showBottomNav()
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    hideBottomNav()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

        }
    }


    override fun onSearchResponse(response: Response) {
        if (searchType == SearchType.POINTSEARCH && response.collection.children.size != 0) {
            val uriMetadata =
                response.collection.children[0].obj?.metadataContainer?.getItem(UriObjectMetadata::class.java)
            uriMetadata?.uris?.get(0)?.let {
                searchType = SearchType.SELECTEDSEARCH
                requestUriData(it.value)
            }
        } else if (searchType == SearchType.URISEARCH || searchType == SearchType.SELECTEDSEARCH) {
            for (child in response.collection.children) {
                handleGeoObject(child.obj)
            }
        }
    }

    override fun onSearchError(error: Error) {
        Log.e(TAG, "onSearchError: $error")
    }
}

enum class SearchType {
    POINTSEARCH,
    URISEARCH,
    SELECTEDSEARCH
}