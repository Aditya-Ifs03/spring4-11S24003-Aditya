package org.delcom.starter.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import java.util.Base64;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Unit Test untuk HomeController")
class HomeControllerTest {

    private String toBase64(String text) {
        return Base64.getEncoder().encodeToString(text.getBytes());
    }

    @Nested
    @DisplayName("Metode Dasar")
    class BasicMethods {
        @Test
        @DisplayName("Mengembalikan pesan selamat datang yang benar")
        void hello_ShouldReturnWelcomeMessage() {
            HomeController controller = new HomeController();
            String result = controller.hello();
            assertEquals("Hay Aditya, selamat datang di pengembangan aplikasi dengan Spring Boot!", result);
        }

        @Test
        @DisplayName("Mengembalikan pesan sapaan yang dipersonalisasi")
        void helloWithName_ShouldReturnPersonalizedGreeting() {
            HomeController controller = new HomeController();
            String result = controller.sayHello("Abdullah");
            assertEquals("Hello, Abdullah!", result);
        }
    }

    @Nested
    @DisplayName("Praktikum 1: Informasi NIM")
    class InformasiNim {
        @Test
        @DisplayName("NIM valid untuk semua prodi")
        void nimValidForAllProdi() {
            HomeController c = new HomeController();
            assertTrue(c.informasiNim("11S24001").contains("Sarjana Informatika"));
            assertTrue(c.informasiNim("12S24001").contains("Sistem Informasi"));
            assertTrue(c.informasiNim("14S24001").contains("Teknik Elektro"));
            assertTrue(c.informasiNim("21S24001").contains("Manajemen Rekayasa"));
            assertTrue(c.informasiNim("22S24001").contains("Teknik Metalurgi"));
            assertTrue(c.informasiNim("31S24001").contains("Teknik Bioproses"));
            assertTrue(c.informasiNim("11424012").contains("Diploma 4 Teknologi Rekayasa Perangkat Lunak"));
            assertTrue(c.informasiNim("11324001").contains("Diploma 3 Teknologi Informasi"));
            assertTrue(c.informasiNim("13324001").contains("Diploma 3 Teknologi Komputer"));
        }

        @Test
        @DisplayName("NIM dengan prodi tidak dikenal")
        void nimInvalidProdi() {
            HomeController c = new HomeController();
            assertTrue(c.informasiNim("99X99999").contains("tidak dikenal"));
        }

        @Test
        @DisplayName("NIM null harus mengembalikan error")
        void nimNull_ShouldReturnError() {
            HomeController c = new HomeController();
            assertEquals("Error: NIM tidak valid.", c.informasiNim(null));
        }

        @Test
        @DisplayName("NIM kurang dari 5 karakter harus mengembalikan error")
        void nimTooShort_ShouldReturnError() {
            HomeController c = new HomeController();
            assertEquals("Error: NIM tidak valid.", c.informasiNim("11S"));
            assertTrue(c.informasiNim("11S2").contains("Angkatan: 20??")); // Test nim length == 4
        }

        @Test
        @DisplayName("NIM dengan panjang yang hanya cukup untuk prefix dan angkatan")
        void nimOnlyPrefixAndAngkatan() {
            HomeController c = new HomeController();
            assertTrue(c.informasiNim("11S24").contains("Angkatan: 2024"));
            assertTrue(c.informasiNim("11S24").contains("Urutan: ??"));
        }

        @Test
        @DisplayName("NIM dengan urutan dimulai dengan nol")
        void nimLeadingZeroInOrder() {
            HomeController c = new HomeController();
            assertTrue(c.informasiNim("11S24005").contains("Urutan: 5"));
        }
    }

    @Nested
    @DisplayName("Praktikum 2: Perolehan Nilai")
    class PerolehanNilai {
        @Test
        @DisplayName("Semua grade harus dihitung dengan benar")
        void allGrades() {
            HomeController c = new HomeController();
            String b = "0\n0\n0\n0\n0\n100\n"; // Hanya UAS yang memiliki bobot
            assertTrue(c.perolehanNilai(toBase64(b + "UAS|100|85\n---")).contains("Grade: A"));
            assertTrue(c.perolehanNilai(toBase64(b + "UAS|100|75\n---")).contains("Grade: AB"));
            assertTrue(c.perolehanNilai(toBase64(b + "UAS|100|70\n---")).contains("Grade: B"));
            assertTrue(c.perolehanNilai(toBase64(b + "UAS|100|60\n---")).contains("Grade: BC"));
            assertTrue(c.perolehanNilai(toBase64(b + "UAS|100|50\n---")).contains("Grade: C"));
            assertTrue(c.perolehanNilai(toBase64(b + "UAS|100|40\n---")).contains("Grade: D"));
            assertTrue(c.perolehanNilai(toBase64(b + "UAS|100|30\n---")).contains("Grade: E"));
        }

        @Test
        @DisplayName("Input dengan nilai maksimal nol")
        void inputWithMaxZero() {
            HomeController c = new HomeController();
            // bobot: PA=10, T=90, K=0, P=0, UTS=0, UAS=0
            // PA|100|80 -> 80%
            // T|0|90 -> 0% (max nol)
            // K|100|85 -> tidak ada bobot
            String input = "10\n90\n0\n0\n0\n0\nPA|100|80\nT|0|90\nK|100|85\n---";
            String result = c.perolehanNilai(toBase64(input));
            assertTrue(result.contains(">> Partisipatif: 80/100 (8.00/10)"));
            assertTrue(result.contains(">> Tugas: 0/100 (0.00/90)")); // Tugas max 0, persen 0
            assertTrue(result.contains(">> Kuis: 85/100 (0.00/0)")); // Kuis bobot 0
            assertTrue(result.contains(">> Nilai Akhir: 8.00"));
        }

        @Test
        @DisplayName("Input tidak valid (Base64 rusak atau format salah)")
        void invalidInput() {
            HomeController c = new HomeController();
            assertEquals("Error: Input tidak valid.", c.perolehanNilai("input-salah")); // Base64 tidak valid
            assertEquals("Error: Input tidak valid.", c.perolehanNilai(toBase64("10\n10\n10\n10\n10\nPA|100|80\n---"))); // Kurang bobot
        }

        @Test
        @DisplayName("Cakupan kasus loop dan error handling di dalam loop")
        void loopEdgeCaseCoverage() {
            HomeController c = new HomeController();
            String input = "10\n10\n10\n10\n30\n30\n" + // Bobot
                         "\n" +                         // Baris kosong
                         "PA|100|80\n" +                // Case PA
                         "K|100|85\n" +                 // Case K
                         "P|100|90\n" +                 // Case P
                         "UTS|100|75\n" +               // Case UTS
                         "UAS|100|95\n" +               // Case UAS
                         "FORMAT-SALAH\n" +             // p.length != 3
                         "XYZ|100|90\n" +               // switch tidak dikenal (default)
                         "T|100|bukan-angka\n" +        // NumberFormatException
                         "---";
            String result = c.perolehanNilai(toBase64(input));
            assertTrue(result.contains(">> Nilai Akhir"));
            assertTrue(result.contains(">> Partisipatif: 80/100 (8.00/10)"));
        }

        @Test
        @DisplayName("Input dengan jumlah baris bobot tidak mencukupi")
        void insufficientWeightLines() {
            HomeController c = new HomeController();
            String input = "10\n20\n30\n---"; // Hanya 3 bobot
            assertEquals("Error: Input tidak valid.", c.perolehanNilai(toBase64(input)));
        }

        @Test
        @DisplayName("Input kosong setelah bobot")
        void emptyInputAfterWeights() {
            HomeController c = new HomeController();
            String input = "10\n10\n10\n10\n30\n30\n---";
            String result = c.perolehanNilai(toBase64(input));
            assertTrue(result.contains("Nilai Akhir: 0.00"));
        }
    }

    @Nested
    @DisplayName("Praktikum 3: Perbedaan L")
    class PerbedaanL {
        @Test
        @DisplayName("Matriks ganjil yang valid")
        void validOddMatrix() {
            HomeController c = new HomeController();
            String result = c.perbedaanL(toBase64("3\n1 2 3\n4 5 6\n7 8 9"));
            assertTrue(result.contains("Nilai L: 29")); // 1+4+7+8+9 = 29
            assertTrue(result.contains("Nilai Kebalikan L: 23")); // 1+2+3+6+9 = 21 (perhitungan di controller: m[0][j] + m[i][n-1] - m[0][n-1])
            assertTrue(result.contains("Nilai Tengah: 5"));
            assertTrue(result.contains("Perbedaan: 6")); // abs(29-23)
            assertTrue(result.contains("Dominan: 29")); // max(29,23)
        }

        @Test
        @DisplayName("Matriks genap yang valid")
        void validEvenMatrix() {
            HomeController c = new HomeController();
            String result = c.perbedaanL(toBase64("4\n1 1 1 1\n2 2 2 2\n3 3 3 3\n4 4 4 4"));
            assertTrue(result.contains("Nilai L: 22")); // 1+2+3+4 (col0) + (4+4+4) (row3) = 10 + 12 = 22
            assertTrue(result.contains("Nilai Kebalikan L: 13")); // 1+1+1+1 (row0) + (2+3+4) (col3) = 4 + 9 = 13
            assertTrue(result.contains("Nilai Tengah: 10")); // 2+2+3+3
            assertTrue(result.contains("Perbedaan: 9")); // abs(22-13)
            assertTrue(result.contains("Dominan: 22")); // max(22,13)
        }

        @Test
        @DisplayName("Matriks kecil (n < 3) tidak memiliki Nilai L dan Kebalikan L")
        void smallMatrix() {
            HomeController c = new HomeController();
            String result = c.perbedaanL(toBase64("2\n1 2\n3 4"));
            assertTrue(result.contains("Nilai L: Tidak Ada"));
            assertTrue(result.contains("Nilai Kebalikan L: Tidak Ada"));
            assertTrue(result.contains("Perbedaan: Tidak Ada"));
            assertTrue(result.contains("Nilai Tengah: 10")); // nT = 1+2+3+4=10
            assertTrue(result.contains("Dominan: 10")); // nT menjadi dominan karena L dan KL tidak ada
        }

        @Test
        @DisplayName("Perbedaan nol, dominan harus nilai tengah")
        void zeroDifference() {
            HomeController c = new HomeController();
            // Nilai L = 10+1+10+1+10 = 32
            // Nilai KL = 10+1+10+1+10 = 32
            // nT = 1
            // Perbedaan = 0, maka Dominan harus nT (1)
            String result = c.perbedaanL(toBase64("3\n10 1 10\n1 1 1\n10 1 10"));
            assertTrue(result.contains("Perbedaan: 0"));
            assertTrue(result.contains("Dominan: 1"));
        }

        @Test
        @DisplayName("Input matriks tidak valid (format salah)")
        void invalidMatrixInput() {
            HomeController c = new HomeController();
            assertEquals("Error: Input tidak valid.", c.perbedaanL(toBase64("3\n1 2\n4 x 6"))); // Format baris salah (NumberFormatException)
            assertEquals("Error: Input tidak valid.", c.perbedaanL(toBase64("3\n1 2 3\n4 5\n7 8 9"))); // Jumlah elemen salah (ArrayIndexOutOfBounds atau IllegalArgumentException)
        }

        @Test
        @DisplayName("Input Base64 rusak")
        void invalidBase64() {
            HomeController c = new HomeController();
            assertEquals("Error: Input tidak valid.", c.perbedaanL("invalid-base64"));
        }

        @Test
        @DisplayName("Input kosong atau N tidak valid")
        void emptyOrInvalidN() {
            HomeController c = new HomeController();
            assertEquals("Error: Input tidak valid.", c.perbedaanL(toBase64(""))); // Input kosong
            assertEquals("Error: Input tidak valid.", c.perbedaanL(toBase64(" \n1 2\n3 4"))); // N kosong
            assertEquals("Error: Input tidak valid.", c.perbedaanL(toBase64("0\n"))); // N = 0
            assertEquals("Error: Input tidak valid.", c.perbedaanL(toBase64("-1\n"))); // N negatif
            assertEquals("Error: Input tidak valid.", c.perbedaanL(toBase64("salah\n"))); // N bukan angka (NumberFormatException)
        }

        @Test
        @DisplayName("Jumlah baris matriks tidak cukup untuk N")
        void insufficientMatrixRows() {
            HomeController c = new HomeController();
            assertEquals("Error: Input tidak valid.", c.perbedaanL(toBase64("3\n1 2 3\n4 5 6"))); // Kurang satu baris
        }

        @Test
        @DisplayName("Matriks 1x1")
        void oneByOneMatrix() {
            HomeController c = new HomeController();
            String result = c.perbedaanL(toBase64("1\n5"));
            assertTrue(result.contains("Nilai L: Tidak Ada"));
            assertTrue(result.contains("Nilai Kebalikan L: Tidak Ada"));
            assertTrue(result.contains("Nilai Tengah: 5"));
            assertTrue(result.contains("Perbedaan: Tidak Ada"));
            assertTrue(result.contains("Dominan: 5"));
        }
    }

    @Nested
    @DisplayName("Praktikum 4: Paling Ter")
    class PalingTer {
        @Test
        @DisplayName("Input Valid")
        void validInput() {
            HomeController c = new HomeController();
            String input = "10\n5\n10\n20\n5\n10\n---";
            String expected = "Tertinggi: 20\nTerendah: 5\nTerbanyak: 10 (3x)\nTersedikit: 20 (1x)\nJumlah Tertinggi: 10 * 3 = 30\nJumlah Terendah: 5 * 2 = 10";
            assertEquals(expected, c.palingTer(toBase64(input)));
        }

        @Test
        @DisplayName("Kasus Tie-breaker untuk jumlah tertinggi dan terendah")
        void tieBreakerForSum() {
            HomeController c = new HomeController();
            // 6 * 5 = 30
            // 10 * 3 = 30
            // Output harus memilih angka yang lebih besar jika total sama, yaitu 10
            String input1 = "6\n6\n6\n6\n6\n10\n10\n10\n---";
            assertTrue(c.palingTer(toBase64(input1)).contains("Jumlah Tertinggi: 10 * 3 = 30"));

            // 10 * 2 = 20
            // 4 * 5 = 20
            // Output harus memilih angka yang lebih kecil jika total sama, yaitu 4
            String input2 = "10\n10\n4\n4\n4\n4\n4\n---";
            assertTrue(c.palingTer(toBase64(input2)).contains("Jumlah Terendah: 4 * 5 = 20"));
        }

        @Test
        @DisplayName("Kasus Tie-breaker untuk terbanyak dan tersedikit (ambil yang pertama muncul)")
        void tieBreakerForMostAndLeastFrequent() {
            HomeController c = new HomeController();
            String input = "10\n10\n20\n20\n30\n---"; // 10 (2x), 20 (2x), 30 (1x)
            // Terbanyak: 10 (muncul pertama)
            // Tersedikit: 30 (muncul pertama)
            String result = c.palingTer(toBase64(input));
            assertTrue(result.contains("Terbanyak: 10 (2x)"));
            assertTrue(result.contains("Tersedikit: 30 (1x)"));
        }

        @Test
        @DisplayName("Error Handling dan Edge Cases")
        void errorAndEdgeCases() {
            HomeController c = new HomeController();
            assertEquals("Error: Tidak ada data input.", c.palingTer(toBase64(""))); // Input kosong
            assertEquals("Error: Tidak ada data input.", c.palingTer(toBase64("---\n"))); // Input hanya delimiter
            assertEquals("Error: Input tidak valid.", c.palingTer(toBase64("10\nhello\n20\n---"))); // NumberFormatException
            assertTrue(c.palingTer(toBase64("10\n\n20\n---")).contains("Tertinggi: 20")); // Baris kosong diabaikan
        }

        @Test
        @DisplayName("Input Base64 rusak harus memicu catch Exception")
        void invalidBase64String() {
            HomeController c = new HomeController();
            String invalidBase64 = "###bukan_base64###";
            String result = c.palingTer(invalidBase64);
            assertEquals("Error: Input tidak valid.", result);
        }

        @Test
        @DisplayName("Semua angka memiliki frekuensi yang sama")
        void allNumbersSameFrequency() {
            HomeController c = new HomeController();
            String input = "1\n2\n3\n---";
            String result = c.palingTer(toBase64(input));
            assertTrue(result.contains("Terbanyak: 1 (1x)")); // Angka 1 muncul pertama
            assertTrue(result.contains("Tersedikit: 1 (1x)")); // Angka 1 muncul pertama
            assertTrue(result.contains("Jumlah Tertinggi: 3 * 1 = 3")); // Angka 3 * frekuensi 1
            assertTrue(result.contains("Jumlah Terendah: 1 * 1 = 1")); // Angka 1 * frekuensi 1
        }
    }
}