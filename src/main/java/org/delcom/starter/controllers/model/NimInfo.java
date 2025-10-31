package org.delcom.starter.controllers.model;

public class NimInfo {

    private String nim;
    private String programStudi;
    private String angkatan;
    private String urutan;

    public NimInfo(String nim, String programStudi, String angkatan, String urutan) {
        this.nim = nim;
        this.programStudi = programStudi;
        this.angkatan = angkatan;
        this.urutan = urutan;
    }

    // --- Getters and Setters ---

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getProgramStudi() {
        return programStudi;
    }

    public void setProgramStudi(String programStudi) {
        this.programStudi = programStudi;
    }

    public String getAngkatan() {
        return angkatan;
    }

    public void setAngkatan(String angkatan) {
        this.angkatan = angkatan;
    }

    public String getUrutan() {
        return urutan;
    }

    public void setUrutan(String urutan) {
        this.urutan = urutan;
    }
}