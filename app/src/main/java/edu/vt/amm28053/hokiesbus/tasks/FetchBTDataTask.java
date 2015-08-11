package edu.vt.amm28053.hokiesbus.tasks;

import android.util.Log;

import java.util.Random;

import edu.vt.amm28053.hokiesbus.services.BTUpdateService;

/**
 * Created by Alex on 8/10/2015.
 */
public class FetchBTDataTask implements Runnable {

    private BTUpdateService service;

    public FetchBTDataTask(BTUpdateService service) {
        this.service = service;
    }

    @Override
    public void run() {
        int rand = 0;
        try {
            Log.d("HokieBus", "Going out to server... on thread " + Thread.currentThread().getName());
            Thread.sleep(2000);

            rand = new Random(System.currentTimeMillis()).nextInt();

            Log.d("HokieBus", "I'm back from the server! Random number: " + rand);


        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        service.updateService(rand);
    }
}
