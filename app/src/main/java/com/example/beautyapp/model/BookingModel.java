package com.example.beautyapp.model;

import java.util.List;

public class BookingModel {
    private boolean success;
    private String message;
    private List<Booking> result;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Booking> getResult() {
        return result;
    }

    public void setResult(List<Booking> result) {
        this.result = result;
    }
}
