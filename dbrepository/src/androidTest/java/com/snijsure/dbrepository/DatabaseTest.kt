package com.snijsure.dbrepository

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.snijsure.dbrepository.repo.room.DataRepository
import com.snijsure.dbrepository.repo.room.FavoriteDBRepoImpl
import com.snijsure.dbrepository.repo.room.FavoriteEntry
import com.snijsure.dbrepository.repo.room.FavoriteRoomDb
import com.snijsure.utility.CoroutinesContextProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class DatabaseTest {

    @JvmField
    @Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var repo: DataRepository // Object under test
    private var db: FavoriteRoomDb? = null

    @Before
    fun initDb() {
        MockitoAnnotations.initMocks(this)

        db = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getContext(),
            FavoriteRoomDb::class.java
        ).allowMainThreadQueries() // allowing main thread queries, just for testing
            .build()
        val dbImpl = FavoriteDBRepoImpl(db!!.favoriteDao(), CoroutinesContextProvider(
            main = Dispatchers.Unconfined,
            io = Dispatchers.Unconfined,
            computation = Dispatchers.Unconfined)
        )
        repo = DataRepository(dbImpl)
    }

    @After
    fun closeDb() {
        db?.close()
    }

    @Test
    fun insertItems() {

        createEntries()

        var ret = runBlocking { repo.isFavorite("id1") }
        assertTrue(ret == 1)
        ret = runBlocking { repo.isFavorite("id2") }
        assertTrue(ret == 1)
        ret = runBlocking { repo.isFavorite("id3") }
        assertTrue(ret == 0)
    }

    @Test
    fun getAllItems() {

        createEntries()
        val ret = runBlocking {  repo.getFavorites() }

        assertTrue(4 == ret.size)
        assertTrue(ret[3].imdbid == "id5")
        assertTrue(ret[3].poster == "poster5")
    }

    private fun createEntries() {
        // Make sure we find items id1 & id2 but don't find id3
        val fav1 = FavoriteEntry(title = "movie1", imdbid = "id1", poster = "poster1")
        val fav2 = FavoriteEntry(title = "movie2", imdbid = "id2", poster = "poster2")
        val fav4 = FavoriteEntry(title = "movie4", imdbid = "id4", poster = "poster4")
        val fav5 = FavoriteEntry(title = "movie5", imdbid = "id5", poster = "poster5")

        repo.addMovieToFavorites(fav1)
        repo.addMovieToFavorites(fav2)
        repo.addMovieToFavorites(fav4)
        repo.addMovieToFavorites(fav5)
    }

    // This could be extension function in TestUtils.
    @Throws(InterruptedException::class)
    fun <T> getLiveDataValue(liveData: LiveData<T>): T {
        val data = arrayOfNulls<Any>(1)
        val latch = CountDownLatch(1)
        val observer = object : Observer<T> {
            override fun onChanged(t: T?) {
                data[0] = t
                latch.countDown()
                liveData.removeObserver(this)
            }
        }
        liveData.observeForever(observer)
        latch.await(2, TimeUnit.SECONDS)
        return data[0] as T
    }
}