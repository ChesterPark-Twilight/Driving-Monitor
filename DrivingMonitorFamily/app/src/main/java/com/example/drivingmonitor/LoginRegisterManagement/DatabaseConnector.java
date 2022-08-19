package com.example.drivingmonitor.LoginRegisterManagement;

import android.content.Context;
import android.content.Intent;
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
            String SQL = "select * from family_account_info where user=? and password=?";
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
            String SQL = "insert into family_account_info(user,password) values(?,?)";
            try {
                PreparedStatement preparedStatement = (PreparedStatement) connection.prepareStatement(SQL);
                preparedStatement.setString(1, user);
                preparedStatement.setString(2, password);
                if (!preparedStatement.execute()) {
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

    public int loginFamily(String user, String password) {
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

    public int downloadData(Context context, String user) {
        Connection connection = getConnection();
        if (connection == null) {
            Log.i("Connection", "Fail...");
            return CONNECTION_FAIL;
        }else {
            Log.i("Connection", "Success...");
            String SQL = "select * from real_time_data where user=?";
            try {
                PreparedStatement preparedStatement = (PreparedStatement) connection.prepareStatement(SQL);
                preparedStatement.setString(1, user);
                ResultSet resultSet = preparedStatement.executeQuery();
                int result = QUERY_FAIL;
                while (resultSet.next()) {
                    String alcoholConcentration = resultSet.getString("alcohol_concentration");
                    String heartRate = resultSet.getString("heart_rate");
                    String bloodOxygen = resultSet.getString("blood_oxygen");
                    String microCirculation = resultSet.getString("micro_circulation");
                    String highPressure = resultSet.getString("high_pressure");
                    String lowPressure = resultSet.getString("low_pressure");
                    String fatigueState = resultSet.getString("fatigue_state");
                    String abnormalState = resultSet.getString("abnormal_state");
                    String currentLocation = resultSet.getString("current_location");
                    String realTimeData = alcoholConcentration + " " + heartRate + " " + bloodOxygen +
                                " " + microCirculation + " " + highPressure + " " + lowPressure +
                                " " + fatigueState + " " + abnormalState + " " + currentLocation;
                    Log.d("Data", realTimeData);
                    Intent intent = new Intent();
                    intent.setAction("com.example.drivingmonitor.realTimeData");
                    intent.putExtra("realTimeData", realTimeData);
                    context.sendBroadcast(intent);
                    Intent location = new Intent();
                    intent.setAction("com.example.drivingmonitor.currentLocation");
                    intent.putExtra("currentLocation", currentLocation);
                    context.sendBroadcast(location);
                    result = QUERY_SUCCESS;
                }
                return result;
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
