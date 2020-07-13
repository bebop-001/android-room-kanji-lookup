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