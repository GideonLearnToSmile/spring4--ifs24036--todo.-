package org.delcom.starter.controllers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
public class AnalisisNilaiController {

    // Helper record to store a number and its frequency.
    // Ini adalah cara modern dan ringkas untuk membuat kelas data sederhana.
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
}
