package org.delcom.starter.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class HomeControllerUnitTest {

    private final HomeController controller = new HomeController();

    @Test
    @DisplayName("Test endpoint root - mengembalikan pesan selamat datang")
    void testHomeEndpoint() {
        String result = controller.home();
        assertEquals("Hay Abdullah, selamat datang di pengembangan aplikasi dengan Spring Boot!", result);
    }

    @Test
    @DisplayName("Test endpoint hello dengan nama - mengembalikan sapaan personal")
    void testHelloWithName() {
        String result = controller.hello("TestUser");
        assertEquals("Hello, TestUser!", result);
    }

    @Test
    @DisplayName("Test informasi NIM dengan NIM valid")
    void testInformasiNIM_Valid() {
        // Test 11S (Sarjana Informatika)
        ResponseEntity<?> response = controller.getInfoNIM("11S23001");
        
        assertTrue(response.getStatusCode().is2xxSuccessful());
        String body = (String) response.getBody();
        assertTrue(body.contains("Sarjana Informatika"));
        assertTrue(body.contains("2023"));
        assertTrue(body.contains("001"));
    }

    @Test
    @DisplayName("Test informasi NIM dengan NIM valid - Prodi Lain (114 D4 TRPL)")
    void testInformasiNIM_ValidOtherProdi() {
        // Test 114 (Diploma 4 Teknologi Rekayasa Perangkat Lunak)
        ResponseEntity<?> response = controller.getInfoNIM("11419020");
        
        assertTrue(response.getStatusCode().is2xxSuccessful());
        String body = (String) response.getBody();
        assertTrue(body.contains("Diploma 4 Teknologi Rekasaya Perangkat Lunak"));
        assertTrue(body.contains("2019"));
        assertTrue(body.contains("20"));
    }

    @Test
    @DisplayName("Test informasi NIM dengan NIM invalid")
    void testInformasiNIM_Invalid() {
        // NIM kurang dari 8 digit
        ResponseEntity<?> response = controller.getInfoNIM("123");
        
        assertTrue(response.getStatusCode().is4xxClientError());
        String body = (String) response.getBody();
        assertTrue(body.contains("NIM harus 8 digit"));
    }

    @Test
    @DisplayName("Test informasi NIM dengan NIM null")
    void testInformasiNIM_Null() {
        // NIM null (meskipun jarang terjadi di PathVariable, perlu diuji untuk logic)
        ResponseEntity<?> response = controller.getInfoNIM(null);
        
        assertTrue(response.getStatusCode().is4xxClientError());
        String body = (String) response.getBody();
        assertTrue(body.contains("NIM harus 8 digit"));
    }

    @Test
    @DisplayName("Test informasi NIM dengan kode prodi tidak dikenal")
    void testInformasiNIM_UnknownProdi() {
        ResponseEntity<?> response = controller.getInfoNIM("99923001");
        
        assertTrue(response.getStatusCode().is4xxClientError());
        String body = (String) response.getBody();
        assertTrue(body.contains("Kode program studi tidak dikenali"));
    }
    
    // --- PENAMBAHAN UNTUK 100% COVERAGE convertToGrade ---
    @Test
    @DisplayName("Test konversi nilai ke Grade - Semua Grade")
    void testConvertToGrade_AllGrades() {
        // Menggunakan Reflection untuk mengakses method private
        try {
            java.lang.reflect.Method convertToGrade = HomeController.class.getDeclaredMethod("convertToGrade", double.class);
            convertToGrade.setAccessible(true);

            // A (>= 79.5)
            assertEquals("A", convertToGrade.invoke(controller, 79.5), "Harusnya A");
            // AB (>= 72.0)
            assertEquals("AB", convertToGrade.invoke(controller, 72.0), "Harusnya AB");
            assertEquals("AB", convertToGrade.invoke(controller, 79.49), "Harusnya AB");
            // B (>= 64.5)
            assertEquals("B", convertToGrade.invoke(controller, 64.5), "Harusnya B");
            assertEquals("B", convertToGrade.invoke(controller, 71.99), "Harusnya B");
            // BC (>= 57.0)
            assertEquals("BC", convertToGrade.invoke(controller, 57.0), "Harusnya BC");
            assertEquals("BC", convertToGrade.invoke(controller, 64.49), "Harusnya BC");
            // C (>= 49.5)
            assertEquals("C", convertToGrade.invoke(controller, 49.5), "Harusnya C");
            assertEquals("C", convertToGrade.invoke(controller, 56.99), "Harusnya C");
            // D (>= 34.0)
            assertEquals("D", convertToGrade.invoke(controller, 34.0), "Harusnya D");
            assertEquals("D", convertToGrade.invoke(controller, 49.49), "Harusnya D");
            // E (< 34.0)
            assertEquals("E", convertToGrade.invoke(controller, 33.99), "Harusnya E");

        } catch (Exception e) {
            fail("Gagal mengakses method private: " + e.getMessage());
        }
    }
    // ----------------------------------------------------------------------


    @Test
    @DisplayName("Test proses nilai dengan data valid")
    void testProcessNilai_Valid() {
        String input = "10 20 10 20 20 20\n" +
                        "PA|100|85\nT|100|80\nK|100|75\nP|100|70\nUTS|100|65\nUAS|100|60\n---";
        String encoded = java.util.Base64.getEncoder().encodeToString(input.getBytes());
        
        ResponseEntity<?> response = controller.getNilai(encoded);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        
        String body = (String) response.getBody();
        assertTrue(body.contains("Perolehan Nilai:"));
        assertTrue(body.contains("Nilai Akhir:"));
        assertTrue(body.contains("Grade:"));
    }

    // --- PENAMBAHAN UNTUK 100% COVERAGE processNilai ---
    @Test
    @DisplayName("Test proses nilai dengan total maksimal 0 untuk satu komponen (cabang maks == 0)")
    void testProcessNilai_MaxZero() {
        // Bobot: PA(10) T(20) K(10) P(20) UTS(20) UAS(20)
        // Data: PA(85/100), T(0/0) -> max score 0
        String input = "10 20 10 20 20 20\n" +
                        "PA|100|85\n" +
                        "T|0|0\n" + // Komponen T (Tugas) dengan Max 0
                        "K|100|75\n" +
                        "---";
        String encoded = java.util.Base64.getEncoder().encodeToString(input.getBytes());

        ResponseEntity<?> response = controller.getNilai(encoded);
        assertTrue(response.getStatusCode().is2xxSuccessful());

        String body = (String) response.getBody();
        // PA: 85% dari 10 = 8.5
        // T: 0% dari 20 = 0.0 (Memastikan cabang maks == 0 teruji)
        // K: 75% dari 10 = 7.5
        // Total Nilai Akhir = 8.5 + 0.0 + 7.5 = 16.0
        assertTrue(body.contains("T: 0/100 (0.00/20)")); 
        assertTrue(body.contains("Nilai Akhir: 16.00"));
    }

    @Test
    @DisplayName("Test proses nilai dengan baris kosong/tidak valid dalam data (continue/skip)")
    void testProcessNilai_SkipLines() {
        // Memastikan baris kosong dan baris dengan format tidak valid dilewati (continue)
        String input = "10 20 10 20 20 20\n" +
                        "PA|100|85\n" +
                        "\n" +  // Baris kosong
                        "Data Tidak Valid\n" + // Baris tidak valid (parts.length != 3)
                        "T|100|80\n" +
                        "---";
        String encoded = java.util.Base64.getEncoder().encodeToString(input.getBytes());

        ResponseEntity<?> response = controller.getNilai(encoded);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        
        String body = (String) response.getBody();
        // PA: 85% dari 10 = 8.5. T: 80% dari 20 = 16.0. Total = 24.5
        assertTrue(body.contains("Nilai Akhir: 24.50"));
    }

    @Test
    @DisplayName("Test proses nilai dengan Base64 invalid")
    void testProcessNilai_InvalidBase64() {
        ResponseEntity<?> response = controller.getNilai("Ini-bukan-base64-valid!!!");
        assertTrue(response.getStatusCode().is4xxClientError());
        String body = (String) response.getBody();
        assertTrue(body.contains("Data Base64 tidak valid"));
    }
    // ----------------------------------------------------------------------


    @Test
    @DisplayName("Test proses matriks dengan ukuran 3x3")
    void testProcessMatrix_3x3() {
        String input = "3\n1 2 3\n4 5 6\n7 8 9";
        String encoded = java.util.Base64.getEncoder().encodeToString(input.getBytes());
        
        ResponseEntity<?> response = controller.getPerbedaanL(encoded);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        
        String body = (String) response.getBody();
        // Nilai L = (1+4+7) + (8) = 20
        // Nilai Kebalikan L = (3+6+9) + (2) = 20
        // Nilai Tengah = 5
        // Perbedaan = |20 - 20| = 0
        // Dominan = Nilai Tengah = 5
        assertTrue(body.contains("Nilai L: 20"));
        assertTrue(body.contains("Nilai Kebalikan L: 20"));
        assertTrue(body.contains("Nilai Tengah: 5"));
        assertTrue(body.contains("Perbedaan: 0"));
        assertTrue(body.contains("Dominan: 5"));
    }

    // --- PENAMBAHAN UNTUK 100% COVERAGE processMatrix ---
    @Test
    @DisplayName("Test proses matriks dengan ukuran 1x1 (ukuran <= 2)")
    void testProcessMatrix_1x1() {
        String input = "1\n5";
        String encoded = java.util.Base64.getEncoder().encodeToString(input.getBytes());
        
        ResponseEntity<?> response = controller.getPerbedaanL(encoded);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        
        String body = (String) response.getBody();
        // total = 5. Semua 'Tidak Ada'. Dominan=5
        assertTrue(body.contains("Nilai L: Tidak Ada"));
        assertTrue(body.contains("Nilai Tengah: 5"));
        assertTrue(body.contains("Dominan: 5"));
    }

    @Test
    @DisplayName("Test proses matriks dengan ukuran 2x2 (ukuran <= 2)")
    void testProcessMatrix_2x2() {
        String input = "2\n1 2\n3 4";
        String encoded = java.util.Base64.getEncoder().encodeToString(input.getBytes());

        ResponseEntity<?> response = controller.getPerbedaanL(encoded);
        assertTrue(response.getStatusCode().is2xxSuccessful());

        String body = (String) response.getBody();
        // total = 1+2+3+4 = 10
        assertTrue(body.contains("Nilai Tengah: 10"));
        assertTrue(body.contains("Dominan: 10"));
    }

    @Test
    @DisplayName("Test proses matriks dengan ukuran 4x4 (ukuran genap, nilai tengah 4 elemen)")
    void testProcessMatrix_4x4() {
        // 1 2 3 4
        // 5 6 7 8
        // 9 10 11 12
        // 13 14 15 16
        String input = "4\n1 2 3 4\n5 6 7 8\n9 10 11 12\n13 14 15 16";
        String encoded = java.util.Base64.getEncoder().encodeToString(input.getBytes());

        ResponseEntity<?> response = controller.getPerbedaanL(encoded);
        assertTrue(response.getStatusCode().is2xxSuccessful());

        String body = (String) response.getBody();
        // Nilai L = (1+5+9+13) + (14+15) = 57
        // Nilai Kebalikan L = (4+8+12+16) + (2+3) = 45
        // Nilai Tengah (2x2 di tengah) = 6+7+10+11 = 34
        // Perbedaan = |57 - 45| = 12
        // Dominan = Max(57, 45) = 57
        assertTrue(body.contains("Nilai L: 57"));
        assertTrue(body.contains("Nilai Kebalikan L: 45"));
        assertTrue(body.contains("Nilai Tengah: 34"));
        assertTrue(body.contains("Perbedaan: 12"));
        assertTrue(body.contains("Dominan: 57"));
    }
    // ----------------------------------------------------------------------


    @Test
    @DisplayName("Test analisis frekuensi dengan data normal")
    void testProcessFrekuensi_Normal() {
        // Frekuensi: 1(1x), 2(2x), 3(3x), 4(4x)
        String input = "1 2 2 3 3 3 4 4 4 4";
        String encoded = java.util.Base64.getEncoder().encodeToString(input.getBytes());
        
        ResponseEntity<?> response = controller.getPalingTer(encoded);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        
        String body = (String) response.getBody();
        // Tertinggi: 4, Terendah: 1, Terbanyak: 4 (4x), Tersedikit: 1 (1x)
        // Jumlah Tertinggi: 4*4 = 16, Jumlah Terendah: 1*1 = 1
        assertTrue(body.contains("Tertinggi: 4"));
        assertTrue(body.contains("Terendah: 1"));
        assertTrue(body.contains("Terbanyak: 4 (4x)"));
        assertTrue(body.contains("Tersedikit: 1 (1x)"));
        assertTrue(body.contains("Jumlah Tertinggi: 4 * 4 = 16"));
        assertTrue(body.contains("Jumlah Terendah: 1 * 1 = 1"));
    }

    @Test
    @DisplayName("Test analisis frekuensi dengan data kosong")
    void testProcessFrekuensi_Empty() {
        String input = "";
        String encoded = java.util.Base64.getEncoder().encodeToString(input.getBytes());
        
        ResponseEntity<?> response = controller.getPalingTer(encoded);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        
        String body = (String) response.getBody();
        assertEquals("Tidak ada data angka", body);
    }

    // --- PENAMBAHAN UNTUK 100% COVERAGE findLeastFrequent ---
    @Test
    @DisplayName("Test findLeastFrequent - angka tersedikit teridentifikasi unik (cabang return current)")
    void testProcessFrekuensi_UniqueAndElimination() {
        // Data: [5, 1, 2, 1, 3]
        // Eliminasi: current=5, j tidak ditemukan. -> return 5 (unique)
        // Frekuensi: 5(1x), 1(2x), 2(1x), 3(1x)
        String input = "5 1 2 1 3";
        String encoded = java.util.Base64.getEncoder().encodeToString(input.getBytes());

        ResponseEntity<?> response = controller.getPalingTer(encoded);
        assertTrue(response.getStatusCode().is2xxSuccessful());

        String body = (String) response.getBody();
        // Tertinggi=5, Terendah=1, Terbanyak=1(2x), Tersedikit=5(1x)
        assertTrue(body.contains("Terbanyak: 1 (2x)"));
        assertTrue(body.contains("Tersedikit: 5 (1x)")); 
    }

    @Test
    @DisplayName("Test findLeastFrequent - semua tereliminasi (cabang Collections.min)")
    void testProcessFrekuensi_AllEliminated() {
        // Data: [1, 2, 3, 4, 1, 2, 3, 4]
        // Eliminasi: (1 tereliminasi bersama 2), (3 tereliminasi bersama 4). Semua eliminated.
        // Masuk ke Collections.min -> mengembalikan key terkecil, yaitu 1
        // Frekuensi: 1(2x), 2(2x), 3(2x), 4(2x)
        String input = "1 2 3 4 1 2 3 4";
        String encoded = java.util.Base64.getEncoder().encodeToString(input.getBytes());

        ResponseEntity<?> response = controller.getPalingTer(encoded);
        assertTrue(response.getStatusCode().is2xxSuccessful());

        String body = (String) response.getBody();
        // Terbanyak: 4 (2x) -> Terbanyak mengambil yang terbesar jika frekuensi sama
        // Tersedikit: 1 (2x) -> Collections.min mengambil key terkecil (1)
        assertTrue(body.contains("Terbanyak: 4 (2x)"));
        assertTrue(body.contains("Tersedikit: 1 (2x)")); 
    }
    // ----------------------------------------------------------------------


    @Test
    @DisplayName("Test decode Base64 dengan data invalid")
    void testDecodeBase64_Invalid() {
        String invalidBase64 = "Ini-bukan-base64-valid!!!";
        
        // Memastikan semua endpoint yang menggunakan decodeBase64 teruji errornya
        ResponseEntity<?> response1 = controller.getPalingTer(invalidBase64);
        assertTrue(response1.getStatusCode().is4xxClientError());

        ResponseEntity<?> response2 = controller.getNilai(invalidBase64);
        assertTrue(response2.getStatusCode().is4xxClientError());

        ResponseEntity<?> response3 = controller.getPerbedaanL(invalidBase64);
        assertTrue(response3.getStatusCode().is4xxClientError());
    }

    @Test
    @DisplayName("Test semua endpoint tidak throw exception (Positive Flow Check)")
    void testAllEndpoints_NoException() {
        assertDoesNotThrow(() -> {
            controller.home();
            controller.hello("Test");
            controller.getInfoNIM("11S23001");
            
            String encodedNilai = java.util.Base64.getEncoder().encodeToString("1 1 1 1 1 1\nPA|1|1\n---".getBytes());
            controller.getNilai(encodedNilai);
            
            String encodedMatrix = java.util.Base64.getEncoder().encodeToString("3\n1 1 1\n1 1 1\n1 1 1".getBytes());
            controller.getPerbedaanL(encodedMatrix);

            String encodedFrekuensi = java.util.Base64.getEncoder().encodeToString("1 2 3".getBytes());
            controller.getPalingTer(encodedFrekuensi);
        });
    }

    @Test
    @DisplayName("Test format output hello endpoint")
    void testHelloOutputFormat() {
        String result = controller.hello("Alice");
        assertEquals("Hello, Alice!", result);
    }

    @Test
    @DisplayName("Test konsistensi home endpoint")
    void testHomeEndpointConsistency() {
        String firstCall = controller.home();
        String secondCall = controller.home();
        
        assertEquals(firstCall, secondCall);
    }
}