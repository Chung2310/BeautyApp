package com.example.beautyapp.model;

import java.util.List;

public class ConsultantModel {
    private boolean success;
    private String message;
    private List<Consultant> result;

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

    public List<Consultant> getResult() {
        return result;
    }

    public void setResult(List<Consultant> result) {
        this.result = result;
    }
}
