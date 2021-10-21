package com.parkit.parkingsystem.model;

import java.time.LocalDateTime;
import java.util.Date;

public class Ticket {
    private int id;
    private ParkingSpot parkingSpot;
    private String vehicleRegNumber;
    private double price;
    private LocalDateTime inTime;
    private LocalDateTime outTime;
    private boolean loyal;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ParkingSpot getParkingSpot() {
        ParkingSpot ps = new ParkingSpot();
        //this.parkingSpot = parkingSpot;
        ps.setId(parkingSpot.getId());
        ps.setAvailable(true);
        ps.setParkingType(parkingSpot.getParkingType());
        return ps;
    }

    public void setParkingSpot(ParkingSpot parkingSpot) {
        ParkingSpot ps = new ParkingSpot();
        //this.parkingSpot = parkingSpot;
        ps.setId(parkingSpot.getId());
        ps.setAvailable(true);
        ps.setParkingType(parkingSpot.getParkingType());
        this.parkingSpot = ps;
    }

    public String getVehicleRegNumber() {
        return vehicleRegNumber;
    }

    public void setVehicleRegNumber(String vehicleRegNumber) {
        this.vehicleRegNumber = vehicleRegNumber;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public LocalDateTime getInTime() {
        return inTime;
    }

    public void setInTime(LocalDateTime inTime) {
        this.inTime = inTime;
    }

    public LocalDateTime getOutTime() {
        return outTime;
    }

    public void setOutTime(LocalDateTime outTime) {
        this.outTime = outTime;
    }

    public void setLoyal(boolean value) {
        this.loyal = value;
    }

    public boolean getLoyal() {
        return loyal;
    }
}
