package com.example.student.project

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_preview.view.*
import java.io.File
import java.io.FileOutputStream
import java.util.*

class PreviewFrag() : Fragment() {
    var mView: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_preview, container, false)
        mView = view

        val card = arguments!!.getSerializable("card") as Card
        updateCard(card)

        return view
    }

    fun updateCard(card: Card) {
        // template image
        val rarity = card.rarity.toLowerCase() // rarity
        val className = card.className.toLowerCase() // class
        var templateName = ""

        // name
        val cardName = card.name
        mView!!.name.setText(cardName)

        // description
        val cardDescription = card.description
        mView!!.descriptionText.setText(cardDescription)

        // mana cost
        val cardCost = card.cost
        mView!!.manaCost.setText(cardCost)

        if (card.type.equals("Minion", true) || card.type.equals("Weapon", true)) {
            mView!!.attackValue.visibility = View.VISIBLE
            mView!!.healthDurabilityValue.visibility = View.VISIBLE

            // attack value
            val cardAttack = card.attack
            mView!!.attackValue.setText(cardAttack)

            val manaMarginParams: ViewGroup.MarginLayoutParams = mView!!.manaCost.layoutParams as ViewGroup.MarginLayoutParams
            manaMarginParams.leftMargin = resources.getDimension(R.dimen.minion_cost_margin_left).toInt()

            val descMarginParams: ViewGroup.MarginLayoutParams = mView!!.descriptionText.layoutParams as ViewGroup.MarginLayoutParams
            descMarginParams.bottomMargin = resources.getDimension(R.dimen.minion_desc_margin_bottom).toInt()

            mView!!.cardImage.requestLayout()

            mView!!.cardImage.layoutParams.width = resources.getDimension(R.dimen.minion_card_width).toInt()
            mView!!.cardImage.layoutParams.height = resources.getDimension(R.dimen.minion_card_height).toInt()

            val marginParams: ViewGroup.MarginLayoutParams = mView!!.cardImage.layoutParams as ViewGroup.MarginLayoutParams
            marginParams.bottomMargin = resources.getDimension(R.dimen.minion_card_margin_bottom).toInt()
            marginParams.leftMargin = resources.getDimension(R.dimen.minion_card_margin_left).toInt()
        }

        if (card.type.equals("Minion", true)) {
            // health value
            val cardHealth = card.health
            mView!!.healthDurabilityValue.setText(cardHealth)
            // set minion race

            templateName = className + "_" + rarity + "_minion"
        }

        if (card.type.equals("Weapon", true)) {
            val cardDurability = card.durability
            mView!!.healthDurabilityValue.setText(cardDurability)
            templateName = className + "_" + rarity + "_weapon"
        }

        if (card.type.equals("Spell", true)) {
            mView!!.attackValue.visibility = View.INVISIBLE
            mView!!.healthDurabilityValue.visibility = View.INVISIBLE
            // set minion race to invisible

            templateName = className + "_" + rarity + "_spell"

            val manaMarginParams: ViewGroup.MarginLayoutParams = mView!!.manaCost.layoutParams as ViewGroup.MarginLayoutParams
            manaMarginParams.leftMargin = resources.getDimension(R.dimen.spell_cost_margin_left).toInt()

            val descMarginParams: ViewGroup.MarginLayoutParams = mView!!.descriptionText.layoutParams as ViewGroup.MarginLayoutParams
            descMarginParams.bottomMargin = resources.getDimension(R.dimen.spell_desc_margin_bottom).toInt()

            mView!!.cardImage.requestLayout()

            // image width and height
            mView!!.cardImage.layoutParams.width = resources.getDimension(R.dimen.spell_card_width).toInt()
            mView!!.cardImage.layoutParams.height = resources.getDimension(R.dimen.spell_card_height).toInt()

            // margins
            val imageMarginParams: ViewGroup.MarginLayoutParams = mView!!.cardImage.layoutParams as ViewGroup.MarginLayoutParams
            imageMarginParams.bottomMargin = resources.getDimension(R.dimen.spell_card_margin_bottom).toInt()
            imageMarginParams.leftMargin = resources.getDimension(R.dimen.spell_card_margin_left).toInt()

        }
        // set image
        mView!!.cardImage.setImageBitmap(card.image)

        val context = mView!!.cardTemplate.getContext()
        val id = context.resources.getIdentifier(templateName, "drawable", context.packageName)
        mView!!.cardTemplate.setImageResource(id)
    }

    fun takeScreenshot() {
        val now = Date()
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now)

        try {
            val sdCard = Environment.getExternalStorageDirectory()
            val dir = File(sdCard.absolutePath + "/CustomCards")
            dir.mkdirs()

            // create bitmap screen capture
            mView!!.setDrawingCacheEnabled(true)
            val bitmap = Bitmap.createBitmap(mView!!.getDrawingCache())
            mView!!.setDrawingCacheEnabled(false)

            val fileName = String.format("%d.png", System.currentTimeMillis())
            val outFile = File(dir, fileName)

            val outputStream = FileOutputStream(outFile)
            val quality = 100
            bitmap.compress(Bitmap.CompressFormat.PNG, quality, outputStream)
            outputStream.flush()
            outputStream.close()
        } catch (e: Throwable) {
            // Several error may come out with file handling or DOM
            e.printStackTrace()
        }
    }
}