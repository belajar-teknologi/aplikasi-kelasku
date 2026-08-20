package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.data.model.GradeRecord
import com.example.data.model.Student

object EmailHelper {

    fun sendGradeReportEmail(
        context: Context,
        student: Student,
        grades: List<GradeRecord>
    ) {
        val averageScore = if (grades.isNotEmpty()) grades.map { it.score }.average() else 0.0
        val subjectRows = grades.joinToString("\n") { grade ->
            "- ${grade.subject} (${grade.type}): ${String.format("%.1f", grade.score)} / 100 [${grade.date}] -> ${grade.notes.ifEmpty { "Tuntas" }}"
        }

        val subject = "Laporan Hasil Belajar Siswa: ${student.name} - ${student.className}"
        val body = """
            Kepada Yth. Bapak/Ibu ${student.parentName},
            Orang Tua/Wali dari ${student.name} (NIS: ${student.nis})
            Kelas: ${student.className}
            
            Dengan hormat,
            Berikut kami sampaikan rekapan evaluasi dan nilai akademik ananda yang tercatat pada Sistem Asisten Guru Pintar:
            
            DAFTAR NILAI:
            --------------------------------------------------------
            $subjectRows
            --------------------------------------------------------
            RATA-RATA NILAI: ${String.format("%.1f", averageScore)}
            STATUS KELULUSAN KKM: ${if (averageScore >= 75.0) "TUNTAS (Memenuhi Standar KKM)" else "PERLU REMEDIAL / PENGAYAAN"}
            
            CATATAN GURU & PERKEMBANGAN:
            ${student.notes.ifEmpty { "Ananda mengikuti kegiatan pembelajaran dengan baik dan tertib." }}
            
            Demikian laporan ini kami sampaikan sebagai bentuk transparansi dan sinergi antara sekolah dan orang tua murid.
            
            Hormat kami,
            Wali Kelas ${student.className}
            Sistem Informasi Guru Pintar
        """.trimIndent()

        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:${student.parentEmail}")
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(Intent.createChooser(intent, "Kirim Laporan Nilai ke Email"))
        } catch (e: Exception) {
            Toast.makeText(context, "Tidak dapat membuka aplikasi Email: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }
}
