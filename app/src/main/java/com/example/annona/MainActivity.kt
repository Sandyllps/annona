package com.example.annona

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.annona.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var viewPager: ViewPager2
    private lateinit var bottomNav: BottomNavigationView
    private val menuItemIdAtPos = listOf(
        R.id.nav_despensa,
        R.id.nav_receitas,
        R.id.nav_doacao,
        R.id.nav_perfil
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNav) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }

        viewPager = binding.viewPager
        bottomNav = binding.bottomNav
        bottomNav.itemIconTintList = null

        // LISTA DE FRAGMENTS ATUALIZADA
        val userId = intent.getIntExtra("USER_ID", 0) // pegar o ID do login
        val fragments = listOf(
            TelaDespensaFragment.newInstance("page", 0, userId),
            ReceitasFragment(),
            TelaDespensaFragment.newInstance("page", 2, userId),
            TelaPerfilFragment()
        )

        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = fragments.size
            override fun createFragment(position: Int) = fragments[position]
        }

        viewPager.post { viewPager.setCurrentItem(0, false) }

        viewPager.offscreenPageLimit = 1

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val menuId = menuItemIdAtPos[position]
                if (bottomNav.selectedItemId != menuId) {
                    bottomNav.selectedItemId = menuId
                }
            }
        })

        bottomNav.setOnItemSelectedListener { item ->
            val index = menuItemIdAtPos.indexOf(item.itemId)
            if (index >= 0 && viewPager.currentItem != index) {
                // Mude de 'true' para 'false' para remover o rastro visual
                viewPager.setCurrentItem(index, false)
            }
            true
        }
    }
}