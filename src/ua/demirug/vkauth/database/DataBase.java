package ua.demirug.vkauth.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DataBase {

    private Connection connection;
    private String host, userName, password;

    public DataBase(String host, String username, String password) {
        this.host = host;
        this.userName = username;
        this.password = password;
        
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            ex.printStackTrace();
        }
        this.getConnection();
    }

    public Connection getConnection() {
        try {
            if (this.connection != null && !this.connection.isClosed() && this.connection.isValid(20)) {
                return this.connection;
            }
            this.connection = DriverManager.getConnection(host, userName, password);
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return this.connection;
    }
    
    public void execute(String query) {
        try {
            PreparedStatement ps = this.getConnection().prepareStatement(query);
            ps.execute();
            ps.close();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}

