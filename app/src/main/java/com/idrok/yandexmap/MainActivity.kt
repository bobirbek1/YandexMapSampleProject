package com.idrok.yandexmap

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.idrok.yandexmap.databinding.ActivityMainBinding
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.places.PlacesFactory
import com.yandex.mapkit.search.SearchFactory

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        MapKitFactory.setApiKey("32eb2b2a-d6c7-4f97-ac00-1aebf0f4b638")
        MapKitFactory.initialize(this)
        SearchFactory.initialize(this)
        PlacesFactory.initialize(this)


        val navHost =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHost.navController
        navView.setupWithNavController(navController)
    }


    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        super.onStop()
        MapKitFactory.getInstance().onStop()
    }

}