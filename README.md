# ShakeItUp

### Overview

### Checkpoint 0 - Bootstrap an application

1. Open android studio and update everything. Either click on Help->Check for updates if a project is open, or Configurations -> Check for updates if a project is not open.  
2. Create a new android project. Target api 19+ and choose empty activity.  
3. Navigate to AndroidManifest.xml and declare your activity as fixed to portrait orientation (For simplicity sake we will do all our work in this activity and do not wish it to rotate. As an exercise later you can extract logic to a background service).  
```xml
        <activity android:name=".MainActivity"
                android:screenOrientation="portrait" >
```
4. Create a "raw" resources directory by right-clicking src/main/res and selecting new resource directory.  
5. Add one or more of you favorite .mp3 files to the raw folder. Ensure the file names are snake cased (b/c that is what we do with resource files)

### Checkpoint 1 - Implement a music player that plays and pauses an mp3 on button clicks.

1. Add two buttons to your activities layout with text for play and pause.
2. Implement a `MusicPlayer` that wraps and android `MediaPlayer` instance. Give it lifecycle methods for create, destroy, play and pause.
3. Play and Pause functionality of the `MusicPlayer` should not interact with the `MediaPlayer` if it is currently playing or paused respectively. (This will be important later)
4. Initialize your `MusicPlayer` on the activity's `onCreate()` method and release it `onDestroy()`.
5. Give a `View.OnClickListener()` to each of the buttons that performs the play and pause functionality on the music player.
6. Test your implementation.