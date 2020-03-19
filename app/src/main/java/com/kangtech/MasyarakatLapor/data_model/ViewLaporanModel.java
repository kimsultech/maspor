package com.kangtech.MasyarakatLapor.data_model;

public class ViewLaporanModel {

    private String id_nik;
    private String nama;
    private String tgl;

    private String isilaporan;
    private String fotolaporan;

    private String status;


    public ViewLaporanModel(String id_nik, String nama, String tgl, String isilaporan, String fotolaporan, String status) {
        this.id_nik = id_nik;
        this.nama = nama;
        this.tgl = tgl;

        this.isilaporan = isilaporan;
        this.fotolaporan = fotolaporan;

        this.status = status;
    }

    public String getId_nik() {
        return id_nik;
    }

    public String getNama() {
        return nama;
    }

    public String getTgl() {
        return tgl;
    }

    public String getIsilaporan() {
        return isilaporan;
    }

    public String getFotolaporan() {
        return fotolaporan;
    }

    public String getStatus() {
        return status;
    }
}
