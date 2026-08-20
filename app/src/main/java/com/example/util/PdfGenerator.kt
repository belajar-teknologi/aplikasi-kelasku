package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.model.AttendanceRecord
import com.example.data.model.AttendanceStatus
import com.example.data.model.GradeRecord
import com.example.data.model.Student
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfGenerator {

    fun generateAndShareReportPdf(
        context: Context,
        className: String,
        students: List<Student>,
        attendanceList: List<AttendanceRecord>,
        gradesList: List<GradeRecord>,
        reportTitle: String = "Laporan Mingguan Kemajuan Belajar & Kehadiran"
    ): File? {
        val pdfDocument = PdfDocument()
        val pageWidth = 595 // A4 standard point width
        val pageHeight = 842 // A4 standard point height

        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val titlePaint = Paint().apply {
            color = Color.rgb(0, 77, 64) // Dark Emerald
            textSize = 14f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val subTitlePaint = Paint().apply {
            color = Color.rgb(55, 65, 81)
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }

        val headerBgPaint = Paint().apply {
            color = Color.rgb(0, 108, 80)
            isAntiAlias = true
        }

        val headerTextPaint = Paint().apply {
            color = Color.WHITE
            textSize = 9f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val rowTextPaint = Paint().apply {
            color = Color.rgb(31, 41, 55)
            textSize = 8.5f
            isAntiAlias = true
        }

        val rowBgPaintEven = Paint().apply {
            color = Color.rgb(248, 250, 252)
        }

        val rowBgPaintOdd = Paint().apply {
            color = Color.rgb(255, 255, 255)
        }

        val borderPaint = Paint().apply {
            color = Color.rgb(203, 213, 225)
            style = Paint.Style.STROKE
            strokeWidth = 0.5f
        }

        // Draw Header Banner
        val bannerRect = RectF(20f, 20f, (pageWidth - 20).toFloat(), 75f)
        val bannerBg = Paint().apply { color = Color.rgb(236, 253, 245) }
        canvas.drawRoundRect(bannerRect, 6f, 6f, bannerBg)

        canvas.drawText("SISTEM PENDIDIKAN ASISTEN GURU PINTAR", 35f, 40f, titlePaint)
        canvas.drawText("$reportTitle - $className", 35f, 55f, subTitlePaint)
        val currentDate = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(Date())
        canvas.drawText("Tanggal Cetak: $currentDate | Status Data: Real-time", 35f, 68f, subTitlePaint)

        // Summary Stats Box
        var yPos = 90f
        val totalHadir = attendanceList.count { it.status == AttendanceStatus.HADIR }
        val totalSakit = attendanceList.count { it.status == AttendanceStatus.SAKIT }
        val totalIzin = attendanceList.count { it.status == AttendanceStatus.IZIN }
        val totalAlpa = attendanceList.count { it.status == AttendanceStatus.ALPA }
        val totalAttendanceRecords = attendanceList.size
        val attendancePct = if (totalAttendanceRecords > 0) (totalHadir * 100.0 / totalAttendanceRecords) else 100.0

        val statsBox = RectF(20f, yPos, (pageWidth - 20).toFloat(), yPos + 35f)
        val statsPaint = Paint().apply { color = Color.rgb(241, 245, 249) }
        canvas.drawRoundRect(statsBox, 4f, 4f, statsPaint)

        val statText = "Total Siswa: ${students.size} | Kehadiran: ${String.format("%.1f", attendancePct)}% (Hadir: $totalHadir, Sakit: $totalSakit, Izin: $totalIzin, Alpa: $totalAlpa)"
        canvas.drawText(statText, 30f, yPos + 22f, Paint().apply {
            color = Color.rgb(15, 23, 42)
            textSize = 9f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        })

        // Table Header
        yPos += 50f
        val tableLeft = 20f
        val tableRight = (pageWidth - 20).toFloat()
        val rowHeight = 22f

        canvas.drawRect(tableLeft, yPos, tableRight, yPos + rowHeight, headerBgPaint)

        // Columns: No (25), NIS (50), Nama Siswa (130), Kehadiran (90), Rata-rata (60), KKM (70), Kontak Ortu (remainder)
        canvas.drawText("NO", 25f, yPos + 15f, headerTextPaint)
        canvas.drawText("NIS", 50f, yPos + 15f, headerTextPaint)
        canvas.drawText("NAMA SISWA", 100f, yPos + 15f, headerTextPaint)
        canvas.drawText("KEHADIRAN", 230f, yPos + 15f, headerTextPaint)
        canvas.drawText("RATA2", 320f, yPos + 15f, headerTextPaint)
        canvas.drawText("STATUS KKM", 370f, yPos + 15f, headerTextPaint)
        canvas.drawText("WALI MURID & KONTAK", 450f, yPos + 15f, headerTextPaint)

        yPos += rowHeight

        // Draw Rows
        students.forEachIndexed { index, student ->
            val bg = if (index % 2 == 0) rowBgPaintEven else rowBgPaintOdd
            canvas.drawRect(tableLeft, yPos, tableRight, yPos + rowHeight, bg)
            canvas.drawRect(tableLeft, yPos, tableRight, yPos + rowHeight, borderPaint)

            val studentGrades = gradesList.filter { it.studentId == student.id }
            val avg = if (studentGrades.isNotEmpty()) studentGrades.map { it.score }.average() else 0.0
            val statusKKM = if (avg >= 75.0) "TUNTAS" else if (avg > 0) "REMEDIAL" else "-"

            val studentAttendance = attendanceList.filter { it.studentId == student.id }
            val hadirCount = studentAttendance.count { it.status == AttendanceStatus.HADIR }
            val totalAtt = studentAttendance.size
            val attPct = if (totalAtt > 0) "${(hadirCount * 100 / totalAtt)}%" else "100%"

            canvas.drawText("${index + 1}", 25f, yPos + 14f, rowTextPaint)
            canvas.drawText(student.nis, 50f, yPos + 14f, rowTextPaint)
            
            val truncatedName = if (student.name.length > 20) student.name.take(18) + ".." else student.name
            canvas.drawText(truncatedName, 100f, yPos + 14f, rowTextPaint)
            
            canvas.drawText("$attPct ($hadirCount/$totalAtt)", 230f, yPos + 14f, rowTextPaint)
            canvas.drawText(if (avg > 0) String.format("%.1f", avg) else "-", 320f, yPos + 14f, rowTextPaint)
            canvas.drawText(statusKKM, 370f, yPos + 14f, rowTextPaint)
            
            val parentInfo = "${student.parentName} (${student.parentPhone})"
            val truncatedParent = if (parentInfo.length > 22) parentInfo.take(20) + ".." else parentInfo
            canvas.drawText(truncatedParent, 450f, yPos + 14f, rowTextPaint)

            yPos += rowHeight
        }

        // Bottom Signature Section
        yPos = (pageHeight - 120).toFloat()
        canvas.drawText("Mengetahui,", 50f, yPos, subTitlePaint)
        canvas.drawText("Kepala Sekolah SD/SMP Pintar", 50f, yPos + 14f, subTitlePaint)
        canvas.drawText("( ______________________ )", 50f, yPos + 70f, subTitlePaint)

        canvas.drawText("Guru / Wali Kelas $className,", 380f, yPos, subTitlePaint)
        canvas.drawText("Pengampu Kelas", 380f, yPos + 14f, subTitlePaint)
        canvas.drawText("( ______________________ )", 380f, yPos + 70f, subTitlePaint)

        pdfDocument.finishPage(page)

        // Save PDF to files directory
        return try {
            val reportsDir = File(context.cacheDir, "reports")
            if (!reportsDir.exists()) reportsDir.mkdirs()

            val fileName = "Laporan_${className.replace(" ", "_")}_${System.currentTimeMillis()}.pdf"
            val file = File(reportsDir, fileName)
            val outputStream = FileOutputStream(file)
            pdfDocument.writeTo(outputStream)
            outputStream.flush()
            outputStream.close()
            pdfDocument.close()

            openOrSharePdf(context, file)
            file
        } catch (e: Exception) {
            pdfDocument.close()
            Toast.makeText(context, "Gagal membuat PDF: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            null
        }
    }

    private fun openOrSharePdf(context: Context, file: File) {
        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Laporan Kemajuan Belajar Siswa")
                putExtra(Intent.EXTRA_TEXT, "Berikut terlampir dokumen laporan mingguan/bulanan hasil belajar dan kehadiran siswa dalam format PDF.")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(Intent.createChooser(shareIntent, "Buka atau Bagikan Laporan PDF"))
        } catch (e: Exception) {
            Toast.makeText(context, "PDF tersimpan di: ${file.name}", Toast.LENGTH_LONG).show()
        }
    }
}
