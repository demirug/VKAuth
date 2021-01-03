package ua.demirug.vkauth.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PermissionsBD extends DataBase {
    
    public PermissionsBD(String host, String username, String password) {
        super(host, username, password);
    }
    
    public ArrayList<String> getGroups(String playerName) {
        ArrayList<String> list = new ArrayList();
        try {
            String UUID = getUUID(playerName);
            if(UUID != null) {
            ResultSet result = this.getConnection().createStatement().executeQuery("SELECT * FROM `permissions_inheritance` WHERE `child`='" + UUID + "'");
            while(result.next()) {
                list.add(result.getString("parent").toLowerCase());
            }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }
    
    public String getUUID(String playerName) {
        try {
            ResultSet result = this.getConnection().createStatement().executeQuery("SELECT * FROM `permissions` WHERE `value`='" + playerName + "'");
            if(result.next()) return result.getString("name");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
}
