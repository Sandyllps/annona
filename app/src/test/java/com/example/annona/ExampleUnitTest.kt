package com.example.annona

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

// Testes unitários das regras de negócio do Annona (B19, Seção 10 da Documentação Funcional)
class ExampleUnitTest {

    private fun criarDataDaquiADias(dias: Int): java.util.Date {
        return Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, dias)
        }.time
    }

    // CT04: Item sem data de validade deve ser Seguro (flag 4) e sem prazo (RN02)
    @Test
    fun itemSemValidade_deveSerSeguroESemPrazo() {
        val (flag, dias, prazo) = calcularSemaforoData(possuiValidade = false, dataValidade = null)
        assertEquals(4, flag)
        assertNull(dias)
        assertEquals("", prazo)
    }

    // CT09 / B14: Item que vence hoje deve ser Crítico (flag 2) e não Vencido (RN01)
    @Test
    fun itemQueVenceHoje_deveSerCritico() {
        val hoje = criarDataDaquiADias(0)
        val (flag, dias, prazo) = calcularSemaforoData(possuiValidade = true, dataValidade = hoje)
        assertEquals(2, flag)
        assertEquals(0, dias)
        assertEquals("0 DIAS", prazo)
    }

    // CT05: Item que vence em 2 dias deve ser Crítico (flag 2) e mostrar "2 DIAS" (RN01)
    @Test
    fun itemComValidadeEm2Dias_deveSerCritico() {
        val daquiA2Dias = criarDataDaquiADias(2)
        val (flag, dias, prazo) = calcularSemaforoData(possuiValidade = true, dataValidade = daquiA2Dias)
        assertEquals(2, flag)
        assertEquals(2, dias)
        assertEquals("2 DIAS", prazo)
    }

    // RN01: Item que vence entre 4 e 7 dias deve ser Atenção (flag 3)
    @Test
    fun itemComValidadeEm5Dias_deveSerAtencao() {
        val daquiA5Dias = criarDataDaquiADias(5)
        val (flag, dias, prazo) = calcularSemaforoData(possuiValidade = true, dataValidade = daquiA5Dias)
        assertEquals(3, flag)
        assertEquals(5, dias)
        assertEquals("5 DIAS", prazo)
    }

    // RN01: Item que vence em 8 dias ou mais deve ser Seguro (flag 4)
    @Test
    fun itemComValidadeEm10Dias_deveSerSeguro() {
        val daquiA10Dias = criarDataDaquiADias(10)
        val (flag, dias, prazo) = calcularSemaforoData(possuiValidade = true, dataValidade = daquiA10Dias)
        assertEquals(4, flag)
        assertEquals(10, dias)
        assertEquals("10 DIAS", prazo)
    }

    // RN01: Item com validade no passado deve ser Vencido (flag 1)
    @Test
    fun itemComValidadePassada_deveSerVencido() {
        val ontem = criarDataDaquiADias(-1)
        val (flag, dias, prazo) = calcularSemaforoData(possuiValidade = true, dataValidade = ontem)
        assertEquals(1, flag)
        assertEquals(-1, dias)
        assertEquals("VENCIDO", prazo)
    }

    // CT07 / B01: Medidas no app devem ter exatamente os mesmos IDs do banco de dados
    @Test
    fun medidasNoApp_devemBaterComIdsDoBanco() {
        assertEquals(1, Medida.obterPeloNome("Unidade").idMedida)
        assertEquals(2, Medida.obterPeloNome("Pacote").idMedida)
        assertEquals(3, Medida.obterPeloNome("Caixa").idMedida)
        assertEquals(4, Medida.obterPeloNome("Grama").idMedida)
        assertEquals(5, Medida.obterPeloNome("Kg").idMedida)
        assertEquals(6, Medida.obterPeloNome("Ml").idMedida)
        assertEquals(7, Medida.obterPeloNome("Litro").idMedida)
    }

    // CT16 / RF17 / RN14: Receitas devem ser filtradas pelos itens da despensa
    @Test
    fun receitasPelaDespensa_deveSugerirReceitasComIngredienteEmComum() {
        val itensDespensa = listOf(
            Item(id = 1, nome = "Feijão", idCategoria = 4)
        )
        val sugeridas = ReceitasData.filtrarPorDespensa(ReceitasData.receitas, itensDespensa)
        assertTrue(sugeridas.any { it.nome == "Feijoada" })
        assertTrue(sugeridas.first { it.nome == "Feijoada" }.ingredientesDisponiveis.contains("Feijão"))
    }
}