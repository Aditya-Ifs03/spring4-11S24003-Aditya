package org.delcom.starter.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;

@RestController
public class HomeController {

    // ==========================
    // 1️⃣ Metode Dasar
    // ==========================
    @GetMapping("/")
    public String hello() {
        return "Hay Aditya, selamat datang di pengembangan aplikasi dengan Spring Boot!";
    }

    @GetMapping("/hello/{name}")
    public String sayHello(@PathVariable String name) {
        return "Hello, " + name + "!";
    }

    // ==========================
    // 2️⃣ Praktikum 1 - Informasi NIM
    // ==========================
    @GetMapping("/informasiNim/{nim}")
    public String informasiNim(@PathVariable String nim) {
        if (nim == null || nim.length() < 5)
            return "Error: NIM tidak valid.";

        String prefix = nim.substring(0, 3);
        String angkatan = nim.length() >= 5 ? nim.substring(3, 5) : "??";
        String nomor = nim.length() > 5 ? nim.substring(5) : "??";
        String prodi;

        switch (prefix) {
            case "11S":
                prodi = "Sarjana Informatika";
                break;
            case "12S":
                prodi = "Sarjana Sistem Informasi";
                break;
            case "14S":
                prodi = "Sarjana Teknik Elektro";
                break;
            case "21S":
                prodi = "Sarjana Manajemen Rekayasa";
                break;
            case "22S":
                prodi = "Sarjana Teknik Metalurgi";
                break;
            case "31S":
                prodi = "Sarjana Teknik Bioproses";
                break;
            case "114":
                prodi = "Diploma 4 Teknologi Rekayasa Perangkat Lunak";
                break;
            case "113":
                prodi = "Diploma 3 Teknologi Informasi";
                break;
            case "133":
                prodi = "Diploma 3 Teknologi Komputer";
                break;
            default:
                prodi = "Program studi tidak dikenal";
        }

        return String.format(
                "Inforamsi NIM %s: \n>> Program Studi: %s\n>> Angkatan: 20%s\n>> Urutan: %s",
                nim,
                prodi,
                angkatan,
                nomor.replaceFirst("^0+", "")
        );
    }

    // ==========================
    // 3️⃣ Praktikum 2 - Perolehan Nilai
    // ==========================
    @GetMapping("/perolehanNilai/{strBase64}")
    public String perolehanNilai(@PathVariable String strBase64) {
        try {
            String decoded = new String(Base64.getDecoder().decode(strBase64));
            String[] lines = decoded.split("\\r?\\n");
            int i = 0;

            if (lines.length < 6) { // Menambah validasi minimal jumlah baris untuk bobot
                throw new IllegalArgumentException("Jumlah baris bobot tidak mencukupi.");
            }

            int bPA = Integer.parseInt(lines[i++].trim());
            int bT = Integer.parseInt(lines[i++].trim());
            int bK = Integer.parseInt(lines[i++].trim());
            int bP = Integer.parseInt(lines[i++].trim());
            int bUTS = Integer.parseInt(lines[i++].trim());
            int bUAS = Integer.parseInt(lines[i++].trim());

            double pPA = 0, pT = 0, pK = 0, pP = 0, pUTS = 0, pUAS = 0;
            double mPA = 0, mT = 0, mK = 0, mP = 0, mUTS = 0, mUAS = 0;

            for (; i < lines.length; i++) {
                String line = lines[i].trim();
                if (line.equals("---")) break;
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\|");
                if (parts.length != 3) continue;

                try {
                    String jenis = parts[0].trim();
                    int nilaiMax = Integer.parseInt(parts[1].trim());
                    int nilaiPeroleh = Integer.parseInt(parts[2].trim());

                    switch (jenis) {
                        case "PA": pPA += nilaiPeroleh; mPA += nilaiMax; break;
                        case "T":  pT += nilaiPeroleh;  mT += nilaiMax;  break;
                        case "K":  pK += nilaiPeroleh;  mK += nilaiMax;  break;
                        case "P":  pP += nilaiPeroleh;  mP += nilaiMax;  break;
                        case "UTS":pUTS += nilaiPeroleh;mUTS += nilaiMax;break;
                        case "UAS":pUAS += nilaiPeroleh;mUAS += nilaiMax;break;
                        default:
                            // Menambah cabang default untuk cakupan, meskipun tidak melakukan apa-apa
                            // Anda bisa log di sini jika ingin memantau input yang tidak dikenal
                            break;
                    }
                } catch (NumberFormatException e) {
                    // Melewatkan input salah tanpa error, NumberFormatException sudah ditangani
                    // Ini tetap dibutuhkan untuk cakupan karena block catch dihitung
                }
            }

            class Hitung {
                int persen(double peroleh, double max) {
                    return max <= 0 ? 0 : (int) Math.floor((peroleh / max) * 100.0 + 1e-9);
                }

                double kontribusi(int persen, int bobot) {
                    return (persen / 100.0) * bobot;
                }

                String grade(double n) {
                    if (n >= 79.5) return "A";
                    else if (n >= 72) return "AB";
                    else if (n >= 64.5) return "B";
                    else if (n >= 57) return "BC";
                    else if (n >= 49.5) return "C";
                    else if (n >= 34) return "D";
                    else return "E";
                }
            }

            Hitung h = new Hitung();
            int pp = h.persen(pPA, mPA);
            int pt = h.persen(pT, mT);
            int pk = h.persen(pK, mK);
            int ppj = h.persen(pP, mP);
            int puts = h.persen(pUTS, mUTS);
            int puas = h.persen(pUAS, mUAS);

            double nilaiAkhir = h.kontribusi(pp, bPA)
                    + h.kontribusi(pt, bT)
                    + h.kontribusi(pk, bK)
                    + h.kontribusi(ppj, bP)
                    + h.kontribusi(puts, bUTS)
                    + h.kontribusi(puas, bUAS);

            return "Perolehan Nilai:\n"
                    + String.format(Locale.US, ">> Partisipatif: %d/100 (%.2f/%d)\n", pp, h.kontribusi(pp, bPA), bPA)
                    + String.format(Locale.US, ">> Tugas: %d/100 (%.2f/%d)\n", pt, h.kontribusi(pt, bT), bT)
                    + String.format(Locale.US, ">> Kuis: %d/100 (%.2f/%d)\n", pk, h.kontribusi(pk, bK), bK)
                    + String.format(Locale.US, ">> Proyek: %d/100 (%.2f/%d)\n", ppj, h.kontribusi(ppj, bP), bP)
                    + String.format(Locale.US, ">> UTS: %d/100 (%.2f/%d)\n", puts, h.kontribusi(puts, bUTS), bUTS)
                    + String.format(Locale.US, ">> UAS: %d/100 (%.2f/%d)\n", puas, h.kontribusi(puas, bUAS), bUAS)
                    + String.format(Locale.US, "\n>> Nilai Akhir: %.2f\n", nilaiAkhir)
                    + ">> Grade: " + h.grade(nilaiAkhir);
        } catch (Exception e) {
            return "Error: Input tidak valid.";
        }
    }

    // ==========================
    // 4️⃣ Praktikum 3 - Perbedaan L
    // ==========================
    @GetMapping("/perbedaanL/{strBase64}")
    public String perbedaanL(@PathVariable String strBase64) {
        try {
            String decoded = new String(Base64.getDecoder().decode(strBase64));
            String[] lines = decoded.split("\\r?\\n");

            if (lines.length == 0 || lines[0].trim().isEmpty()) { // Handle empty input for n
                throw new IllegalArgumentException("Input untuk N tidak valid.");
            }

            int n = Integer.parseInt(lines[0].trim());

            if (n < 1) { // Handle n < 1
                throw new IllegalArgumentException("Ukuran matriks N harus lebih besar dari 0.");
            }
            if (lines.length < n + 1) { // Check if enough rows are provided for the matrix
                throw new IllegalArgumentException("Jumlah baris matriks tidak mencukupi.");
            }

            int[][] m = new int[n][n];

            for (int i = 0; i < n; i++) {
                String rowLine = lines[i + 1].trim();
                String[] row = rowLine.split("\\s+");
                if (row.length != n) { // Check if row has correct number of elements
                    throw new IllegalArgumentException("Jumlah elemen dalam baris matriks tidak sesuai.");
                }
                for (int j = 0; j < n; j++) {
                    m[i][j] = Integer.parseInt(row[j]);
                }
            }

            int nL = -1, nKL = -1;
            if (n >= 3) {
                nL = 0;
                for (int i = 0; i < n; i++) nL += m[i][0];
                for (int j = 0; j < n; j++) nL += m[n - 1][j];
                nL -= m[n - 1][0];

                nKL = 0;
                for (int j = 0; j < n; j++) nKL += m[0][j];
                for (int i = 0; i < n; i++) nKL += m[i][n - 1];
                nKL -= m[0][n - 1];
            }

            int nT;
            if (n % 2 == 1) {
                nT = m[n / 2][n / 2];
            } else {
                // Menambahkan pengecekan n >= 2 agar indeks tidak keluar batas untuk matriks 1x1 atau 0x0
                if (n < 2) {
                    nT = 0; // Atau nilai default lain yang sesuai
                } else {
                    nT = m[n / 2 - 1][n / 2 - 1] + m[n / 2 - 1][n / 2] + m[n / 2][n / 2 - 1] + m[n / 2][n / 2];
                }
            }


            String pS;
            int p = 0;
            if (nL == -1) {
                pS = "Tidak Ada";
            } else {
                p = Math.abs(nL - nKL);
                pS = String.valueOf(p);
            }

            int dominan;
            if (nL == -1 || nKL == -1) { // Jika salah satu tidak ada, dominan adalah nT
                dominan = nT;
            } else { // Jika keduanya ada, baru bandingkan
                dominan = (p == 0) ? nT : Math.max(nL, nKL);
            }


            return String.format(
                    "Nilai L: %s\nNilai Kebalikan L: %s\nNilai Tengah: %d\nPerbedaan: %s\nDominan: %d",
                    (nL == -1 ? "Tidak Ada" : nL),
                    (nKL == -1 ? "Tidak Ada" : nKL),
                    nT,
                    pS,
                    dominan
            );
        } catch (Exception e) {
            return "Error: Input tidak valid.";
        }
    }

    // ==========================
    // 5️⃣ Praktikum 4 - Paling Ter
    // ==========================
    @GetMapping("/palingTer/{strBase64}")
    public String palingTer(@PathVariable String strBase64) {
        try {
            String decoded = new String(Base64.getDecoder().decode(strBase64));
            List<Integer> listNilai = new ArrayList<>();

            for (String line : decoded.split("\\r?\\n")) {
                String t = line.trim();
                if (t.equals("---")) break;
                if (t.isEmpty()) continue;
                listNilai.add(Integer.parseInt(t));
            }

            if (listNilai.isEmpty())
                return "Error: Tidak ada data input.";

            Map<Integer, Integer> freqMap = new LinkedHashMap<>();
            for (int val : listNilai)
                freqMap.put(val, freqMap.getOrDefault(val, 0) + 1);

            int maxVal = Collections.max(listNilai);
            int minVal = Collections.min(listNilai);
            int frekTerbanyak = Collections.max(freqMap.values());
            int frekTersedikit = Collections.min(freqMap.values());

            int angkaTerbanyak = 0;
            // Iterasi melalui freqMap untuk menemukan angkaTerbanyak pertama yang memiliki frekTerbanyak
            for (Map.Entry<Integer, Integer> entry : freqMap.entrySet()) {
                if (entry.getValue() == frekTerbanyak) {
                    angkaTerbanyak = entry.getKey();
                    break; // Ambil yang pertama ditemukan
                }
            }

            int angkaTersedikit = 0;
            // Iterasi melalui freqMap untuk menemukan angkaTersedikit pertama yang memiliki frekTersedikit
            for (Map.Entry<Integer, Integer> entry : freqMap.entrySet()) {
                if (entry.getValue() == frekTersedikit) {
                    angkaTersedikit = entry.getKey();
                    break; // Ambil yang pertama ditemukan
                }
            }

            int nilaiJumlahTertinggi = 0, jumlahTertinggi = -1, frekJumlahTertinggi = 0;
            for (Map.Entry<Integer, Integer> entry : freqMap.entrySet()) {
                int angka = entry.getKey();
                int freq = entry.getValue();
                int total = angka * freq;
                if (total > jumlahTertinggi || (total == jumlahTertinggi && angka > nilaiJumlahTertinggi)) {
                    jumlahTertinggi = total;
                    nilaiJumlahTertinggi = angka;
                    frekJumlahTertinggi = freq;
                }
            }

            int nilaiJumlahTerendah = 0;
            int jumlahTerendah = Integer.MAX_VALUE;
            // Menambah inisialisasi frek untuk nilaiJumlahTerendah
            int frekJumlahTerendah = 0;
            for (Map.Entry<Integer, Integer> entry : freqMap.entrySet()) {
                int angka = entry.getKey();
                int freq = entry.getValue(); // Mengambil frekuensi di sini
                int total = angka * freq;
                if (total < jumlahTerendah || (total == jumlahTerendah && angka < nilaiJumlahTerendah)) {
                    jumlahTerendah = total;
                    nilaiJumlahTerendah = angka;
                    frekJumlahTerendah = freq; // Menyimpan frekuensi
                }
            }

            return String.format(
                    "Tertinggi: %d\nTerendah: %d\nTerbanyak: %d (%dx)\nTersedikit: %d (%dx)\nJumlah Tertinggi: %d * %d = %d\nJumlah Terendah: %d * %d = %d",
                    maxVal, minVal,
                    angkaTerbanyak, frekTerbanyak,
                    angkaTersedikit, frekTersedikit,
                    nilaiJumlahTertinggi, frekJumlahTertinggi, jumlahTertinggi,
                    nilaiJumlahTerendah, frekJumlahTerendah, jumlahTerendah // Menggunakan frekJumlahTerendah
            );
        } catch (Exception e) {
            return "Error: Input tidak valid.";
        }
    }
}