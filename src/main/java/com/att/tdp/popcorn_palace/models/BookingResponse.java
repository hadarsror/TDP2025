package com.att.tdp.popcorn_palace.models;

import java.util.UUID;

public class BookingResponse {
    private UUID bookingId;

    public BookingResponse() {}

    public BookingResponse(UUID bookingId) {
        this.bookingId = bookingId;
    }

    public UUID getBookingId() {
        return bookingId;
    }

    public void setBookingId(UUID bookingId) {
        this.bookingId = bookingId;
    }
}