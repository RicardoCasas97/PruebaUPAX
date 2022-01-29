package com.example.pruebaupax.Elementos;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.pruebaupax.R;


public class creaNotificacion {
    Context contexto;

    public creaNotificacion(Context contexto) {
        this.contexto = contexto;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)

    public creaNotificacion(Context contexto, String titulo, String Contenido, String TextoLargo) {
        
        Uri sonido= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder= new NotificationCompat.Builder(contexto,"Notificacion");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(contexto.getResources(), R.mipmap.ic_launcher));
        long [] patron={12,50,12};
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(TextoLargo));
        builder.setVibrate(patron);
        builder.setSound(sonido)
                .setAutoCancel(true);
        builder.setContentTitle(titulo).setContentText(Contenido).setPriority(NotificationCompat.PRIORITY_MAX);
        //builder.build().flags|= Notification.GROUP_ALERT_ALL|NotificationCompat.GROUP_ALERT_ALL;
        builder.build().flags|=NotificationCompat.FLAG_ONLY_ALERT_ONCE;
        NotificationManager mNotificationManager;
        mNotificationManager =
                (NotificationManager) contexto.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "Canal1";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Canal notificaciones",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setShowBadge(true);
            channel.enableVibration(true);
            channel.setImportance(NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(contexto);
        notificationManagerCompat.notify("Aviso",1,builder.build());

    }
}
