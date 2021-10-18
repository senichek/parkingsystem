package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ParkingSpotTest {

    private static ParkingSpot parkingSpot;

    @BeforeEach
    private void setUpPerTest() {
        parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
    }

    @Test
    public void testSetID() {
        parkingSpot.setId(2);
        assertEquals(2, parkingSpot.getId());
    }

    @Test
    public void testSetParkingType() {
        parkingSpot.setParkingType(ParkingType.BIKE);
        assertEquals(ParkingType.BIKE, parkingSpot.getParkingType());
    }

    @Test
    public void testEquals() {
        assertTrue(new ParkingSpot(1, ParkingType.CAR, true).equals(new ParkingSpot(1, ParkingType.CAR, true)));
    }

    @Test
    public void testHash() {
        assertEquals(1, new ParkingSpot(1, ParkingType.CAR, true).hashCode());
    }
}
