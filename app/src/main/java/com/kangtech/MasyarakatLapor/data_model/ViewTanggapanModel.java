package com.kangtech.MasyarakatLapor.data_model;

public class ViewTanggapanModel {

    private String idtanggapan;
    private String idpengaduna;
    private String tgltanggapan;

    private String tanggapan;
    private String idpetugas;
    private String namapetugas;



    public ViewTanggapanModel(String idtanggapan, String idpengaduna, String tgltanggapan, String tanggapan, String idpetugas, String namapetugas) {
        this.idtanggapan = idtanggapan;
        this.idpengaduna = idpengaduna;
        this.tgltanggapan = tgltanggapan;

        this.tanggapan = tanggapan;
        this.idpetugas = idpetugas;
        this.namapetugas = namapetugas;

    }

    public String getIdtanggapan() {
        return idtanggapan;
    }

    public String getIdpengaduna() {
        return idpengaduna;
    }

    public String getTgltanggapan() {
        return tgltanggapan;
    }

    public String getTanggapan() {
        return tanggapan;
    }

    public String getIdpetugas() {
        return idpetugas;
    }

    public String getNamapetugas() {
        return namapetugas;
    }

}
