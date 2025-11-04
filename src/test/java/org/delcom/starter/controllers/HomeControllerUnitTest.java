package org.delcom.starter.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class HomeControllerUnitTest {

    private final HomeController controller = new HomeController();

    private String encodeFileToBase64(String filePath) throws IOException {
        Path path = Paths.get("src/test/resources/" + filePath);
        assertTrue(Files.exists(path), "File tes tidak ditemukan: " + path.toAbsolutePath());
        byte[] fileBytes = Files.readAllBytes(path);
        return Base64.getEncoder().encodeToString(fileBytes);
    }

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
    // TES UNTUK METHOD DASAR
    //======================================================================
    @Test @DisplayName("hello() - Mengembalikan pesan selamat datang")
    void hello_ShouldReturnWelcomeMessage() {
        assertEquals("Hay Gideon, selamat datang di pengembangan aplikasi dengan Spring Boot!", controller.hello());
    }

        @Test
    @DisplayName("sayHello() - Mengembalikan pesan sapaan yang dipersonalisasi")
    void sayHello_ShouldReturnPersonalizedGreeting() {
        HomeController controller = new HomeController();
        String result = controller.sayHello("Abdullah");
        assertEquals("Hello, Abdullah!", result);
    }
    //======================================================================
    // TES UNTUK METHOD: perolehanNilai()
    //======================================================================
     private static Stream<Arguments> providePerolehanNilaiTestData() {
        return Stream.of(
            // --- Tes Kasus Valid dan Edge Case ---
            Arguments.of("Kasus Lengkap", "testdata/nilai-kasus-lengkap.txt", "83.70", "A"),
            Arguments.of("Kasus dari Gambar (Tanpa Proyek)", "testdata/nilai-dari-gambar.txt", "29.93", "E"),
            Arguments.of("Kasus Hanya UAS (max... == 0)", "testdata/nilai-hanya-uas.txt", "20.00", "E"),
            Arguments.of("Kasus dengan Maksimum Nol", "testdata/nilai-maks-nol.txt", "13.50", "E"),
            Arguments.of("Mendapatkan Grade AB", "testdata/nilai-grade-ab.txt", "75.00", "AB"),
            Arguments.of("Mendapatkan Grade B", "testdata/nilai-grade-b.txt", "70.00", "B"),
            Arguments.of("Mendapatkan Grade BC", "testdata/nilai-grade-bc.txt", "60.00", "BC"),
            Arguments.of("Mendapatkan Grade C", "testdata/nilai-grade-c.txt", "50.00", "C"),
            // ==========================================================
            // KASUS UJI BARU UNTUK PERCABANGAN YANG DITANDAI KUNING
            // ==========================================================
            Arguments.of("Mengabaikan Baris Kosong", "testdata/nilai-dengan-baris-kosong.txt", "44.00", "D"),
            Arguments.of("Hanya Bobot (Loop Kosong)", "testdata/nilai-hanya-bobot.txt", "0.00", "E"),
            Arguments.of("Mengabaikan Format Baris Salah", "testdata/nilai-format-baris-salah.txt", "16.00", "E"),
            Arguments.of("Mengabaikan Simbol Tidak Dikenal", "testdata/nilai-simbol-tidak-dikenal.txt", "30.50", "E")
        );
    }

    @ParameterizedTest(name = "[perolehanNilai] {0}")
    @MethodSource("providePerolehanNilaiTestData")
    @DisplayName("perolehanNilai() - Menguji semua cabang logika dari file TXT")
    void perolehanNilai_shouldProcessAllLogicBranchesFromFile(String deskripsi, String filePath, String expectedNilaiAkhir, String expectedGrade) throws IOException {
        String base64Input = encodeFileToBase64(filePath);
        String result = controller.perolehanNilai(base64Input);

        Pattern pattern = Pattern.compile(">> Nilai Akhir: (\\d+\\.\\d{2})<br>");
        Matcher matcher = pattern.matcher(result);

        if (matcher.find()) {
            String actualNilaiAkhir = matcher.group(1);
            assertEquals(expectedNilaiAkhir, actualNilaiAkhir, "Nilai akhir salah untuk: " + deskripsi);
        } else {
            fail("Format output 'Nilai Akhir' tidak ditemukan dalam respons HTML untuk kasus: " + deskripsi + ". Output aktual: " + result);
        }

        assertTrue(result.contains(">> Grade: " + expectedGrade), "Grade salah untuk: " + deskripsi);
    }

    //======================================================================
    // TES UNTUK METHOD: perolehanNilai() - KASUS ERROR EKSPLISIT
    //======================================================================
    @Test
    @DisplayName("perolehanNilai() - [ERROR] Menguji cabang 'if (lines.size() < 6)'")
    void perolehanNilai_shouldReturnErrorForIncompleteBobot() throws IOException {
        String base64Input = encodeFileToBase64("testdata/nilai-error-kurang-bobot.txt");
        String result = controller.perolehanNilai(base64Input);
        assertTrue(result.contains("Data tidak lengkap. Diperlukan minimal 6 baris untuk bobot."),
                   "Pesan error untuk bobot tidak lengkap tidak sesuai.");
    }

    @Test
    @DisplayName("perolehanNilai() - [CATCH] Menguji cabang 'catch (Exception e)' dengan format bobot salah")
    void perolehanNilai_shouldCatchErrorForInvalidBobotFormat() throws IOException {
        String base64Input = encodeFileToBase64("testdata/nilai-error-format-bobot.txt");
        String result = controller.perolehanNilai(base64Input);
        assertTrue(result.contains("Terjadi kesalahan saat memproses input Base64"),
                   "Blok catch seharusnya dieksekusi.");
        assertTrue(result.contains("For input string: \"sepuluh\""),
                   "Pesan error harus menyertakan detail dari NumberFormatException.");
    }
    //======================================================================
    // TES UNTUK METHOD LAINNYA (TIDAK ADA PERUBAHAN)
    //======================================================================
 private static Stream<Arguments> providePerbedaanLTestData() {
        return Stream.of(
            // --- Menguji cabang ukuran matriks ---
            Arguments.of("Matriks 1x1", "testdata/matriks-1x1.txt", "Tidak Ada", "Tidak Ada", "42", "42"),
            Arguments.of("Matriks 2x2", "testdata/matriks-2x2.txt", "Tidak Ada", "Tidak Ada", "100", "100"),
            
            // --- Menguji cabang dominan (L > K, K > L, L == K) dan ukuran >= 3 ---
            Arguments.of("Matriks 3x3 (L > K)", "testdata/matriks-kebalikan-dominan.txt", "19", "21", "5", "21"), // Sebenarnya ini K > L
            Arguments.of("Matriks 4x4 (L > K)", "testdata/matriks-4x4.txt", "66", "30", "38", "66"),
            Arguments.of("Matriks Kebalikan Dominan (K > L)", "testdata/matriks-tie-dominan.txt", "21", "19", "5", "21"), // Sebenarnya ini L > K
            Arguments.of("Matriks Dominan Tie (L == K)", "testdata/matriks-3x3.txt", "20", "20", "5", "5"),
            
            // --- Menguji cabang filter baris kosong ---
            Arguments.of("Matriks dengan Baris Kosong", "testdata/matriks-dengan-baris-kosong.txt", "20", "20", "5", "5")
        );
    }

    @ParameterizedTest(name = "[perbedaanL] {0}")
    @MethodSource("providePerbedaanLTestData")
    @DisplayName("perbedaanL() - Menguji berbagai ukuran dan kasus matriks valid dari file TXT")
    void perbedaanL_shouldProcessValidBase64FromFile(String desc, String filePath, String l, String k, String t, String d) throws IOException {
        String base64Input = encodeFileToBase64(filePath);
        String result = controller.perbedaanL(base64Input);

        // Debugging: Cetak output jika tes gagal
        // System.out.println("--- Testing: " + desc + " ---\n" + result + "\n--------------------");
        
        assertTrue(result.contains("Nilai L: " + l), "Nilai L salah untuk: " + desc);
        assertTrue(result.contains("Nilai Kebalikan L: " + k), "Nilai Kebalikan L salah untuk: " + desc);
        assertTrue(result.contains("Nilai Tengah: " + t), "Nilai Tengah salah untuk: " + desc);
        assertTrue(result.contains("Dominan: " + d), "Dominan salah untuk: " + desc);
    }

    //======================================================================
    // TES UNTUK METHOD: perbedaanL() - KASUS ERROR EKSPLISIT
    //======================================================================
    @Test
    @DisplayName("perbedaanL() - [ERROR] Menguji cabang data non-persegi")
    void perbedaanL_shouldReturnErrorForNonSquareData() throws IOException {
        String base64Input = encodeFileToBase64("testdata/matriks-error-non-persegi.txt");
        String result = controller.perbedaanL(base64Input);
        assertTrue(result.contains("tidak membentuk matriks persegi"),
                   "Pesan error untuk data non-persegi tidak sesuai.");
    }

    @Test
    @DisplayName("perbedaanL() - [CATCH] Menguji cabang data non-numerik")
    void perbedaanL_shouldCatchErrorForNonNumericData() throws IOException {
        String base64Input = encodeFileToBase64("testdata/matriks-error-non-numerik.txt");
        String result = controller.perbedaanL(base64Input);
        assertTrue(result.contains("Terjadi kesalahan saat memproses data Base64"),
                   "Blok catch seharusnya dieksekusi.");
        assertTrue(result.contains("For input string: \"tiga\""),
                   "Pesan error harus menyertakan detail dari NumberFormatException.");
    }
    

     //======================================================================
    // TES UNTUK METHOD: palingTer() - KASUS VALID (SEMUA CABANG)
    //======================================================================
    // ... sisa tes untuk palingTer() tetap sama ...
    private static Stream<Arguments> providePalingTerTestData() {
        return Stream.of(
            Arguments.of("Kasus Standar", "testdata/palingter-kasus-standar.txt", "95", "70", "85 (3x)", "70 (1x)", "85 * 3 = 255", "70 * 1 = 70"),
            Arguments.of("Satu Nilai", "testdata/palingter-satu-nilai.txt", "100", "100", "100 (1x)", "100 (1x)", "100 * 1 = 100", "100 * 1 = 100"),
            Arguments.of("Semua Nilai Sama", "testdata/palingter-semua-sama.txt", "50", "50", "50 (5x)", "50 (5x)", "50 * 5 = 250", "50 * 5 = 250"),
            Arguments.of("Semua Nilai Beda", "testdata/palingter-semua-beda.txt", "50", "10", "10 (1x)", "10 (1x)", "50 * 1 = 50", "10 * 1 = 10"),
            Arguments.of("Tie pada Jumlah Tertinggi", "testdata/palingter-jumlah-tie.txt", "20", "15", "15 (4x)", "20 (3x)", "20 * 3 = 60", "15 * 4 = 60"),
            Arguments.of("Mengabaikan Baris Kosong", "testdata/palingter-dengan-baris-kosong.txt", "95", "70", "85 (3x)", "70 (1x)", "85 * 3 = 255", "70 * 1 = 70")
        );
    }

    @ParameterizedTest(name = "[palingTer] {0}")
    @MethodSource("providePalingTerTestData")
    @DisplayName("palingTer() - Menguji skenario nilai valid dari file TXT")
    void palingTer_shouldProcessValidBase64FromFile(String desc, String fp, String t, String r, String byk, String sdkt, String jt, String jr) throws IOException {
        String base64Input = encodeFileToBase64(fp);
        String result = controller.palingTer(base64Input);
        assertTrue(result.contains("Tertinggi: " + t), "Tertinggi salah untuk: " + desc);
        assertTrue(result.contains("Terendah: " + r), "Terendah salah untuk: " + desc);
        assertTrue(result.contains("Terbanyak: " + byk), "Terbanyak salah untuk: " + desc);
        assertTrue(result.contains("Tersedikit: " + sdkt), "Tersedikit salah untuk: " + desc);
        assertTrue(result.contains("Jumlah Tertinggi: " + jt), "Jumlah Tertinggi salah untuk: " + desc);
        assertTrue(result.contains("Jumlah Terendah: " + jr), "Jumlah Terendah salah untuk: " + desc);
    }

    //======================================================================
    // TES UNTUK METHOD: palingTer() - KASUS ERROR EKSPLISIT
    //======================================================================
    
    // ==========================================================
    // METODE TES BARU YANG MENGGANTIKAN DUA TES SEBELUMNYA
    // INI SECARA EKSPLISIT MENGUJI CABANG YANG ANDA TANDAI
    // ==========================================================
    @Test
    @DisplayName("palingTer() - [ERROR] Menguji cabang input kosong atau hanya spasi")
    void palingTer_shouldHandleEmptyAndWhitespaceInput() {
        // Kasus 1: Input adalah string kosong ""
        String emptyString = "";
        String base64Empty = Base64.getEncoder().encodeToString(emptyString.getBytes(StandardCharsets.UTF_8));
        String result1 = controller.palingTer(base64Empty);
        assertTrue(result1.contains("Tidak ada data nilai yang diberikan"),
                   "Pesan error untuk data kosong (empty string) tidak sesuai.");

        // Kasus 2: Input hanya berisi spasi dan baris baru
        String whitespaceString = "\n \t \n";
        String base64Whitespace = Base64.getEncoder().encodeToString(whitespaceString.getBytes(StandardCharsets.UTF_8));
        String result2 = controller.palingTer(base64Whitespace);
        assertTrue(result2.contains("Tidak ada data nilai yang diberikan"),
                   "Pesan error untuk data yang hanya berisi spasi tidak sesuai.");
    }

    @Test
    @DisplayName("palingTer() - [CATCH] Menguji cabang data non-numerik")
    void palingTer_shouldCatchErrorForNonNumericData() throws IOException {
        String base64Input = encodeFileToBase64("testdata/palingter-error-non-numerik.txt");
        String result = controller.palingTer(base64Input);
        assertTrue(result.contains("Terjadi kesalahan saat memproses data Base64"),
                   "Blok catch seharusnya dieksekusi.");
        assertTrue(result.contains("For input string: \"tiga\""),
                   "Pesan error harus menyertakan detail dari NumberFormatException.");
    }

    @Test
    @DisplayName("[CATCH ALL] Menangani input yang bukan Base64 valid")
    void allMethods_shouldCatchErrorForInvalidBase64() {
        String invalidBase64 = "ini-bukan-base64-string";
        
        String resultNilai = controller.perolehanNilai(invalidBase64);
        String resultMatriks = controller.perbedaanL(invalidBase64);
        String resultPalingTer = controller.palingTer(invalidBase64);

        assertTrue(resultNilai.contains("Terjadi kesalahan saat memproses input Base64"));
        assertTrue(resultMatriks.contains("Terjadi kesalahan saat memproses data Base64"));
        assertTrue(resultPalingTer.contains("Terjadi kesalahan saat memproses data Base64"));
    }
}