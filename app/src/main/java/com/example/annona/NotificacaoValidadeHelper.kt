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

// Gerenciador de notificações locais de vencimento (RF16, RN15)
object NotificacaoValidadeHelper {

    private const val CHANNEL_ID = "annona_validade_channel"
    private const val NOTIFICATION_ID = 1001
    private var ultimaContagemNotificada: Int = -1

    fun criarCanal(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nome = "Alertas de Validade"
            val descricao = "Notificações sobre itens próximos do vencimento na despensa"
            val importancia = NotificationManager.IMPORTANCE_DEFAULT
            val canal = NotificationChannel(CHANNEL_ID, nome, importancia).apply {
                description = descricao
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(canal)
        }
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

        val totalAlerta = criticos.size + vencidos.size
        if (totalAlerta == 0 && atencao.isEmpty()) {
            ultimaContagemNotificada = 0
            return
        }

        if (!forcarEnvio && ultimaContagemNotificada == (criticos.size * 100 + vencidos.size * 10 + atencao.size)) {
            return
        }
        ultimaContagemNotificada = criticos.size * 100 + vencidos.size * 10 + atencao.size

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        criarCanal(context)

        val titulo = when {
            criticos.isNotEmpty() -> "Atenção: ${criticos.size} item(ns) em estado crítico!"
            vencidos.isNotEmpty() -> "Você tem ${vencidos.size} item(ns) vencido(s) na despensa"
            else -> "${atencao.size} item(ns) vencem nos próximos dias"
        }

        val nomesDestaque = (criticos + atencao + vencidos).take(3).joinToString(", ") { it.nome }
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
            .setStyle(NotificationCompat.BigTextStyle().bigText(texto))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
        } catch (_: SecurityException) {
        }
    }

    fun cancelarNotificacoes(context: Context) {
        try {
            NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)
            ultimaContagemNotificada = -1
        } catch (_: Exception) {
        }
    }
}

