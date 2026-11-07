package com.example.annona

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ItensCompartilhadosViewModel : ViewModel() {

    private val _itensDespensa = MutableLiveData<List<Item>>(emptyList())
    val itensDespensa: LiveData<List<Item>> = _itensDespensa

    private val _itensDoacao = MutableLiveData<List<Item>>(emptyList())
    val itensDoacao: LiveData<List<Item>> = _itensDoacao

    fun atualizarItensDespensa(lista: List<Item>) {
        _itensDespensa.value = lista
    }

    fun atualizarItensParaDoacao(lista: List<Item>) {
        _itensDoacao.value = lista
    }
}