package org.delcom.starter.controllers;

import org.springframework.web.bind.annotation.*;
import java.util.*;

/**
 * Controller utama yang menangani berbagai endpoint untuk pengujian logika sederhana.
 * Seluruh endpoint dipertahankan sama, hanya perbaikan struktur dan efisiensi dilakukan.
 */
@RestController
public class HomeController {

    // ✅ Tetap seperti aslinya
    @GetMapping("/")
    public String hello() {
        return "Hay Abdullah, selamat datang di pengembangan aplikasi dengan Spring Boot!";
    }

    // =========================================================
    // 🔹 1. Say Hello
    // =========================================================
    @GetMapping("/sayHello/{nama}")
    public String sayHello(@PathVariable String nama) {
        return String.format("Hello, %s!", nama);
    }

    // =========================================================
    // 🔹 2. Informasi NIM
    // =========================================================
    @PostMapping("/informasiNim")
    public String informasiNim(@RequestBody String nim) {
        nim = nim.trim();

        if (nim.length() != 8) {
            return "NIM harus 8 karakter";
        }

        String kodeProdi = nim.substring(0, 3);
        String angkatan = "20" + nim.substring(3, 5);
        String urutan = nim.substring(7);

        Map<String, String> prodiMap = new HashMap<>();
        prodiMap.put("11S", "Sarjana Informatika");
        prodiMap.put("12S", "Sarjana Sistem Informasi");
        prodiMap.put("14S", "Sarjana Teknik Elektro");
        prodiMap.put("21S", "Sarjana Manajemen Rekayasa");
        prodiMap.put("22S", "Sarjana Teknik Metalurgi");
        prodiMap.put("31S", "Sarjana Teknik Bioproses");
        prodiMap.put("114", "Diploma 4 Teknologi Rekasaya Perangkat Lunak");
        prodiMap.put("113", "Diploma 3 Teknologi Informasi");
        prodiMap.put("133", "Diploma 3 Teknologi Komputer");

        String programStudi = prodiMap.get(kodeProdi);
        if (programStudi == null) {
            return "Program Studi tidak Tersedia";
        }

        return String.format(
            "Inforamsi NIM %s: >> Program Studi: %s>> Angkatan: %s>> Urutan: %s",
            nim, programStudi, angkatan, urutan
        );
    }

    // =========================================================
    // 🔹 3. Perolehan Nilai
    // =========================================================
    @GetMapping("/perolehanNilai")
    public String perolehanNilai(@RequestParam String strBase64) {
        byte[] decodedBytes = Base64.getDecoder().decode(strBase64);
        String nilai = new String(decodedBytes);
        return "Perolehan Nilai: " + nilai;
    }

    // =========================================================
    // 🔹 4. Perbedaan L
    // =========================================================
    @GetMapping("/perbedaanL/{strBase64}")
    public String perbedaanL(@PathVariable String strBase64) {
        try {
            String decoded = new String(Base64.getDecoder().decode(strBase64));
            String[] parts = decoded.trim().split("\\|");

            if (parts.length != 3) {
                return "Error: Format input tidak valid. Harusnya 'angka1|angka2|angka3'.";
            }

            int nilaiL = Integer.parseInt(parts[0]);
            int nilaiKebalikanL = Integer.parseInt(parts[1]);
            int nilaiTengah = Integer.parseInt(parts[2]);
            int perbedaan = Math.abs(nilaiL - nilaiKebalikanL);

            return String.format(
                "Nilai L: %d: Nilai Kebalikan L: %d: Nilai Tengah: %d Perbedaan: %d Dominan: %d",
                nilaiL, nilaiKebalikanL, nilaiTengah, perbedaan, nilaiTengah
            );

        } catch (NumberFormatException e) {
            return "Error: Input bukan angka yang valid.";
        } catch (IllegalArgumentException e) {
            return "Error: Format Base64 tidak valid.";
        }
    }

    // =========================================================
    // 🔹 5. Paling Ter
    // =========================================================
    @GetMapping("/palingTer/{strBase64}")
    public String palingTer(@PathVariable String strBase64) {
        try {
            String decoded = new String(Base64.getDecoder().decode(strBase64));
            String[] baris = decoded.split("\\R");

            List<Integer> angkaValid = new ArrayList<>();
            for (String b : baris) {
                try {
                    angkaValid.add(Integer.parseInt(b.trim()));
                } catch (NumberFormatException ignored) {
                }
            }

            if (angkaValid.isEmpty()) {
                return "Tidak ada angka yang valid ditemukan.";
            }

            int min = Collections.min(angkaValid);
            int max = Collections.max(angkaValid);

            Map<Integer, Integer> frekuensi = new LinkedHashMap<>();
            for (int n : angkaValid) {
                frekuensi.put(n, frekuensi.getOrDefault(n, 0) + 1);
            }

            int terbanyakVal = min, tersedikitVal = min;
            int terbanyakCount = 0, tersedikitCount = Integer.MAX_VALUE;

            for (Map.Entry<Integer, Integer> entry : frekuensi.entrySet()) {
                int val = entry.getKey();
                int count = entry.getValue();
                if (count > terbanyakCount) {
                    terbanyakCount = count;
                    terbanyakVal = val;
                }
                if (count < tersedikitCount) {
                    tersedikitCount = count;
                    tersedikitVal = val;
                }
            }

            long jumlahTertinggi = (long) max * terbanyakCount;
            long jumlahTerendah = (long) min * frekuensi.get(min);

            return String.format(
                "Tertinggi: %d Terendah: %d Terbanyak: %d (%dx) Tersedikit: %d (%dx) " +
                "Jumlah Tertinggi: %d * %d = %d Jumlah Terendah: %d * %d = %d",
                max, min, terbanyakVal, terbanyakCount, tersedikitVal, tersedikitCount,
                max, terbanyakCount, jumlahTertinggi,
                min, frekuensi.get(min), jumlahTerendah
            );

        } catch (IllegalArgumentException e) {
            return "Error: Format Base64 tidak valid.";
        }
    }
}
