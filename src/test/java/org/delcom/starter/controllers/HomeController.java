package org.delcom.starter.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import org.junit.jupiter.api.DisplayName;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HomeControllerUnitTest {

    private HomeController controller;

    // Fungsi bantu untuk encode string ke Base64
    private String encode(String text) {
        return Base64.getEncoder().encodeToString(text.getBytes());
    }

    // @BeforeEach memastikan controller baru dibuat sebelum SETIAP tes
    @BeforeEach
    void setUp() {
        controller = new HomeController();
    }

    // == Tes Asli dari Test 1 (disesuaikan dengan @BeforeEach) ==
    @Test
    @DisplayName("Mengembalikan pesan selamat datang yang benar")
    void hello_ShouldReturnWelcomeMessage() throws Exception {
        // Act
        String result = controller.hello();
        // Assert
        assertEquals("Hay Abdullah, selamat datang di pengembangan aplikasi dengan Spring Boot!", result);
    }

    @Test
    @DisplayName("Mengembalikan pesan sapaan yang dipersonalisasi")
    void helloWithName_ShouldReturnPersonalizedGreeting() throws Exception {
        // Act
        String result = controller.sayHello("Abdullah");
        // Assert
        assertEquals("Hello, Abdullah!", result);
    }

    @Test
    @DisplayName("Menguji semua kemungkinan NIM valid dan tidak valid")
    void informasiNIM_semua_kemungkinan_nim_valid_dan_tidak_valid() throws Exception {
        // Test NIM Tidak Valid
        assertEquals("NIM harus 8 karakter", controller.informasiNim("11S180"));
        
        // Test NIM Valid dengan Prodi Tidak Tersedia
        assertEquals("Program Studi tidak Tersedia", controller.informasiNim("ZZS18005"));

        // Test Sarjana Informatika
        assertEquals("Inforamsi NIM 11S18005: >> Program Studi: Sarjana Informatika>> Angkatan: 2018>> Urutan: 5", controller.informasiNim("11S18005"));
        
        // Test Sarjana Sistem Informasi
        assertEquals("Inforamsi NIM 12S18005: >> Program Studi: Sarjana Sistem Informasi>> Angkatan: 2018>> Urutan: 5", controller.informasiNim("12S18005"));
        
        // Test Sarjana Teknik Elektro
        assertEquals("Inforamsi NIM 14S18005: >> Program Studi: Sarjana Teknik Elektro>> Angkatan: 2018>> Urutan: 5", controller.informasiNim("14S18005"));

        // Test Sarjana Manajemen Rekayasa
        assertEquals("Inforamsi NIM 21S18005: >> Program Studi: Sarjana Manajemen Rekayasa>> Angkatan: 2018>> Urutan: 5", controller.informasiNim("21S18005"));
        
        // Test Sarjana Teknik Metalurgi
        assertEquals("Inforamsi NIM 22S18005: >> Program Studi: Sarjana Teknik Metalurgi>> Angkatan: 2018>> Urutan: 5", controller.informasiNim("22S18005"));
        
        // Test Sarjana Teknik Bioproses
        assertEquals("Inforamsi NIM 31S18005: >> Program Studi: Sarjana Teknik Bioproses>> Angkatan: 2018>> Urutan: 5", controller.informasiNim("31S18005"));

        // Test D4 TRPL
        assertEquals("Inforamsi NIM 11418005: >> Program Studi: Diploma 4 Teknologi Rekasaya Perangkat Lunak>> Angkatan: 2018>> Urutan: 5", controller.informasiNim("11418005"));
        
        // Test D3 TI
        assertEquals("Inforamsi NIM 11318005: >> Program Studi: Diploma 3 Teknologi Informasi>> Angkatan: 2018>> Urutan: 5", controller.informasiNim("11318005"));
        
        // Test D3 TK
        assertEquals("Inforamsi NIM 13318005: >> Program Studi: Diploma 3 Teknologi Komputer>> Angkatan: 2018>> Urutan: 5", controller.informasiNim("13318005"));
    }

    @Test
    @DisplayName("Mendekode base64 dan mengembalikan nilai dengan benar")
    void perolehanNilai_ShouldDecodeBase64Correctly() {
        // Arrange
        String nilaiAsli = "A=90 B=80 C=70";
        String base64 = Base64.getEncoder().encodeToString(nilaiAsli.getBytes(StandardCharsets.UTF_8));
        // Act
        String result = controller.perolehanNilai(base64);
        // Assert
        assertEquals("Perolehan Nilai: " + nilaiAsli, result);
    }
    
    // == Blok Tes untuk 'perbedaanL' ==
    @Test
    @DisplayName("perbedaanL - Input valid")
    void testPerbedaanL_ValidInput() {
        String input = "20|20|5";
        String base64Input = encode(input);
        String result = controller.perbedaanL(base64Input);
        
        String expected = "Nilai L: 20: Nilai Kebalikan L: 20: Nilai Tengah: 5 Perbedaan: 0 Dominan: 5";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("perbedaanL - Format input salah (bukan 3 bagian)")
    void testPerbedaanL_InvalidFormat() {
        String input = "20|20"; // Hanya 2 bagian
        String base64Input = encode(input);
        String result = controller.perbedaanL(base64Input);
        assertEquals("Error: Format input tidak valid. Harusnya 'angka1|angka2|angka3'.", result);
    }

    @Test
    @DisplayName("perbedaanL - Input bukan angka (NumberFormatException)")
    void testPerbedaanL_NotANumber() {
        String input = "20|abc|5"; // 'abc' bukan angka
        String base64Input = encode(input);
        String result = controller.perbedaanL(base64Input);
        assertEquals("Error: Input bukan angka yang valid.", result);
    }

    @Test
    @DisplayName("perbedaanL - Base64 tidak valid (IllegalArgumentException)")
    void testPerbedaanL_InvalidBase64() {
        String invalidBase64 = "MTIz*"; // Base64 rusak
        String result = controller.perbedaanL(invalidBase64);
        assertEquals("Error: Format Base64 tidak valid.", result);
    }
    
    // == Blok Tes untuk 'palingTer' ==
    @Test
    @DisplayName("Paling Ter - Input valid")
    void testPalingTer_ValidInput() {
        String input = "10\n5\n9\n10\n5\n10";
        String base64Input = encode(input);
        String result = controller.palingTer(base64Input);
        String expected = "Tertinggi: 10 Terendah: 5 Terbanyak: 10 (3x) Tersedikit: 9 (1x) Jumlah Tertinggi: 10 * 3 = 30 Jumlah Terendah: 5 * 2 = 10";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Paling Ter - Tidak ada angka valid")
    void testPalingTer_NoValidNumbers() {
        String input = "abc\ndef\nghi";
        String base64Input = encode(input);
        String result = controller.palingTer(base64Input);
        assertEquals("Tidak ada angka yang valid ditemukan.", result);
    }

    @Test
    @DisplayName("Paling Ter - Base64 tidak valid")
    void testPalingTer_InvalidBase64() {
        String invalidBase64 = "MTIz*"; // Base64 rusak
        String result = controller.palingTer(invalidBase64);
        assertEquals("Error: Format Base64 tidak valid.", result);
    }
    
    @Test
    @DisplayName("Paling Ter - Input dengan frekuensi yang sama")
    void testPalingTer_SameFrequency() {
        String input = "10\n5\n10\n5";
        String base64Input = encode(input);
        String result = controller.palingTer(base64Input);
        
        String expected = "Tertinggi: 10 Terendah: 5 Terbanyak: 10 (2x) Tersedikit: 10 (2x) Jumlah Tertinggi: 10 * 2 = 20 Jumlah Terendah: 5 * 2 = 10";
        assertEquals(expected, result);
    }
}