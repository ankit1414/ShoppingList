package com.example.shoppinglist.model;

public class Data {
    String type;
    int amount;
    String note;
    String date;
    String id;

    public Data(){

    }

    public Data(String type, int amount, String note, String date, String id) {
        this.type = type;
        this.amount = amount;
        this.note = note;
        this.date = date;
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }

    public String getNote() {
        return note;
    }

    public String getDate() {
        return date;
    }

    public String getId() {
        return id;
    }
}
