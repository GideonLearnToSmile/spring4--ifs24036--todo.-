package org.delcom.starter.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HomeControllerUnitTest {

    //======================================================================
    // TES UNTUK METHOD: hello() dan sayHello() (Sudah Ada)
    //======================================================================

    @Test
    @DisplayName("hello() - Mengembalikan pesan selamat datang yang benar")
    void hello_ShouldReturnWelcomeMessage() {
        HomeController controller = new HomeController();
        String result = controller.hello();
        assertEquals("Hay Abdullah, selamat datang di pengembangan aplikasi dengan Spring Boot!", result);
    }

    @Test
    @DisplayName("sayHello() - Mengembalikan pesan sapaan yang dipersonalisasi")
    void sayHello_ShouldReturnPersonalizedGreeting() {
        HomeController controller = new HomeController();
        String result = controller.sayHello("Abdullah");
        assertEquals("Hello, Abdullah!", result);
    }

    //======================================================================
    // TES UNTUK METHOD: getInformasiNIM()
    //======================================================================

    @Test
    @DisplayName("getInformasiNIM() - Mengembalikan data yang benar untuk NIM valid")
    void getInformasiNIM_shouldReturnCorrectDataForValidNim() {
        HomeController controller = new HomeController();
        String nim = "11S21042";
        String result = controller.getInformasiNIM(nim);
        
        // PERBAIKAN: Dibuat lebih spesifik agar cocok dengan output HTML
        assertTrue(result.contains("<strong>Program Studi:</strong> Sarjana Informatika"), "Harus mengandung prodi yang benar");
        assertTrue(result.contains("<strong>Angkatan:</strong> 2021"), "Harus mengandung angkatan yang benar");
        assertTrue(result.contains("<strong>Urutan:</strong> 42"), "Harus mengandung nomor urut yang benar");
    }

    @Test
    @DisplayName("getInformasiNIM() - Mengembalikan pesan error untuk format NIM salah")
    void getInformasiNIM_shouldReturnErrorForInvalidFormat() {
        HomeController controller = new HomeController();
        String nim = "11S21";
        String result = controller.getInformasiNIM(nim);
        assertTrue(result.contains("Format NIM '11S21' tidak valid."), "Harus mengembalikan pesan error format");
    }

    //======================================================================
    // TES UNTUK METHOD: hitungNilai()
    //======================================================================

    @Test
    @DisplayName("hitungNilai() - Menghitung nilai akhir dan grade dengan benar")
    void hitungNilai_shouldCalculateFinalScoreAndGradeCorrectly() {
        HomeController controller = new HomeController();
        List<String> daftarNilai = List.of("PA|10|9", "T|20|18", "K|10|7", "P|20|16", "UTS|100|85", "UAS|100|80");
        String result = controller.hitungNilai(10, 15, 10, 20, 20, 25, daftarNilai);
        assertTrue(result.contains(">> Nilai Akhir: 82.50<br>"), "Harus menghitung nilai akhir yang benar");
        assertTrue(result.contains(">> Grade: A<br>"), "Harus memberikan grade yang benar");
    }

    //======================================================================
    // TES UNTUK METHOD: analisisMatriks()
    //======================================================================

   @Test
@DisplayName("analisisMatriks() - Menganalisis matriks 3x3 dengan benar")
void analisisMatriks_shouldAnalyze3x3MatrixCorrectly() {
    HomeController controller = new HomeController();
    String data = "1,2,3,4,5,6,7,8,9";
    String result = controller.analisisMatriks(data);
    
    // PERBAIKAN: Menggunakan nilai yang benar hasil perhitungan ulang
    assertTrue(result.contains("Nilai L: 20:<br>"), "Nilai L harus benar");
    assertTrue(result.contains("Nilai Kebalikan L: 20<br>"), "Nilai Kebalikan L harus benar");
    assertTrue(result.contains("Nilai Tengah: 5<br>"), "Nilai Tengah harus benar");
    assertTrue(result.contains("Dominan: 5<br>"), "Nilai Dominan harus benar");
}

    @Test
    @DisplayName("analisisMatriks() - Mengembalikan error untuk data non-persegi")
    void analisisMatriks_shouldReturnErrorForNonSquareData() {
        HomeController controller = new HomeController();
        String data = "1,2,3,4,5";
        String result = controller.analisisMatriks(data);
        assertTrue(result.contains("tidak membentuk matriks persegi"), "Harus ada pesan error untuk data non-persegi");
    }

    //======================================================================
    // TES UNTUK METHOD: analisisNilai()
    //======================================================================

    @Test
    @DisplayName("analisisNilai() - Menganalisis daftar nilai dengan benar")
    void analisisNilai_shouldAnalyzeListOfNumbersCorrectly() {
        HomeController controller = new HomeController();
        List<Integer> daftarNilai = List.of(10, 20, 10, 30, 20, 10, 5);
        String result = controller.analisisNilai(daftarNilai);
        
        // PERBAIKAN: Menyesuaikan jumlah spasi agar sama persis dengan output controller
        assertTrue(result.contains("Tertinggi         : 30<br>"), "Nilai tertinggi harus benar");
        assertTrue(result.contains("Terendah          : 5<br>"), "Nilai terendah harus benar");
        assertTrue(result.contains("Terbanyak         : 10 (3x)<br>"), "Nilai terbanyak harus benar");
        assertTrue(result.contains("Tersedikit        : 5 (1x)<br>"), "Nilai tersedikit harus benar");
        assertTrue(result.contains("Jumlah Tertinggi  : 20 * 2 = 40<br>"), "Jumlah tertinggi harus benar");
        assertTrue(result.contains("Jumlah Terendah   : 5 * 1 = 5<br>"), "Jumlah terendah harus benar");
    }

    @Test
    @DisplayName("analisisNilai() - Mengembalikan error untuk daftar kosong")
    void analisisNilai_shouldReturnErrorForEmptyList() {
        HomeController controller = new HomeController();
        List<Integer> daftarNilai = Collections.emptyList();
        String result = controller.analisisNilai(daftarNilai);
        assertTrue(result.contains("Tidak ada data nilai yang diberikan"), "Harus ada pesan error untuk list kosong");
    }
}