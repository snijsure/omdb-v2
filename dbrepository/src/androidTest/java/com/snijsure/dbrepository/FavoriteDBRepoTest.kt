package com.snijsure.dbrepository

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.persistence.room.Room
import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.util.Config
import com.snijsure.dbrepository.repo.room.*
import com.snijsure.utility.CoroutinesContextProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.junit.After
import org.junit.Before

import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith


class FavoriteDBRepoTest {
    /* @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @JvmField
    @Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    */

    lateinit var repo: DataRepository // Object under test
    private var db: FavoriteRoomDb? = null


    @Before
    fun initDb() {
        val context: Context = InstrumentationRegistry.getTargetContext()

        db = Room.inMemoryDatabaseBuilder(
            context,
            FavoriteRoomDb::class.java
        ).allowMainThreadQueries() //allowing main thread queries, just for testing
            .build()
        val x = FavoriteDBRepoImpl(db!!.favoriteDao(),CoroutinesContextProvider(Dispatchers.Unconfined,Dispatchers.Unconfined))
        repo = DataRepository(x)
    }

    @After
    fun closeDb() {
        db?.close()
    }

    @Test
    fun insertItems() {
        val fav1 = FavoriteEntry(title = "movie1",imdbid = "id1",poster = "poster1")
        val fav2 = FavoriteEntry(title = "movie2",imdbid = "id2",poster = "poster2")

        repo.addMovieToFavorites(fav1)
        repo.addMovieToFavorites(fav2)

      GlobalScope.launch(Dispatchers.Unconfined) {
            var ret = async {repo.isFavorite("id1")}.await()
            assert(ret == 1)
           ret = async {repo.isFavorite("id2")}.await()

          assert(ret == 1)

      }
    }
}