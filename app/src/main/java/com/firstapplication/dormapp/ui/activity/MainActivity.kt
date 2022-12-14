package com.firstapplication.dormapp.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.firstapplication.dormapp.R
import com.firstapplication.dormapp.databinding.ActivityLoginBinding
import com.firstapplication.dormapp.di.ActivitySubComponent
import com.firstapplication.dormapp.extensions.appComponent
import com.firstapplication.dormapp.ui.fragments.admin.AddWorkFragment
import com.firstapplication.dormapp.ui.fragments.login.MainLoginFragment
import com.firstapplication.dormapp.ui.fragments.student.AccountFragment
import com.firstapplication.dormapp.ui.fragments.student.NewsListFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    var activityComponent: ActivitySubComponent? = null
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activityComponent = appComponent.activityComponentBuilder().build()

        if (savedInstanceState == null) {
            val sharedPreferences = getSharedPreferences(LOGIN_USER_PREF, MODE_PRIVATE)
            val loginKey = sharedPreferences.getString(LOGIN_KEY, null)
            if (loginKey == null) {
                openLoginFragment()
            } else {
                openUserFragment(loginKey)
            }
        }

        binding.studentBottomView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.itemAccount -> navigateStudent(ACCOUNT_FRAGMENT_TAG)
                R.id.itemNewsList -> navigateStudent(NEWS_FRAGMENT_TAG)
            }

            return@setOnItemSelectedListener true
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (supportFragmentManager.findFragmentByTag(NEWS_FRAGMENT_TAG)?.isVisible == true) {
            binding.studentBottomView.selectedItemId = R.id.itemNewsList
        } else if (supportFragmentManager.findFragmentByTag(ACCOUNT_FRAGMENT_TAG)?.isVisible == true) {
            binding.studentBottomView.selectedItemId = R.id.itemAccount
            supportFragmentManager.popBackStack()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activityComponent = null
    }

    private fun navigateStudent(tag: String) {
        val fragment = supportFragmentManager.findFragmentByTag(tag)
        if (fragment != null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment, tag)
                .commit()
        } else {
            supportFragmentManager.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragmentContainer, NewsListFragment.newInstance(), tag)
                .commit()
        }
    }

    private fun openUserFragment(key: String) = when (key) {
        resources.getString(R.string.admin_key) -> openAdminFragment()
        else -> {
            try {
                openStudentFragment(key.toInt())
            } catch (e: Exception) {
                Log.e(this::class.java.simpleName, e.message ?: "")
                openLoginFragment()
            }
        }
    }

    private fun openAdminFragment() {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, AddWorkFragment.newInstance(), ADD_NEWS_TAG)
            .commit()
    }

    private fun openStudentFragment(key: Int) {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, AccountFragment.newInstance(key = key), ACCOUNT_FRAGMENT_TAG)
            .commit()
    }

    private fun openLoginFragment() {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, MainLoginFragment.newInstance())
            .commit()
    }

    companion object {
        const val LOGIN_USER_PREF = "LOGIN_USER"
        const val LOGIN_KEY = "LOGIN_KEY"

        const val NEWS_FRAGMENT_TAG = "NEWS_LIST_TAG"
        const val ACCOUNT_FRAGMENT_TAG = "ACCOUNT_FRAGMENT_TAG"
        const val ADD_NEWS_TAG = "ADD_NEWS_TAG"
    }
}