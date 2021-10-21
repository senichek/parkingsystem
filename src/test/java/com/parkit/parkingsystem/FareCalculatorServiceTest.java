package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Date;

public class FareCalculatorServiceTest {

    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket;

    @BeforeAll
    private static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }

    @BeforeEach
    private void setUpPerTest() {
        ticket = new Ticket();
    }

    @Test
    public void calculateFareCar() {
        LocalDateTime inTime = LocalDateTime.now().minusHours(1);
        LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(ticket.getPrice(), Fare.CAR_RATE_PER_HOUR);
    }

    @Test
    public void calculateFareBike() {
        LocalDateTime inTime = LocalDateTime.now().minusHours(1);
        LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR);
    }

    @Test
    public void calculateFareNullType() {
        LocalDateTime inTime = LocalDateTime.now().minusHours(1);
        LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, null, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithFutureInTime() {
        LocalDateTime inTime = LocalDateTime.now().plusHours(1);
        LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithLessThanOneHourParkingTime() {
        //Date inTime = new Date();
       // inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));// 45 minutes parking time should give 3/4th
                                                                      // parking fare
       // Date outTime = new Date();

        LocalDateTime inTime = LocalDateTime.now().minusMinutes(45);
        LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice(), 0.0000001);
    }

    @Test
    public void calculateFareCarWithLessThanOneHourParkingTime() {
        LocalDateTime inTime = LocalDateTime.now().minusMinutes(45);
        LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals((0.75 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice(), 0.0000001);
    }

    @Test
    public void calculateFareCarWithMoreThanADayParkingTime() {
        LocalDateTime inTime = LocalDateTime.now().minusDays(1);
        LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals((24 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
    }

    @Test
    public void calculateFareBikeWith30MinParkingTime() {
        // Parking is free for those who stayed 30 min or less;
        LocalDateTime inTime = LocalDateTime.now().minusMinutes(30);
        LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(0, ticket.getPrice());
    }

    @Test
    public void calculateFareCarWith30MinParkingTime() {
        // Parking is free for those who stayed 30 min or less;
        LocalDateTime inTime = LocalDateTime.now().minusMinutes(30);
        LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(0, ticket.getPrice());
    }

    @Test
    public void discountCar() {
        LocalDateTime inTime = LocalDateTime.now().minusDays(1);
        LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        ticket.setLoyal(true);
        fareCalculatorService.calculateFare(ticket);
        assertEquals((24 * Fare.CAR_RATE_PER_HOUR_DISCOUNT), ticket.getPrice(), 0.000001);
    }

    @Test
    public void discountBike() {
        LocalDateTime inTime = LocalDateTime.now().minusDays(1);
        LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        ticket.setLoyal(true);
        fareCalculatorService.calculateFare(ticket);
        assertEquals((24 * Fare.BIKE_RATE_PER_HOUR_DISCOUNT), ticket.getPrice(), 0.000001);
    }
 }
