package com.parkit.parkingsystem.integration.service;

import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Date;

public class DataBasePrepareService {

    DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();

    public void clearDataBaseEntries() {
        Connection connection = null;
        try {
            connection = dataBaseTestConfig.getConnection();

            // set parking entries to available
            connection.prepareStatement("update parking set available = true").execute();

            // clear ticket entries;
            connection.prepareStatement("truncate table ticket").execute();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dataBaseTestConfig.closeConnection(connection);
        }
    }

    public void pushLoyalCustomerToDB(int ID, int parkingNum, String vehicleRegNum, double price) {
        Connection connection = null;
        try {
            connection = dataBaseTestConfig.getConnection();
            // Putting a loyal customer in database
            Date inTime = new Date();
            Date outTime = new Date();
            inTime.setTime(System.currentTimeMillis() - (48 * 60 * 60 * 1000));
            outTime.setTime((System.currentTimeMillis() - (24 * 60 * 60 * 1000)));
            String sqlQuery = "Insert into ticket(ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME) values (?, ?, ?, ?, ?, ?)";
            // ps.setTimestamp(4, new Timestamp(ticket.getInTime().getTime()));
            PreparedStatement ps = connection.prepareStatement(sqlQuery);
            ps.setInt(1, ID);
            ps.setInt(2, parkingNum);
            ps.setString(3, vehicleRegNum);
            ps.setDouble(4, price);
            ps.setTimestamp(5, new Timestamp(inTime.getTime()));
            ps.setTimestamp(6, new Timestamp(outTime.getTime()));
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dataBaseTestConfig.closeConnection(connection);
        }
    }
}
