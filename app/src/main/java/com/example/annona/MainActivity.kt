package com.example.annona

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.annona.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

// Tela principal que segura as quatro abas (Despensa, Receitas, Doação e Perfil)
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

        // B08: Garante que haja um usuário válido na sessão; caso contrário, retorna para o Login
        val session = SecaoUsuario(this)
        val userId = intent.getIntExtra("USER_ID", -1).takeIf { it > 0 } ?: session.obterUserId()
        if (userId == null) {
            val loginIntent = Intent(this, TelaLogin::class.java)
            loginIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(loginIntent)
            finish()
            return
        }

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

        val fragments = listOf(
            TelaDespensaFragment.newInstance("page", 0, userId),
            ReceitasFragment(),
            DoacoesFragment(),
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
                viewPager.setCurrentItem(index, false)
            }
            true
        }
    }

    private val adicionarItemLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        // A atualização da lista é feita pelo BroadcastReceiver na TelaDespensaFragment
    }

    fun abrirAdicionarItem() {
        val intent = Intent(this, AdicionarItem::class.java)
        adicionarItemLauncher.launch(intent)
    }

    // Usado pelo botão fechar (X) da aba Perfil para voltar à Despensa com segurança (B10)
    fun irParaDespensa() {
        viewPager.setCurrentItem(0, false)
        bottomNav.selectedItemId = R.id.nav_despensa
    }
}