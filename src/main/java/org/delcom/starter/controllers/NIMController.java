package org.delcom.starter.controllers;
import org.delcom.starter.controllers.model.NimInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/nim")
public class NimController {

    @GetMapping("/info")
    public ResponseEntity<Object> getNimInfo(@RequestParam(name = "nim") String nim) {
        if (nim == null || nim.trim().length() < 8) {
             return ResponseEntity.badRequest().body("NIM tidak valid atau terlalu pendek.");
        }

        String nimTrimmed = nim.trim();
        // Menggunakan switch expression, fitur modern Java yang lebih ringkas
        String namaProdi = switch (nimTrimmed.substring(0, 3)) {
            case "11S" -> "Sarjana Informatika";
            case "12S" -> "Sarjana Sistem Informasi";
            case "14S" -> "Sarjana Teknik Elektro";
            case "21S" -> "Sarjana Manajemen Rekayasa";
            case "22S" -> "Sarjana Teknik Metalurgi";
            case "31S" -> "Sarjana Teknik Bioproses";
            case "114" -> "Diploma 4 Teknologi Rekasaya Perangkat Lunak";
            case "113" -> "Diploma 3 Teknologi Informasi";
            case "133" -> "Diploma 3 Teknologi Komputer";
            default -> "Program Studi Tidak Dikenal";
        };

        if (namaProdi.equals("Program Studi Tidak Dikenal")) {
            return ResponseEntity.status(404).body("Informasi untuk prefix NIM tidak ditemukan.");
        }

        String angkatan = "20" + nimTrimmed.substring(3, 5);
        int urutInt = Integer.parseInt(nimTrimmed.substring(nimTrimmed.length() - 3));
        String urutan = String.valueOf(urutInt);

        NimInfo response = new NimInfo(nimTrimmed, namaProdi, angkatan, urutan);
        return ResponseEntity.ok(response);
    }
}