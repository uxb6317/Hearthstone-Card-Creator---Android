package com.example.student.project


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.makeramen.roundedimageview.RoundedImageView
import kotlinx.android.synthetic.main.fragment_card.*
import kotlinx.android.synthetic.main.fragment_card.view.*
import java.io.IOException
import android.os.Environment.getExternalStorageDirectory
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.util.*


class CardFrag() : Fragment() {
    var mainActivity: MainActivity? = null
    var dataPasser: DataInputI? = null
    var mView: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_card, container, false)
        mView = view

        val card = arguments!!.getSerializable("card") as Card
        updateCard(card)

        mainActivity = activity as MainActivity?

        view.manaCost.setOnClickListener {
            dataPasser!!.newData("Cost", mainActivity!!.card.cost)
        }

        view.healthDurabilityValue.setOnClickListener {
            dataPasser!!.newData("Health", mainActivity!!.card.health)
        }

        view.attackValue.setOnClickListener {
            dataPasser!!.newData("Attack", mainActivity!!.card.attack)
        }

        view.name.setOnClickListener {
            dataPasser!!.newData("Name", mainActivity!!.card.name)
        }

        view.description.setOnClickListener {
            dataPasser!!.newData("Description", mainActivity!!.card.description)
        }

        view.uploadImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 1)
        }

        view.undo.setOnClickListener {
            mainActivity!!.undo()
        }

        view.redo.setOnClickListener {
            mainActivity!!.redo()
        }
        // Return the fragment view/layout
        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        dataPasser = context as DataInputI
    }

    @Throws(IOException::class)
    private fun getBitmapFromUri(selectedImageUri: Uri?): Bitmap {
        val parcelFileDescriptor = activity!!.contentResolver.openFileDescriptor(selectedImageUri!!, "r")
        val fileDescriptor = parcelFileDescriptor!!.fileDescriptor
        val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        parcelFileDescriptor.close()
        return image
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            // get the image
            try {
                val selectedImage = data.data
                val bmp = getBitmapFromUri(selectedImage)
                mainActivity!!.card.image = bmp
                mainActivity!!.addToHistory(mainActivity!!.card)
                cardImage.setImageBitmap(mainActivity!!.card.image)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(mainActivity, "Please try again", Toast.LENGTH_LONG).show()
            }
        }
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
        mView!!.description.setText(cardDescription)

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

            val descMarginParams: ViewGroup.MarginLayoutParams = mView!!.description.layoutParams as ViewGroup.MarginLayoutParams
            descMarginParams.bottomMargin = resources.getDimension(R.dimen.minion_desc_margin_bottom).toInt()

            mView!!.cardImage.requestLayout()

            mView!!.cardImage.layoutParams.width = resources.getDimension(R.dimen.minion_card_width).toInt()
            mView!!.cardImage.layoutParams.height = resources.getDimension(R.dimen.minion_card_height).toInt()

            val marginParams: ViewGroup.MarginLayoutParams = mView!!.cardImage.layoutParams as ViewGroup.MarginLayoutParams
            marginParams.bottomMargin = resources.getDimension(R.dimen.minion_card_margin_bottom).toInt()
            marginParams.leftMargin = resources.getDimension(R.dimen.minion_card_margin_left).toInt()

            val ri: RoundedImageView = mView!!.cardImage
            ri.isOval = true
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

            val descMarginParams: ViewGroup.MarginLayoutParams = mView!!.description.layoutParams as ViewGroup.MarginLayoutParams
            descMarginParams.bottomMargin = resources.getDimension(R.dimen.spell_desc_margin_bottom).toInt()

            mView!!.cardImage.requestLayout()

            // image width and height
            mView!!.cardImage.layoutParams.width = resources.getDimension(R.dimen.spell_card_width).toInt()
            mView!!.cardImage.layoutParams.height = resources.getDimension(R.dimen.spell_card_height).toInt()

            // margins
            val imageMarginParams: ViewGroup.MarginLayoutParams = mView!!.cardImage.layoutParams as ViewGroup.MarginLayoutParams
            imageMarginParams.bottomMargin = resources.getDimension(R.dimen.spell_card_margin_bottom).toInt()
            imageMarginParams.leftMargin = resources.getDimension(R.dimen.spell_card_margin_left).toInt()

            // not oval
            val ri: RoundedImageView = mView!!.cardImage
            ri.isOval = false
        }


        // set image
        mView!!.cardImage.setImageBitmap(card.image)

        val context = mView!!.cardTemplate.getContext()
        val id = context.resources.getIdentifier(templateName, "drawable", context.packageName)
        mView!!.cardTemplate.setImageResource(id)
    }

    fun showUndo() {
        undo.visibility = View.VISIBLE
    }

    fun hideUndo() {
        undo.visibility = View.INVISIBLE
    }

    fun showRedo() {
        redo.visibility = View.VISIBLE
    }

    fun hideRedo() {
        redo.visibility = View.INVISIBLE
    }
}

fun EditText.valueUpdated(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
    })
}