package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket) {
        if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
            throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
        }

        Long inHour = ticket.getInTime().getTime();
        Long outHour = ticket.getOutTime().getTime();

        double duration = outHour.doubleValue() - inHour.doubleValue(); // One hour in milliseconds;

        duration = duration / 3600000;

        // Parking is free for those who stayed 30 min or less;
        if (duration <= 0.50) {
            duration = 0;
        }

        switch (ticket.getParkingSpot().getParkingType()) {
            case CAR: {
                if (ticket.getLoyal()) {
                    ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR_DISCOUNT);
                } else {
                    ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
                }
                break;
            }
            case BIKE: {
                if (ticket.getLoyal()) {
                    ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR_DISCOUNT);

                } else {
                    ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
                }
                break;
            }
            default:
                throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}