
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

@GetMapping("/info-nim/{nim}")
    public String getInformasiNIM(@PathVariable String nim) {
        
        String namaProdi;
        String angkatan;
        int nomorUrut;

        // Melakukan validasi sederhana dan memproses NIM
        try {
            // Logika inti untuk memecah NIM (sama persis seperti sebelumnya)
            String kodeProdi = nim.substring(0, 3);
            String kodeAngkatan = nim.substring(3, 5);
            String strNomorUrut = nim.substring(5);

            switch (kodeProdi) {
                case "11S": namaProdi = "Sarjana Informatika"; break;
                case "12S": namaProdi = "Sarjana Sistem Informasi"; break;
                case "14S": namaProdi = "Sarjana Teknik Elektro"; break;
                case "21S": namaProdi = "Sarjana Manajemen Rekayas"; break;
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
            // Jika format NIM salah (misal: terlalu pendek), kembalikan pesan error
            return "<h2>Error</h2><p>Format NIM '" + nim + "' tidak valid.</p>";
        }

        // Membangun string HTML sebagai respons untuk ditampilkan di browser
        // Spring akan otomatis mengatur Content-Type ke text/html
        return String.format(
            """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Informasi NIM</title>
                <style>
                    body { font-family: sans-serif; margin: 2em; }
                    div { border: 1px solid #ccc; padding: 1em; border-radius: 8px; max-width: 600px; }
                    h2, p { margin: 0.5em 0; }
                </style>
            </head>
            <body>
                <div>
                    <h2>Informasi NIM %s</h2>
                    <p><strong>Program Studi:</strong> %s</p>
                    <p><strong>Angkatan:</strong> %s</p>
                    <p><strong>Urutan:</strong> %d</p>
                </div>
            </body>
            </html>
            """,
            nim, namaProdi, angkatan, nomorUrut
        );
    }

        private record NilaiFrekuensi(int nilai, int frekuensi) {}

    @GetMapping("/analisis-nilai")
    public String analisisNilai(
            // Menerima beberapa parameter 'nilai' dan Spring akan mengubahnya menjadi List
            @RequestParam("nilai") List<Integer> daftarNilai) {
        
        // Cek jika input kosong
        if (daftarNilai == null || daftarNilai.isEmpty()) {
            return "<h2>Error</h2><p>Tidak ada data nilai yang diberikan. Coba URL seperti: /analisis-nilai?nilai=10&nilai=20&nilai=10</p>";
        }

        try {
            // --- Re-implementasi Logika Tanpa HashMap ---

            // 1. Hitung Nilai Tertinggi dan Terendah
            // Cara mudah menggunakan Collections, ini tidak menggunakan HashMap.
            int nilaiTertinggi = Collections.max(daftarNilai);
            int nilaiTerendah = Collections.min(daftarNilai);

            // 2. Hitung Frekuensi Setiap Angka
            // Sortir daftar untuk mengelompokkan angka yang sama secara berurutan.
            Collections.sort(daftarNilai);
            
            List<NilaiFrekuensi> daftarFrekuensi = new ArrayList<>();
            if (!daftarNilai.isEmpty()) {
                int angkaSaatIni = daftarNilai.get(0);
                int hitunganSaatIni = 1;

                for (int i = 1; i < daftarNilai.size(); i++) {
                    if (daftarNilai.get(i) == angkaSaatIni) {
                        hitunganSaatIni++;
                    } else {
                        // Angka berubah, simpan hasil sebelumnya
                        daftarFrekuensi.add(new NilaiFrekuensi(angkaSaatIni, hitunganSaatIni));
                        // Reset untuk angka baru
                        angkaSaatIni = daftarNilai.get(i);
                        hitunganSaatIni = 1;
                    }
                }
                // Simpan data frekuensi untuk angka terakhir di dalam list
                daftarFrekuensi.add(new NilaiFrekuensi(angkaSaatIni, hitunganSaatIni));
            }

            // 3. Cari Nilai Terbanyak dan Tersedikit dari daftarFrekuensi
            int nilaiTerbanyak = -1;
            int frekuensiTerbanyak = 0;
            int nilaiTersedikit = -1;
            int frekuensiTerdikit = Integer.MAX_VALUE;

            if (!daftarFrekuensi.isEmpty()) {
                for (NilaiFrekuensi nf : daftarFrekuensi) {
                    // Cari yang terbanyak (jika frekuensi sama, yang nilainya lebih kecil menang karena data sudah disortir)
                    if (nf.frekuensi() > frekuensiTerbanyak) {
                        frekuensiTerbanyak = nf.frekuensi();
                        nilaiTerbanyak = nf.nilai();
                    }
                    // Cari yang tersedikit (jika frekuensi sama, yang nilainya lebih kecil menang)
                    if (nf.frekuensi() < frekuensiTerdikit) {
                        frekuensiTerdikit = nf.frekuensi();
                        nilaiTersedikit = nf.nilai();
                    }
                }
            }
            
            // 4. Hitung Jumlah Tertinggi dan Jumlah Terendah
            int nilaiJumlahTertinggi = -1;
            int frekuensiNilaiJumlahTertinggi = 0;
            int jumlahTertinggi = -1;

            int nilaiJumlahTerendah = -1;
            int frekuensiNilaiJumlahTerendah = 0;
            int jumlahTerendah = Integer.MAX_VALUE;

            if (!daftarFrekuensi.isEmpty()) {
                for (NilaiFrekuensi nf : daftarFrekuensi) {
                    int jumlah = nf.nilai() * nf.frekuensi();
                    
                    // Logika Jumlah Tertinggi: Jika jumlah sama, pilih nilai yang lebih besar
                    if (jumlah > jumlahTertinggi || (jumlah == jumlahTertinggi && nf.nilai() > nilaiJumlahTertinggi)) {
                        jumlahTertinggi = jumlah;
                        nilaiJumlahTertinggi = nf.nilai();
                        frekuensiNilaiJumlahTertinggi = nf.frekuensi();
                    }

                    // Logika Jumlah Terendah: Jika jumlah sama, pilih nilai yang lebih kecil
                    if (jumlah < jumlahTerendah || (jumlah == jumlahTerendah && nf.nilai() < nilaiJumlahTerendah)) {
                        jumlahTerendah = jumlah;
                        nilaiJumlahTerendah = nf.nilai();
                        frekuensiNilaiJumlahTerendah = nf.frekuensi();
                    }
                }
            }


            // --- Membangun output HTML ---
            StringBuilder htmlResponse = new StringBuilder();
            htmlResponse.append("<!DOCTYPE html><html><head><title>Hasil Analisis Nilai</title>");
            htmlResponse.append("<style>body { font-family: monospace; white-space: pre; margin: 2em; } div { border: 1px solid #ccc; padding: 1em; border-radius: 8px; } </style>");
            htmlResponse.append("</head><body><div>");

            htmlResponse.append("<h2>Data Input:</h2>");
            htmlResponse.append(daftarNilai.toString()).append("<hr>");

            htmlResponse.append("<h2>Hasil Analisis:</h2>");
            htmlResponse.append("Tertinggi         : ").append(nilaiTertinggi).append("<br>");
            htmlResponse.append("Terendah          : ").append(nilaiTerendah).append("<br>");
            htmlResponse.append("Terbanyak         : ").append(nilaiTerbanyak).append(" (").append(frekuensiTerbanyak).append("x)").append("<br>");
            htmlResponse.append("Tersedikit        : ").append(nilaiTersedikit).append(" (").append(frekuensiTerdikit).append("x)").append("<br>");
            htmlResponse.append("Jumlah Tertinggi  : ").append(nilaiJumlahTertinggi).append(" * ").append(frekuensiNilaiJumlahTertinggi).append(" = ").append(jumlahTertinggi).append("<br>");
            htmlResponse.append("Jumlah Terendah   : ").append(nilaiJumlahTerendah).append(" * ").append(frekuensiNilaiJumlahTerendah).append(" = ").append(jumlahTerendah).append("<br>");

            htmlResponse.append("</div></body></html>");
            return htmlResponse.toString();

        } catch (Exception e) {
            return "<h2>Error</h2><p>Terjadi kesalahan saat memproses data: " + e.getMessage() + "</p>";
        }
    }

    @GetMapping("/analisis-matriks")
    public String analisisMatriks(
            // Menerima semua elemen matriks sebagai satu String, dipisahkan oleh koma
            @RequestParam("data") String dataMatriks) {

        try {
            // Memisahkan string data menjadi array string individu
            String[] elemen = dataMatriks.split(",");

            // Menentukan ukuran matriks (n x n)
            int totalElemen = elemen.length;
            double nDouble = Math.sqrt(totalElemen);
            if (nDouble != Math.floor(nDouble)) {
                return "<h2>Error</h2><p>Jumlah elemen data (" + totalElemen + ") tidak membentuk matriks persegi (bukan kuadrat sempurna).</p>";
            }
            int n = (int) nDouble;

            // Membangun matriks 2D dari input
            int[][] matriks = new int[n][n];
            int index = 0;
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    matriks[i][j] = Integer.parseInt(elemen[index++].trim());
                }
            }

            // --- Logika Inti dari StudiKasus3 (disalin dan diadaptasi) ---
            String nilaiLStr = "Tidak Ada";
            String nilaiKebalikanStr = "Tidak Ada";
            String nilaiTengahStr = "Tidak Ada";
            String perbedaanStr = "Tidak Ada";
            int dominan = 0;

            // Nilai tengah (umum)
            if (n == 1) {
                int center = matriks[0][0];
                nilaiTengahStr = String.valueOf(center);
                dominan = center;
            } else {
                if (n % 2 == 1) {
                    int center = matriks[n / 2][n / 2];
                    nilaiTengahStr = String.valueOf(center);
                } else {
                    int mid = n / 2;
                    int centerSum = matriks[mid - 1][mid - 1] + matriks[mid - 1][mid] + matriks[mid][mid - 1] + matriks[mid][mid];
                    nilaiTengahStr = String.valueOf(centerSum);
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

                int perbedaan = Math.abs(nilaiL - nilaiKebalikan);
                perbedaanStr = String.valueOf(perbedaan);

                if (nilaiL > nilaiKebalikan) {
                    dominan = nilaiL;
                } else if (nilaiKebalikan > nilaiL) {
                    dominan = nilaiKebalikan;
                } else {
                    dominan = Integer.parseInt(nilaiTengahStr);
                }
            }

            // --- Membangun output HTML ---
            StringBuilder htmlResponse = new StringBuilder();
            htmlResponse.append("<!DOCTYPE html><html><head><title>Hasil Analisis Matriks</title>");
            htmlResponse.append("<style>body { font-family: monospace; white-space: pre; margin: 2em; } div { border: 1px solid #ccc; padding: 1em; border-radius: 8px; } </style>");
            htmlResponse.append("</head><body><div>");
            
            // Mencetak matriks input untuk verifikasi
            htmlResponse.append("<h2>Matriks Input (" + n + "x" + n + "):</h2>");
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    htmlResponse.append(String.format("%-4d", matriks[i][j]));
                }
                htmlResponse.append("<br>");
            }
            htmlResponse.append("<hr>");

            htmlResponse.append("<h2>Hasil Analisis:</h2>");
            // Logika khusus untuk "Nilai L:" vs "Nilai L: [nilai]:"
            if (nilaiLStr.equals("Tidak Ada")) {
                htmlResponse.append("Nilai L: " + nilaiLStr + "<br>");
            } else {
                htmlResponse.append("Nilai L: " + nilaiLStr + ":<br>");
            }
            htmlResponse.append("Nilai Kebalikan L: " + nilaiKebalikanStr + "<br>");
            htmlResponse.append("Nilai Tengah: " + nilaiTengahStr + "<br>");
            htmlResponse.append("Perbedaan: " + perbedaanStr + "<br>");
            htmlResponse.append("Dominan: " + dominan + "<br>");

            htmlResponse.append("</div></body></html>");
            return htmlResponse.toString();

        } catch (NumberFormatException e) {
            return "<h2>Error</h2><p>Data input mengandung karakter yang bukan angka. Pastikan semua elemen adalah integer.</p>";
        } catch (Exception e) {
            return "<h2>Error</h2><p>Terjadi kesalahan saat memproses data: " + e.getMessage() + "</p>";
        }
    }

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


