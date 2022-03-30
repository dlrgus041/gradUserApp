package org.grad.user

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.edit
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.util.*

class MainActivity : AppCompatActivity() {

    private var backPressedTime = 0L
    private val hints = Hashtable<EncodeHintType, String>().apply {
        put(EncodeHintType.CHARACTER_SET, "UTF-8")
    }

    private lateinit var pref: SharedPreferences

    private lateinit var layoutInfo: ConstraintLayout
    private lateinit var layoutQR: ConstraintLayout
    private lateinit var code: EditText
    private lateinit var phone: EditText
    private lateinit var confirm: Button
    private lateinit var modify: Button
    private lateinit var imgView: ImageView

    private lateinit var spn1: Spinner
    private lateinit var spn2: Spinner

    private lateinit var adArr: IntArray
    private var address = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pref = getPreferences(Context.MODE_PRIVATE)
        imgView = findViewById(R.id.imgView)

        layoutInfo = findViewById(R.id.layoutInfo)
        layoutQR = findViewById(R.id.layoutQR)

        code = findViewById(R.id.editTextId)
        phone = findViewById(R.id.editTextPhone)

        adArr = intArrayOf(
            R.array.addr0, R.array.addr1, R.array.addr2, R.array.addr3,
            R.array.addr4, R.array.addr5, R.array.addr6, R.array.addr7,
            R.array.addr8, R.array.addr9, R.array.addr10, R.array.addr11,
            R.array.addr12, R.array.addr13, R.array.addr14, R.array.addr15,
            R.array.addr16, R.array.addr17
        )

        spn1 = findViewById(R.id.spinner1)
        spn2 = findViewById(R.id.spinner2)

        enableSecondSpinner(false)

        spn1.adapter = ArrayAdapter.createFromResource(this, R.array.addr00, android.R.layout.simple_spinner_dropdown_item)
        spn1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                changeSecondSpinner(position)
                enableSecondSpinner(position > 0)
                address = 0
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        confirm = findViewById(R.id.btnQR)
        confirm.setOnClickListener {
            if (code.text.isEmpty() || phone.text.isEmpty() || address == 0) alertError()
            else {
                pref.edit {
                    putString("id", code.text.toString())
                    putString("phone", phone.text.toString())
                    putInt("address", address)
                }
                createQR(imgView, makeMsg())
                switchInfoToQR()
            }
        }

        modify = findViewById(R.id.btnInfoModify)
        modify.setOnClickListener {
            pref.edit { clear() }
            switchQRToInfo()
        }

        if (pref.contains("id")) createQR(imgView, makeMsg())
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

    private fun alertError() {
        AlertDialog.Builder(this).setTitle("오류").setMessage(
            if (code.text.isEmpty()) "ID를 입력해주세요."
            else if (phone.text.isEmpty()) "전화번호를 입력해주세요."
            else "주소를 입력해주세요."
        ).setPositiveButton("확인") { _, _ -> }.show()
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
            getString("id", "-1") + "#" + getString("phone", "null") + "$" + getInt("address", -1)
        }
    }

    private fun createQR(view: ImageView, msg: String) {
        view.setImageBitmap(
            BarcodeEncoder().createBitmap(
                QRCodeWriter().encode(
                    msg, BarcodeFormat.QR_CODE, 300, 300, hints
                )
            )
        )
    }

    fun enableSecondSpinner(flag: Boolean) {
        spn2.isClickable = flag
    }

    fun changeSecondSpinner(pos: Int) {
        spn2.adapter = ArrayAdapter.createFromResource(this, adArr[pos], android.R.layout.simple_spinner_dropdown_item)
        spn2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                address = if (position > 0) 1000 + 100 * pos + position else 0
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }
}