package com.kangtech.MasyarakatLapor.data_model;

public class ProfilePetugasModel {

    private String id_petugas;
    private String nama;
    private  String username;
    private String telp;
    private  String tipe;


    public ProfilePetugasModel(String id_petugas, String nama, String username, String telp, String tipe) {
        this.id_petugas = id_petugas;
        this.nama = nama;
        this.username = username;
        this.telp = telp;
        this.tipe = tipe;
    }

    public String getId_petugas() {
        return id_petugas;
    }

    public String getNama() {
        return nama;
    }

    public String getUsername() {
        return username;
    }

    public String getTelp() {
        return telp;
    }

    public String getTipe() {
        return tipe;
    }
}
