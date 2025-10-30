import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NIMController {

    // Metode ini akan diaktifkan untuk URL seperti:
    // http://localhost:8080/info-nim/12S20760
    // http://localhost:8080/info-nim/13399046
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
}