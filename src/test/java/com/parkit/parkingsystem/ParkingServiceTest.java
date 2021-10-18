package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ParkingServiceTest {

    private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;

    @BeforeEach
    private void setUpPerTest() {
        try {
            when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set up test mock objects");
        }
    }

    @Test
    public void testParkingBike() throws Exception {
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("bike01");
        when(inputReaderUtil.readSelection()).thenReturn(2);
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE)).thenReturn(4);

        ParkingSpot parkingSpot = new ParkingSpot(4, ParkingType.BIKE, false);

        Ticket ticket = new Ticket();
        ticket.setInTime(new Date(System.currentTimeMillis() - (120 * 60 * 1000)));
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("bike01");
        when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

        parkingService.processIncomingVehicle();

        parkingService.processExitingVehicle();
        // If the exit was processed correctly the OutTime will be generated
        Date outTime = ticketDAO.getTicket("bike01").getOutTime();
        assertNotNull(outTime);
    }

    @Test
    public void testGetNextParkingNumberInvalidInput() {
        // Here we do not set up vehicleType (it will be zero by default), 
        //this is why getNextParkingNumberIfAvailable will fail and ps will be null.
       ParkingSpot ps = parkingService.getNextParkingNumberIfAvailable();
       assertNull(ps);
    }

    @Test
    public void testGetNextParkingNumberUnavailable() {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(-1);
        // If there is no parking slot available the checked exception 
        // will be generated and ps will remain null.
        ParkingSpot ps = parkingService.getNextParkingNumberIfAvailable();
        assertNull(ps);
    }
}
