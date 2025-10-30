import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MatriksController {

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
}
