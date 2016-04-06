package com.example.daniel.cd9_parent_client.JSONClassFiles;

/**
 * Created by daniel on 3/22/16.
 */
public class Text {
    String number, content, date, text_type, contact;

    public Text(String phone, String content, String date, String type, String person) {
        this.number = phone;
        this.content = content;
        this.date = date;
        this.text_type = type;
        this.contact = person;
    }

    public String getNumber() {
        return number;
    }

    public String getContent() {
        return content;
    }

    public String getDate() {
        return date;
    }

    public String getType() {
        return text_type;
    }

    public String getPerson() {
        return contact;
    }
}
