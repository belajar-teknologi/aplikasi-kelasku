package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.data.model.AttendanceRecord
import com.example.data.model.AttendanceStatus
import com.example.data.model.GradeRecord
import com.example.data.model.Student
import java.net.URLEncoder

object WhatsAppHelper {

    fun sendAttendanceNotification(
        context: Context,
        student: Student,
        record: AttendanceRecord
    ) {
        val statusText = when (record.status) {
            AttendanceStatus.HADIR -> "HADIR di kelas tepat waktu ✅"
            AttendanceStatus.SAKIT -> "SAKIT 🩺 (Catatan: ${record.notes.ifEmpty { "Dalam pemulihan" }})"
            AttendanceStatus.IZIN -> "IZIN 📝 (Catatan: ${record.notes.ifEmpty { "Keperluan keluarga" }})"
            AttendanceStatus.ALPA -> "TIDAK HADIR TANPA KETERANGAN ⚠️"
        }

        val message = """
            *NOTIFIKASI KEHADIRAN SISWA*
            _Sistem Asisten Guru SD/SMP Pintar_
            
            Yth. *${student.parentName}*,
            Wali murid dari: *${student.name}* (NIS: ${student.nis})
            Kelas: *${student.className}*
            
            Diberitahukan bahwa pada tanggal *${record.date}*, ananda tercatat:
            👉 *Status: $statusText*
            
            ${if (record.status == AttendanceStatus.ALPA) "Dimohon Bapak/Ibu wali murid segera mengonfirmasi alasan ketidakhadiran ananda kepada wali kelas." else "Terima kasih atas perhatian dan kerja samanya."}
            
            _Salam hangat,_
            *Wali Kelas ${student.className}*
        """.trimIndent()

        openWhatsApp(context, student.parentPhone, message)
    }

    fun sendGradeReport(
        context: Context,
        student: Student,
        grades: List<GradeRecord>
    ) {
        val averageScore = if (grades.isNotEmpty()) grades.map { it.score }.average() else 0.0
        val gradesListText = grades.joinToString("\n") { grade ->
            "• *${grade.subject}* (${grade.type}): *${String.format("%.1f", grade.score)}* / ${grade.maxScore.toInt()} _(${grade.notes.ifEmpty { "Tuntas" }})_"
        }

        val message = """
            *LAPORAN PERKEMBANGAN HASIL BELAJAR*
            _Sistem Asisten Guru Pintar_
            
            Yth. *${student.parentName}*,
            Wali murid dari: *${student.name}* (${student.className})
            
            Berikut adalah rekapitulasi nilai dan hasil belajar ananda:
            $gradesListText
            
            📊 *Rata-rata Nilai: ${String.format("%.1f", averageScore)}*
            🎯 *Status Evaluasi: ${if (averageScore >= 75) "Sangat Baik (Tuntas KKM)" else "Perlu Penguatan Materi"}*
            
            Catatan Guru: _${student.notes.ifEmpty { "Ananda menunjukkan semangat belajar yang sangat baik." }}_
            
            Mohon bimbingan dan dorongan terus diberikan kepada ananda di rumah.
            
            _Hormat kami,_
            *Wali Kelas ${student.className}*
        """.trimIndent()

        openWhatsApp(context, student.parentPhone, message)
    }

    fun sendHomeworkReminder(
        context: Context,
        student: Student,
        homeworkTitle: String,
        subject: String,
        dueDate: String,
        dueTime: String,
        description: String
    ) {
        val message = """
            *PENGINGAT TUGAS RUMAH (PR)* 📚
            _Sistem Asisten Guru Pintar_
            
            Yth. *${student.parentName}*,
            Orang tua dari: *${student.name}* (${student.className})
            
            Mengingatkan bahwa terdapat tugas rumah yang perlu diselesaikan:
            📌 *Mata Pelajaran:* $subject
            📝 *Judul Tugas:* $homeworkTitle
            ⏰ *Tenggat Waktu:* $dueDate pukul $dueTime WIB
            📋 *Instruksi:* $description
            
            Mohon bantuan Bapak/Ibu untuk mendampingi ananda dalam penyelesaian tugas ini. Terima kasih!
            
            _Wali Kelas ${student.className}_
        """.trimIndent()

        openWhatsApp(context, student.parentPhone, message)
    }

    fun sendQuizLink(
        context: Context,
        student: Student,
        quizTitle: String,
        subject: String,
        shareCode: String
    ) {
        val quizLink = "https://gurupintar.sch.id/quiz?code=$shareCode"
        val message = """
            *LINK KUIS INTERAKTIF SISWA* 🎯
            _Asisten Guru Pintar_
            
            Halo *${student.name}* / Yth. *${student.parentName}*,
            Telah dibuka kuis interaktif daring:
            
            📖 *Mata Pelajaran:* $subject
            📝 *Topik:* $quizTitle
            🔑 *Kode Akses:* *$shareCode*
            🔗 *Tautan Kuis:* $quizLink
            
            Silakan buka tautan atau masukkan kode akses pada aplikasi untuk mulai mengerjakan. Selamat belajar!
        """.trimIndent()

        openWhatsApp(context, student.parentPhone, message)
    }

    private fun openWhatsApp(context: Context, rawPhone: String, message: String) {
        try {
            var formattedPhone = rawPhone.replace("+", "").replace("-", "").replace(" ", "").trim()
            if (formattedPhone.startsWith("0")) {
                formattedPhone = "62" + formattedPhone.substring(1)
            }
            val encodedMessage = URLEncoder.encode(message, "UTF-8")
            val uri = Uri.parse("https://api.whatsapp.com/send?phone=$formattedPhone&text=$encodedMessage")
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Tidak dapat membuka WhatsApp: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }
}
