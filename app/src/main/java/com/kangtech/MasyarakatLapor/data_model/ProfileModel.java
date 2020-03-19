package com.kangtech.MasyarakatLapor.data_model;

public class ProfileModel {

    private String id_nik;
    private String nama;
    private  String username;
    private String telp;


    public  ProfileModel(String id_nik, String nama, String username, String telp) {
        this.id_nik = id_nik;
        this.nama = nama;
        this.username = username;
        this.telp = telp;
    }

    public String getId_nik() {
        return id_nik;
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
}
