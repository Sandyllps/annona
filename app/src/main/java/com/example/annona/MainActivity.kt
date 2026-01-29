package com.example.annona

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.annona.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

// Esta é a tela principal que segura todas as outras abas (Despensa, Receitas, Doação, Perfil...)
class MainActivity : AppCompatActivity() {

    // O 'binding' ajuda o código a encontrar os componentes do XML sem erros
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var bottomNav: BottomNavigationView

    // Esta lista associa a posição de cada tela (0, 1, 2, 3) com o ID do botão no menu
    private val menuItemIdAtPos = listOf(
        R.id.nav_despensa,
        R.id.nav_receitas,
        R.id.nav_doacao,
        R.id.nav_perfil
    )



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inicializa o binding e prepara o visual da tela
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge() // Faz o app ocupar a tela toda, até as bordinhas
        setContentView(binding.root)

        // Ajusta o preenchimento (padding) para o menu não ficar escondido pela barra do Android
        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNav) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }

        // Conecta as variáveis com os componentes desenhados no XML
        viewPager = binding.viewPager
        bottomNav = binding.bottomNav

        // Mantém as cores originais dos ícones que eu defini
        bottomNav.itemIconTintList = null

        //CONFIGURAÇÃO DAS TELAS (Fragments)
        // Recupera o ID do usuário que veio da tela de Login
        val userId = intent.getIntExtra("USER_ID", 0)

        // É aqui que definimos a lista de Fragments que vão existir:
        val fragments = listOf(
            TelaDespensaFragment.newInstance("page", 0, userId), // Aba 0: Despensa
            ReceitasFragment(),                                 // Aba 1: Receitas
            DoacoesFragment(),                                  // Aba 2: Doação
            TelaPerfilFragment()                                // Aba 3: Perfil
        )

        // O 'adapter' é quem ensina o ViewPager a mostrar cada fragmento da lista acima
        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = fragments.size
            override fun createFragment(position: Int) = fragments[position]
        }

        // Faz o app começar mostrando a aba 0 (Despensa) sem animação de deslize porque tava feio
        viewPager.post { viewPager.setCurrentItem(0, false) }

        // Mantém pelo menos 1 tela carregada na memória para o app não empancar
        viewPager.offscreenPageLimit = 1

        //SE DESLIZAMOS A TELA, O MENU DE BAIXO MUDA
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val menuId = menuItemIdAtPos[position]
                // Se o usuário arrastar a tela, o ícone selecionado lá embaixo muda sozinho
                if (bottomNav.selectedItemId != menuId) {
                    bottomNav.selectedItemId = menuId
                }
            }
        })

        //SE CLICAR NO MENU, A TELA MUDA
        bottomNav.setOnItemSelectedListener { item ->
            val index = menuItemIdAtPos.indexOf(item.itemId)
            // Quando clica num ícone, o ViewPager pula para a tela correspondente
            if (index >= 0 && viewPager.currentItem != index) {
                // O 'false' faz a troca ser instantânea, sem aquele rastro visual de deslize
                viewPager.setCurrentItem(index, false)
            }
            true
        }
    }


    //A tarefa dessa launcher é levar o usuário até a tela de cadastro e ficar esperando ele voltar.
    private val adicionarItemLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        // Quando o usuário fecha a tela de cadastro e volta para cá, este bloco é executado.
        // Atualmente ele está vazio porque o aviso de atualizar a lista está sendo feito
        // por um grito (Broadcast) que a tela da Despensa escuta sozinha.
    }


    // Essa função abre a tela de cadastro.
    // Como a TelaDespensaFragment agora tem um ouvinte (Broadcast), a atualização será automática quando fechar essa tela.
    fun abrirAdicionarItem() {
        val intent = Intent(this, AdicionarItem::class.java)


        // Dizemos para o launcher levar esse intent e abrir a tela.
        // Sem essa linha, o botão clicará, mas o app não saberá que deve abrir uma nova janela.
        adicionarItemLauncher.launch(intent)
    }
}