package com.example.pharmacypdf

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileOutputStream

class MainActivity : Activity() {

    private val PICK_PDF = 1001
    private var extractedText = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PDFBoxResourceLoader.init(applicationContext)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 200, 50, 200)

        val btnPick = Button(this)
        btnPick.text = "1. اختر فاتورة PDF"
        btnPick.textSize = 20f

        val btnConvert = Button(this)
        btnConvert.text = "2. حول إلى Excel وعدّل"
        btnConvert.textSize = 20f
        btnConvert.isEnabled = false

        val textView = TextView(this)
        textView.text = "لم يتم اختيار ملف"
        textView.textSize = 16f
        textView.setPadding(0, 50, 0, 50)

        btnPick.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/pdf"
            startActivityForResult(intent, PICK_PDF)
        }

        btnConvert.setOnClickListener {
            showEditScreen()
        }

        layout.addView(btnPick)
        layout.addView(btnConvert)
        layout.addView(textView)
        setContentView(layout)

        // نخليهم Global عشان نستخدمهم
        this.btnConvert = btnConvert
        this.textView = textView
    }

    private lateinit var btnConvert: Button
    private lateinit var textView: TextView

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_PDF && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                extractTextFromPDF(uri)
            }
        }
    }

    private fun extractTextFromPDF(uri: Uri) {
        try {
            val input = contentResolver.openInputStream(uri)
            val doc = PDDocument.load(input)
            val stripper = PDFTextStripper()
            extractedText = stripper.getText(doc)
            doc.close()

            textView.text = "تم قراءة الملف بنجاح ✓\nعدد الأحرف: ${extractedText.length}"
            btnConvert.isEnabled = true

        } catch (e: Exception) {
            Toast.makeText(this, "خطأ في قراءة PDF: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun showEditScreen() {
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(40, 40, 40, 40)

        val editText = EditText(this)
        editText.setText(extractedText)
        editText.setSingleLine(false)
        editText.minLines = 15
        editText.maxLines = 20
        editText.isVerticalScrollBarEnabled = true

        val btnSave = Button(this)
        btnSave.text = "حفظ كـ Excel"
        btnSave.setOnClickListener {
            saveAsExcel(editText.text.toString())
        }

        val scrollView = ScrollView(this)
        scrollView.addView(editText)

        layout.addView(TextView(this).apply { text = "عدّل الأرقام والنص هنا:"; textSize = 18f })
        layout.addView(scrollView)
        layout.addView(btnSave)

        setContentView(layout)
    }

    private fun saveAsExcel(text: String) {
        try {
            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet("الفاتورة")

            val lines = text.split("\n")
            for (i in lines.indices) {
                val row = sheet.createRow(i)
                val cells = lines[i].split("\\s+".toRegex())
                for (j in cells.indices) {
                    row.createCell(j).setCellValue(cells[j])
                }
            }

            val fileName = "فاتورة_${System.currentTimeMillis()}.xlsx"
            val file = getExternalFilesDir(null)?.resolve(fileName)
            val fos = FileOutputStream(file)
            workbook.write(fos)
            fos.close()
            workbook.close()

            Toast.makeText(this, "تم الحفظ: $fileName", Toast.LENGTH_LONG).show()
            Toast.makeText(this, "المسار: Android/data/com.example.pharmacypdf/files", Toast.LENGTH_LONG).show()

        } catch (e: Exception) {
            Toast.makeText(this, "خطأ في الحفظ: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
