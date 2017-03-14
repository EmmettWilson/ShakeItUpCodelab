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
