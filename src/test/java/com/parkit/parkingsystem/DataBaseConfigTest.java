package com.parkit.parkingsystem;

import com.parkit.parkingsystem.config.DataBaseConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class DataBaseConfigTest {

    private static DataBaseConfig dataBaseConfig = new DataBaseConfig();

    @Test
    public void getConnectionTest() {

        Connection etalon = null;
        Connection connection = null;
        DatabaseMetaData etalonMetaData = null;
        DatabaseMetaData connectionMetaData = null;
        try {
            etalon = DriverManager.getConnection("jdbc:mysql://localhost:3306/prod?serverTimezone=CET", "root",
                    "rootroot");
            connection = dataBaseConfig.getConnection();

            etalonMetaData = etalon.getMetaData();
            connectionMetaData = connection.getMetaData();

            assertEquals(etalonMetaData.getDatabaseProductName(), connectionMetaData.getDatabaseProductName());
            assertEquals(etalonMetaData.getUserName(), connectionMetaData.getUserName());

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCloseConnectionTest() throws ClassNotFoundException, SQLException {
        Connection connection = dataBaseConfig.getConnection();
        PreparedStatement ps = connection.prepareStatement("Select * from ticket");
        dataBaseConfig.closeConnection(connection);
        assertThrows(SQLException.class, () -> ps.executeQuery());
    }

    @Test
    public void testClosePreparedStatement() throws ClassNotFoundException, SQLException {
        Connection connection = dataBaseConfig.getConnection();
        PreparedStatement ps = connection.prepareStatement("Select * from ticket");
        dataBaseConfig.closePreparedStatement(ps);
        assertThrows(SQLException.class, () -> ps.executeQuery());
    }

    @Test
    public void testCloseResultSet() throws ClassNotFoundException, SQLException {
        Connection connection = dataBaseConfig.getConnection();
        PreparedStatement ps = connection.prepareStatement("Select * from ticket");
        ResultSet rs = ps.executeQuery(); 
        dataBaseConfig.closeResultSet(rs);
        assertThrows(SQLException.class, () -> rs.next());
    }
}
