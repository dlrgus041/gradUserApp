package org.grad.user

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.edit
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity() {

    private var infoStatus = false
    private var backPressedTime = 0L
    private val hints = Hashtable<EncodeHintType, String>().apply {
        put(EncodeHintType.CHARACTER_SET, "UTF-8")
    }

    private lateinit var pref: SharedPreferences

    private lateinit var layoutInfo: ConstraintLayout
    private lateinit var layoutQR: ConstraintLayout
    private lateinit var frameQR: FrameLayout

    private lateinit var code: EditText
    private lateinit var name: EditText
    private lateinit var phone: EditText
    private lateinit var confirm: Button

    private lateinit var prefCode: TextView
    private lateinit var prefName: TextView
    private lateinit var prefPhone: TextView
    private lateinit var prefAddress: TextView
    private lateinit var modify: Button
    private lateinit var flip: Button

    private lateinit var imgView: ImageView
    private lateinit var quickInfo: TableLayout

    private lateinit var spn1: Spinner
    private lateinit var spn2: Spinner

    private lateinit var adArr: IntArray
    private var address = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(R.layout.activity_main)

        pref = getPreferences(Context.MODE_PRIVATE)

        imgView = findViewById(R.id.imgView)
        quickInfo = findViewById(R.id.quickInfo)

        layoutInfo = findViewById(R.id.layoutInfo)
        layoutQR = findViewById(R.id.layoutQR)
        frameQR = findViewById(R.id.frameQR)

        code = findViewById(R.id.editTextCode)
        name = findViewById(R.id.editTextName)
        phone = findViewById(R.id.editTextPhone)

        prefCode = findViewById(R.id.prefCode)
        prefName = findViewById(R.id.prefName)
        prefPhone = findViewById(R.id.prefPhone)
        prefAddress = findViewById(R.id.prefAddress)

        adArr = intArrayOf(
            R.array.addr0, R.array.addr1, R.array.addr2, R.array.addr3,
            R.array.addr4, R.array.addr5, R.array.addr6, R.array.addr7,
            R.array.addr8, R.array.addr9, R.array.addr10, R.array.addr11,
            R.array.addr12, R.array.addr13, R.array.addr14, R.array.addr15,
            R.array.addr16, R.array.addr17
        )

        spn1 = findViewById(R.id.spinner1)
        spn2 = findViewById(R.id.spinner2)

        spn2.isClickable = false

        spn1.adapter = ArrayAdapter.createFromResource(this, R.array.addr00, android.R.layout.simple_spinner_dropdown_item)
        spn1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                changeSecondSpinner(position)
                spn2.isClickable = (position > 0)
                address = 0
            }

            override fun onNothingSelected(parent: AdapterView<*>?) { }
        }

        confirm = findViewById(R.id.btnQR)
        confirm.setOnClickListener {
            if (code.text.isEmpty() || phone.text.isEmpty() || address == 0) alertError()
            else {
                pref.edit {
                    putString("code", code.text.toString())
                    putString("name", name.text.toString())
                    putString("phone", phone.text.toString())
                    putInt("address", address)
                }
                createQR(imgView, makeMsg())
                layoutSwitchInfoToQR()
            }
        }

        flip = findViewById(R.id.btnFlip)
        flip.setOnClickListener {
            if (infoStatus) imageSwitchInfoToQR() else imageSwitchQRToInfo()
            infoStatus = !infoStatus
        }

        modify = findViewById(R.id.btnModify)
        modify.setOnClickListener {
            pref.edit { clear() }
            layoutSwitchQRToInfo()
            imageSwitchInfoToQR()
            infoStatus = false
        }

        if (pref.contains("code")) createQR(imgView, makeMsg())
        else layoutSwitchQRToInfo()
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
            else if (!check(code.text.toString())) "잘못된 ID입니다."
            else if (phone.text.isEmpty()) "전화번호를 입력해주세요."
            else "주소를 입력해주세요."
        ).setPositiveButton("확인") { _, _ -> }.show()
    }

    private fun layoutSwitchInfoToQR() {
        layoutInfo.visibility = View.INVISIBLE
        layoutQR.visibility = View.VISIBLE
    }

    private fun layoutSwitchQRToInfo() {
        layoutInfo.visibility = View.VISIBLE
        layoutQR.visibility = View.INVISIBLE
    }

    private fun imageSwitchInfoToQR() {
        quickInfo.visibility = View.INVISIBLE
        imgView.visibility = View.VISIBLE
        modify.visibility = View.INVISIBLE
        flip.text = "입력 정보 보기"
    }

    private fun imageSwitchQRToInfo() {
        quickInfo.visibility = View.VISIBLE
        imgView.visibility = View.INVISIBLE
        modify.visibility = View.VISIBLE
        flip.text = "QR코드 보기"
    }

    private fun makeMsg(): String {
        return with(pref) {
            getString("code", "-1") + "#" + getString("phone", "null") + "$" + getInt("address", -1).toString() + "t" + System.currentTimeMillis()
        }
    }

    private fun createQR(view: ImageView, msg: String) {

        with(pref) {
            prefCode.text = getString("code", "code")
            prefName.text = getString("name", "name")
            prefPhone.text = getString("phone", "phone")
            prefAddress.text = getInt("address", -1).toString()
        }

        view.setImageBitmap(
            BarcodeEncoder().createBitmap(
                QRCodeWriter().encode(
                    msg, BarcodeFormat.QR_CODE, 175, 175, hints
                )
            )
        )

        view.setBackgroundResource(R.drawable.layout_design)
    }

    private fun changeSecondSpinner(pos: Int) {
        spn2.adapter = ArrayAdapter.createFromResource(this, adArr[pos], android.R.layout.simple_spinner_dropdown_item)
        spn2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                address = if (position > 0) 1000 + 100 * pos + position else 0
            }

            override fun onNothingSelected(parent: AdapterView<*>?) { }
        }
    }

    private fun check(id: String): Boolean {

        var target = id.toInt()
        var sum = 0

        for (i in 1 .. 6) {
            sum += i * (target % 10)
            target /= 10
        }

        return sum % 7 == 0
    }
}