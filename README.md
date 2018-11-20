This sample demonstrates use of MVVM , Kotlin, Kotlin Coroutines to execute background job.

Sample uses OkHttp, Retrofit to access OMDB API

Sample uses Dagger-2 for DI , that is further used to unit test the view model.

Some notes:

* You can long click on a item item to add it to favorites.

* You will find unit test for MovieViewModel in MovieViewModelTest

* You can run ktlint using target ./gradlew lintKotlin and fix things using formatKotlin

* You can compile APK from command line using ./grawdlew clean assembleDebug and install APK found in ./app/build/outputs/apk/debug/app-debug.apk

* Note you can't build assembleRelease target as there is no signing config defined.

* I created this project as part of my job search, everyone loves to ask cookie cutter question about implement 
  sample app that does some network I/O and display items. 
  __**Of course that is 100% better than freak show that is technical screening, reversing string, counting number of repeating characters, explain how bit.ly is implemented,
  or any other completely irrelevant questions that hacker-rank or other live coding platforms throw up.**__

* Would absolutely love feedback on architecture/code of this *SAMPLE* app. You can reach me at subodh-dot-nijsure-at-gmail.com
