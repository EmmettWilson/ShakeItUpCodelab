package com.asynchrony.shakeitup;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;

import com.google.common.collect.EvictingQueue;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

public class MainActivity extends AppCompatActivity {

    public static final int EVENT_COUNT = 10;
    private SensorManager sensorManager;

    private final BehaviorSubject<Float> xPosition = BehaviorSubject.create();
    private final BehaviorSubject<Float> yPosition = BehaviorSubject.create();
    private final BehaviorSubject<Float> zPosition = BehaviorSubject.create();
    private SensorEventListener sensorEventListener;
    private Subscription subscription;
    private AudioPlayer audioPlayer;
    private EvictingQueue<Float> events;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        events = EvictingQueue.create(EVENT_COUNT);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(final SensorEvent event) {
                Log.e(MainActivity.class.getSimpleName(), "sensor event: " + Arrays.toString(event.values));
                xPosition.onNext(event.values[0]);
                yPosition.onNext(event.values[1]);
                zPosition.onNext(event.values[2]);
            }

            @Override
            public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
                //No op 
            }
        };

        audioPlayer = new AudioPlayer();
        audioPlayer.create(this, R.raw.ska_trek);
    }

    @Override
    protected void onStart() {
        super.onStart();

        final Sensor gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        Log.i(getClass().getSimpleName(), "default gyro: " + gyroSensor.getName());
        sensorManager.registerListener(sensorEventListener, gyroSensor, SensorManager.SENSOR_DELAY_NORMAL);

        subscription = Observable.combineLatest(xPosition, yPosition, zPosition, (x, y, z) -> Math.abs(x) + Math.abs(y) + Math.abs(z))
                .sample(20, TimeUnit.MILLISECONDS)
                .map(list -> list)
                .map(rawMagnitude -> {
                    events.add(rawMagnitude);
                    float sum = 0;
                    for(Float foo : events){
                        sum += foo;
                    }
                    return sum/ EVENT_COUNT;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(weightedMagnitude -> {
                    Log.e("Music", "weighted magnitude was " + weightedMagnitude);
                    if(weightedMagnitude != null && weightedMagnitude > 3){
                        audioPlayer.play();
                    } else {
                        audioPlayer.pause();
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();

        sensorManager.unregisterListener(sensorEventListener);

        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    @Override
    protected void onDestroy() {
        audioPlayer.destroy();
        super.onDestroy();
    }
}
