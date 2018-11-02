package com.snijsure.dbrepository

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.snijsure.dbrepository.repo.room.DataRepository
import com.snijsure.dbrepository.repo.room.FavoriteDBRepoImpl
import com.snijsure.dbrepository.repo.room.FavoriteEntry
import com.snijsure.dbrepository.repo.room.FavoriteRoomDb
import com.snijsure.utility.CoroutinesContextProvider
import kotlinx.coroutines.*
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations


@RunWith(AndroidJUnit4::class)
class DatabaseTest {

    @JvmField
    @Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var repo: DataRepository // Object under test
    private var db: FavoriteRoomDb? = null


    @Before
    fun initDb() {
        MockitoAnnotations.initMocks(this)

        db = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getContext(),
            FavoriteRoomDb::class.java
        ).allowMainThreadQueries() //allowing main thread queries, just for testing
            .build()
        val dbImpl = FavoriteDBRepoImpl(db!!.favoriteDao(), CoroutinesContextProvider(
            Dispatchers.Unconfined,
            Dispatchers.Unconfined)
        )
        repo = DataRepository(dbImpl)
    }

    @After
    fun closeDb() {
        db?.close()
    }

    @Test
    fun insertItems() {

        // Make sure we find items id1 & id2 but don't find id3
        val fav1 = FavoriteEntry(title = "movie1",imdbid = "id1",poster = "poster1")
        val fav2 = FavoriteEntry(title = "movie2",imdbid = "id2",poster = "poster2")

        repo.addMovieToFavorites(fav1)
        repo.addMovieToFavorites(fav2)

        var ret = runBlocking {  repo.isFavorite("id1") }
        assertTrue(ret == 1)
        ret = runBlocking {  repo.isFavorite("id2") }
        assertTrue(ret == 1)
        ret = runBlocking {  repo.isFavorite("id3") }
        assertTrue(ret == 0)
    }
}