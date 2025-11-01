
package org.delcom.starter.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    private record NilaiFrekuensi(int nilai, int frekuensi) {}

    //======================================================================
    // METHOD 1: Dari NIMController
    // Endpoint: /info-nim/{nim}
    //======================================================================
    @GetMapping("/info-nim/{nim}")
    public String getInformasiNIM(@PathVariable String nim) {
        String namaProdi;
        String angkatan;
        int nomorUrut;

        try {
            String kodeProdi = nim.substring(0, 3);
            String kodeAngkatan = nim.substring(3, 5);
            String strNomorUrut = nim.substring(5);

            switch (kodeProdi) {
                case "11S": namaProdi = "Sarjana Informatika"; break;
                case "12S": namaProdi = "Sarjana Sistem Informasi"; break;
                case "14S": namaProdi = "Sarjana Teknik Elektro"; break;
                case "21S": namaProdi = "Sarjana Manajemen Rekayasa"; break;
                case "22S": namaProdi = "Sarjana Teknik Metalurgi"; break;
                case "31S": namaProdi = "Sarjana Teknik Bioproses"; break;
                case "114": namaProdi = "Diploma 4 Teknologi Rekayasa Perangkat Lunak"; break;
                case "113": namaProdi = "Diploma 3 Teknologi Informasi"; break;
                case "133": namaProdi = "Diploma 3 Teknologi Komputer"; break;
                default: namaProdi = "Program Studi tidak dikenal"; break;
            }

            angkatan = "20" + kodeAngkatan;
            nomorUrut = Integer.parseInt(strNomorUrut);

        } catch (Exception e) {
            return "<h2>Error</h2><p>Format NIM '" + nim + "' tidak valid.</p>";
        }

        return String.format(
            """
            <!DOCTYPE html><html><head><title>Informasi NIM</title><style>body { font-family: sans-serif; margin: 2em; } div { border: 1px solid #ccc; padding: 1em; border-radius: 8px; max-width: 600px; }</style></head><body>
            <div><h2>Informasi NIM %s</h2><p><strong>Program Studi:</strong> %s</p><p><strong>Angkatan:</strong> %s</p><p><strong>Urutan:</strong> %d</p></div></body></html>
            """,
            nim, namaProdi, angkatan, nomorUrut
        );
    }

    //======================================================================
    // METHOD 2: Dari PerolehanNilaiController
    // Endpoint: /hitung-nilai
    //======================================================================
    @GetMapping("/hitung-nilai")
    public String hitungNilai(
            @RequestParam("bobotPA") int bobotPA, @RequestParam("bobotT") int bobotT,
            @RequestParam("bobotK") int bobotK, @RequestParam("bobotP") int bobotP,
            @RequestParam("bobotUTS") int bobotUTS, @RequestParam("bobotUAS") int bobotUAS,
            @RequestParam("nilai") List<String> daftarNilai) {

        Locale.setDefault(Locale.US);

        int totalPA = 0, maxPA = 0;
        int totalT = 0, maxT = 0;
        int totalK = 0, maxK = 0;
        int totalP = 0, maxP = 0;
        int totalUTS = 0, maxUTS = 0;
        int totalUAS = 0, maxUAS = 0;

        try {
            for (String line : daftarNilai) {
                String[] parts = line.split("\\|");
                if (parts.length != 3) continue;

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
            return "<h2>Error</h2><p>Terjadi kesalahan saat memproses input: " + e.getMessage() + "</p>";
        }
    }

    //======================================================================
    // METHOD 3: Dari MatriksController
    // Endpoint: /analisis-matriks
    //======================================================================
    @GetMapping("/analisis-matriks")
    public String analisisMatriks(@RequestParam("data") String dataMatriks) {
        try {
            String[] elemen = dataMatriks.split(",");
            int totalElemen = elemen.length;
            double nDouble = Math.sqrt(totalElemen);
            if (nDouble != Math.floor(nDouble)) {
                return "<h2>Error</h2><p>Jumlah elemen data (" + totalElemen + ") tidak membentuk matriks persegi.</p>";
            }
            int n = (int) nDouble;
            int[][] matriks = new int[n][n];
            int index = 0;
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    matriks[i][j] = Integer.parseInt(elemen[index++].trim());
                }
            }

            String nilaiLStr = "Tidak Ada";
            String nilaiKebalikanStr = "Tidak Ada";
            String nilaiTengahStr = "Tidak Ada";
            String perbedaanStr = "Tidak Ada";
            int dominan = 0;

            if (n == 1) {
                dominan = matriks[0][0];
                nilaiTengahStr = String.valueOf(dominan);
            } else {
                if (n % 2 == 1) {
                    nilaiTengahStr = String.valueOf(matriks[n / 2][n / 2]);
                } else {
                    int mid = n / 2;
                    nilaiTengahStr = String.valueOf(matriks[mid - 1][mid - 1] + matriks[mid - 1][mid] + matriks[mid][mid - 1] + matriks[mid][mid]);
                }
            }
            if (n == 2) {
                dominan = Integer.parseInt(nilaiTengahStr);
            }
            if (n >= 3) {
                int nilaiL = 0;
                for (int i = 0; i < n; i++) nilaiL += matriks[i][0];
                for (int j = 1; j <= n - 2; j++) nilaiL += matriks[n - 1][j];
                nilaiLStr = String.valueOf(nilaiL);

                int nilaiKebalikan = 0;
                for (int i = 0; i < n; i++) nilaiKebalikan += matriks[i][n - 1];
                for (int j = 1; j <= n - 2; j++) nilaiKebalikan += matriks[0][j];
                nilaiKebalikanStr = String.valueOf(nilaiKebalikan);

                perbedaanStr = String.valueOf(Math.abs(nilaiL - nilaiKebalikan));

                dominan = (nilaiL > nilaiKebalikan) ? nilaiL : (nilaiKebalikan > nilaiL) ? nilaiKebalikan : Integer.parseInt(nilaiTengahStr);
            }
            
            StringBuilder htmlResponse = new StringBuilder();
            htmlResponse.append("<!DOCTYPE html><html><head><title>Hasil Analisis Matriks</title><style>body { font-family: monospace; white-space: pre; margin: 2em; } div { border: 1px solid #ccc; padding: 1em; border-radius: 8px; } </style></head><body><div>");
            htmlResponse.append("<h2>Matriks Input (" + n + "x" + n + "):</h2>");
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) htmlResponse.append(String.format("%-4d", matriks[i][j]));
                htmlResponse.append("<br>");
            }
            htmlResponse.append("<hr><h2>Hasil Analisis:</h2>");
            if (nilaiLStr.equals("Tidak Ada")) htmlResponse.append("Nilai L: ").append(nilaiLStr).append("<br>");
            else htmlResponse.append("Nilai L: ").append(nilaiLStr).append(":<br>");
            htmlResponse.append("Nilai Kebalikan L: ").append(nilaiKebalikanStr).append("<br>");
            htmlResponse.append("Nilai Tengah: ").append(nilaiTengahStr).append("<br>");
            htmlResponse.append("Perbedaan: ").append(perbedaanStr).append("<br>");
            htmlResponse.append("Dominan: ").append(dominan).append("<br>");
            htmlResponse.append("</div></body></html>");
            return htmlResponse.toString();

        } catch (Exception e) {
            return "<h2>Error</h2><p>Terjadi kesalahan saat memproses data: " + e.getMessage() + "</p>";
        }
    }
    
    //======================================================================
    // METHOD 4: Dari AnalisisNilaiController
    // Endpoint: /analisis-nilai
    //======================================================================

@GetMapping("/analisis-nilai")
public String analisisNilai(@RequestParam("nilai") List<Integer> daftarNilai) {
    if (daftarNilai == null || daftarNilai.isEmpty()) {
        return "<h2>Error</h2><p>Tidak ada data nilai yang diberikan.</p>";
    }

    try {
        List<Integer> daftarNilaiMutable = new ArrayList<>(daftarNilai);

        int nilaiTertinggi = Collections.max(daftarNilaiMutable);
        int nilaiTerendah = Collections.min(daftarNilaiMutable);

        Collections.sort(daftarNilaiMutable);
        
        List<NilaiFrekuensi> daftarFrekuensi = new ArrayList<>();
        // Pengecekan isEmpty() yang berlebihan sudah dihapus
        int angkaSaatIni = daftarNilaiMutable.get(0);
        int hitunganSaatIni = 1;
        for (int i = 1; i < daftarNilaiMutable.size(); i++) {
            if (daftarNilaiMutable.get(i) == angkaSaatIni) {
                hitunganSaatIni++;
            } else {
                daftarFrekuensi.add(new NilaiFrekuensi(angkaSaatIni, hitunganSaatIni));
                angkaSaatIni = daftarNilaiMutable.get(i);
                hitunganSaatIni = 1;
            }
        }
        daftarFrekuensi.add(new NilaiFrekuensi(angkaSaatIni, hitunganSaatIni));

        int nilaiTerbanyak = -1, frekuensiTerbanyak = 0;
        int nilaiTersedikit = -1, frekuensiTerdikit = Integer.MAX_VALUE;
        for (NilaiFrekuensi nf : daftarFrekuensi) {
            if (nf.frekuensi() > frekuensiTerbanyak) {
                frekuensiTerbanyak = nf.frekuensi();
                nilaiTerbanyak = nf.nilai();
            }
            if (nf.frekuensi() < frekuensiTerdikit) {
                frekuensiTerdikit = nf.frekuensi();
                nilaiTersedikit = nf.nilai();
            }
        }

        int nilaiJumlahTertinggi = -1, frekuensiNilaiJumlahTertinggi = 0, jumlahTertinggi = -1;
        int nilaiJumlahTerendah = -1, frekuensiNilaiJumlahTerendah = 0, jumlahTerendah = Integer.MAX_VALUE;
        for (NilaiFrekuensi nf : daftarFrekuensi) {
            int jumlah = nf.nilai() * nf.frekuensi();
            
            // LOGIKA YANG DISEMPURNAKAN
            if (jumlah >= jumlahTertinggi) { // Lebih simpel, mencakup > dan ==
                jumlahTertinggi = jumlah;
                nilaiJumlahTertinggi = nf.nilai();
                frekuensiNilaiJumlahTertinggi = nf.frekuensi();
            }
            
            // LOGIKA YANG DISEMPURNAKAN
            if (jumlah < jumlahTerendah) { // Logika || yang tak terjangkau dihapus
                jumlahTerendah = jumlah;
                nilaiJumlahTerendah = nf.nilai();
                frekuensiNilaiJumlahTerendah = nf.frekuensi();
            }
        }

        StringBuilder htmlResponse = new StringBuilder();
        htmlResponse.append("<!DOCTYPE html><html><head><title>Hasil Analisis Nilai</title><style>body { font-family: monospace; white-space: pre; margin: 2em; } div { border: 1px solid #ccc; padding: 1em; border-radius: 8px; } </style></head><body><div>");
        htmlResponse.append("<h2>Data Input:</h2>").append(daftarNilaiMutable.toString()).append("<hr><h2>Hasil Analisis:</h2>");
        
        htmlResponse.append("Tertinggi: ").append(nilaiTertinggi).append("<br>");
        htmlResponse.append("Terendah: ").append(nilaiTerendah).append("<br>");
        htmlResponse.append("Terbanyak: ").append(nilaiTerbanyak).append(" (").append(frekuensiTerbanyak).append("x)").append("<br>");
        htmlResponse.append("Tersedikit: ").append(nilaiTersedikit).append(" (").append(frekuensiTerdikit).append("x)").append("<br>");
        htmlResponse.append("Jumlah Tertinggi: ").append(nilaiJumlahTertinggi).append(" * ").append(frekuensiNilaiJumlahTertinggi).append(" = ").append(jumlahTertinggi).append("<br>");
        htmlResponse.append("Jumlah Terendah: ").append(nilaiJumlahTerendah).append(" * ").append(frekuensiNilaiJumlahTerendah).append(" = ").append(jumlahTerendah).append("<br>");
        
        htmlResponse.append("</div></body></html>");
        return htmlResponse.toString();

    } catch (Exception e) {
        return "<h2>Error</h2><p>Terjadi kesalahan saat memproses data: " + e.getMessage() + "</p>";
    }
}

    //======================================================================
    // HELPER METHOD: Untuk hitungNilai
    //======================================================================
    private String getGrade(double nilai) {
        if (nilai >= 79.5) return "A";
        if (nilai >= 72) return "AB";
        if (nilai >= 64.5) return "B";
        if (nilai >= 57) return "BC";
        if (nilai >= 49.5) return "C";
        if (nilai >= 34) return "D";
        return "E";
    }
}


