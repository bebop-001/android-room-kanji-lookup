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
import android.content.Context.INPUT_METHOD_SERVICE
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.search_window.view.*
import kotlinx.android.synthetic.main.search_window.view.search_ET
import kotlinx.android.synthetic.main.search_window.view.search_clear_BTN

interface SearchOnClick {
    fun searchOnClick(view : View, textIn : String)
}
class SearchWindow @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle : Int = 0
) : RelativeLayout(context, attrs, defStyle) {
    private var searchOnClickListener : SearchOnClick? = null

    private fun hideKeyboard() {
        val imm = context.getSystemService(INPUT_METHOD_SERVICE)
                as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    init {
        Log.d("SearchWindow", "$context:$attrs")
        View.inflate(context, R.layout.search_window, this)

        attrs?.let {
            val typedArray =
                context.obtainStyledAttributes(
                    it, R.styleable.SearchWindow, 0, 0)
            val hint = resources.getText(typedArray.getResourceId(
                R.styleable.SearchWindow_hint, R.string.search))
            search_ET.hint = hint
            val colorId = ContextCompat.getColor(context, typedArray.getResourceId(
                R.styleable.SearchWindow_textColor, android.R.color.tab_indicator_text))
            search_ET.setTextColor(colorId)

            typedArray.recycle()
        }
        search_ET.setOnEditorActionListener { v, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    val textIn = v.text.toString()
                    searchOnClickListener?.searchOnClick(this, textIn)
                    hideKeyboard()
                    Log.d("SearchWindow", "text in:${v.text.toString()}")
                    true
                }
                else -> {
                    false
                }
            }
        }
        search_clear_BTN.setOnClickListener(
            {search_ET.setText("")}
        )
        search_search_BTN.setOnClickListener(fun (_) {
            val textIn = search_ET.text.toString()
            if (text.length > 0) {
                hideKeyboard()
                searchOnClickListener?.searchOnClick(this, textIn)
            }
        })
    }
    var search_btn_visibility : Int
        get() = search_search_BTN.visibility
        set(vis) { search_search_BTN.visibility = vis }
    var text : String
        get() : String  = search_ET.text.toString()
        set(str) { search_ET.setText(str) }
    fun setSearchOnClick(listener: SearchOnClick) {
        searchOnClickListener = listener
    }
}
