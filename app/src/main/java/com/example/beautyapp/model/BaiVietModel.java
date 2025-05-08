package com.example.beautyapp.model;

import java.util.List;

public class BaiVietModel {
    private boolean success;
    private String message;
    private List<BaiViet> result;

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

    public List<BaiViet> getResult() {
        return result;
    }

    public void setResult(List<BaiViet> result) {
        this.result = result;
    }
}
