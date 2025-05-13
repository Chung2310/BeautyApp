package com.example.beautyapp.model;

import com.google.type.DateTime;

import java.time.LocalDateTime;

public class BaiViet {
    private String id;
    private String user_id;
    private Double soLike;
    private String noiDung;
    private LocalDateTime time;
    private String nameUser;
    private String imageUser;


    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImageUser() {

        return imageUser;
    }

    public void setImageUser(String imageUser) {
        this.imageUser = imageUser;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getSoLike() {
        return soLike;
    }

    public void setSoLike(Double soLike) {
        this.soLike = soLike;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

}
