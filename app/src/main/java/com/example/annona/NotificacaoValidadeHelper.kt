package com.example.annona

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Gerenciador de notificações e alertas locais de vencimento (RF16, RN15)
object NotificacaoValidadeHelper {

    private const val CHANNEL_ID = "annona_alertas_validade_high"
    private const val NOTIFICATION_ID = 1001
    private const val NOTIFICATION_ITEM_ID = 1002
    private var ultimaAssinaturaNotificada: Int = -1
    var alertaDialogExibidoNaSessao: Boolean = false

    fun criarCanal(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nome = "Alertas de Validade Annona"
            val descricao = "Alertas sobre itens vencidos, críticos ou próximos do vencimento na despensa"
            val importancia = NotificationManager.IMPORTANCE_HIGH
            val canal = NotificationChannel(CHANNEL_ID, nome, importancia).apply {
                description = descricao
                enableVibration(true)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(canal)
        }
    }

    private fun formatarDataCurta(data: Date?): String {
        if (data == null) return ""
        return try {
            SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).format(data)
        } catch (_: Exception) {
            ""
        }
    }

    fun montarTextoResumoAlerta(itens: List<Item>): String {
        val vencidos = itens.filter { it.flag == 1 }
        val criticos = itens.filter { it.flag == 2 }
        val atencao = itens.filter { it.flag == 3 }

        return buildString {
            append("Olá! Confira o resumo de validade dos itens na sua despensa hoje:\n\n")
            if (criticos.isNotEmpty()) {
                append("🔴 CRÍTICO (${criticos.size} item(ns) - vencem em até 3 dias):\n")
                criticos.forEach { item ->
                    val p = item.prazo?.trim().orEmpty()
                    val dataFmt = formatarDataCurta(item.dataValidade)
                    val detalhe = when {
                        p.isNotEmpty() && dataFmt.isNotEmpty() -> " — vence em $p ($dataFmt)"
                        p.isNotEmpty() -> " — vence em $p"
                        dataFmt.isNotEmpty() -> " — validade: $dataFmt"
                        else -> ""
                    }
                    append("  • ${item.nome}$detalhe\n")
                }
                append("\n")
            }
            if (vencidos.isNotEmpty()) {
                append("🟤 VENCIDO (${vencidos.size} item(ns)):\n")
                vencidos.forEach { item ->
                    val dataFmt = formatarDataCurta(item.dataValidade)
                    val detalhe = if (dataFmt.isNotEmpty()) " (venceu em $dataFmt)" else " (VENCIDO)"
                    append("  • ${item.nome}$detalhe\n")
                }
                append("\n")
            }
            if (atencao.isNotEmpty()) {
                append("🟡 ATENÇÃO (${atencao.size} item(ns) - vencem em 4 a 7 dias):\n")
                atencao.forEach { item ->
                    val p = item.prazo?.trim().orEmpty()
                    val dataFmt = formatarDataCurta(item.dataValidade)
                    val detalhe = when {
                        p.isNotEmpty() && dataFmt.isNotEmpty() -> " — vence em $p ($dataFmt)"
                        p.isNotEmpty() -> " — vence em $p"
                        dataFmt.isNotEmpty() -> " — validade: $dataFmt"
                        else -> ""
                    }
                    append("  • ${item.nome}$detalhe\n")
                }
                append("\n")
            }
            append("💡 Dica: Consuma os itens prioritários nas Receitas ou separe os itens fechados na aba Doação!")
        }.trim()
    }

    fun verificarENotificar(context: Context, itens: List<Item>, forcarEnvio: Boolean = false) {
        val session = SecaoUsuario(context)
        if (!session.notificacoesAtivas()) {
            cancelarNotificacoes(context)
            return
        }

        val criticos = itens.filter { it.flag == 2 }
        val atencao = itens.filter { it.flag == 3 }
        val vencidos = itens.filter { it.flag == 1 }

        if (criticos.isEmpty() && vencidos.isEmpty() && atencao.isEmpty()) {
            ultimaAssinaturaNotificada = 0
            return
        }

        val assinatura = criticos.size * 1000 + vencidos.size * 100 + atencao.size
        if (!forcarEnvio && ultimaAssinaturaNotificada == assinatura) {
            return
        }
        ultimaAssinaturaNotificada = assinatura

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        criarCanal(context)

        val titulo = when {
            criticos.isNotEmpty() && vencidos.isNotEmpty() ->
                "Resumo da Despensa: ${criticos.size} crítico(s) e ${vencidos.size} vencido(s)"
            criticos.isNotEmpty() ->
                "Atenção na despensa: ${criticos.size} item(ns) vencem em breve"
            vencidos.isNotEmpty() ->
                "Você possui ${vencidos.size} item(ns) vencido(s) na despensa"
            else ->
                "${atencao.size} item(ns) vencem nos próximos dias"
        }

        val nomesDestaque = (criticos + vencidos + atencao).take(4).joinToString(", ") { it.nome }
        val texto = "Confira na sua despensa: $nomesDestaque. Evite o desperdício ou separe para doação!"

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(titulo)
            .setContentText(texto)
            .setStyle(NotificationCompat.BigTextStyle().bigText(montarTextoResumoAlerta(itens)))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
        } catch (_: SecurityException) {
        }
    }

    // Dispara alerta imediato quando o usuário cadastra ou edita um item próximo do vencimento
    fun notificarItemSalvo(context: Context, nomeItem: String, flag: Int, diasRestantes: Int?) {
        val session = SecaoUsuario(context)
        if (!session.notificacoesAtivas()) return
        if (flag != 1 && flag != 2 && flag != 3) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        criarCanal(context)

        val (titulo, msg) = when (flag) {
            1 -> "🟤 Item vencido: $nomeItem" to "O item \"$nomeItem\" está vencido. Verifique o descarte adequado."
            2 -> {
                val diasTxt = when (diasRestantes) {
                    0 -> "vence hoje"
                    1 -> "vence em 1 dia"
                    else -> "vence em ${diasRestantes ?: 3} dias"
                }
                "🔴 Alerta Crítico: $nomeItem" to "O item \"$nomeItem\" $diasTxt! Consuma logo ou envie para doação."
            }
            else -> "🟡 Atenção de Validade: $nomeItem" to "O item \"$nomeItem\" vence em ${diasRestantes ?: 7} dias."
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(titulo)
            .setContentText(msg)
            .setStyle(NotificationCompat.BigTextStyle().bigText(msg))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ITEM_ID, builder.build())
        } catch (_: SecurityException) {
        }
    }

    fun cancelarNotificacoes(context: Context) {
        try {
            NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)
            NotificationManagerCompat.from(context).cancel(NOTIFICATION_ITEM_ID)
            ultimaAssinaturaNotificada = -1
        } catch (_: Exception) {
        }
    }
}
