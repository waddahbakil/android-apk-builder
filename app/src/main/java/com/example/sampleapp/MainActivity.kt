package com.example.pharmacypdf

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.print.PrintHelper
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.rendering.PDFRenderer

class MainActivity : Activity() {
    
    private val PICK_PDF = 1001
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // مهم: لازم تهيئة PDFBox قبل أي استخدام
        PDFBoxResourceLoader.init(applicationContext)
        
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(100, 300, 100, 300)
        
        val btn = Button(this)
        btn.text = "اختر ملف PDF للطباعة"
        btn.textSize = 22f
        btn.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/pdf"
            startActivityForResult(intent, PICK_PDF)
        }
        
        layout.addView(btn)
        setContentView(layout)
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_PDF && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                printPDF(uri)
            }
        }
    }
    
    private fun printPDF(uri: Uri) {
        try {
            val input = contentResolver.openInputStream(uri)
            val doc = PDDocument.load(input)
            val renderer = PDFRenderer(doc)
            
            // نرندر أول صفحة بدقة 300 DPI للطباعة
            val bitmap = renderer.renderImageWithDPI(0, 300f)
            doc.close()
            
            val printHelper = PrintHelper(this)
            printHelper.scaleMode = PrintHelper.SCALE_MODE_FIT
            printHelper.printBitmap("فاتورة الصيدلية", bitmap)
            
            Toast.makeText(this, "تم إرسال للطابعة", Toast.LENGTH_SHORT).show()
            
        } catch (e: Exception) {
            Toast.makeText(this, "خطأ: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
