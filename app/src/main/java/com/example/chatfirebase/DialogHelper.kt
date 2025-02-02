package com.example.chatfirebase

import android.content.Context
import android.health.connect.datatypes.units.Length
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class DialogHelper {

    fun showInputDialog(context: Context, onTextEntered: (String) -> Unit) {
        val editText = EditText(context)
        editText.hint = "Type name of group"

        val dialog = AlertDialog.Builder(context)
            .setView(editText)
            .setPositiveButton("SAVE") { _, _ ->
                val enteredText = editText.text.toString()
                if (enteredText.isNotEmpty()) {
                    onTextEntered(enteredText)
                }else{
                    editText.text.clear()
                    Toast.makeText(context, "Name is empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("CLOSE") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }

    fun showInputDialog(context: Context, text: String, onTextEntered: (String) -> Unit) {
        val editText = EditText(context)
        editText.hint = "Type name of group"
        editText.setText(text)

        val dialog = AlertDialog.Builder(context)
            .setView(editText)
            .setPositiveButton("SAVE") { _, _ ->
                val enteredText = editText.text.toString()
                if (enteredText.isNotEmpty()) {
                    onTextEntered(enteredText)
                }else{
                    editText.text.clear()
                    Toast.makeText(context, "Name is empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("CLOSE") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }
}