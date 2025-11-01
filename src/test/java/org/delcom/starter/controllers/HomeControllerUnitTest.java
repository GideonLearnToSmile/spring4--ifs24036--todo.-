package org.delcom.starter.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Arrays;
import java.util.stream.Collectors;

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

    private static Stream<Arguments> provideNimTestData() throws IOException {
        // Tentukan path ke file data, relatif terhadap root proyek
        Path path = Paths.get("src/test/java/org/delcom/starter/controllers/data-tes-nim.csv");
        
        // Baca semua baris, lewati header, dan ubah menjadi argumen tes
        return Files.lines(path)
                .skip(1) // Melewati baris header
                .map(line -> {
                    String[] parts = line.split(",");
                    return Arguments.of(parts[0].trim(), parts[1].trim());
                });
    }

    @ParameterizedTest(name = "NIM {0} harus menghasilkan prodi {1}")
    @MethodSource("provideNimTestData") // Menggunakan method di atas sebagai sumber data
    @DisplayName("getInformasiNIM() - Menguji semua kemungkinan prodi dari file")
    void getInformasiNIM_shouldCoverAllCasesFromFile(String nimInput, String expectedProdi) {
        // Arrange
        HomeController controller = new HomeController();

        // Act
        String result = controller.getInformasiNIM(nimInput);

        // Assert
        assertTrue(result.contains("<strong>Program Studi:</strong> " + expectedProdi),
                "Gagal untuk NIM: " + nimInput);
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
private static Stream<Arguments> provideHitungNilaiTestData() throws IOException {
    // PERBAIKAN: Menggunakan path relatif dari root proyek, sama seperti tes NIM.
    Path path = Paths.get("src/test/java/org/delcom/starter/controllers/data-tes-hitungnilai.csv");
    
    return Files.lines(path)
            .skip(1)
            .map(line -> {
                String[] parts = line.split(",", 5);
                String deskripsi = parts[0];
                String[] bobotStr = parts[1].split(":");
                int bobotPA = Integer.parseInt(bobotStr[0]);
                int bobotT = Integer.parseInt(bobotStr[1]);
                int bobotK = Integer.parseInt(bobotStr[2]);
                int bobotP = Integer.parseInt(bobotStr[3]);
                int bobotUTS = Integer.parseInt(bobotStr[4]);
                int bobotUAS = Integer.parseInt(bobotStr[5]);
                List<String> daftarNilai = (parts[2] == null || parts[2].trim().isEmpty()) ? Collections.emptyList() : List.of(parts[2].split(";"));
                String expectedNilaiAkhir = parts[3];
                String expectedGrade = parts[4];
                return Arguments.of(deskripsi, bobotPA, bobotT, bobotK, bobotP, bobotUTS, bobotUAS, daftarNilai, expectedNilaiAkhir, expectedGrade);
            });
}

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("provideHitungNilaiTestData")
    @DisplayName("hitungNilai() - Menguji semua skenario dari file")
    void hitungNilai_shouldCoverAllCasesFromFile(String deskripsi,
            int bobotPA, int bobotT, int bobotK, int bobotP, int bobotUTS, int bobotUAS,
            List<String> daftarNilai, String expectedNilaiAkhir, String expectedGrade) {
        
        // Arrange
        HomeController controller = new HomeController();

        // Act
        String result = controller.hitungNilai(bobotPA, bobotT, bobotK, bobotP, bobotUTS, bobotUAS, daftarNilai);

        // Assert
        assertTrue(result.contains(">> Nilai Akhir: " + expectedNilaiAkhir + "<br>"), "Nilai akhir salah untuk kasus: " + deskripsi);
        assertTrue(result.contains(">> Grade: " + expectedGrade + "<br>"), "Grade salah untuk kasus: " + deskripsi);
    }

    @Test
    @DisplayName("hitungNilai() - Menangani format angka yang salah (catch block)")
    void hitungNilai_shouldCatchExceptionForInvalidNumberFormat() {
        // Arrange
        HomeController controller = new HomeController();
        List<String> daftarNilaiError = List.of("PA|10|bukan-angka");

        // Act
        String result = controller.hitungNilai(10, 15, 10, 20, 20, 25, daftarNilaiError);

        // Assert
        assertTrue(result.contains("Terjadi kesalahan saat memproses input:") && result.contains("bukan-angka"),
                "Pesan error harus menyertakan detail dan input yang salah");
    }

    //======================================================================
    // TES UNTUK METHOD: analisisMatriks()
    //======================================================================

     private static Stream<Arguments> provideMatriksTestData() throws IOException {
        Path path = Paths.get("src/test/java/org/delcom/starter/controllers/data-tes-matriks.csv");
        return Files.lines(path)
                .skip(1) // Melewati baris header
                .map(line -> {
                    String[] parts = line.split(",");
                    
                    // Ambil kolom pertama (deskripsi) dan 4 kolom terakhir (hasil yang diharapkan)
                    String deskripsi = parts[0];
                    String expectedDominan = parts[parts.length - 1];
                    String expectedTengah = parts[parts.length - 2];
                    String expectedKebalikan = parts[parts.length - 3];
                    String expectedL = parts[parts.length - 4];
                    
                    // Gabungkan kembali semua bagian di tengah sebagai satu string data matriks
                    String inputMatriks = String.join(",", java.util.Arrays.copyOfRange(parts, 1, parts.length - 4));
                    
                    // Kembalikan sebagai argumen individual yang cocok dengan method tes
                    return Arguments.of(deskripsi, inputMatriks, expectedL, expectedKebalikan, expectedTengah, expectedDominan);
                });
    }
   // Di dalam file: HomeControllerUnitTest.java

    @ParameterizedTest(name = "[{index}] {0}") // Menggunakan deskripsi dari CSV sebagai nama tes
    @MethodSource("provideMatriksTestData")
    @DisplayName("analisisMatriks() - Menguji semua skenario matriks valid dari file")
    void analisisMatriks_shouldCoverAllCasesFromFile(String deskripsi, String inputMatriks, String expectedL, String expectedKebalikan, String expectedTengah, String expectedDominan) {
        // Arrange
        HomeController controller = new HomeController();

        // Act
        String result = controller.analisisMatriks(inputMatriks);

        // Assert
        // Logika ini menambahkan ":" jika nilai L bukan "Tidak Ada"
        String expectedLText = expectedL.equals("Tidak Ada") ? "Nilai L: " + expectedL : "Nilai L: " + expectedL + ":";

        assertTrue(result.contains(expectedLText), "Nilai L salah untuk kasus: " + deskripsi);
        assertTrue(result.contains("Nilai Kebalikan L: " + expectedKebalikan), "Nilai Kebalikan L salah untuk kasus: " + deskripsi);
        assertTrue(result.contains("Nilai Tengah: " + expectedTengah), "Nilai Tengah salah untuk kasus: " + deskripsi);
        assertTrue(result.contains("Dominan: " + expectedDominan), "Nilai Dominan salah untuk kasus: " + deskripsi);
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

    @Test
    @DisplayName("analisisMatriks() - Menangani format angka yang salah dalam data")
    void analisisMatriks_shouldCatchExceptionForInvalidNumberFormat() {
        // Arrange
        HomeController controller = new HomeController();
        String dataError = "1,2,tiga,4"; // 'tiga' akan menyebabkan error

        // Act
        String result = controller.analisisMatriks(dataError);

        // Assert
        assertTrue(result.contains("Terjadi kesalahan saat memproses data:"), "Harus menangkap exception");
    }

    //======================================================================
    // TES UNTUK METHOD: analisisNilai()
    //======================================================================

// Di dalam file: HomeControllerUnitTest.java

    private static Stream<Arguments> provideAnalisisNilaiTestData() throws IOException {
        Path path = Paths.get("src/test/java/org/delcom/starter/controllers/data-tes-analisisnilai.csv");
        return Files.lines(path)
                .skip(1) // Melewati baris header
                .map(line -> {
                    String[] parts = line.split(",");
                    String deskripsi = parts[0];
                    // Mengubah string "10;20;5" menjadi List<Integer>
                    List<Integer> inputNilai = Arrays.stream(parts[1].split(";"))
                                                      .map(Integer::parseInt)
                                                      .collect(Collectors.toList());
                    String expectedTertinggi = parts[2];
                    String expectedTerendah = parts[3];
                    String expectedTerbanyak = parts[4];
                    String expectedTersedikit = parts[5];
                    String expectedJumlahTertinggi = parts[6];
                    String expectedJumlahTerendah = parts[7];
                    
                    return Arguments.of(deskripsi, inputNilai, expectedTertinggi, expectedTerendah, 
                                        expectedTerbanyak, expectedTersedikit, expectedJumlahTertinggi, 
                                        expectedJumlahTerendah);
                });
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("provideAnalisisNilaiTestData")
    @DisplayName("analisisNilai() - Menguji semua skenario dari file")
    void analisisNilai_shouldCoverAllCasesFromFile(String deskripsi, List<Integer> daftarNilai,
            String expectedTertinggi, String expectedTerendah, String expectedTerbanyak,
            String expectedTersedikit, String expectedJumlahTertinggi, String expectedJumlahTerendah) {
        
        // Arrange
        HomeController controller = new HomeController();

        // Act
        String result = controller.analisisNilai(daftarNilai);

        // Assert
        assertTrue(result.contains("Tertinggi: " + expectedTertinggi), "Nilai tertinggi salah untuk kasus: " + deskripsi);
        assertTrue(result.contains("Terendah: " + expectedTerendah), "Nilai terendah salah untuk kasus: " + deskripsi);
        assertTrue(result.contains("Terbanyak: " + expectedTerbanyak), "Nilai terbanyak salah untuk kasus: " + deskripsi);
        assertTrue(result.contains("Tersedikit: " + expectedTersedikit), "Nilai tersedikit salah untuk kasus: " + deskripsi);
        assertTrue(result.contains("Jumlah Tertinggi: " + expectedJumlahTertinggi), "Jumlah tertinggi salah untuk kasus: " + deskripsi);
        assertTrue(result.contains("Jumlah Terendah: " + expectedJumlahTerendah), "Jumlah terendah salah untuk kasus: " + deskripsi);
    }

    @Test
    @DisplayName("analisisNilai() - Mengembalikan error untuk daftar kosong")
    void analisisNilai_shouldReturnErrorForEmptyList() {
        HomeController controller = new HomeController();
        List<Integer> daftarNilai = Collections.emptyList();
        String result = controller.analisisNilai(daftarNilai);
        assertTrue(result.contains("Tidak ada data nilai yang diberikan"), "Harus ada pesan error untuk list kosong");
    }

        @Test
    @DisplayName("analisisNilai() - Mengembalikan error untuk daftar null")
    void analisisNilai_shouldReturnErrorForNullList() {
        // Arrange
        HomeController controller = new HomeController();
        
        // Act
        String result = controller.analisisNilai(null);
        
        // Assert
        assertTrue(result.contains("Tidak ada data nilai yang diberikan"), "Harus ada pesan error untuk list null");
    }

    @Test
    @DisplayName("analisisNilai() - Menangkap exception jika ada elemen null di dalam list")
    void analisisNilai_shouldCatchExceptionForListWithNullElement() {
        // Arrange
        HomeController controller = new HomeController();
        // List.of() tidak mengizinkan null, jadi kita gunakan Arrays.asList()
        List<Integer> daftarNilaiDenganNull = java.util.Arrays.asList(10, 20, null, 30);
        
        // Act
        String result = controller.analisisNilai(daftarNilaiDenganNull);
        
        // Assert
        assertTrue(result.contains("Terjadi kesalahan saat memproses data:"), "Harus menangkap exception dan mengembalikan pesan error");
    }

    
}