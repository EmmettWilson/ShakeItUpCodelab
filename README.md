# ShakeItUp

### Overview

RxJava is a Functional Reactive Paradigm. It is essentially the Observer pattern, but taken to the extreme. In the simplest of cases you have observables and observers. The real power of Reactive Extensions comes from chaining observables in a functional paradigm. This requires a bit of a mind shift from standard Object oriented programming so do not expect to full grok it today. The goal of this codelab is to play with RxJava a bit using a fun project. Hopefully by the end you will see a little bit of the power that RxJava provides to Android Development. Some of the things that will will touch on briefly include:

- How to turn a callback into an observable using a `Subject`
- How to take multiple observables and combine the results into a cohesive unit.
- How to put operations on a background thread and recieve the result on another thread.
- How to mutate data by chaining Observables in a functional way. (Do not hold state)
- A grab bag of operators.

The Codelab starts intentionally slow. Everyone leaving should have a finished app that plays music only while the user is dancing within an hour or so. For those more advanced or wanting to explore more there are open ended further steps at the end. Feel free to move ahead at your own pace.

### Checkpoint 0 - Bootstrap an application

- Open android studio and update everything. Either click on Help->Check for updates if a project is open, or Configurations -> Check for updates if a project is not open.  
- Create a new android project. Target api 19+ and choose empty activity.  
- Navigate to AndroidManifest.xml and declare your activity as fixed to portrait orientation (For simplicity sake we will do all our work in this activity and do not wish it to rotate. As an exercise later you can extract logic to a background service).  
```xml
        <activity android:name=".MainActivity"
                android:screenOrientation="portrait" >
```
- Create a "raw" resources directory by right-clicking src/main/res and selecting new resource directory.  
- Add one or more of you favorite .mp3 files to the raw folder. Ensure the file names are snake cased (b/c that is what we do with resource files)

### Checkpoint 1 - Implement a music player that plays and pauses an mp3 on button clicks.

- Add two buttons to your activities layout with text for play and pause.
- Implement a `MusicPlayer` that wraps and android `MediaPlayer` instance. Give it lifecycle methods for create, destroy, play and pause.
- Play and Pause functionality of the `MusicPlayer` should not interact with the `MediaPlayer` if it is currently playing or paused respectively. (This will be important later)
- Initialize your `MusicPlayer` on the activity's `onCreate()` method and release it `onDestroy()`.
- Give a `View.OnClickListener()` to each of the buttons that performs the play and pause functionality on the music player.
- Test your implementation.

### Checkpoint 2 - Add RxJava, RxAndroid, and Retrolambda to your project.

- Add Retrolambda to your project. Navigate to your app level build.gradle. Apply the following buildscript and apply the retrolambda plugin. This will allow you to use lambdas even though Android is currently limited to java 7. This is not necessary, but cleans up the code and makes it much more readable.
```groovy
buildscript {
        repositories {
            mavenCentral()
        }
        dependencies {
            classpath 'me.tatarka:gradle-retrolambda:3.2.4'
        }
}
``` 
- Add the following lines to the compile block of your app level build.gradle to import RxJava and RxAndroid dependencies. The latest versions can be found on the RxAndroid Github https://github.com/ReactiveX/RxAndroid
```groovy
compile 'io.reactivex.rxjava2:rxandroid:2.0.1'
compile 'io.reactivex.rxjava2:rxjava:2.0.1'
```
- In the Android block of your build.gradle add the following compile options. 
```
compileOptions {
    targetCompatibility 1.8
    sourceCompatibility 1.8
}
```
- Clean and compile your app. Test it and ensure everything is still working.

### Checkpoint 3 - Make observables of the Accelerometers X,Y, and Z values.

In this step we will be taking a callback provided by an external dependency and turn it into a stream of events using a `PublishSubject`. While not necessary we will be creating an Observable for the emmited x, y , and z values. This will allow us to demonstrate one of the most useful Operators for Android Development. 

- Using the following lines of code register a listener to the accelerometer.
```java
sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
final Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
sensorEventListener = new SensorEventListener() {
    @Override
    public void onSensorChanged(final SensorEvent event) {
    }

   @Override
   public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
   }
};
```
- Create three `PublishSubject`'s in your activity using the `PublishSubject.create()` method.
- In the `onSensorChanged` method publish the events to your `PublishSubject` using the `onNext()` method with each of your sensor values.
- In on Start you can now subscribe to one of your `PublishSubject`'s like so
```java       
Disposable disposable = xValue.subscribe(new Consumer<Float>() {
               @Override
               public void accept(Float aFloat) throws Exception {
                   Log.i(MainActivity.class.getSimpleName(), "Received xValue " + aFloat);
               }
           });`
```
- Make sure you dispose of your disposable in the appropriate lifecycle method to ensure we do not have a memory leak. When using sensors this is important because the service is longer lived than our activity.
- If Retrolambda is correctly configured you can the reduce this to a lambda using alt+enter
```java 
disposable = xValue.subscribe(aFloat -> Log.i(TAG, "Received xValue " + aFloat));
```
- At this point you should be observing the xValues emitted by the accelerometer and logging them to the android monitor. Run the app and (carefully) shake your phone to ensure they are correctly being emitted.
 
 
### Next Steps:  A few Ideas

- Turn you MusicPlayer into an observable that emits progress for the mp3. Show progress on screen as the user plays the file. 
- Adjust the volume of the device track based on the vigor of the shake.
- Show mp3's in a recycler view. Allow user to select which mp3 they wish to listen to.
- Move the music player out of the activity and into a background service. Allowing the music to be played while the screen is off.
- Sense different types of movement and play a different sound file (ex Walking, Cycling, Runnning, Jumping)
- Make an Android Things music player, using an accelerometer and speaker.