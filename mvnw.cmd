package org.delcom.starter.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Base64;

@RestController
public class HomeController {

    @GetMapping("/")
    public String hello() {
        return "Hay Abdullah, selamat datang di pengembangan aplikasi dengan Spring Boot!";
    }

    @GetMapping("/hello/{name}")
    public String sayHello(@PathVariable String name) {
        return "Hello, " + name + "!";
    }

    // ============================
    // 4.1 Migrasi Kode Praktikum 1
    // ============================

    // 1️⃣ Informasi NIM
    @GetMapping("/informasiNim/{nim}")
    public String informasiNim(@PathVariable String nim) {
        String programStudi = getProgramStudi(nim.substring(0, 3));
        String angkatan = "20" + nim.substring(3, 5);
        String urutan = String.valueOf(Integer.parseInt(nim.substring(5)));

        return String.format(
            "Informasi NIM %s:<br>> Program Studi: %s<br>> Angkatan: %s<br>> Urutan: %s",
            nim, programStudi, angkatan, urutan
        );
    }

    // ✅ Disesuaikan agar cocok dengan unit test
    private String getProgramStudi(String prefix) {
        switch (prefix) {
            case "11S": return "Sarjana Informatika";
            case "12S": return "Sarjana Sistem Informasi";
            case "14S": return "Sarjana Teknik Elektro";
            case "21S": return "Sarjana Manajemen Rekayasa";
            case "22S": return "Sarjana Teknik Metalurgi";
            case "31S": return "Sarjana Teknik Bioproses";
            case "114": return "Diploma Teknik Rekayasa Perangkat Lunak";
            case "113": return "Diploma Teknologi Informasi";
            case "133": return "Diploma Teknologi Komputer";
            default: return "Program Studi Tidak Dikenal";
        }
    }

    // 2️⃣ Perolehan Nilai
    @GetMapping("/perolehanNilai")
    public String perolehanNilai(@RequestParam String strBase64) {
        byte[] decoded = Base64.getDecoder().decode(strBase64);
        String nilai = new String(decoded);
        return "Perolehan Nilai: " + nilai;
    }

    // 3️⃣ Perbedaan L dan Kebalikannya
    @GetMapping("/perbedaanL")
    public String perbedaanL(@RequestParam String strBase64) {
        byte[] decoded = Base64.getDecoder().decode(strBase64);
        String text = new String(decoded);

        StringBuilder reversed = new StringBuilder(text).reverse();
        StringBuilder diff = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) != reversed.charAt(i)) {
                diff.append(text.charAt(i));
            }
        }

        return String.format(
            "Teks Asli: %s<br>Kebalikannya: %s<br>Perbedaannya: %s",
            text, reversed, diff
        );
    }

    // 4️⃣ Paling Ter
    @GetMapping("/palingTer")
    public String palingTer(@RequestParam String strBase64) {
        byte[] decoded = Base64.getDecoder().decode(strBase64);
        String data = new String(decoded);

        // Pisahkan berdasarkan spasi, lalu cari kata terpanjang dan terpendek
        String[] kata = data.split("\\s+");
        String terpendek = kata[0];
        String terpanjang = kata[0];

        for (String k : kata) {
            if (k.length() < terpendek.length()) terpendek = k;
            if (k.length() > terpanjang.length()) terpanjang = k;
        }

        return String.format(
            "Kalimat: %s<br>Paling Pendek: %s<br>Paling Panjang: %s",
            data, terpendek, terpanjang
        );
    }
}