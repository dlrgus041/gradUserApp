package org.grad.user

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit

class ActivityInfo : AppCompatActivity() {

    private var backPressedTime = 0L

    private lateinit var pref: SharedPreferences

    private lateinit var code: EditText
    private lateinit var name: EditText
    private lateinit var phone: EditText
    private lateinit var confirm: Button

    private lateinit var spn1: Spinner
    private lateinit var spn2: Spinner

    private lateinit var adArr: IntArray
    private var address = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        pref = getPreferences(Context.MODE_PRIVATE)

        code = findViewById(R.id.editTextCode)
        name = findViewById(R.id.editTextName)
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

        spn2.isClickable = false

        spn1.adapter = ArrayAdapter.createFromResource(this, R.array.addr00, android.R.layout.simple_spinner_dropdown_item)
        spn1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                changeSecondSpinner(position)
                spn2.isClickable = ( position > 0 )
                address = 0
            }

            override fun onNothingSelected(parent: AdapterView<*>?) { }
        }

        confirm = findViewById(R.id.btnQR)
        confirm.setOnClickListener {
            if (code.text.isEmpty() || phone.text.isEmpty() || address == 0) alertError()
            else {
                pref.edit {
                    putString("name", name.text.toString())
                    putString("code", code.text.toString())
                    putString("phone", phone.text.toString())
                    putInt("address", address)
                }
                switchQR()
            }
        }

        if (pref.contains("code")) switchQR()
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

    private fun switchQR() {
        val intent = Intent(this, ActivityQR::class.java)

        intent.putExtra("code", code.text)
        intent.putExtra("name", name.text)
        intent.putExtra("phone", phone.text)
        intent.putExtra("address", address)

        startActivity(intent)
        finish()
    }

    private fun alertError() {
        AlertDialog.Builder(this).setTitle("오류").setMessage(
            if (code.text.isEmpty()) "인증번호를 입력해주세요."
            else if (phone.text.isEmpty()) "전화번호를 입력해주세요."
            else "주소를 입력해주세요."
        ).setPositiveButton("확인") { _, _ -> }.show()
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
}