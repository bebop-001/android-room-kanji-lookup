/*
 * Copyright 2020 Steven Smith (sjs@kana-tutor.com)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kana_tutor.example.room_kanji_lookup

import android.content.Context
import androidx.room.Room
import java.lang.Exception
import java.lang.StringBuilder
import androidx.room.*

@Database(entities = [Kanji::class], version = 1001, exportSchema = true)

abstract class AppDatabase : RoomDatabase() {
    abstract val kanjiDAO: KanjiDAO
}
@Entity(tableName = "Kanji")
data class Kanji (
    @PrimaryKey val kanji: String,
    val nelsonIndex:        Int?,
    val meaning:            String?,
    val heisigKeyword:      String?,
    val strokeCount:        Int,
    val onYomi:             String?,
    val kunYomi:            String?,
    val nanori:             String?,
    val radicals:           String?
) {
    fun compare(that: Kanji) : Int {
        if (this.strokeCount != that.strokeCount)
            return this.strokeCount.compareTo(that.strokeCount)
        return this.kanji.compareTo(that.kanji)
    }
    fun equals(that: Kanji) = this.kanji.compareTo(that.kanji)
}
@Dao
interface KanjiDAO {
    @Query("SELECT * FROM Kanji WHERE kanji = :kanji")
    fun getKanji(kanji: String): Array<Kanji>
}
class KanjiDbSearch private constructor () {
    companion object {
        private var INSTANCE: KanjiDbSearch? = null
        private lateinit var kanjiDAO: KanjiDAO

        fun getInstance(context: Context): KanjiDbSearch {
            synchronized(this) {
                if (INSTANCE == null)
                    INSTANCE = KanjiDbSearch()
                val instance = INSTANCE!!
                kanjiDAO =
                    Room.databaseBuilder(context, AppDatabase::class.java, "AniKanjiDB.sqlite")
                        .allowMainThreadQueries()
                        .createFromAsset("db/AniKanjiDB.sqlite")
                        .build().kanjiDAO
                return instance
            }
        }
    }
    fun getKanjiIn(stringIn : String) : List<Pair<String, Kanji?>> {
        val rv = mutableListOf<Pair<String,Kanji?>>()
        var kanjiIn = ".".toRegex().findAll(stringIn)
        for (match in kanjiIn) {
            val k = match.value
            val result = kanjiDAO.getKanji(k)
            rv.add(Pair(k, if(result.size == 0) null else result[0]))
        }
        return rv
    }
}
