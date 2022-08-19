package com.example.drivingmonitor.LoginRegisterManagement;

import android.util.Log;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseConnector {
    private final static String DRIVER = "com.mysql.jdbc.Driver";
    private final static String URL = "jdbc:mysql://rm-bp1956ce020989jk0do.mysql.rds.aliyuncs.com:3306/driving_monitor";
    private final static String USER = "tqp";
    private final static String PASSWORD = "Twilight@2001";

    public final static int CONNECTION_FAIL = 0;
    public final static int QUERY_SUCCESS = 1;
    public final static int QUERY_FAIL = 2;
    public final static int INSERT_SUCCESS = 3;
    public final static int INSERT_FAIL = 4;
    public final static int UPDATE_SUCCESS = 5;
    public final static int UPDATE_FAIL = 6;

    private Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName(DRIVER);
            connection = (Connection) DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public int login(String user, String password) {
        Connection connection = getConnection();
        if (connection == null) {
            Log.i("Connection", "Fail...");
            return CONNECTION_FAIL;
        }else {
            Log.i("Connection", "Success...");
            String SQL = "select * from driver_account_info where user=? and password=?";
            try {
                PreparedStatement preparedStatement = (PreparedStatement) connection.prepareStatement(SQL);
                preparedStatement.setString(1, user);
                preparedStatement.setString(2, password);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return QUERY_SUCCESS;
                }else {
                    return QUERY_FAIL;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return CONNECTION_FAIL;
            } finally {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public int register(String user, String password) {
        Connection connection = getConnection();
        if (connection == null) {
            Log.i("Connection", "Fail...");
            return CONNECTION_FAIL;
        }else {
            Log.i("Connection", "Success...");
            String SQL_ACCOUNT = "insert into driver_account_info(user,password) values(?,?)";
            String SQL_DATA = "insert into real_time_data(user) values(?)";
            try {
                PreparedStatement preparedStatementAccount = (PreparedStatement) connection.prepareStatement(SQL_ACCOUNT);
                preparedStatementAccount.setString(1, user);
                preparedStatementAccount.setString(2, password);
                boolean executeAccount = preparedStatementAccount.execute();
                PreparedStatement preparedStatementData = (PreparedStatement) connection.prepareStatement(SQL_DATA);
                preparedStatementData.setString(1, user);
                boolean executeData = preparedStatementData.execute();
                if (!executeAccount && !executeData) {
                    return INSERT_SUCCESS;
                }else {
                    return INSERT_FAIL;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return CONNECTION_FAIL;
            } finally {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public int upLoadData(String user, String alcoholConcentration, String heartRate, String bloodOxygen,
                          String microCirculation, String highPressure, String lowPressure,
                          String fatigueState, String abnormalState, String currentLocation) {
        Connection connection = getConnection();
        if (connection == null) {
            Log.i("Connection", "Fail...");
            return CONNECTION_FAIL;
        }else {
            Log.i("Connection", "Success...");
            String SQL = "update real_time_data set alcohol_concentration=?, heart_rate=?, blood_oxygen=?, " +
                    "micro_circulation=?, high_pressure=?, low_pressure=?, fatigue_state=?, abnormal_state=?, " +
                    "current_location=? where user=?";
            try {
                PreparedStatement preparedStatement = (PreparedStatement) connection.prepareStatement(SQL);
                preparedStatement.setString(1, alcoholConcentration);
                preparedStatement.setString(2, heartRate);
                preparedStatement.setString(3, bloodOxygen);
                preparedStatement.setString(4, microCirculation);
                preparedStatement.setString(5, highPressure);
                preparedStatement.setString(6, lowPressure);
                preparedStatement.setString(7, fatigueState);
                preparedStatement.setString(8, abnormalState);
                preparedStatement.setString(9, currentLocation);
                preparedStatement.setString(10, user);
                if (!preparedStatement.execute()) {
                    Log.d("数据库操作", "更新成功");
                    return UPDATE_SUCCESS;
                }else {
                    return UPDATE_FAIL;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return CONNECTION_FAIL;
            } finally {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
