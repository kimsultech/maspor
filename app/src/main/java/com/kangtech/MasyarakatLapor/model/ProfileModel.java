package com.kangtech.MasyarakatLapor.model;

public class ProfileModel {

    private String id_nik;
    private String nama;
    private  String username;
    private String telp;
    private String fotop;


    public  ProfileModel(String id_nik, String nama, String username, String telp, String fotop) {
        this.id_nik = id_nik;
        this.nama = nama;
        this.username = username;
        this.telp = telp;
        this.fotop = fotop;

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

    public String getFotop() {
        return fotop;
    }
}
