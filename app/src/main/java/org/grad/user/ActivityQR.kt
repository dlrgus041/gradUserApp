package org.grad.user

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.util.*

class ActivityQR : AppCompatActivity() {

    private var backPressedTime = 0L
    private val hints = Hashtable<EncodeHintType, String>().apply {
        put(EncodeHintType.CHARACTER_SET, "UTF-8")
    }

    private lateinit var imgView: ImageView

    private lateinit var code: TextView
    private lateinit var name: TextView
    private lateinit var phone: TextView
    private lateinit var address: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr)

        imgView = findViewById(R.id.imgView)

        code = findViewById(R.id.prefCode)
        name = findViewById(R.id.prefName)
        phone = findViewById(R.id.prefPhone)
        address = findViewById(R.id.prefAddress)

        code.text = intent.getCharSequenceExtra("code")
        name.text = intent.getCharSequenceExtra("name")
        phone.text = intent.getCharSequenceExtra("phone")
        address.text = intent.getIntExtra("address", -1).toString()

        createQR(imgView, makeMsg())
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.btnInfoModify -> {
                startActivity(Intent(this, ActivityInfo::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun makeMsg() = code.text.toString() + "#" + phone.text.toString() + "$" + address.text.toString()

    private fun createQR(view: ImageView, msg: String) {
        view.setImageBitmap(
            BarcodeEncoder().createBitmap(
                QRCodeWriter().encode(
                    msg, BarcodeFormat.QR_CODE, 300, 300, hints
                )
            )
        )
    }
}