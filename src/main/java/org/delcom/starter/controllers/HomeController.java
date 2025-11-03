package org.delcom.starter.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
public class HomeController {

    // Constants for grade thresholds
    private static final double NILAI_MINIMAL_A = 79.5;
    private static final double NILAI_MINIMAL_AB = 72.0;
    private static final double NILAI_MINIMAL_B = 64.5;
    private static final double NILAI_MINIMAL_BC = 57.0;
    private static final double NILAI_MINIMAL_C = 49.5;
    private static final double NILAI_MINIMAL_D = 34.0;

    // Study program data
    private static final Map<String, String> MAP_PRODI = createProdiMap();

    private static Map<String, String> createProdiMap() {
        Map<String, String> map = new HashMap<>();
        map.put("11S", "Sarjana Informatika");
        map.put("12S", "Sarjana Sistem Informasi");
        map.put("14S", "Sarjana Teknik Elektro");
        map.put("21S", "Sarjana Manajemen Rekayasa");
        map.put("22S", "Sarjana Teknik Metalurgi");
        map.put("31S", "Sarjana Teknik Bioproses");
        map.put("114", "Diploma 4 Teknologi Rekasaya Perangkat Lunak");
        map.put("113", "Diploma 3 Teknologi Informasi");
        map.put("133", "Diploma 3 Teknologi Komputer");
        return Collections.unmodifiableMap(map);
    }

    // Basic endpoints
    @GetMapping("/")
    public String home() {
        return "Hay Abdullah, selamat datang di pengembangan aplikasi dengan Spring Boot!";
    }

    @GetMapping("/hello/{nama}")
    public String hello(@PathVariable String nama) {
        return "Hello, " + nama + "!";
    }

    // Case study endpoints
    @GetMapping("/informasiNim/{nim}")
    public ResponseEntity<?> getInfoNIM(@PathVariable String nim) {
        try {
            String result = processNIM(nim);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/perolehanNilai/{encodedData}")
    public ResponseEntity<?> getNilai(@PathVariable String encodedData) {
        try {
            String decoded = decodeBase64(encodedData);
            String result = processNilai(decoded);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/perbedaanL/{encodedData}")
    public ResponseEntity<?> getPerbedaanL(@PathVariable String encodedData) {
        try {
            String decoded = decodeBase64(encodedData);
            String result = processMatrix(decoded);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/palingTer/{encodedData}")
    public ResponseEntity<?> getPalingTer(@PathVariable String encodedData) {
        try {
            String decoded = decodeBase64(encodedData);
            String result = processFrekuensi(decoded);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // Utility methods
    private String decodeBase64(String data) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(data);
            return new String(decodedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalArgumentException("Data Base64 tidak valid");
        }
    }

    private String convertToGrade(double score) {
        if (score >= NILAI_MINIMAL_A) return "A";
        if (score >= NILAI_MINIMAL_AB) return "AB";
        if (score >= NILAI_MINIMAL_B) return "B";
        if (score >= NILAI_MINIMAL_BC) return "BC";
        if (score >= NILAI_MINIMAL_C) return "C";
        if (score >= NILAI_MINIMAL_D) return "D";
        return "E";
    }

    // Business logic methods
    private String processNIM(String nim) {
        if (nim == null || nim.length() != 8) {
            throw new IllegalArgumentException("NIM harus 8 digit");
        }

        String kodeProdi = nim.substring(0, 3);
        String tahun = nim.substring(3, 5);
        String nomor = nim.substring(5);

        String namaProdi = MAP_PRODI.get(kodeProdi);
        if (namaProdi == null) {
            throw new IllegalArgumentException("Kode program studi tidak dikenali");
        }

        int tahunAngkatan = 2000 + Integer.parseInt(tahun);
        int urutan = Integer.parseInt(nomor);

        StringBuilder sb = new StringBuilder();
        sb.append("Informasi NIM ").append(nim).append(":\n");
        sb.append(">> Program Studi: ").append(namaProdi).append("\n");
        sb.append(">> Angkatan: ").append(tahunAngkatan).append("\n");
        sb.append(">> Urutan: ").append(urutan);

        return sb.toString();
    }

    private String processNilai(String input) {
        Scanner scanner = new Scanner(input);
        
        try {
            // Read weights
            int bobotPA = scanner.nextInt();
            int bobotTugas = scanner.nextInt();
            int bobotKuis = scanner.nextInt();
            int bobotProyek = scanner.nextInt();
            int bobotUTS = scanner.nextInt();
            int bobotUAS = scanner.nextInt();
            scanner.nextLine(); // Move to next line

            // Initialize counters
            Map<String, Integer> totalNilai = new HashMap<>();
            Map<String, Integer> totalMaksimal = new HashMap<>();
            
            String[] types = {"PA", "T", "K", "P", "UTS", "UAS"};
            for (String type : types) {
                totalNilai.put(type, 0);
                totalMaksimal.put(type, 0);
            }

            // Process data lines
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.equals("---")) break;
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\|");
                if (parts.length != 3) continue;

                String type = parts[0];
                int maxScore = Integer.parseInt(parts[1]);
                int score = Integer.parseInt(parts[2]);

                if (totalNilai.containsKey(type)) {
                    totalNilai.put(type, totalNilai.get(type) + score);
                    totalMaksimal.put(type, totalMaksimal.get(type) + maxScore);
                }
            }

            // Calculate percentages and weighted scores
            double nilaiAkhir = 0;
            StringBuilder result = new StringBuilder();
            result.append("Perolehan Nilai:\n");

            Map<String, Integer> bobotMap = Map.of(
                "PA", bobotPA, "T", bobotTugas, "K", bobotKuis,
                "P", bobotProyek, "UTS", bobotUTS, "UAS", bobotUAS
            );

            Map<String, String> namaMap = Map.of(
                "PA", "Partisipatif", "T", "Tugas", "K", "Kuis",
                "P", "Proyek", "UTS", "UTS", "UAS", "UAS"
            );

            for (String type : types) {
                int total = totalNilai.get(type);
                int maks = totalMaksimal.get(type);
                int bobot = bobotMap.get(type);

                double persentase = (maks == 0) ? 0 : (total * 100.0 / maks);
                int persentaseBulat = (int) Math.round(persentase);
                double nilaiTerbobot = (persentaseBulat / 100.0) * bobot;
                nilaiAkhir += nilaiTerbobot;

                result.append(String.format(">> %s: %d/100 (%.2f/%d)\n", 
                    namaMap.get(type), persentaseBulat, nilaiTerbobot, bobot));
            }

            result.append("\n");
            result.append(String.format(">> Nilai Akhir: %.2f\n", nilaiAkhir));
            result.append(">> Grade: ").append(convertToGrade(nilaiAkhir));

            return result.toString();
        } finally {
            scanner.close();
        }
    }

    private String processMatrix(String input) {
        Scanner scanner = new Scanner(input);
        try {
            int size = scanner.nextInt();
            int[][] matrix = new int[size][size];

            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    matrix[i][j] = scanner.nextInt();
                }
            }

            if (size <= 2) {
                int total = 0;
                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        total += matrix[i][j];
                    }
                }
                return String.format(
                    "Nilai L: Tidak Ada\nNilai Kebalikan L: Tidak Ada\nNilai Tengah: %d\nPerbedaan: Tidak Ada\nDominan: %d",
                    total, total
                );
            }

            // Calculate L shape (left and bottom)
            int nilaiL = 0;
            for (int i = 0; i < size; i++) {
                nilaiL += matrix[i][0]; // Left column
            }
            for (int j = 1; j < size - 1; j++) {
                nilaiL += matrix[size - 1][j]; // Bottom row (excluding corners)
            }

            // Calculate reverse L shape (right and top)
            int nilaiLBalik = 0;
            for (int i = 0; i < size; i++) {
                nilaiLBalik += matrix[i][size - 1]; // Right column
            }
            for (int j = 1; j < size - 1; j++) {
                nilaiLBalik += matrix[0][j]; // Top row (excluding corners)
            }

            // Calculate center value
            int nilaiTengah;
            if (size % 2 == 1) {
                nilaiTengah = matrix[size / 2][size / 2];
            } else {
                int mid = size / 2;
                nilaiTengah = matrix[mid - 1][mid - 1] + matrix[mid - 1][mid] + 
                             matrix[mid][mid - 1] + matrix[mid][mid];
            }

            int perbedaan = Math.abs(nilaiL - nilaiLBalik);
            int dominan = (nilaiL == nilaiLBalik) ? nilaiTengah : Math.max(nilaiL, nilaiLBalik);

            return String.format(
                "Nilai L: %d\nNilai Kebalikan L: %d\nNilai Tengah: %d\nPerbedaan: %d\nDominan: %d",
                nilaiL, nilaiLBalik, nilaiTengah, perbedaan, dominan
            );
        } finally {
            scanner.close();
        }
    }

    private String processFrekuensi(String input) {
        Scanner scanner = new Scanner(input);
        try {
            List<Integer> numbers = new ArrayList<>();
            
            while (scanner.hasNextInt()) {
                numbers.add(scanner.nextInt());
            }

            if (numbers.isEmpty()) {
                return "Tidak ada data angka";
            }

            // Calculate frequency
            Map<Integer, Integer> frekuensi = new HashMap<>();
            for (int num : numbers) {
                frekuensi.put(num, frekuensi.getOrDefault(num, 0) + 1);
            }

            // Find min, max, most frequent
            int tertinggi = Collections.max(numbers);
            int terendah = Collections.min(numbers);
            
            int terbanyak = 0;
            int frekuensiTerbanyak = 0;
            for (Map.Entry<Integer, Integer> entry : frekuensi.entrySet()) {
                if (entry.getValue() > frekuensiTerbanyak || 
                    (entry.getValue() == frekuensiTerbanyak && entry.getKey() > terbanyak)) {
                    terbanyak = entry.getKey();
                    frekuensiTerbanyak = entry.getValue();
                }
            }

            // Find least frequent using elimination algorithm
            int tersedikit = findLeastFrequent(numbers, frekuensi);

            // Calculate highest and lowest products
            long produkTertinggi = Long.MIN_VALUE;
            int nilaiProdukTertinggi = 0;
            long produkTerendah = Long.MAX_VALUE;
            int nilaiProdukTerendah = 0;

            for (Map.Entry<Integer, Integer> entry : frekuensi.entrySet()) {
                long produk = (long) entry.getKey() * entry.getValue();
                if (produk > produkTertinggi) {
                    produkTertinggi = produk;
                    nilaiProdukTertinggi = entry.getKey();
                }
                if (produk < produkTerendah) {
                    produkTerendah = produk;
                    nilaiProdukTerendah = entry.getKey();
                }
            }

            return String.format(
                "Tertinggi: %d\nTerendah: %d\nTerbanyak: %d (%dx)\nTersedikit: %d (%dx)\n" +
                "Jumlah Tertinggi: %d * %d = %d\nJumlah Terendah: %d * %d = %d",
                tertinggi, terendah, terbanyak, frekuensiTerbanyak, 
                tersedikit, frekuensi.get(tersedikit),
                nilaiProdukTertinggi, frekuensi.get(nilaiProdukTertinggi), produkTertinggi,
                nilaiProdukTerendah, frekuensi.get(nilaiProdukTerendah), produkTerendah
            );
        } finally {
            scanner.close();
        }
    }

    private int findLeastFrequent(List<Integer> numbers, Map<Integer, Integer> frekuensi) {
        Set<Integer> eliminated = new HashSet<>();
        int i = 0;
        
        while (i < numbers.size()) {
            int current = numbers.get(i);
            if (eliminated.contains(current)) {
                i++;
                continue;
            }
            
            // Find next occurrence
            int j = i + 1;
            while (j < numbers.size() && numbers.get(j) != current) {
                j++;
            }
            
            if (j < numbers.size()) {
                // Eliminate all numbers between i and j
                for (int k = i; k <= j; k++) {
                    eliminated.add(numbers.get(k));
                }
                i = j + 1;
            } else {
                return current; // Found the least frequent
            }
        }
        
        // If no unique found, return the one with smallest frequency
        return Collections.min(frekuensi.entrySet(), 
            Map.Entry.comparingByValue()).getKey();
    }
}