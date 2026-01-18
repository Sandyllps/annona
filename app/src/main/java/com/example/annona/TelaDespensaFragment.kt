package com.example.annona

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TelaDespensaFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TelaDespensaFragment : Fragment() {
    companion object {
        fun newInstance(key: String, pos: Int) = TelaDespensaFragment().apply {
            arguments = Bundle().apply {
                putInt("pos", pos)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Inicializa RecyclerView, adapter, etc.
        val rv = view.findViewById<RecyclerView>(R.id.rvItems)
        rv.layoutManager = LinearLayoutManager(requireContext())
        val adapter = AdaptadorItem()
        rv.adapter = adapter
        adapter.submitList(sampleData())
    }

    private fun sampleData(): List<Item> {
        return listOf(
            Item( 1,"Feijão",  "Consumindo", "4 DIAS", "Armazenar na geladeira", R.drawable.hortifruti_tomate),

        )
    }
}