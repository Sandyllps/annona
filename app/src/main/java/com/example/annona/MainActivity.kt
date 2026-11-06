package com.example.annona

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.annona.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Locale

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

        // Garante formatação e textos em Português (Brasil)
        Locale.setDefault(Locale("pt", "BR"))

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

        NotificacaoValidadeHelper.criarCanal(this)

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

        // Cria uma instância nova por posição e mantém as 4 abas ativas em memória (offscreenPageLimit = 3)
        // Isso evita que a aba Perfil trave ou destrua as outras páginas ao navegar de volta!
        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 4

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> TelaDespensaFragment.newInstance("page", 0, userId)
                    1 -> ReceitasFragment()
                    2 -> DoacoesFragment()
                    else -> TelaPerfilFragment()
                }
            }
        }

        viewPager.offscreenPageLimit = 3
        viewPager.setCurrentItem(0, false)

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
            if (index >= 0) {
                if (viewPager.currentItem != index) {
                    viewPager.setCurrentItem(index, false)
                }
                true
            } else {
                false
            }
        }

        // Se o usuário apertar o botão voltar do celular em Receitas, Doação ou Perfil, volta para a Despensa
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (viewPager.currentItem != 0) {
                    irParaDespensa()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
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

    // Volta para a aba Despensa com segurança a partir de qualquer outra aba
    fun irParaDespensa() {
        if (::viewPager.isInitialized && ::bottomNav.isInitialized) {
            viewPager.setCurrentItem(0, false)
            bottomNav.selectedItemId = R.id.nav_despensa
        }
    }

    fun irParaReceitas() {
        if (::viewPager.isInitialized && ::bottomNav.isInitialized) {
            viewPager.setCurrentItem(1, false)
            bottomNav.selectedItemId = R.id.nav_receitas
        }
    }

    fun irParaDoacao() {
        if (::viewPager.isInitialized && ::bottomNav.isInitialized) {
            viewPager.setCurrentItem(2, false)
            bottomNav.selectedItemId = R.id.nav_doacao
        }
    }
}
