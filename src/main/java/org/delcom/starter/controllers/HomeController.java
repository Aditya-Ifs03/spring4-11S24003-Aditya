package org.delcom.starter.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("unused")
@RestController
public class HomeController {

    @GetMapping("/")
    public String hello() {
        return "Hay, selamat datang di aplikasi dengan Spring Boot!";
    }

    @GetMapping("/hello/{name}")
    public String sayHello(@PathVariable String name) {
        return "Hello, " + name + "!";
    }

    @GetMapping("/informasi-nim/{nim}")
    public String informasiNim(@PathVariable String nim) {
        if (nim.length() != 8) {
            return "NIM harus 8 karakter";
        }

        String kodeProdi = nim.substring(0, 2);
        String angkatan = "20" + nim.substring(3, 5);
        String urutan = nim.substring(5, 8);

        Map<String, String> prodiMap = new HashMap<>();
        prodiMap.put("11", "Sarjana Informatika");
        prodiMap.put("12", "Sarjana Sistem Informasi");
        prodiMap.put("14", "Sarjana Teknik Elektro");
        prodiMap.put("21", "Sarjana Manajemen Rekayasa");
        prodiMap.put("22", "Sarjana Teknik Metalurgi");
        prodiMap.put("31", "Sarjana Teknik Bioproses");
        prodiMap.put("114", "Diploma 4 Teknologi Rekasaya Perangkat Lunak");
        prodiMap.put("113", "Diploma 3 Teknologi Informasi");
        prodiMap.put("133", "Diploma 3 Teknologi Komputer");

        String prodi = prodiMap.get(kodeProdi);
        if (prodi == null) {
            return "Program Studi tidak Tersedia";
        }

        return "Inforamsi NIM " + nim + ": >> Program Studi: " + prodi + ">> Angkatan: " + angkatan + ">> Urutan: " + urutan;
    }

    @SuppressWarnings("resource")
    @GetMapping("/perolehan-nilai/{dataBase64}")
    public String perolehanNilai(@PathVariable String dataBase64) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(dataBase64);
            String decodedString = new String(decodedBytes);
            Scanner scanner = new Scanner(decodedString);

            // Read bobot values
            int bobotPartisipatif = Integer.parseInt(scanner.nextLine());
            int bobotTugas = Integer.parseInt(scanner.nextLine());
            int bobotKuis = Integer.parseInt(scanner.nextLine());
            int bobotProyek = Integer.parseInt(scanner.nextLine());
            int bobotUTS = Integer.parseInt(scanner.nextLine());
            int bobotUAS = Integer.parseInt(scanner.nextLine());

            // Check if total bobot is 100
            int totalBobot = bobotPartisipatif + bobotTugas + bobotKuis + bobotProyek + bobotUTS + bobotUAS;
            if (totalBobot != 100) {
                return "Total bobot harus 100";
            }

            // Initialize variables for calculating scores
            double totalNilaiPartisipatif = 0;
            double totalNilaiTugas = 0;
            double totalNilaiKuis = 0;
            double totalNilaiProyek = 0;
            double totalNilaiUTS = 0;
            double totalNilaiUAS = 0;

            int countPartisipatif = 0;
            int countTugas = 0;
            int countKuis = 0;
            int countProyek = 0;
            int countUTS = 0;
            int countUAS = 0;

            StringBuilder errorMessage = new StringBuilder();

            // Process each line of data
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.equals("---")) {
                    break;
                }

                String[] parts = line.split("\\|");
                if (parts.length != 3) {
                    if (errorMessage.length() == 0) {
                        errorMessage.append("Data tidak valid. Silahkan menggunakan format: Simbol|Bobot|Perolehan-Nilai");
                    }
                    continue;
                }

                String simbol = parts[0];
                int bobot = Integer.parseInt(parts[1]);
                int perolehan = Integer.parseInt(parts[2]);

                double nilai = (double) perolehan / 100 * bobot;

                switch (simbol) {
                    case "PA":
                        totalNilaiPartisipatif += nilai;
                        countPartisipatif += bobot;
                        break;
                    case "T":
                        totalNilaiTugas += nilai;
                        countTugas += bobot;
                        break;
                    case "K":
                        totalNilaiKuis += nilai;
                        countKuis += bobot;
                        break;
                    case "P":
                        totalNilaiProyek += nilai;
                        countProyek += bobot;
                        break;
                    case "UTS":
                        totalNilaiUTS += nilai;
                        countUTS += bobot;
                        break;
                    case "UAS":
                        totalNilaiUAS += nilai;
                        countUAS += bobot;
                        break;
                    default:
                        if (errorMessage.length() == 0) {
                            errorMessage.append("Simbol tidak dikenal");
                        }
                        break;
                }
            }
            scanner.close();

            // Calculate final scores for each component
            double nilaiAkhirPartisipatif = countPartisipatif > 0 ? (totalNilaiPartisipatif / countPartisipatif * 100) : 0;
            double nilaiAkhirTugas = countTugas > 0 ? (totalNilaiTugas / countTugas * 100) : 0;
            double nilaiAkhirKuis = countKuis > 0 ? (totalNilaiKuis / countKuis * 100) : 0;
            double nilaiAkhirProyek = countProyek > 0 ? (totalNilaiProyek / countProyek * 100) : 0;
            double nilaiAkhirUTS = countUTS > 0 ? (totalNilaiUTS / countUTS * 100) : 0;
            double nilaiAkhirUAS = countUAS > 0 ? (totalNilaiUAS / countUAS * 100) : 0;

            // Calculate weighted scores
            double weightedPartisipatif = nilaiAkhirPartisipatif * bobotPartisipatif / 100;
            double weightedTugas = nilaiAkhirTugas * bobotTugas / 100;
            double weightedKuis = nilaiAkhirKuis * bobotKuis / 100;
            double weightedProyek = nilaiAkhirProyek * bobotProyek / 100;
            double weightedUTS = nilaiAkhirUTS * bobotUTS / 100;
            double weightedUAS = nilaiAkhirUAS * bobotUAS / 100;

            // Calculate final grade
            double nilaiAkhir = weightedPartisipatif + weightedTugas + weightedKuis + weightedProyek + weightedUTS + weightedUAS;
            String grade = calculateGrade(nilaiAkhir);

            // Build result string
            StringBuilder result = new StringBuilder();
            if (errorMessage.length() > 0) {
                result.append(errorMessage).append("<br/>");
            }
            
            result.append("Perolehan Nilai:<br/>")
                  .append(">> Partisipatif: ").append(String.format("%.0f", nilaiAkhirPartisipatif)).append("/100 (").append(String.format("%.2f", weightedPartisipatif)).append("/").append(bobotPartisipatif).append(")<br/>")
                  .append(">> Tugas: ").append(String.format("%.0f", nilaiAkhirTugas)).append("/100 (").append(String.format("%.2f", weightedTugas)).append("/").append(bobotTugas).append(")<br/>")
                  .append(">> Kuis: ").append(String.format("%.0f", nilaiAkhirKuis)).append("/100 (").append(String.format("%.2f", weightedKuis)).append("/").append(bobotKuis).append(")<br/>")
                  .append(">> Proyek: ").append(String.format("%.0f", nilaiAkhirProyek)).append("/100 (").append(String.format("%.2f", weightedProyek)).append("/").append(bobotProyek).append(")<br/>")
                  .append(">> UTS: ").append(String.format("%.0f", nilaiAkhirUTS)).append("/100 (").append(String.format("%.2f", weightedUTS)).append("/").append(bobotUTS).append(")<br/>")
                  .append(">> UAS: ").append(String.format("%.0f", nilaiAkhirUAS)).append("/100 (").append(String.format("%.2f", weightedUAS)).append("/").append(bobotUAS).append(")<br/><br/>")
                  .append(">> Nilai Akhir: ").append(String.format("%.2f", nilaiAkhir)).append("<br/>")
                  .append(">> Grade: ").append(grade);

            return result.toString();

        } catch (Exception e) {
            return "Error processing data: " + e.getMessage();
        }
    }

    private String calculateGrade(double nilai) {
        if (nilai >= 85) return "A";
        if (nilai >= 80) return "AB";
        if (nilai >= 70) return "B";
        if (nilai >= 65) return "BC";
        if (nilai >= 60) return "C";
        if (nilai >= 40) return "D";
        return "E";
    }

    @GetMapping("/perbedaan-l/{dataBase64}")
    public String perbedaanL(@PathVariable String dataBase64) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(dataBase64);
            String decodedString = new String(decodedBytes);
            Scanner scanner = new Scanner(decodedString);

            int n = Integer.parseInt(scanner.nextLine());
            int[][] matrix = new int[n][n];

            // Read matrix
            for (int i = 0; i < n; i++) {
                String[] row = scanner.nextLine().split(" ");
                for (int j = 0; j < n; j++) {
                    matrix[i][j] = Integer.parseInt(row[j]);
                }
            }
            scanner.close();

            if (n < 3) {
                int sum = 0;
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        sum += matrix[i][j];
                    }
                }
                return "Nilai L: Tidak Ada<br/>Nilai Kebalikan L: Tidak Ada<br/>Nilai Tengah: " + sum + "<br/>Perbedaan: Tidak Ada<br/>Dominan: " + sum;
            }

            // Calculate L value (top-left to bottom-right diagonal excluding corners)
            int nilaiL = 0;
            for (int i = 1; i < n - 1; i++) {
                nilaiL += matrix[i][i];
            }

            // Calculate inverse L value (top-right to bottom-left diagonal excluding corners)
            int nilaiKebalikanL = 0;
            for (int i = 1; i < n - 1; i++) {
                nilaiKebalikanL += matrix[i][n - 1 - i];
            }

            // Calculate middle value (center of matrix)
            int nilaiTengah = 0;
            if (n % 2 == 1) {
                nilaiTengah = matrix[n / 2][n / 2];
            } else {
                int mid = n / 2;
                nilaiTengah = (matrix[mid - 1][mid - 1] + matrix[mid - 1][mid] + matrix[mid][mid - 1] + matrix[mid][mid]) / 4;
            }

            int perbedaan = Math.abs(nilaiL - nilaiKebalikanL);
            int dominan = Math.max(nilaiL, nilaiKebalikanL);

            return "Nilai L: " + nilaiL + "<br/>Nilai Kebalikan L: " + nilaiKebalikanL + "<br/>Nilai Tengah: " + nilaiTengah + "<br/>Perbedaan: " + perbedaan + "<br/>Dominan: " + dominan;

        } catch (Exception e) {
            return "Error processing matrix: " + e.getMessage();
        }
    }

    @GetMapping("/paling-ter/{dataBase64}")
    public String palingTer(@PathVariable String dataBase64) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(dataBase64);
            String decodedString = new String(decodedBytes);
            Scanner scanner = new Scanner(decodedString);

            List<Integer> numbers = new ArrayList<>();
            
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.equals("---")) {
                    break;
                }
                numbers.add(Integer.parseInt(line));
            }
            scanner.close();

            if (numbers.isEmpty()) {
                return "Informasi tidak tersedia";
            }

            // Find highest, lowest, most frequent, and least frequent
            int tertinggi = Collections.max(numbers);
            int terendah = Collections.min(numbers);

            Map<Integer, Integer> frequencyMap = new HashMap<>();
            for (int num : numbers) {
                frequencyMap.put(num, frequencyMap.getOrDefault(num, 0) + 1);
            }

            int maxFrequency = Collections.max(frequencyMap.values());
            int minFrequency = Collections.min(frequencyMap.values());

            List<Integer> mostFrequent = new ArrayList<>();
            List<Integer> leastFrequent = new ArrayList<>();

            for (Map.Entry<Integer, Integer> entry : frequencyMap.entrySet()) {
                if (entry.getValue() == maxFrequency) {
                    mostFrequent.add(entry.getKey());
                }
                if (entry.getValue() == minFrequency) {
                    leastFrequent.add(entry.getKey());
                }
            }

            Collections.sort(mostFrequent);
            Collections.sort(leastFrequent);

            int terbanyak = mostFrequent.get(mostFrequent.size() - 1);
            int tersedikit = leastFrequent.get(leastFrequent.size() - 1);

            // Calculate sum of highest and lowest numbers
            int jumlahTertinggi = tertinggi * frequencyMap.get(tertinggi);
            int jumlahTerendah = terendah * frequencyMap.get(terendah);

            return "Tertinggi: " + tertinggi + "<br/>Terendah: " + terendah + "<br/>Terbanyak: " + terbanyak + " (" + maxFrequency + "x)<br/>Tersedikit: " + tersedikit + " (" + minFrequency + "x)<br/>Jumlah Tertinggi: " + tertinggi + " * " + frequencyMap.get(tertinggi) + " = " + jumlahTertinggi + "<br/>Jumlah Terendah: " + terendah + " * " + frequencyMap.get(terendah) + " = " + jumlahTerendah;

        } catch (Exception e) {
            return "Error processing data: " + e.getMessage();
        }
    }
}