package com.example.student.project

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import android.widget.ArrayAdapter


class MainActivity : AppCompatActivity(), DialogI, DataInputI {
    var card = Card()
    var cardFrag = CardFrag()
    var cardListFrag = CardListFrag()
    var cardPreviewFrag = PreviewFrag()

    var historyStack: ArrayList<Card> = arrayListOf(card.clone() as Card) // keep track of history of edits
    var redoStack: ArrayList<Card> = arrayListOf() // keep track of available redos

    val classSpinnerItems = arrayOf("Class", "Neutral", "Warrior", "Mage", "Rogue", "Warlock", "Shaman", "Paladin", "Priest", "Druid", "Hunter")
    val typeSpinnerItems = arrayOf("Type", "Minion", "Spell", "Weapon", "Hero")
    val raritySpinnerITems = arrayOf("Rarity", "Collectable", "Common", "Rare", "Epic", "Legendary")
    val raceSpinnerItems = arrayOf("Race", "Beast", "Demon", "Dragon", "Mech", "Murloc", "Pirate", "Totem", "Elemental")


    override fun newData(type: String, data: String) {
        val ft = supportFragmentManager.beginTransaction()
        val newFragment = Input.newInstance(type, data)
        newFragment.show(ft, "input")
    }

    override fun okHandler(type: String?, data: String) {
        when (type) {
            "Health" -> card.health = data
            "Cost" -> card.cost = data
            "Attack" -> card.attack = data
            "Name" -> card.name = data
            "Description" -> card.description = data
            else -> {
                print("No matching types found!")
            }
        }
        addToHistory(card)
        cardFrag.updateCard(card)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // show the card edit fragment on startup
        if (supportFragmentManager.findFragmentById(R.id.content) == null) {
            val args = Bundle()
            args.putSerializable("card", card)
            cardFrag.arguments = args
            supportFragmentManager.beginTransaction()
                    .add(R.id.content, cardFrag)
                    .commit()
        }

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_edit -> {
                    val args = Bundle()
                    args.putSerializable("card", card)
                    cardFrag.arguments = args
                    supportFragmentManager.beginTransaction()
                            .replace(R.id.content, cardFrag)
                            .commit()
                    it.isChecked = true
                }
                R.id.action_preview -> {
                    val args = Bundle()
                    args.putSerializable("card", card)
                    cardPreviewFrag.arguments = args
                    supportFragmentManager.beginTransaction()
                            .replace(R.id.content, cardPreviewFrag)
                            .commit()
                    it.isChecked = true
                }
                R.id.action_share -> {
                    supportFragmentManager.beginTransaction()
                            .replace(R.id.content, cardListFrag)
                            .commit()
                    it.isChecked = true
                }
                R.id.action_save -> {
                    val args = Bundle()
                    args.putSerializable("card", card)
                    cardPreviewFrag.arguments = args
                    supportFragmentManager.beginTransaction()
                            .replace(R.id.content, cardPreviewFrag)
                            .commit()
                    it.isChecked = true
                    cardPreviewFrag.takeScreenshot()
                }
            }
            false
        }

        classSpinner.setSelection(0, false) // don't do anything when user selects the first item in spinner
        classSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val className = parent!!.getItemAtPosition(position).toString()
                card.className = className
                addToHistory(card)
                cardFrag.updateCard(card) // update the UI with the latest card change
            }

        }

        raritySpinner.setSelection(0, false)
        raritySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val rarity = parent!!.getItemAtPosition(position).toString()
                card.rarity = rarity
                addToHistory(card)
                cardFrag.updateCard(card) // update the UI with the latest card change
            }

        }

        typeSpinner.setSelection(0, false)
        typeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val type = parent!!.getItemAtPosition(position).toString()
                // if the new selected type is not equal to old card type
                if (card.type.equals("Minion", true) && type.equals("Spell", true)) {
                    // if switching from minion to spell, we set attack, race, and health to empty string.
                    card.attack = ""
                    card.health = ""
                    card.race = ""
                } else if (card.type.equals("Minion", true) && type.equals("Weapon", true)) {
                    // if switching from minion to weapon, set health and race to empty
                    card.health = ""
                    card.race = ""
                } else if (card.type.equals("Weapon", true) && type.equals("Spell", true)) {
                    // if switching from weapon to spell, set attack and durability to empty
                    card.attack = ""
                    card.durability = ""
                } else if (card.type.equals("Weapon", true) && type.equals("Minion", true)) {
                    // if switch from weapon to minion, set durability to empty
                    card.durability = ""
                }

                card.type = type
                addToHistory(card)
                cardFrag.updateCard(card) // update the UI with the latest card change
            }

        }
    }

    fun undo() {
        val cardToRemoveFromHistory = historyStack[historyStack.size - 1].clone() as Card // get the last card from historyStack
        redoStack.add(cardToRemoveFromHistory) // add the removed card to the redoStack
        historyStack.removeAt(historyStack.size - 1) // remove the last card from historyStack

        if (historyStack.size == 1) {
            // reached the oldest version, which is the empty Card object
            card = Card()
            cardFrag.hideUndo()
        } else {
            // set the current card
            card = historyStack[historyStack.size - 1]
        }
        cardFrag.updateCard(card) // update the UI with the old card
        cardFrag.showRedo()

        // get the items that was previously selected in the spinners
        updateSpinners()
    }

    private fun updateSpinners() {
        for (cardClass in classSpinnerItems) {
            if (cardClass.equals(card.className, true)) {
                val index = classSpinnerItems.indexOf(cardClass)
                classSpinner.setSelection(index)
            }
        }

        for (cardType in typeSpinnerItems) {
            if (cardType.equals(card.type, true)) {
                val index = typeSpinnerItems.indexOf(cardType)
                typeSpinner.setSelection(index)
            }
        }

        for (cardRarity in raritySpinnerITems) {
            if (cardRarity.equals(card.rarity, true)) {
                val index = raritySpinnerITems.indexOf(cardRarity)
                raritySpinner.setSelection(index)
            }
        }
    }

    fun redo() {
        val cardToRemoveFromRedo = redoStack[redoStack.size - 1].clone() as Card // last entry in redoStack to be removed
        historyStack.add(cardToRemoveFromRedo) // add the removed card to the historyStack
        redoStack.removeAt(redoStack.size - 1) // remove the last card from the redoStack

        // if there are not more changes to redo, hide the button
        if (redoStack.size == 0) {
            cardFrag.hideRedo()
        }

        card = historyStack[historyStack.size - 1] // set the current card as the last item in history
        cardFrag.updateCard(card) // update the UI

        // get the items that was previously selected in the spinners
        updateSpinners()

        if (historyStack.size > 1) {
            // if this block gets executed, that means the user is at the oldest version of the changes
            cardFrag.showUndo()
        }
    }

    fun addToHistory(card: Card) {
        historyStack.add(card.clone() as Card)

        if (historyStack.size == 1 || historyStack.size == 0) {
            cardFrag.hideUndo()
            return
        }
        cardFrag.showUndo()
//
    }
}
