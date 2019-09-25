package com.example.student.project

import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_input.view.*

class Input() : DialogFragment() {

    private var inputType: String? = null
    private var oldData: String? = null
    var dataPasser: DialogI? = null

    companion object {
        fun newInstance(type: String, data: String): Input {
            val f = Input()

            // Supply num input as an argument.
            val args = Bundle()
            args.putString("label", type)
            args.putString("oldData", data)
            f.arguments = args

            return f
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        dataPasser = context as DialogI
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_input, container, false)

        val okBtn = view.inputOk
        val cancelBtn = view.inputCancel
        view.valueLabel.text = inputType
        view.valueInput.setText(oldData)

        okBtn.setOnClickListener {
            print("HELLO" + oldData)
            dataPasser!!.okHandler(inputType, view.valueInput.text.toString())
            dismiss()
        }

        cancelBtn.setOnClickListener {
            Toast.makeText(activity!!, "Input Cancelled", Toast.LENGTH_SHORT).show()
            dismiss()
        }

        return view

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inputType = arguments!!.getString("label")
        oldData = arguments!!.getString("oldData")
    }
}
