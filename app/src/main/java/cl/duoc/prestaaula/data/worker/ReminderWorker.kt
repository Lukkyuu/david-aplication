package cl.duoc.prestaaula.data.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import cl.duoc.prestaaula.R

class ReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val titulo = inputData.getString("titulo") ?: "Recordatorio PrestaAula"
        val mensaje = inputData.getString("mensaje") ?: "Tiene una reserva próxima o recurso pendiente en el CRA."

        showNotification(applicationContext, titulo, mensaje)
        return Result.success()
    }

    private fun showNotification(context: Context, title: String, message: String) {
        val channelId = "prestaaula_cra_channel"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "PrestaAula CRA",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones de reservas y préstamos del CRA - Colegio Marcela Paz"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }
}
