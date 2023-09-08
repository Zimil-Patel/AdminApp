package com.example.adminapp;

public class requestsData {
    private String name, email, subject, message, status, key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public requestsData(String name, String email, String subject, String message, String status, String key) {
        this.name = name;
        this.email = email;
        this.subject = subject;
        this.message = message;
        this.status = status;
        this.key = key;
    }

    public requestsData() {
    }
}
