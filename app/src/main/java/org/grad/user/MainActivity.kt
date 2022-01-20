package org.grad.user

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.edit
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder

class MainActivity : AppCompatActivity() {

    private var backPressedTime = 0L

    private lateinit var pref: SharedPreferences

    private lateinit var layoutInfo: ConstraintLayout
    private lateinit var layoutQR: ConstraintLayout
    private lateinit var name: EditText
    private lateinit var phone: EditText
    private lateinit var address: EditText
    private lateinit var confirm: Button
    private lateinit var modify: Button
    private lateinit var imgView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pref = getPreferences(Context.MODE_PRIVATE)
        imgView = findViewById(R.id.imgView)

        layoutInfo = findViewById(R.id.layoutInfo)
        layoutQR = findViewById(R.id.layoutQR)

        name = findViewById(R.id.editTextName)
        phone = findViewById(R.id.editTextPhone)
        address = findViewById(R.id.editTextAddress)

        confirm = findViewById(R.id.btnQR)
        confirm.setOnClickListener {
            pref.edit {
                putString("name", name.text.toString())
                putString("phone", phone.text.toString())
                putString("address", address.text.toString())
            }
            createQR(imgView, makeMsg())
            switchInfoToQR()
        }

        modify = findViewById(R.id.btnInfoModify)
        modify.setOnClickListener {
            pref.edit { clear() }
            switchQRToInfo()
        }

        if (pref.contains("name")) createQR(imgView, makeMsg())
        else switchQRToInfo()
    }

    override fun onBackPressed() {

        val tempTime = System.currentTimeMillis()
        val diff = tempTime - backPressedTime

        if (diff in 0 .. 2000) super.onBackPressed()
        else {
            backPressedTime = tempTime
            Toast.makeText(this, "종료하려면 버튼을 한 번 더 눌러주세요.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun switchInfoToQR() {
        layoutInfo.visibility = View.INVISIBLE
        layoutQR.visibility = View.VISIBLE
    }

    private fun switchQRToInfo() {
        layoutInfo.visibility = View.VISIBLE
        layoutQR.visibility = View.INVISIBLE
    }

    private fun makeMsg(): String {
        return with(pref) {
            getString("name", "null") + "#" +
                    getString("phone", "null") + "$" +
                    getString("address", "null")
        }
    }

    private fun createQR(view: ImageView, msg: String) {
        view.setImageBitmap(
            BarcodeEncoder().createBitmap(
                MultiFormatWriter().encode(
                    msg, BarcodeFormat.QR_CODE, 300, 300
                )
            )
        )
    }
}