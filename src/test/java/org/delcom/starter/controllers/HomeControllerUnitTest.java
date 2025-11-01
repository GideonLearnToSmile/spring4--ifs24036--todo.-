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
        // Arrange
        HomeController controller = new HomeController();
        // Act
        String result = controller.hello();
        // Assert
        assertEquals("Hay Abdullah, selamat datang di pengembangan aplikasi dengan Spring Boot!", result);
    }

    @Test
    @DisplayName("sayHello() - Mengembalikan pesan sapaan yang dipersonalisasi")
    void sayHello_ShouldReturnPersonalizedGreeting() {
        // Arrange
        HomeController controller = new HomeController();
        // Act
        String result = controller.sayHello("Abdullah");
        // Assert
        assertEquals("Hello, Abdullah!", result);
    }

    //======================================================================
    // TES UNTUK METHOD: getInformasiNIM()
    //======================================================================

    @Test
    @DisplayName("getInformasiNIM() - Mengembalikan data yang benar untuk NIM valid")
    void getInformasiNIM_shouldReturnCorrectDataForValidNim() {
        // Arrange
        HomeController controller = new HomeController();
        String nim = "11S21042"; // Informatika, 2021, urutan 42
        // Act
        String result = controller.getInformasiNIM(nim);
        // Assert
        assertTrue(result.contains("Sarjana Informatika"), "Harus mengandung prodi yang benar");
        assertTrue(result.contains("Angkatan: 2021"), "Harus mengandung angkatan yang benar");
        assertTrue(result.contains("Urutan: 42"), "Harus mengandung nomor urut yang benar");
    }

    @Test
    @DisplayName("getInformasiNIM() - Mengembalikan pesan error untuk format NIM salah")
    void getInformasiNIM_shouldReturnErrorForInvalidFormat() {
        // Arrange
        HomeController controller = new HomeController();
        String nim = "11S21"; // Terlalu pendek
        // Act
        String result = controller.getInformasiNIM(nim);
        // Assert
        assertTrue(result.contains("Format NIM '11S21' tidak valid."), "Harus mengembalikan pesan error format");
    }

    //======================================================================
    // TES UNTUK METHOD: hitungNilai()
    //======================================================================

    @Test
    @DisplayName("hitungNilai() - Menghitung nilai akhir dan grade dengan benar")
    void hitungNilai_shouldCalculateFinalScoreAndGradeCorrectly() {
        // Arrange
        HomeController controller = new HomeController();
        List<String> daftarNilai = List.of(
            "PA|10|9",   // 90%
            "T|20|18",   // 90%
            "K|10|7",    // 70%
            "P|20|16",   // 80%
            "UTS|100|85",// 85%
            "UAS|100|80" // 80%
        );
        // Act
        String result = controller.hitungNilai(10, 15, 10, 20, 20, 25, daftarNilai);
        // Assert
        // Perhitungan manual: (0.1*90)+(0.15*90)+(0.1*70)+(0.2*80)+(0.2*85)+(0.25*80) = 82.5
        assertTrue(result.contains("Nilai Akhir: 82.50"), "Harus menghitung nilai akhir yang benar");
        assertTrue(result.contains("Grade: A"), "Harus memberikan grade yang benar");
    }

    //======================================================================
    // TES UNTUK METHOD: analisisMatriks()
    //======================================================================

    @Test
    @DisplayName("analisisMatriks() - Menganalisis matriks 3x3 dengan benar")
    void analisisMatriks_shouldAnalyze3x3MatrixCorrectly() {
        // Arrange
        HomeController controller = new HomeController();
        String data = "1,2,3,4,5,6,7,8,9";
        // Act
        String result = controller.analisisMatriks(data);
        // Assert
        assertTrue(result.contains("Nilai L: 12"), "Nilai L harus benar");
        assertTrue(result.contains("Nilai Kebalikan L: 18"), "Nilai Kebalikan L harus benar");
        assertTrue(result.contains("Nilai Tengah: 5"), "Nilai Tengah harus benar");
        assertTrue(result.contains("Dominan: 18"), "Nilai Dominan harus benar");
    }

    @Test
    @DisplayName("analisisMatriks() - Mengembalikan error untuk data non-persegi")
    void analisisMatriks_shouldReturnErrorForNonSquareData() {
        // Arrange
        HomeController controller = new HomeController();
        String data = "1,2,3,4,5";
        // Act
        String result = controller.analisisMatriks(data);
        // Assert
        assertTrue(result.contains("tidak membentuk matriks persegi"), "Harus ada pesan error untuk data non-persegi");
    }

    //======================================================================
    // TES UNTUK METHOD: analisisNilai()
    //======================================================================

    @Test
    @DisplayName("analisisNilai() - Menganalisis daftar nilai dengan benar")
    void analisisNilai_shouldAnalyzeListOfNumbersCorrectly() {
        // Arrange
        HomeController controller = new HomeController();
        List<Integer> daftarNilai = List.of(10, 20, 10, 30, 20, 10, 5);
        // Act
        String result = controller.analisisNilai(daftarNilai);
        // Assert
        assertTrue(result.contains("Tertinggi         : 30"), "Nilai tertinggi harus benar");
        assertTrue(result.contains("Terendah          : 5"), "Nilai terendah harus benar");
        assertTrue(result.contains("Terbanyak         : 10 (3x)"), "Nilai terbanyak harus benar");
        assertTrue(result.contains("Tersedikit        : 5 (1x)"), "Nilai tersedikit harus benar");
        assertTrue(result.contains("Jumlah Tertinggi  : 20 * 2 = 40"), "Jumlah tertinggi harus benar");
        assertTrue(result.contains("Jumlah Terendah   : 5 * 1 = 5"), "Jumlah terendah harus benar");
    }

    @Test
    @DisplayName("analisisNilai() - Mengembalikan error untuk daftar kosong")
    void analisisNilai_shouldReturnErrorForEmptyList() {
        // Arrange
        HomeController controller = new HomeController();
        List<Integer> daftarNilai = Collections.emptyList();
        // Act
        String result = controller.analisisNilai(daftarNilai);
        // Assert
        assertTrue(result.contains("Tidak ada data nilai yang diberikan"), "Harus ada pesan error untuk list kosong");
    }
}