
This sample demonstrates use of MVVM , Kotlin, Kotlin Coroutines to execute background job.

Sample uses OkHttp, Retrofit to access OMDB API

Sample uses Dagger-2 for DI , that is further used to unit test the view model.

You can access example from https://github.com/snijsure/salesforce-omdb.git which is a 
private repo, to gain access send email to subodh.nijsure@gmail.com 

Some notes:

* OMDBApi now requires API key , currently this key is provided via app/build.gradle, one could
  write JNI interface to make the key less accessible. Further that key could be encrypted with
  some internal salt etc. etc.

* You can long click on a item item to add it to favorites.

* Currently favorites are stored in shared preferences, one could of course use Room to store that that.

* You will find unit test for MovieViewModel in MovieViewModelTest

* You can compile APK from command line using ./grawdlew clean assembleDebug and install APK found in ./app/build/outputs/apk/debug/app-debug.apk

* Note you can't build assembleRelease target as there is no signing config defined.
