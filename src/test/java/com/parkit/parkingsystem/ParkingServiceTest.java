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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ParkingServiceTest {

    private static ParkingService parkingService;

    private Ticket ticket;

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
            when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

            ticket = new Ticket();
            ticket.setInTime(LocalDateTime.now());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set up test mock objects");
        }
    }

    @Test
    public void testParkingBike() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(2);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("bike01");
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE)).thenReturn(4);

        ParkingSpot parkingSpot = new ParkingSpot(4, ParkingType.BIKE, false);

        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("bike01");

        when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(ticket);
        when(ticketDAO.getTicket(any(String.class))).thenReturn(ticket);

        // If there are no problems in processIncomingVehicle it will return
        // ticket which can be compared with the ticket passed to ticketDAO.
        Ticket fromService =  parkingService.processIncomingVehicle();

        assertEquals(ticket.getId(), fromService.getId());
        assertEquals(ticket.getInTime().truncatedTo(ChronoUnit.SECONDS), fromService.getInTime().truncatedTo(ChronoUnit.SECONDS));
        assertEquals(ticket.getParkingSpot(), fromService.getParkingSpot());
        assertEquals(ticket.getOutTime(), fromService.getOutTime());        
        

        parkingService.processExitingVehicle();
        // If the exit was processed correctly the OutTime will be generated
        LocalDateTime outTime = ticketDAO.getTicket("bike01").getOutTime();
        assertNotNull(outTime);
    }

    @Test
    public void testGetNextParkingNumberInvalidInput() {
        // Here we do not set up vehicleType (it will be zero by default),
        // this is why <getNextParkingNumberIfAvailable> will fail and will generate
        // an Exception that will not be thrown because it is located inside try-catch
        // block. The parkingSpot will be null because the method <getNextParkingNumberIfAvailable>
        // failed to complete.
        ParkingSpot ps = assertDoesNotThrow(() -> parkingService.getNextParkingNumberIfAvailable());
        assertNull(ps);
    }

    @Test
    public void testProcessExitingVehicleFailure() {
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false);
        assertDoesNotThrow(() -> parkingService.processExitingVehicle());
    }
}
