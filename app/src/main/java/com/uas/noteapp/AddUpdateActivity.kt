package com.uas.noteapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_add_update.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.toast
import java.text.SimpleDateFormat
import java.util.*

class AddUpdateActivity : AppCompatActivity() {

    var noteDatabase: NoteDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_update)

        noteDatabase = NoteDatabase.getInstance(this)

        generateDate()
        aua_btn_save.onClick {
            insertNewNote()
        }
    }

    private fun generateDate() {
        val locale = Locale("in", "ID")
        val dateFormat: SimpleDateFormat? =
            SimpleDateFormat("EEE , dd MMM yyyy , hh:mm aaa", locale)
        val date = Calendar.getInstance().time
        val createDate = dateFormat?.format(date).toString()
        aua_tv_date.text = createDate
    }

    private fun doEncrypt(): String {
        val RSA = RSA()
        val keyEncrypt = RSA.eValue(RSA.Qn)
        val plainteks = aua_edt_note.text.toString()
        var cipherTeks = ""
        for (i in 0 until plainteks.length) {
            val character = plainteks[i]
            val cipher = RSA.encrypt(character, keyEncrypt, RSA.n)
            cipherTeks += cipher
        }
        Log.d("Encryption", "$cipherTeks")
        return cipherTeks
    }

    private fun insertNewNote() {
        val cipher = doEncrypt()
        val note = NoteModel(
            title = aua_edt_title.text.toString(),
            message = cipher,
            createdAt = aua_tv_date.text.toString()
        )
        GlobalScope.launch {
            noteDatabase?.noteDao()?.insertNote(note)
            runOnUiThread {
                toast("Data berhasil ditambahkan")
                aua_edt_note.text = null
                aua_edt_title.text = null
                generateDate()
            }

        }
    }
}