package com.example.sotaykiniem.model;

public class Ghichu {
    String tieude;
    String noidung;

    public Ghichu(){}
    public Ghichu(String tieude, String noidung){
        this.tieude = tieude;
        this.noidung = noidung;
    }

    public String getTieude() {
        return tieude;
    }

    public void setTieude(String tieude) {
        this.tieude = tieude;
    }

    public String getNoidung() {
        return noidung;
    }

    public void setNoidung(String noidung) {
        this.noidung = noidung;
    }
}
