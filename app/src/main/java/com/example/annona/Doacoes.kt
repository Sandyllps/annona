package com.example.annona

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ItensCompartilhadosViewModel : ViewModel() {

    private val _itensDoacao = MutableLiveData<List<Item>>()
    val itensDoacao: LiveData<List<Item>> = _itensDoacao

    fun atualizarItensParaDoacao(lista: List<Item>) {
        _itensDoacao.value = lista
    }
}