package com.example.pharmacypdf

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast

class MainActivity : Activity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(100, 300, 100, 300)
        
        val btn = Button(this)
        btn.text = "اضغط هنا للتجربة"
        btn.textSize = 24f
        btn.setOnClickListener {
            Toast.makeText(this, "التطبيق شغال 100%", Toast.LENGTH_LONG).show()
        }
        
        layout.addView(btn)
        setContentView(layout)
    }
}
