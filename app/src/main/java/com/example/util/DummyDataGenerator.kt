package com.example.util

import com.example.data.model.*

object DummyDataGenerator {

    fun getInitialStudents(): List<Student> {
        return listOf(
            Student(
                id = 1,
                nis = "2024001",
                name = "Aditya Pratama",
                className = "Kelas 5A",
                gender = "L",
                parentName = "Bpk. Bambang Pratama",
                parentPhone = "081234567891",
                parentEmail = "bambang.pratama@gmail.com",
                address = "Jl. Merdeka No. 15",
                notes = "Sangat aktif di mata pelajaran Matematika"
            ),
            Student(
                id = 2,
                nis = "2024002",
                name = "Siti Nurhaliza",
                className = "Kelas 5A",
                gender = "P",
                parentName = "Ibu Halimah",
                parentPhone = "081234567892",
                parentEmail = "halimah.nur@gmail.com",
                address = "Jl. Anggrek No. 8",
                notes = "Juara 1 Lomba Membaca Puisi"
            ),
            Student(
                id = 3,
                nis = "2024003",
                name = "Budi Santoso",
                className = "Kelas 5A",
                gender = "L",
                parentName = "Bpk. Joko Santoso",
                parentPhone = "081234567893",
                parentEmail = "joko.santoso@yahoo.com",
                address = "Jl. Melati Blok C2",
                notes = "Perlu bimbingan tambahan dalam IPA"
            ),
            Student(
                id = 4,
                nis = "2024004",
                name = "Dewi Lestari",
                className = "Kelas 5A",
                gender = "P",
                parentName = "Ibu Ratna Dewi",
                parentPhone = "081234567894",
                parentEmail = "ratna.dewi88@gmail.com",
                address = "Jl. Flamboyan No. 24",
                notes = "Rajin dan selalu tepat waktu mengumpulkan tugas"
            ),
            Student(
                id = 5,
                nis = "2024005",
                name = "Rian Hidayat",
                className = "Kelas 5A",
                gender = "L",
                parentName = "Bpk. Hendra Hidayat",
                parentPhone = "081234567895",
                parentEmail = "hendra.h@gmail.com",
                address = "Jl. Mawar No. 5",
                notes = "Bakat kepemimpinan tinggi, ketua kelas"
            ),
            Student(
                id = 6,
                nis = "2024006",
                name = "Aisyah Putri",
                className = "Kelas 5A",
                gender = "P",
                parentName = "Ibu Farida",
                parentPhone = "081234567896",
                parentEmail = "farida.putri@gmail.com",
                address = "Jl. Kenanga No. 17",
                notes = "Kreatif dan gemar menggambar"
            ),
            Student(
                id = 7,
                nis = "2024007",
                name = "Farhan Ramadhan",
                className = "Kelas 5B",
                gender = "L",
                parentName = "Bpk. Rahmat",
                parentPhone = "081234567897",
                parentEmail = "rahmat.farhan@gmail.com",
                address = "Jl. Cendrawasih No. 3",
                notes = "Antusias dalam eksperimen sains"
            ),
            Student(
                id = 8,
                nis = "2024008",
                name = "Nabila Zahra",
                className = "Kelas 5B",
                gender = "P",
                parentName = "Ibu Siska",
                parentPhone = "081234567898",
                parentEmail = "siska.zahra@gmail.com",
                address = "Jl. Garuda No. 45",
                notes = "Sopan dan rajin membaca buku"
            )
        )
    }

    fun getInitialAttendance(todayStr: String): List<AttendanceRecord> {
        return listOf(
            AttendanceRecord(1, 1, todayStr, AttendanceStatus.HADIR, "Hadir tepat waktu", "Kelas 5A"),
            AttendanceRecord(2, 2, todayStr, AttendanceStatus.HADIR, "Hadir tepat waktu", "Kelas 5A"),
            AttendanceRecord(3, 3, todayStr, AttendanceStatus.SAKIT, "Demam (Surat dokter terlampir)", "Kelas 5A"),
            AttendanceRecord(4, 4, todayStr, AttendanceStatus.HADIR, "Hadir tepat waktu", "Kelas 5A"),
            AttendanceRecord(5, 5, todayStr, AttendanceStatus.HADIR, "Hadir tepat waktu", "Kelas 5A"),
            AttendanceRecord(6, 6, todayStr, AttendanceStatus.IZIN, "Acara keluarga ke luar kota", "Kelas 5A"),
            AttendanceRecord(7, 7, todayStr, AttendanceStatus.HADIR, "Hadir tepat waktu", "Kelas 5B"),
            AttendanceRecord(8, 8, todayStr, AttendanceStatus.HADIR, "Hadir tepat waktu", "Kelas 5B")
        )
    }

    fun getInitialGrades(): List<GradeRecord> {
        return listOf(
            GradeRecord(1, 1, "Matematika", GradeType.UTS, 92.0, 100.0, "2026-08-10", "Pemahaman konsep pecahan sangat baik"),
            GradeRecord(2, 1, "IPA", GradeType.KUIS, 88.0, 100.0, "2026-08-14", "Kuis Ekosistem"),
            GradeRecord(3, 1, "Bahasa Indonesia", GradeType.TUGAS, 90.0, 100.0, "2026-08-16", "Membuat Paragraf Deskripsi"),
            
            GradeRecord(4, 2, "Matematika", GradeType.UTS, 95.0, 100.0, "2026-08-10", "Nilai tertinggi UTS"),
            GradeRecord(5, 2, "IPA", GradeType.KUIS, 94.0, 100.0, "2026-08-14", "Kuis Ekosistem"),
            GradeRecord(6, 2, "Bahasa Indonesia", GradeType.TUGAS, 98.0, 100.0, "2026-08-16", "Puisi sangat indah"),

            GradeRecord(7, 3, "Matematika", GradeType.UTS, 68.0, 100.0, "2026-08-10", "Perlu penguatan pembagian pecahan"),
            GradeRecord(8, 3, "IPA", GradeType.KUIS, 72.0, 100.0, "2026-08-14", "Kuis Ekosistem"),
            GradeRecord(9, 3, "Bahasa Indonesia", GradeType.TUGAS, 75.0, 100.0, "2026-08-16", "Tugas meringkas bacaan"),

            GradeRecord(10, 4, "Matematika", GradeType.UTS, 85.0, 100.0, "2026-08-10", "Bagus"),
            GradeRecord(11, 4, "IPA", GradeType.KUIS, 89.0, 100.0, "2026-08-14", "Kuis Ekosistem"),
            GradeRecord(12, 4, "Bahasa Indonesia", GradeType.TUGAS, 88.0, 100.0, "2026-08-16", "Lengkap dan rapi"),

            GradeRecord(13, 5, "Matematika", GradeType.UTS, 88.0, 100.0, "2026-08-10", "Bagus"),
            GradeRecord(14, 5, "IPA", GradeType.KUIS, 86.0, 100.0, "2026-08-14", "Kuis Ekosistem"),
            GradeRecord(15, 5, "Bahasa Indonesia", GradeType.TUGAS, 90.0, 100.0, "2026-08-16", "Rapi dan terstruktur"),

            GradeRecord(16, 6, "Matematika", GradeType.UTS, 84.0, 100.0, "2026-08-10", "Baik"),
            GradeRecord(17, 6, "IPA", GradeType.KUIS, 90.0, 100.0, "2026-08-14", "Kuis Ekosistem"),
            GradeRecord(18, 6, "Bahasa Indonesia", GradeType.TUGAS, 92.0, 100.0, "2026-08-16", "Kreatif")
        )
    }

    fun getInitialQuizzes(): List<Quiz> {
        return listOf(
            Quiz(
                id = 1,
                title = "Kuis Harian: Operasi Hitung Pecahan",
                subject = "Matematika",
                className = "Kelas 5A",
                durationMinutes = 20,
                shareCode = "MAT-PEC-5A",
                materialSummary = "Materi membahas penjumlahan dan pengurangan pecahan dengan penyebut berbeda, serta perkalian pecahan biasa dengan bilangan bulat."
            ),
            Quiz(
                id = 2,
                title = "Evaluasi: Rantai Makanan & Ekosistem",
                subject = "IPA",
                className = "Kelas 5A",
                durationMinutes = 15,
                shareCode = "IPA-EKO-5A",
                materialSummary = "Materi mencakup produsen, konsumen tingkat 1, konsumen puncak, dekomposer (pengurai), dan aliran energi dalam ekosistem sawah dan hutan."
            ),
            Quiz(
                id = 3,
                title = "Uji Pemahaman: Struktur Paragraf & Teks Narasi",
                subject = "Bahasa Indonesia",
                className = "Kelas 5A",
                durationMinutes = 15,
                shareCode = "BIN-PAR-5A",
                materialSummary = "Menentukan gagasan pokok, kalimat penjelas, serta alur cerita orientasi, komplikasi, dan resolusi dalam teks naratif."
            )
        )
    }

    fun getInitialQuizQuestions(): List<QuizQuestion> {
        return listOf(
            // Quiz 1 Questions
            QuizQuestion(
                id = 1,
                quizId = 1,
                questionText = "Berapakah hasil dari 1/2 + 1/4 ?",
                optionA = "2/6",
                optionB = "3/4",
                optionC = "2/4",
                optionD = "1/8",
                correctOptionIndex = 1,
                explanation = "Samakan penyebut: 1/2 = 2/4. Maka 2/4 + 1/4 = 3/4."
            ),
            QuizQuestion(
                id = 2,
                quizId = 1,
                questionText = "Hasil dari 3/5 x 10 adalah...",
                optionA = "6",
                optionB = "5",
                optionC = "30/50",
                optionD = "15",
                correctOptionIndex = 0,
                explanation = "3/5 x 10 = (3 x 10) / 5 = 30 / 5 = 6."
            ),
            QuizQuestion(
                id = 3,
                quizId = 1,
                questionText = "Bentuk pecahan paling sederhana dari 12/16 adalah...",
                optionA = "6/8",
                optionB = "2/3",
                optionC = "3/4",
                optionD = "4/5",
                correctOptionIndex = 2,
                explanation = "Bagi pembilang dan penyebut dengan FPB yaitu 4: 12:4 / 16:4 = 3/4."
            ),
            // Quiz 2 Questions
            QuizQuestion(
                id = 4,
                quizId = 2,
                questionText = "Dalam rantai makanan ekosistem sawah, manakah yang berperan sebagai produsen?",
                optionA = "Tikus",
                optionB = "Padi",
                optionC = "Ular",
                optionD = "Elang",
                correctOptionIndex = 1,
                explanation = "Padi adalah tumbuhan hijau yang mampu memproduksi makanannya sendiri melalui fotosintesis."
            ),
            QuizQuestion(
                id = 5,
                quizId = 2,
                questionText = "Organisme yang berfungsi menguraikan sisa-sisa makhluk hidup yang telah mati disebut...",
                optionA = "Konsumen tingkat 1",
                optionB = "Karnivora",
                optionC = "Dekomposer (Pengurai)",
                optionD = "Herbivora",
                correctOptionIndex = 2,
                explanation = "Bakteri dan jamur adalah dekomposer yang menguraikan zat organik menjadi mineral tanah."
            ),
            // Quiz 3 Questions
            QuizQuestion(
                id = 6,
                quizId = 3,
                questionText = "Kalimat yang memuat inti permasalahan atau gagasan utama dalam sebuah paragraf disebut...",
                optionA = "Kalimat penjelas",
                optionB = "Kalimat utama / pokok",
                optionC = "Kalimat penutup",
                optionD = "Kalimat tanya",
                correctOptionIndex = 1,
                explanation = "Kalimat utama memuat ide pokok yang kemudian dijelaskan lebih lanjut oleh kalimat penjelas."
            )
        )
    }

    fun getInitialHomeworks(): List<Homework> {
        return listOf(
            Homework(
                id = 1,
                title = "Latihan Mandiri Pecahan Campuran (Hal 45 No 1-10)",
                subject = "Matematika",
                className = "Kelas 5A",
                description = "Kerjakan di buku tulis matematika dengan cara lengkap. Fotokan dan kumpulkan besok pagi.",
                dueDate = "2026-08-22",
                dueTime = "07:30",
                isCompleted = false,
                syncedToCalendar = true
            ),
            Homework(
                id = 2,
                title = "Membuat Diorama Rantai Makanan Sederhana",
                subject = "IPA",
                className = "Kelas 5A",
                description = "Gunakan bahan kardus bekas atau kertas bergambar untuk menunjukkan aliran rantai makanan di hutan/laut.",
                dueDate = "2026-08-25",
                dueTime = "08:00",
                isCompleted = false,
                syncedToCalendar = true
            ),
            Homework(
                id = 3,
                title = "Menulis Paragraf Narasi Pengalaman Liburan",
                subject = "Bahasa Indonesia",
                className = "Kelas 5A",
                description = "Minimal 3 paragraf dengan memperhatikan penggunaan huruf kapital dan tanda baca yang tepat.",
                dueDate = "2026-08-21",
                dueTime = "12:00",
                isCompleted = true,
                syncedToCalendar = true
            )
        )
    }

    fun getInitialScheduleEvents(): List<ScheduleEvent> {
        return listOf(
            ScheduleEvent(
                id = 1,
                title = "Jadwal Belajar: Matematika & IPA",
                eventType = EventType.JADWAL_BELAJAR,
                subject = "Matematika",
                className = "Kelas 5A",
                date = "2026-08-20",
                startTime = "07:30",
                endTime = "09:30",
                location = "Ruang Kelas 5A",
                reminderEnabled = true
            ),
            ScheduleEvent(
                id = 2,
                title = "Tenggat Pengumpulan: Paragraf Narasi",
                eventType = EventType.TENGGAT_TUGAS,
                subject = "Bahasa Indonesia",
                className = "Kelas 5A",
                date = "2026-08-21",
                startTime = "12:00",
                endTime = "12:00",
                location = "Meja Guru / LMS",
                reminderEnabled = true
            ),
            ScheduleEvent(
                id = 3,
                title = "Tenggat Pengumpulan: Pecahan Campuran",
                eventType = EventType.TENGGAT_TUGAS,
                subject = "Matematika",
                className = "Kelas 5A",
                date = "2026-08-22",
                startTime = "07:30",
                endTime = "07:30",
                location = "Ruang Kelas 5A",
                reminderEnabled = true
            ),
            ScheduleEvent(
                id = 4,
                title = "Penilaian Tengah Semester (PTS) Matematika",
                eventType = EventType.UJIAN,
                subject = "Matematika",
                className = "Kelas 5A",
                date = "2026-08-28",
                startTime = "08:00",
                endTime = "09:30",
                location = "Ruang Kelas 5A",
                reminderEnabled = true
            ),
            ScheduleEvent(
                id = 5,
                title = "Penilaian Tengah Semester (PTS) IPA",
                eventType = EventType.UJIAN,
                subject = "IPA",
                className = "Kelas 5A",
                date = "2026-08-29",
                startTime = "08:00",
                endTime = "09:30",
                location = "Ruang Kelas 5A",
                reminderEnabled = true
            ),
            ScheduleEvent(
                id = 6,
                title = "Pertemuan Wali Murid & Evaluasi Bulanan",
                eventType = EventType.RAPAT_WALI,
                subject = "Wali Kelas",
                className = "Kelas 5A",
                date = "2026-08-30",
                startTime = "09:00",
                endTime = "11:00",
                location = "Aula Sekolah Lantai 2",
                reminderEnabled = true
            )
        )
    }

    fun getInitialChatMessages(): List<ChatMessage> {
        val now = System.currentTimeMillis()
        val oneHourAgo = now - 3600000
        val twoHoursAgo = now - 7200000
        val yesterday = now - 86400000

        return listOf(
            ChatMessage(
                id = 1,
                studentId = 3,
                parentName = "Bpk. Joko Santoso",
                studentName = "Budi Santoso",
                message = "Selamat pagi Pak Guru, mohon izin mengabarkan bahwa Budi hari ini demam tinggi dan tidak bisa mengikuti kegiatan sekolah. Surat dokter menyusul nggih.",
                isFromTeacher = false,
                timestamp = yesterday,
                tag = "KEHADIRAN"
            ),
            ChatMessage(
                id = 2,
                studentId = 3,
                parentName = "Bpk. Joko Santoso",
                studentName = "Budi Santoso",
                message = "Selamat pagi Pak Joko. Baik, kami catat kehadiran Budi sebagai Sakit. Semoga ananda lekas pulih dan sehat kembali. Materi pelajaran hari ini akan kami kirimkan via kuis daring.",
                isFromTeacher = true,
                timestamp = yesterday + 600000,
                tag = "KEHADIRAN"
            ),
            ChatMessage(
                id = 3,
                studentId = 1,
                parentName = "Bpk. Bambang Pratama",
                studentName = "Aditya Pratama",
                message = "Assalamu'alaikum Pak Guru, terima kasih atas laporan nilai UTS Aditya yang sangat memuaskan di Matematika.",
                isFromTeacher = false,
                timestamp = twoHoursAgo,
                tag = "NILAI"
            ),
            ChatMessage(
                id = 4,
                studentId = 1,
                parentName = "Bpk. Bambang Pratama",
                studentName = "Aditya Pratama",
                message = "Wa'alaikumussalam Bpk. Bambang. Sama-sama, Aditya sangat fokus dan aktif saat sesi latihan di kelas. Mohon tetap didampingi saat belajar mandiri di rumah ya pak.",
                isFromTeacher = true,
                timestamp = oneHourAgo,
                tag = "NILAI"
            ),
            ChatMessage(
                id = 5,
                studentId = 6,
                parentName = "Ibu Farida",
                studentName = "Aisyah Putri",
                message = "Pak Guru, untuk tugas diorama IPA kelompok Aisyah apakah kardusnya boleh memakai ukuran bekas sepatu?",
                isFromTeacher = false,
                timestamp = now - 1800000,
                tag = "TUGAS"
            )
        )
    }

    fun getInitialAlerts(): List<PushAlert> {
        val now = System.currentTimeMillis()
        return listOf(
            PushAlert(
                id = 1,
                title = "Pengingat Tenggat Tugas Rumah",
                description = "Tugas Matematika Bab Pecahan dikumpulkan besok pukul 07:30 WIB.",
                type = "TUGAS",
                targetDate = "2026-08-22",
                isRead = false,
                timestamp = now - 1200000
            ),
            PushAlert(
                id = 2,
                title = "Jadwal Ujian PTS Pekan Depan",
                description = "PTS Matematika & IPA akan dilaksanakan pada 28-29 Agustus 2026.",
                type = "UJIAN",
                targetDate = "2026-08-28",
                isRead = false,
                timestamp = now - 3600000
            )
        )
    }
}
