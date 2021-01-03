package ua.demirug.vkauth.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import ua.demirug.vkauth.Account;
import ua.demirug.vkauth.AccountManager;
import ua.demirug.vkauth.Authorization;

public class AccountBD extends DataBase {
    
    public AccountBD(String host, String username, String password) {
        super(host, username, password);
        this.execute("CREATE TABLE IF NOT EXISTS `VKAuthorization` (`id` int NOT NULL AUTO_INCREMENT, `username` varchar(16) NOT NULL UNIQUE,`realname` varchar(16) NOT NULL, `password` varchar(255) NOT NULL, `vk_id` INTEGER(255) NOT NULL, `vklogin` varchar(5) NOT NULL, `lastlogin` LONG NOT NULL, `address` varchar(255) NOT NULL, `locked` varchar(5) NOT NULL, PRIMARY KEY (`id`)) DEFAULT CHARSET=utf8 COLLATE utf8_bin AUTO_INCREMENT=0");
    }

    public void insertAccount(Account acc) {
        this.execute("INSERT INTO `VKAuthorization` (`username`, `realname`, `password`, `vk_id`, `vklogin`, `lastlogin`, `address`, `locked`) VALUES ('" + acc.getPlayerName().toLowerCase() + "','" + acc.getRealName() + "','" + acc.getPassword() + "','" + acc.getVKID() + "','" + acc.isVKLoggin() + "','" + acc.getLastLogin() + "','" + acc.getLastIP() + "','" + acc.isLocked()+ "')");
    }

    public void updateAccount(Account acc) {
        this.execute("UPDATE `VKAuthorization` SET `realname`='" + acc.getRealName() + "', `password`='" + acc.getPassword() + "', `vk_id`='" + acc.getVKID() + "', `vklogin`='" + acc.isVKLoggin()+ "', `lastlogin`='" + acc.getLastLogin() + "', `address`='" + acc.getLastIP() +  "', `locked`='" + acc.isLocked()+ "' WHERE `username`='" + acc.getPlayerName().toLowerCase() + "' ");
    }

    public short IPregAmmount(String ip) {
        try {
                ResultSet result = this.getConnection().createStatement().executeQuery("SELECT * FROM `VKAuthorization` WHERE `address`='" + ip + "'");
                result.last();
                return (short) result.getRow();
        } catch (SQLException ex) {
          ex.printStackTrace();
        }
        return 0;
    }
    
    public short getAccountByVKAmmount(int id) {
        try {
                ResultSet result = this.getConnection().createStatement().executeQuery("SELECT * FROM `VKAuthorization` WHERE `vk_id`='" + id + "'");
                result.last();
                return (short) result.getRow();
        } catch (SQLException ex) {
          ex.printStackTrace();
        }
        return 0;
    }
    
    public List<String> getAccountsByVKID(int id) {
        ArrayList<String> names = new ArrayList();
        
        try {
                ResultSet result = this.getConnection().createStatement().executeQuery("SELECT * FROM `VKAuthorization` WHERE `vk_id`='" + id + "'");
                while(result.next()) {
                    names.add(result.getString("realname"));
                }
        } catch (SQLException ex) {
          ex.printStackTrace();
        }
        
        return names;
    }
    
    public Account getAccount(String playerName) {
        Account acc = null;
        if(AccountManager.accounts.containsKey(playerName.toLowerCase()))
            return AccountManager.accounts.get(playerName.toLowerCase());
        try {
            ResultSet result = this.getConnection().createStatement().executeQuery("SELECT * FROM `VKAuthorization` WHERE `username`='" + playerName.toLowerCase() + "'");
            if (result.next()) {
                boolean vklogin = result.getString("vklogin").equals("") ? true : result.getBoolean("vklogin");
               acc = new Account(result.getString("username"), result.getString("realname"), result.getString("password"), result.getInt("vk_id"), vklogin, result.getLong("lastlogin"), result.getString("address"), result.getBoolean("locked"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return acc;
    }
    
    public void loadAccount(String playerName) {
        if (!AccountManager.accounts.containsKey(playerName.toLowerCase())) {
            Account acc = getAccount(playerName);
            if(acc != null) AccountManager.accounts.put(playerName.toLowerCase(), acc);
        }
    }
    
    public void purgeUnuse() {
        long time = System.currentTimeMillis();
        try {
            ResultSet result = this.getConnection().createStatement().executeQuery("SELECT * FROM `VKAuthorization`");
        
            while(result.next()) {
                String userName = result.getString("username");
                if(!AccountManager.accounts.containsKey(userName.toLowerCase())) {
                if(((time - result.getLong("lastlogin")) / 1000) >= 604800) {    
                 boolean contains = false;
                 for(String group : Authorization.getInstance().getPermissionsBD().getGroups(userName)) {
                     if(Authorization.getInstance().getConfig().getStringList("PROTECT-GROUPS").contains(group)) {
                         contains = true;
                         break;
                     }
                 }
                 if(!contains)  
                     this.execute("DELETE FROM `VKAuthorization` WHERE `username`='" + userName + "'");
                }
                }
                Thread.sleep(100);
            }
            
        } catch (SQLException | InterruptedException ex) {
           ex.printStackTrace();
        }
    }
    
}
