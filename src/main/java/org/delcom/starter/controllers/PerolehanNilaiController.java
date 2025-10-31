package org.delcom.starter.controllers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;

@RestController
public class PerolehanNilaiController {

    @GetMapping("/hitung-nilai")
    public String hitungNilai(
            // Parameter untuk bobot komponen
            @RequestParam("bobotPA") int bobotPA,
            @RequestParam("bobotT") int bobotT,
            @RequestParam("bobotK") int bobotK,
            @RequestParam("bobotP") int bobotP,
            @RequestParam("bobotUTS") int bobotUTS,
            @RequestParam("bobotUAS") int bobotUAS,
            // Parameter untuk daftar nilai. Spring akan otomatis mengumpulkan semua parameter
            // bernama 'nilai' ke dalam sebuah List<String>.
            @RequestParam("nilai") List<String> daftarNilai) {

        // Menggunakan Locale US agar format desimal menggunakan titik (best practice)
        Locale.setDefault(Locale.US);

        // Variabel untuk menyimpan total skor & total maksimal per kategori
        int totalPA = 0, maxPA = 0;
        int totalT = 0, maxT = 0;
        int totalK = 0, maxK = 0;
        int totalP = 0, maxP = 0;
        int totalUTS = 0, maxUTS = 0;
        int totalUAS = 0, maxUAS = 0;

        try {
            // Mengganti loop 'while' dengan iterasi pada List yang diterima dari URL
            for (String line : daftarNilai) {
                String[] parts = line.split("\\|"); // Pisahkan dengan simbol "|"
                if (parts.length != 3) continue; // Abaikan format yang salah

                String simbol = parts[0].trim();
                int maks = Integer.parseInt(parts[1].trim());
                int nilai = Integer.parseInt(parts[2].trim());

                switch (simbol) {
                    case "PA": maxPA += maks; totalPA += nilai; break;
                    case "T":  maxT += maks;  totalT += nilai;  break;
                    case "K":  maxK += maks;  totalK += nilai;  break;
                    case "P":  maxP += maks;  totalP += nilai;  break;
                    case "UTS":maxUTS += maks;totalUTS += nilai;break;
                    case "UAS":maxUAS += maks;totalUAS += nilai;break;
                }
            }

            // --- Bagian kalkulasi (logika sama persis dengan kode asli) ---
            double rataPA = (maxPA == 0) ? 0 : (totalPA * 100.0 / maxPA);
            double rataT = (maxT == 0) ? 0 : (totalT * 100.0 / maxT);
            double rataK = (maxK == 0) ? 0 : (totalK * 100.0 / maxK);
            double rataP = (maxP == 0) ? 0 : (totalP * 100.0 / maxP);
            double rataUTS = (maxUTS == 0) ? 0 : (totalUTS * 100.0 / maxUTS);
            double rataUAS = (maxUAS == 0) ? 0 : (totalUAS * 100.0 / maxUAS);

            int bulatPA = (int) Math.floor(rataPA);
            int bulatT = (int) Math.floor(rataT);
            int bulatK = (int) Math.floor(rataK);
            int bulatP = (int) Math.floor(rataP);
            int bulatUTS = (int) Math.floor(rataUTS);
            int bulatUAS = (int) Math.floor(rataUAS);

            double nilaiPA = (bulatPA / 100.0) * bobotPA;
            double nilaiT = (bulatT / 100.0) * bobotT;
            double nilaiK = (bulatK / 100.0) * bobotK;
            double nilaiP = (bulatP / 100.0) * bobotP;
            double nilaiUTS = (bulatUTS / 100.0) * bobotUTS;
            double nilaiUAS = (bulatUAS / 100.0) * bobotUAS;

            double totalNilai = nilaiPA + nilaiT + nilaiK + nilaiP + nilaiUTS + nilaiUAS;

            // --- Mengganti System.out.println dengan pembuatan string HTML ---
            // Menggunakan StringBuilder untuk efisiensi
            StringBuilder htmlResponse = new StringBuilder();
            htmlResponse.append("<!DOCTYPE html><html><head><title>Hasil Perolehan Nilai</title>");
            htmlResponse.append("<style>body { font-family: monospace; white-space: pre; margin: 2em; } div { border: 1px solid #ccc; padding: 1em; border-radius: 8px; } </style>");
            htmlResponse.append("</head><body><div>");
            htmlResponse.append("<h2>Perolehan Nilai:</h2>");
            htmlResponse.append(String.format(">> Partisipatif: %d/100 (%.2f/%d)<br>", bulatPA, nilaiPA, bobotPA));
            htmlResponse.append(String.format(">> Tugas:        %d/100 (%.2f/%d)<br>", bulatT, nilaiT, bobotT));
            htmlResponse.append(String.format(">> Kuis:         %d/100 (%.2f/%d)<br>", bulatK, nilaiK, bobotK));
            htmlResponse.append(String.format(">> Proyek:       %d/100 (%.2f/%d)<br>", bulatP, nilaiP, bobotP));
            htmlResponse.append(String.format(">> UTS:          %d/100 (%.2f/%d)<br>", bulatUTS, nilaiUTS, bobotUTS));
            htmlResponse.append(String.format(">> UAS:          %d/100 (%.2f/%d)<br>", bulatUAS, nilaiUAS, bobotUAS));
            htmlResponse.append("<br>");
            htmlResponse.append(String.format(">> Nilai Akhir: %.2f<br>", totalNilai));
            htmlResponse.append(String.format(">> Grade: %s<br>", getGrade(totalNilai)));
            htmlResponse.append("</div></body></html>");

            return htmlResponse.toString();

        } catch (Exception e) {
            // Menangani error jika format input salah
            return "<h2>Error</h2><p>Terjadi kesalahan saat memproses input. Pastikan format nilai benar (contoh: 'PA|10|8').</p><p>Error: " + e.getMessage() + "</p>";
        }
    }

    // Fungsi konversi grade (sama persis dengan kode asli)
    private String getGrade(double nilai) {
        if (nilai >= 79.5) return "A";
        else if (nilai >= 72) return "AB";
        else if (nilai >= 64.5) return "B";
        else if (nilai >= 57) return "BC";
        else if (nilai >= 49.5) return "C";
        else if (nilai >= 34) return "D";
        else return "E";
    }
}