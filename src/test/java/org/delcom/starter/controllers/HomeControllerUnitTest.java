package org.delcom.starter.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import java.util.Base64;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Unit Test untuk HomeController")
class HomeControllerUnitTest {

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
            assertEquals("Hay Abdullah, selamat datang di pengembangan aplikasi dengan Spring Boot!", result);
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
        void nimValid() {
            HomeController c = new HomeController();
            assertTrue(c.informasiNim("11S24001").contains("Sarjana Informatika"));
        }

        @Test
        void otherProdi() {
            HomeController c = new HomeController();
            assertTrue(c.informasiNim("12S24001").contains("Sistem Informasi"));
            assertTrue(c.informasiNim("14S24001").contains("Teknik Elektro"));
            assertTrue(c.informasiNim("21S24001").contains("Manajemen Rekayasa"));
            assertTrue(c.informasiNim("22S24001").contains("Teknik Metalurgi"));
            assertTrue(c.informasiNim("31S24001").contains("Teknik Bioproses"));
            assertTrue(c.informasiNim("11424012").contains("Rekayasa Perangkat Lunak"));
            assertTrue(c.informasiNim("11324001").contains("Teknologi Informasi"));
            assertTrue(c.informasiNim("13324001").contains("Teknologi Komputer"));
        }

        @Test
        void nimInvalid() {
            HomeController c = new HomeController();
            assertTrue(c.informasiNim("99X99999").contains("tidak dikenal"));
        }
    }

    @Nested
    @DisplayName("Praktikum 2: Perolehan Nilai")
    class PerolehanNilai {
        @Test
        void allGrades() {
            HomeController c = new HomeController();
            String b = "0\n0\n0\n0\n0\n100\n";
            assertTrue(c.perolehanNilai(toBase64(b + "UAS|100|85\n---")).contains("A"));
            assertTrue(c.perolehanNilai(toBase64(b + "UAS|100|75\n---")).contains("AB"));
            assertTrue(c.perolehanNilai(toBase64(b + "UAS|100|70\n---")).contains("B"));
            assertTrue(c.perolehanNilai(toBase64(b + "UAS|100|60\n---")).contains("BC"));
            assertTrue(c.perolehanNilai(toBase64(b + "UAS|100|50\n---")).contains("C"));
            assertTrue(c.perolehanNilai(toBase64(b + "UAS|100|40\n---")).contains("D"));
            assertTrue(c.perolehanNilai(toBase64(b + "UAS|100|30\n---")).contains("E"));
        }

        @Test
        void inputWithMaxZero() {
            HomeController c = new HomeController();
            assertTrue(
                    c.perolehanNilai(toBase64("10\n90\n0\n0\n0\n0\nPA|100|80\nT|0|90\n---")).contains("Tugas: 0/100"));
        }

        @Test
        void invalidInput() {
            HomeController c = new HomeController();
            assertEquals("Error: Input tidak valid.", c.perolehanNilai("input-salah"));
        }

        @Test
        void loopEdgeCaseCoverage() {
            HomeController c = new HomeController();
            String input = "10\n10\n10\n10\n30\n30\n" + // Bobot
                         "\n" +                         // Baris kosong
                         "PA|100|80\n" +                // Mencakup case PA
                         "K|100|85\n" +                 // Mencakup case K (sebelumnya merah)
                         "P|100|90\n" +                 // Mencakup case P (sebelumnya merah)
                         "UTS|100|75\n" +               // Mencakup case UTS (sebelumnya merah)
                         "UAS|100|95\n" +               // Mencakup case UAS
                         "FORMAT-SALAH\n" +             // Mencakup if (p.length != 3)
                         "XYZ|100|90\n" +               // Mencakup default di switch
                         "T|100|bukan-angka\n" +        // Mencakup try-catch dan case T
                         "---";
            String result = c.perolehanNilai(toBase64(input));
            assertTrue(result.contains(">> Nilai Akhir: 76.50"));
        }
    }

    @Nested
    @DisplayName("Praktikum 3: Perbedaan L")
    class PerbedaanL {
        @Test
        void validOddMatrix() {
            HomeController c = new HomeController();
            assertTrue(c.perbedaanL(toBase64("3\n1 2 3\n4 5 6\n7 8 9")).contains("Nilai L: 29"));
        }

        @Test
        void validEvenMatrix() {
            HomeController c = new HomeController();
            assertTrue(c.perbedaanL(toBase64("4\n1 1 1 1\n2 2 2 2\n3 3 3 3\n4 4 4 4")).contains("Nilai Tengah: 10"));
        }

        @Test
        void smallMatrix() {
            HomeController c = new HomeController();
            assertTrue(c.perbedaanL(toBase64("2\n1 2\n3 4")).contains("Nilai L: Tidak Ada"));
        }

        @Test
        void zeroDifference() {
            HomeController c = new HomeController();
            assertTrue(c.perbedaanL(toBase64("3\n10 1 10\n1 1 1\n10 1 10")).contains("Dominan: 1"));
        }

        @Test
        void invalidMatrixInput() {
            HomeController c = new HomeController();
            assertEquals("Error: Input tidak valid.", c.perbedaanL(toBase64("3\n1 2\n4 x 6")));
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
        @DisplayName("Kasus Tie-breaker")
        void tieBreaker() {
            HomeController c = new HomeController();
            String input1 = "6\n6\n6\n6\n6\n10\n10\n10\n---";
            assertTrue(c.palingTer(toBase64(input1)).contains("Jumlah Tertinggi: 10 * 3 = 30"));
            String input2 = "10\n10\n4\n4\n4\n4\n4\n---";
            assertTrue(c.palingTer(toBase64(input2)).contains("Jumlah Terendah: 4 * 5 = 20"));
        }

        @Test
        @DisplayName("Error Handling dan Edge Cases")
        void errorAndEdgeCases() {
            HomeController c = new HomeController();
            assertEquals("Error: Tidak ada data input.", c.palingTer(toBase64("")));
            assertEquals("Error: Tidak ada data input.", c.palingTer(toBase64("---\n")));
            assertEquals("Error: Input tidak valid.", c.palingTer(toBase64("10\nhello\n20\n---")));
            assertTrue(c.palingTer(toBase64("10\n\n20\n---")).contains("Tertinggi: 20"));
        }
    }
}