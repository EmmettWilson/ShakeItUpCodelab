package com.asynchrony.shakeitup;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function3;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

public class MainActivity extends AppCompatActivity {


    public static final String TAG = MainActivity.class.getSimpleName();
    private AudioPlayer audioPlayer;
    private final BehaviorSubject<Float> xValue = BehaviorSubject.create();
    private final BehaviorSubject<Float> yValue = BehaviorSubject.create();
    private final BehaviorSubject<Float> zValue = BehaviorSubject.create();

    private SensorEventListener sensorEventListener;
    private SensorManager sensorManager;
    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        audioPlayer = new AudioPlayer();
        audioPlayer.init(this, R.raw.ska_trek);

        findViewById(R.id.play_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioPlayer.play();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(final SensorEvent event) {
                xValue.onNext(event.values[0]);
                xValue.onNext(event.values[1]);
                xValue.onNext(event.values[2]);
            }

            @Override
            public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
                //No op
            }
        };
        final Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        disposable = xValue.subscribe(new Consumer<Float>() {
            @Override
            public void accept(Float aFloat) throws Exception {
                Log.i(TAG, "Received xValue " + aFloat);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(sensorEventListener);
        if(disposable != null && !disposable.isDisposed()){
            disposable.dispose();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        audioPlayer.destroy();
    }
}
