package com.example.annona

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
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
            // Isso adiciona um espaço no fundo do menu igual ao tamanho da barra do Android
            v.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }

        viewPager = binding.viewPager
        bottomNav = binding.bottomNav
        bottomNav.itemIconTintList = null

        // viewPager.isUserInputEnabled = false

        // Adapter com Fragments
        val fragments = listOf(
            TelaDespensaFragment.newInstance("page", 0),
            //  TODO: criar os outros 3 fragments e substituir os nomes certos nessas 3 respectivas linhas
            ReceitasFragment(),                                         //tela de Receitas
            TelaDespensaFragment.newInstance("page", 2),
            TelaDespensaFragment.newInstance("page", 3)
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
                viewPager.currentItem = index
            }
            true
        }
    }
}