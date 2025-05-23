package com.example.beautyapp.model;

public class Booking {
    private int id, consultantId;
    private String userId,consultantName, consultantImage;
    private String timeStart, timeEnd, bookingPhone, address, date, consultantPhone;

    public String getBookingPhone() {
        return bookingPhone;
    }

    public void setBookingPhone(String bookingPhone) {
        this.bookingPhone = bookingPhone;
    }

    public String getConsultantPhone() {
        return consultantPhone;
    }

    public void setConsultantPhone(String consultantPhone) {
        this.consultantPhone = consultantPhone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getConsultantId() {
        return consultantId;
    }

    public void setConsultantId(int consultantId) {
        this.consultantId = consultantId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getConsultantName() {
        return consultantName;
    }

    public void setConsultantName(String consultantName) {
        this.consultantName = consultantName;
    }

    public String getConsultantImage() {
        return consultantImage;
    }

    public void setConsultantImage(String consultantImage) {
        this.consultantImage = consultantImage;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
