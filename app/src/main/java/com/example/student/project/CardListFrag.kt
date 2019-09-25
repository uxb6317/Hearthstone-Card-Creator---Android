package com.example.student.project

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_card_list.*
import kotlinx.android.synthetic.main.fragment_card_list.view.*
import okhttp3.*
import java.io.IOException

class CardListFrag : Fragment() {
    var listAdapter: ListAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_card_list, container, false)
        val cardListRecycler: RecyclerView = view.cardList

        listAdapter = ListAdapter()
        cardListRecycler.adapter = listAdapter
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
        cardListRecycler.layoutManager = layoutManager

        view.existingCardsSpinner.setSelection(0, false) // don't do anything when user selects the first item in spinner
        view.existingCardsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val className = parent!!.getItemAtPosition(position).toString()
                fetchCards(className)
            }

        }
        return view
    }

    fun fetchCards(className: String) {
        val url = "https://omgvamp-hearthstone-v1.p.mashape.com/cards/classes/$className?collectible=1"

        val request = Request.Builder().header("X-Mashape-Key", "TSzxux0GdymshSdMijkrTNKD63nHp1s5X0hjsnPx4gMmRHPU64")
                .url(url).build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.d("fail", "fetch fail")
            }

            override fun onResponse(call: Call?, response: Response?) {
                val body = response?.body()?.string()
                val gson = GsonBuilder().create()
                val existingCards = gson.fromJson(body, Array<ExistingCard>::class.java)

                Cards.cardImgs = arrayListOf()
                Cards.cardNames = arrayListOf()
                for (card in existingCards) {
                    Cards.cardNames.add(card.name)
                    Cards.cardImgs.add(card.img)
                }

                activity!!.runOnUiThread {
                    listAdapter!!.notifyDataSetChanged()
                }
            }
        })
    }
}

class ExistingCard(val name: String, val img: String) {

}