package dev.androidbroadcast.quizapp.ui.main.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dev.androidbroadcast.quizapp.R
import dev.androidbroadcast.quizapp.databinding.ActivityMainBinding
import dev.androidbroadcast.quizapp.ui.helper.ViewModelFactory
import dev.androidbroadcast.quizapp.ui.main.viewmodel.MainViewModel


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainViewModel = ViewModelProvider(this, ViewModelFactory.getInstance(application)).get(MainViewModel::class.java)
        mainViewModel.fetchAndStoreQuizzes()
        setNavigation()
    }

    private fun setNavigation() {
        val bottomNav: BottomNavigationView = binding.bottomNavigation
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        val navController = navHostFragment.navController

        bottomNav.setupWithNavController(navController)
    }
}
