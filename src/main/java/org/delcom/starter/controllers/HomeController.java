package org.delcom.starter.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;

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

    @GetMapping("/informasiNim/{nim}")
    public String informasiNim(@PathVariable String nim) {
        String prefix = nim.substring(0, 3), angkatan = nim.substring(3, 5), nomor = nim.substring(5), prodi;
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
        return String.format("Inforamsi NIM %s: \n>> Program Studi: %s\n>> Angkatan: 20%s\n>> Urutan: %s", nim, prodi,
                angkatan, nomor.replaceFirst("^0+", ""));
    }

    @GetMapping("/perolehanNilai/{strBase64}")
    public String perolehanNilai(@PathVariable String strBase64) {
        try {
            String d = new String(Base64.getDecoder().decode(strBase64));
            String[] l = d.split("\\r?\\n");
            int i = 0;
            int bPA = Integer.parseInt(l[i++].trim()), bT = Integer.parseInt(l[i++].trim()),
                    bK = Integer.parseInt(l[i++].trim()), bP = Integer.parseInt(l[i++].trim()),
                    bUTS = Integer.parseInt(l[i++].trim()), bUAS = Integer.parseInt(l[i++].trim());
            double pPA = 0, pT = 0, pK = 0, pP = 0, pUTS = 0, pUAS = 0, mPA = 0, mT = 0, mK = 0, mP = 0, mUTS = 0,
                    mUAS = 0;
            for (; i < l.length; i++) {
                String ln = l[i].trim();
                if (ln.equals("---"))
                    break;
                if (ln.isEmpty())
                    continue;
                String[] p = ln.split("\\|");
                if (p.length != 3)
                    continue;
                try {
                    String j = p[0].trim();
                    int nm = Integer.parseInt(p[1].trim()), np = Integer.parseInt(p[2].trim());
                    switch (j) {
                        case "PA":
                            pPA += np;
                            mPA += nm;
                            break;
                        case "T":
                            pT += np;
                            mT += nm;
                            break;
                        case "K":
                            pK += np;
                            mK += nm;
                            break;
                        case "P":
                            pP += np;
                            mP += nm;
                            break;
                        case "UTS":
                            pUTS += np;
                            mUTS += nm;
                            break;
                        case "UAS":
                            pUAS += np;
                            mUAS += nm;
                            break;
                    }
                } catch (NumberFormatException e) {
                    continue;
                }
            }
            class H {
                int pf(double p, double m) {
                    if (m <= 0)
                        return 0;
                    return (int) Math.floor((p / m) * 100.0 + 1e-9);
                }

                double c(int pr, int b) {
                    return (pr / 100.0) * b;
                }

                String g(double n) {
                    if (n >= 79.5)
                        return "A";
                    else if (n >= 72)
                        return "AB";
                    else if (n >= 64.5)
                        return "B";
                    else if (n >= 57)
                        return "BC";
                    else if (n >= 49.5)
                        return "C";
                    else if (n >= 34)
                        return "D";
                    else
                        return "E";
                }
            }
            H h = new H();
            int pp = h.pf(pPA, mPA), pt = h.pf(pT, mT), pk = h.pf(pK, mK), pp_proj = h.pf(pP, mP),
                    puts = h.pf(pUTS, mUTS), puas = h.pf(pUAS, mUAS);
            double na = h.c(pp, bPA) + h.c(pt, bT) + h.c(pk, bK) + h.c(pp_proj, bP) + h.c(puts, bUTS) + h.c(puas, bUAS);
            return "Perolehan Nilai:\n"
                    + String.format(Locale.US, ">> Partisipatif: %d/100 (%.2f/%d)\n", pp, h.c(pp, bPA), bPA)
                    + String.format(Locale.US, ">> Tugas: %d/100 (%.2f/%d)\n", pt, h.c(pt, bT), bT)
                    + String.format(Locale.US, ">> Kuis: %d/100 (%.2f/%d)\n", pk, h.c(pk, bK), bK)
                    + String.format(Locale.US, ">> Proyek: %d/100 (%.2f/%d)\n", pp_proj, h.c(pp_proj, bP), bP)
                    + String.format(Locale.US, ">> UTS: %d/100 (%.2f/%d)\n", puts, h.c(puts, bUTS), bUTS)
                    + String.format(Locale.US, ">> UAS: %d/100 (%.2f/%d)\n", puas, h.c(puas, bUAS), bUAS)
                    + String.format(Locale.US, "\n>> Nilai Akhir: %.2f\n", na) + ">> Grade: " + h.g(na);
        } catch (Exception e) {
            return "Error: Input tidak valid.";
        }
    }

    @GetMapping("/perbedaanL/{strBase64}")
    public String perbedaanL(@PathVariable String strBase64) {
        try {
            String d = new String(Base64.getDecoder().decode(strBase64));
            String[] l = d.split("\\r?\\n");
            int n = Integer.parseInt(l[0].trim());
            int[][] m = new int[n][n];
            for (int i = 0; i < n; i++) {
                String[] r = l[i + 1].trim().split("\\s+");
                for (int j = 0; j < n; j++) {
                    m[i][j] = Integer.parseInt(r[j]);
                }
            }
            int nL = -1, nKL = -1;
            if (n >= 3) {
                nL = 0;
                for (int i = 0; i < n; i++)
                    nL += m[i][0];
                for (int j = 0; j < n; j++)
                    nL += m[n - 1][j];
                nL -= m[n - 1][0];
                nKL = 0;
                for (int j = 0; j < n; j++)
                    nKL += m[0][j];
                for (int i = 0; i < n; i++)
                    nKL += m[i][n - 1];
                nKL -= m[0][n - 1];
            }
            int nT;
            if (n % 2 == 1) {
                nT = m[n / 2][n / 2];
            } else {
                int m1 = n / 2 - 1, m2 = n / 2;
                nT = m[m1][m1] + m[m1][m2] + m[m2][m1] + m[m2][m2];
            }
            String pS;
            int p = 0;
            if (nL == -1) {
                pS = "Tidak Ada";
            } else {
                p = Math.abs(nL - nKL);
                pS = String.valueOf(p);
            }
            int dom;
            if (nL == -1 || p == 0) {
                dom = nT;
            } else {
                dom = Math.max(nL, nKL);
            }
            return String.format("Nilai L: %s\nNilai Kebalikan L: %s\nNilai Tengah: %d\nPerbedaan: %s\nDominan: %d",
                    (nL == -1 ? "Tidak Ada" : nL), (nKL == -1 ? "Tidak Ada" : nKL), nT, pS, dom);
        } catch (Exception e) {
            return "Error: Input tidak valid.";
        }
    }

    @GetMapping("/palingTer/{strBase64}")
    public String palingTer(@PathVariable String strBase64) {
        try {
            String decodedInput = new String(Base64.getDecoder().decode(strBase64));
            ArrayList<Integer> listNilai = new ArrayList<>();
            for (String line : decodedInput.split("\\r?\\n")) {
                String t = line.trim();
                if (t.equals("---"))
                    break;
                if (t.isEmpty())
                    continue;
                listNilai.add(Integer.parseInt(t));
            }
            if (listNilai.isEmpty())
                return "Error: Tidak ada data input.";

            Map<Integer, Integer> freqMap = new LinkedHashMap<>();
            for (int val : listNilai) {
                freqMap.put(val, freqMap.getOrDefault(val, 0) + 1);
            }

            int maxVal = Collections.max(listNilai);
            int minVal = Collections.min(listNilai);
            int frekTerbanyak = Collections.max(freqMap.values());
            int frekTersedikit = Collections.min(freqMap.values());

            int angkaTerbanyak = 0;
            for (int val : listNilai) {
                if (freqMap.get(val) == frekTerbanyak) {
                    angkaTerbanyak = val;
                    break;
                }
            }
            int angkaTersedikit = 0;
            for (int val : listNilai) {
                if (freqMap.get(val) == frekTersedikit) {
                    angkaTersedikit = val;
                    break;
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
            for (Map.Entry<Integer, Integer> entry : freqMap.entrySet()) {
                int angka = entry.getKey();
                int total = angka * entry.getValue();
                if (total < jumlahTerendah || (total == jumlahTerendah && angka < nilaiJumlahTerendah)) {
                    jumlahTerendah = total;
                    nilaiJumlahTerendah = angka;
                }
            }

            return String.format(
                    "Tertinggi: %d\nTerendah: %d\nTerbanyak: %d (%dx)\nTersedikit: %d (%dx)\nJumlah Tertinggi: %d * %d = %d\nJumlah Terendah: %d * %d = %d",
                    maxVal, minVal, angkaTerbanyak, frekTerbanyak, angkaTersedikit, frekTersedikit,
                    nilaiJumlahTertinggi, frekJumlahTertinggi, jumlahTertinggi,
                    nilaiJumlahTerendah, freqMap.get(nilaiJumlahTerendah), jumlahTerendah);
        } catch (Exception e) {
            return "Error: Input tidak valid.";
        }
    }
}