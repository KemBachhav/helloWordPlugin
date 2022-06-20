package io.electrosoft.helloworld;

import android.content.Intent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import androidx.annotation.RequiresApi;
import android.app.PendingIntent;
import android.app.Service;
import android.util.Log;

@RequiresApi(api = Build.VERSION_CODES.N)
public class LocationFetchService extends Service {

    private static ScheduledExecutorService scheduler;
    
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("JLS", "Location fetch service started");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            showNotification();
        if (scheduler == null) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
            executeTask();
        } else {
            scheduler.shutdownNow();
            try {
                if (!scheduler.awaitTermination(2, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
            }
            scheduler = Executors.newSingleThreadScheduledExecutor();
            executeTask();
        }
        return START_STICKY;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        startForeground(2, builder.build());
    }

    private void showNotification() {
        startForeground(101, builder.build());
    }

    private void executeTask() {
        scheduler.scheduleAtFixedRate
                (() -> fetchServerLocation(), 0, 5, TimeUnit.SECONDS);
    }

    public void fetchServerLocation() {
        Log.d("In scheduler ---> " + plugin);
    }
    
}
