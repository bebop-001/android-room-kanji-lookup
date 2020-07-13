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
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SearchOnClick {
    companion object {
        var found = mutableListOf<String>()
    }
    private lateinit var kanjiDAO : KanjiDAO
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GlobalScope.launch(Dispatchers.IO) {
            KanjiDbSearch.getInstance(this@MainActivity.applicationContext)
        }
        setContentView(R.layout.activity_main)
        search_window.setSearchOnClick(this)
    }

    override fun searchOnClick(view: View, textIn: String) {
        val kanjiFound = KanjiDbSearch
            .getInstance(this.applicationContext)
            .getKanjiIn(textIn)
        val newFound = mutableListOf<String>()
        newFound.add("========== $textIn")
        for ((key, value) in kanjiFound) {
            newFound.add(
                if (value == null) "$key => ???"
                else "$key => $value"
            )
        }
        found.addAll(0, newFound)
        found.take(20)
        search_results_TV.text = found.joinToString("\n")
    }
}
