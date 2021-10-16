package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown(){

    }

    @Test
    public void testParkingACar(){
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        /*
         * 1. If a ticket is created, it will have ID=1, parking_number=1 and
         * vehicle_reg_num= ABCDEF. These are the values we can check for when we
         * retrieve the ticket from DB.
         * 
         * 2. If the 1st car has been parked (here we work only with the 1st car), it means 
         * that the parking_number=1 has been taken by this car and the next available 
         * parking slot will be parking_number=2.
         */

        Ticket fromDB = ticketDAO.getTicket("ABCDEF");

        assertEquals(1, fromDB.getId());
        assertEquals(1, fromDB.getParkingSpot().getId());
        assertEquals("ABCDEF", fromDB.getVehicleRegNumber());
        assertEquals(2, parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR));
    }

    @Test
    public void testParkingLotExit(){
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        parkingService.processIncomingVehicle();
        // One second time-out so that the data has enough time to get saved in DB
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        parkingService.processExitingVehicle();
        /*
         * If the car's exist was processed correctly, the car's ticket will be
         * generated and its fields will not be null and the parking_number=1
         * will be available again.
         * 
         */
        Ticket fromDB = ticketDAO.getTicket("ABCDEF");
        assertNotNull(fromDB.getOutTime(), "a DATE was expected but it was not returned;");
        assertNotNull(fromDB.getPrice(), "a double was expected but it was not returned;");
        assertEquals(1, parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR));
    }
}
