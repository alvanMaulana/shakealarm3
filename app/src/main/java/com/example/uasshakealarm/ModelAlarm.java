package com.example.uasshakealarm;

public class ModelAlarm {
    int Jam;

    int id;

    int checked;

    int menit;


    String nama,nadadering, kesulitan;

    public String getKesulitan() {
        return kesulitan;
    }

    public void setKesulitan(String kesulitan) {
        this.kesulitan = kesulitan;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNadadering() {
        return nadadering;
    }

    public void setNadadering(String nadadering) {
        this.nadadering = nadadering;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getJam() {
        return Jam;
    }

    public void setJam(int jam) {
        this.Jam = jam;
    }
    public int getMenit() {
        return menit;
    }

    public void setMenit(int menit) {
        this.menit = menit;
    }
    public int getChecked(){
        return checked;
    }
    public void setChecked(int checked){
        this.checked = checked;
    }



}
