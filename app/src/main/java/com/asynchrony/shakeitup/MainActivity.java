package com.asynchrony.shakeitup;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.common.collect.EvictingQueue;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

public class MainActivity extends AppCompatActivity {

    public static final int EVENT_COUNT = 10;
    private SensorManager sensorManager;

    private final BehaviorSubject<Float> xPosition = BehaviorSubject.create();
    private final BehaviorSubject<Float> yPosition = BehaviorSubject.create();
    private final BehaviorSubject<Float> zPosition = BehaviorSubject.create();
    private SensorEventListener sensorEventListener;
    private Disposable subscription;
    private AudioPlayer audioPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

//        final Sensor gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
//        Log.i(getClass().getSimpleName(), "default gyro: " + gyroSensor.getName());
//        sensorManager.registerListener(sensorEventListener, gyroSensor, SensorManager.SENSOR_DELAY_NORMAL);

//        subscription = Observable.combineLatest(xPosition, yPosition, zPosition, (x, y, z) -> Math.abs(x) + Math.abs(y) + Math.abs(z))
//                .subscribeOn(Schedulers.computation())
//                .sample(20, TimeUnit.MILLISECONDS)
//                .scan(EvictingQueue.create(EVENT_COUNT), (BiFunction<EvictingQueue<Float>, Float, EvictingQueue<Float>>) (objects, aFloat) -> {
//                    objects.add(aFloat);
//                    return objects;
//                }).map(floats -> {
//                    float sum = 0;
//                    for(Float foo : floats){
//                        sum += foo;
//                    }
//                    return sum/ EVENT_COUNT;
//                })
//                .observeOn(Schedulers.newThread())
//                .subscribe(weightedMagnitude -> {
//                    Log.e("Music", "weighted magnitude was " + weightedMagnitude);
//                    if(weightedMagnitude != null && weightedMagnitude > 1){
//                        audioPlayer.play();
//                    } else {
//                        audioPlayer.pause();
//                    }
//                });

        final Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        subscription = Observable.combineLatest(xPosition, yPosition, zPosition, (x, y, z) -> (x    * x + y * y + z * z))
                .subscribeOn(Schedulers.computation())
                .sample(20, TimeUnit.MILLISECONDS)
                .scan(EvictingQueue.create(EVENT_COUNT), (BiFunction<EvictingQueue<Float>, Float, EvictingQueue<Float>>) (objects, aFloat) -> {
                    objects.add(aFloat);
                    return objects;
                }).map(floats -> {
                    float sum = 0;
                    for(Float foo : floats){
                        sum += foo;
                    }
                    return sum/ EVENT_COUNT;
                })
                .observeOn(Schedulers.newThread())
                .subscribe(weightedMagnitude -> {
                    Log.e("Music", "weighted magnitude was " + weightedMagnitude);
                    if(weightedMagnitude != null && weightedMagnitude > 225){
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

        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose();
        }
    }

    @Override
    protected void onDestroy() {
        audioPlayer.destroy();
        super.onDestroy();
    }
}
