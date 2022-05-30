package net.ivanvega.audiolibros2020.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.MediaController;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import net.ivanvega.audiolibros2020.DetalleFragment;
import net.ivanvega.audiolibros2020.MainActivity;
import net.ivanvega.audiolibros2020.R;

import java.io.IOException;
import java.net.URI;
import java.util.Random;

public class MiServicio extends Service {

    // Binder given to clients
    private final IBinder binder = new MiServicioBinder();
    private MediaPlayer mediaPlayer;
    int libroId;
    // Random number generator
    private final Random mGenerator = new Random();

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class MiServicioBinder extends Binder {

        public MiServicio getService() {

            return MiServicio.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    /** method for clients */
    public int getRandomNumber() {
        return mGenerator.nextInt(100);
    }

    @Override
    public void onCreate() {

        super.onCreate();

        Log.d("MSAL", "servicio creado");

    }

    public MediaPlayer getMediaPlayer(){
        return mediaPlayer;
    }

    private String CHANNEL_ID="CANALID";
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Este método se manda llamar cuando invocas el servicio con startService()
        //Tarea Pesada

        libroId = intent.getIntExtra("id",0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
            foregroundService();
        }
        Uri uri = Uri.parse(intent.getStringExtra("Audio"));
        mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
        mediaPlayer.start();

        Log.d("MSAL", "Iniciando la tarea pesada "+uri);

        Log.d("MSAL", "Tarea pesada finalizada");

        return START_STICKY;
    }

    Intent notificationIntent;
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void foregroundService() {

        notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.putExtra("rep", libroId);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 1002, notificationIntent, 0);
        Notification notification =
                new Notification.Builder(this, CHANNEL_ID)
                        .setContentTitle("Libro Reproduciendo")
                        .setContentText("Servicio en ejecucion")
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentIntent(pendingIntent)
                        .setTicker("Se inicio el servicio")
                        .build();
            // Notification ID cannot be 0.
             startForeground(1000, notification);

    }





    @Override
    public void onDestroy() {
        stopSelf();
        notificationIntent = null;
        super.onDestroy();
        Log.d("Completo", "Servicio destruido");

    }
}
