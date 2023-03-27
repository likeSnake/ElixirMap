package net.map.elixirmap.act

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mapbox.search.*
import com.mapbox.search.ServiceProvider

import com.mapbox.search.common.AsyncOperationTask
import com.mapbox.search.record.HistoryRecord
import com.mapbox.search.result.SearchResult
import com.mapbox.search.result.SearchSuggestion
import net.map.elixirmap.R
import net.map.elixirmap.adapter.InfoAdapter
import net.map.elixirmap.bean.ResultsBean
import net.map.elixirmap.util.DistanceUtil.getDistances

class SearchAct : AppCompatActivity(), View.OnClickListener {
    private val historyDataProvider =
        ServiceProvider.INSTANCE.historyDataProvider()
    private var task: AsyncOperationTask? = null
    var list: MutableList<ResultsBean> = ArrayList()
    private var destinationAdapter: InfoAdapter? = null
    private var rv_search_result: RecyclerView? = null
    private var rv_search_record: RecyclerView? = null
    private var et_key: EditText? = null
    private var searchEngine: SearchEngine? = null
    private var searchRequestTask: AsyncOperationTask? = null
    private var tv_search_title: TextView? = null
    val options: SearchOptions = SearchOptions.Builder()
        .limit(20)
        .build()
    private val searchCallback: SearchSelectionCallback = object : SearchSelectionCallback {
        override fun onSuggestions(
            suggestions: List<SearchSuggestion>,
            responseInfo: ResponseInfo
        ) {
            list.clear()
            if (suggestions.isEmpty()) {
                Log.i("SearchApiExample", "没搜索到")
            } else {
                tv_search_title!!.text = "SEARCH RESULT"
                Log.i("SearchApiExample", "总共搜索到: $suggestions\nSelecting first...")
                for (i in suggestions.indices) {
                    searchRequestTask = searchEngine!!.select(suggestions[i], this)
                }
            }
        }

        override fun onResult(
            suggestion: SearchSuggestion,
            result: SearchResult,
            info: ResponseInfo
        ) {
            Log.i("SearchApiExample", "精确地址: $result")
            var addressInfo = ""
            val point = result.coordinate
            val address = result.address!!
            val country = address.country
            val region = address.region
            val place = address.place
            val locality = address.locality
            val street = address.street
            val name = result.name
            val MyPoint = result.requestOptions.options.proximity
            if (country != null) {
                addressInfo = "$country "
            }
            if (region != null) {
                addressInfo += "$region "
            }
            if (place != null) {
                addressInfo += "$place "
            }
            if (locality != null) {
                addressInfo += "$locality "
            }
            if (street != null) {
                addressInfo += street
            }
            //  System.out.println(point.longitude()+" "+ point.latitude()+ " "+MyPoint.longitude()+" "+ MyPoint.latitude());
            val distances = getDistances(
                point.longitude(),
                point.latitude(),
                MyPoint!!.longitude(),
                MyPoint.latitude()
            )
            println("-地址名:$name-地址:$addressInfo-距离:$distances")
            list.add(ResultsBean(name, addressInfo, "$distances km", point))
            start(false, list)
            rv_search_record!!.visibility = View.GONE
            rv_search_result!!.visibility = View.VISIBLE
        }

        override fun onCategoryResult(
            suggestion: SearchSuggestion,
            results: List<SearchResult>,
            responseInfo: ResponseInfo
        ) {
            Log.i("SearchApiExample", "Category search results: $results")
        }

        override fun onError(e: Exception) {
            Log.i("SearchApiExample", "Search error: ", e)
        }
    }

    // 检索搜索历史
    private val callback = object :CompletionCallback<List<HistoryRecord>> {
            override fun onComplete(result: List<HistoryRecord>) {
                Log.i("SearchApiExample", "所有搜索历史: $result")
                println(result.size)
                for (i in result.indices) {
                    println(result[i].name)
                }
            }

            override fun onError(e: Exception) {
                Log.i("SearchApiExample", "Unable to retrieve history records", e)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_search)
        searchEngine = SearchEngine.createSearchEngineWithBuiltInDataProviders(
            SearchEngineSettings(this.getString(R.string.mapbox_access_token))
        )
        println(intent.getStringExtra("longitude"))
        et_key = findViewById(R.id.et_key)
        et_key!!.setOnClickListener(this)
        tv_search_title = findViewById(R.id.tv_search_title)
        rv_search_result = findViewById(R.id.rv_search_result)
        rv_search_record = findViewById(R.id.rv_search_record)
        task = historyDataProvider.getAll(callback)
        et_key!!.setOnEditorActionListener(OnEditorActionListener { _, actionId, event ->
            if ((actionId == 0 || actionId == 3) && event != null) {
                (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(
                        this@SearchAct.currentFocus
                            !!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS
                    )
                println("开始搜索")
                searchRequestTask =
                    searchEngine!!.search(et_key!!.text.toString(), options, searchCallback)
            }
            false
        })
        et_key!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {}
        })
        // searchEngine = MapboxSearchSdk.getSearchEngine();
    }

    override fun onDestroy() {
        if (searchRequestTask != null) {
            searchRequestTask!!.cancel()
        }
        if (task != null) {
            task!!.cancel()
        }
        super.onDestroy()
    }

    override fun onClick(v: View) {
        when (v.id) {
        }
    }

    fun start(b: Boolean, list: List<ResultsBean>?) {
        val manager = LinearLayoutManager(this@SearchAct, LinearLayoutManager.VERTICAL, false)
        destinationAdapter = InfoAdapter(list, this)
        if (b) {
            rv_search_result!!.scrollToPosition(destinationAdapter!!.itemCount - 1)
        }
        rv_search_result!!.layoutManager = manager
        rv_search_result!!.adapter = destinationAdapter
    }
}