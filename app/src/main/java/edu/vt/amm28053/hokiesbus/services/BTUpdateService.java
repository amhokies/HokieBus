package edu.vt.amm28053.hokiesbus.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import edu.vt.amm28053.hokiesbus.MainActivity;
import edu.vt.amm28053.hokiesbus.tasks.FetchBTDataTask;

public class BTUpdateService extends Service {

    /*
     *  The binder for this Service.
     */
    private IBinder binder = new LocalBinder();

    /*
     *  Handler to post results on main thread.
     */
    private Handler handler;

    private MainActivity activity;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private int currRand = Integer.MIN_VALUE;

    @Override
    public void onCreate() {
        super.onCreate();

        handler = new Handler();

        startThread();

        Log.d("HokieBus", "BTUpdateService created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("HokieBus", "BTUpdateService started");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        activity = null;
        return super.onUnbind(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return binder;
    }

    public void bindToActivity(MainActivity activity) {
        this.activity = activity;
    }

    public void updateService(final int res) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                currRand = res;
                Log.d("HokieBus", "UpdateService on " + Thread.currentThread().getName());
                Log.d("HokieBus", "Result is " + res);

                // Currently bound to activity
                if (activity != null) {
                    activity.updateActivity(res);
                }
            }
        });
    }

    private void startThread() {
        scheduler.scheduleAtFixedRate(new FetchBTDataTask(this), 0, 15, TimeUnit.SECONDS);
    }

    //An instance of binder will be used to bind with the this service
    // from elsewhere
    public class LocalBinder extends Binder {

        public BTUpdateService getServiceInstance() {
            return BTUpdateService.this;
        }
    }
}
