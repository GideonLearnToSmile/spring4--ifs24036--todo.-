package org.delcom.starter.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc; // Objek untuk mensimulasikan permintaan HTTP ke controller

    //======================================================================
    // TES UNTUK METHOD: getInformasiNIM
    //======================================================================

    /**
     * Menguji endpoint /info-nim/{nim} dengan input NIM yang valid.
     * Ini akan membuat blok 'try' dan 'switch' menjadi hijau.
     */
    @Test
    void testGetInformasiNIM_Success() throws Exception {
        String nimValid = "12S22050"; // Contoh: Sarjana Sistem Informasi, angkatan 2022, urutan 50
        mockMvc.perform(get("/info-nim/" + nimValid))
                .andExpect(status().isOk()) // Mengharapkan status HTTP 200 OK
                .andExpect(content().string(containsString("Sarjana Sistem Informasi")))
                .andExpect(content().string(containsString("Angkatan: 2022")))
                .andExpect(content().string(containsString("Urutan: 50")));
    }

    /**
     * Menguji endpoint /info-nim/{nim} dengan format NIM yang salah.
     * Ini akan membuat blok 'catch' menjadi hijau.
     */
    @Test
    void testGetInformasiNIM_InvalidFormat() throws Exception {
        String nimSalah = "12S21"; // NIM terlalu pendek, akan menyebabkan error
        mockMvc.perform(get("/info-nim/" + nimSalah))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Format NIM '12S21' tidak valid")));
    }


    //======================================================================
    // TES UNTUK METHOD: hitungNilai
    //======================================================================

    /**
     * Menguji endpoint /hitung-nilai dengan input yang lengkap dan valid.
     */
    @Test
    void testHitungNilai_Success() throws Exception {
        mockMvc.perform(get("/hitung-nilai")
                        // Menyertakan semua parameter bobot
                        .param("bobotPA", "10").param("bobotT", "15")
                        .param("bobotK", "10").param("bobotP", "20")
                        .param("bobotUTS", "20").param("bobotUAS", "25")
                        // Menyertakan beberapa parameter 'nilai'
                        .param("nilai", "PA|10|9")      // Rata-rata 90
                        .param("nilai", "T|20|18")      // Rata-rata 90
                        .param("nilai", "UTS|100|85")   // Rata-rata 85
                        .param("nilai", "UAS|100|80"))  // Rata-rata 80
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Nilai Akhir:")))
                .andExpect(content().string(containsString("Grade: A"))); // Berdasarkan perhitungan
    }


    //======================================================================
    // TES UNTUK METHOD: analisisMatriks
    //======================================================================

    /**
     * Menguji endpoint /analisis-matriks dengan matriks 3x3 (ganjil).
     */
    @Test
    void testAnalisisMatriks_OddSize() throws Exception {
        String dataMatriks = "1,2,3,4,5,6,7,8,9";
        mockMvc.perform(get("/analisis-matriks").param("data", dataMatriks))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Nilai L: 12")))
                .andExpect(content().string(containsString("Nilai Kebalikan L: 18")))
                .andExpect(content().string(containsString("Nilai Tengah: 5")))
                .andExpect(content().string(containsString("Dominan: 18")));
    }
    
    /**
     * Menguji endpoint /analisis-matriks dengan data yang tidak membentuk matriks persegi.
     */
    @Test
    void testAnalisisMatriks_InvalidSize() throws Exception {
        String dataMatriks = "1,2,3,4,5"; // 5 elemen tidak bisa jadi matriks persegi
        mockMvc.perform(get("/analisis-matriks").param("data", dataMatriks))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("tidak membentuk matriks persegi")));
    }


    //======================================================================
    // TES UNTUK METHOD: analisisNilai
    //======================================================================
    
    /**
     * Menguji endpoint /analisis-nilai dengan serangkaian data.
     */
    @Test
    void testAnalisisNilai_Success() throws Exception {
        mockMvc.perform(get("/analisis-nilai")
                        .param("nilai", "10").param("nilai", "20")
                        .param("nilai", "10").param("nilai", "30")
                        .param("nilai", "20").param("nilai", "10")
                        .param("nilai", "5"))
                .andExpect(status().isOk())
                // Verifikasi hasil analisis berdasarkan input di atas
                .andExpect(content().string(containsString("Tertinggi         : 30")))
                .andExpect(content().string(containsString("Terendah          : 5")))
                .andExpect(content().string(containsString("Terbanyak         : 10 (3x)")))
                .andExpect(content().string(containsString("Tersedikit        : 5 (1x)")))
                .andExpect(content().string(containsString("Jumlah Tertinggi  : 20 * 2 = 40")))
                .andExpect(content().string(containsString("Jumlah Terendah   : 5 * 1 = 5")));
    }
}