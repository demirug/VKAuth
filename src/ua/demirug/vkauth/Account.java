package ua.demirug.vkauth;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import ua.demirug.vkbot.messages.performance.MessageManager;

public class Account {
    
    private ProxiedPlayer player = null;
    private String playername, realName, password, address, code;
    private boolean logout = false, auth = false, locked = false, vklogin = true;
    private int errors = 0, code_erros = 0, VKID;
    private long lastLogin;
   
    public Account(String player, String rName, String password, Integer vk_id, boolean vklogin, long lastLogin, String address, boolean locked) {
        this.playername = player;
        this.realName = rName.equals("") ? null : rName;
        this.password = password;
        this.VKID = vk_id;
        this.vklogin = vklogin;
        this.lastLogin = lastLogin;
        this.address = address;
        this.locked = locked;
    }

    public boolean isLocked() {
        return this.locked;
    }
    
    public void setLocked(boolean bln) {
        this.locked = bln;
    }
    
    public void setRealName(String name) {
        this.realName = name;
    }
    
    public String getRealName() {
        return this.realName;
    }
    
    public void setPlayer(ProxiedPlayer player) {
        this.player = player;
    }

    public ProxiedPlayer getPlayer() {
        return this.player;
    }

    public void setVKLogin(boolean bool) {
        this.vklogin = bool;
    }
    
    public boolean isVKLoggin() {
        return this.vklogin;
    }
    
    public String getPlayerName() {
        return this.playername;
    }

    public boolean isOnline() {
        return this.player != null;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public boolean isOnCode() {
        return this.code != null;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String string) {
        this.password = Authorization.getInstance().getHashManager().toHash(string);
    }

    public String getLastIP() {
        return this.address;
    }

    public void setLogOut(boolean bool) {
        this.logout = bool;
    }

    public void setAuth(boolean bool) {
        this.auth = bool;
    }

    public int getVKID() {
        return this.VKID;
    }

    public boolean hasVKID() {
        return this.VKID > 0;
    }

    public void setVKID(Integer id) {
        this.VKID = id;
    }

    public boolean isAuth() {
        return this.auth;
    }

    public long getLastLogin() {
        return this.lastLogin;
    }

    public void trycode(String code) {
        if (this.code != null && this.code.equals(code)) {
            this.setCode(null);
            this.address = this.player.getAddress().getAddress().getHostAddress();
            Authorization.getInstance().toServer(this.getPlayer(), Authorization.ServerType.SURVIVAL);
            this.player.sendMessage(Authorization.getInstance().getSTR("MESSAGES.SuccessLogin"));
            if (Coding.tocode.containsKey(this)) {
                Coding.tocode.remove(this);
            }
        } else {
            ++this.code_erros;
            if (this.code_erros >= Authorization.getInstance().getConfig().getInt("MAX-PASSWORD-ERRORS")) {
                    this.player.disconnect(Authorization.getInstance().getSTR("MESSAGES.TooManyLoginAttempts"));
                    MessageManager.sendMessage(this.VKID, Authorization.getInstance().getSTR("BOT-MESSAGES.BAN-INFO").replace("{ip}", this.player.getAddress().getAddress().getHostAddress()));
            } else {
                this.player.sendMessage(Authorization.getInstance().getSTR("MESSAGES.CODE-WRONG").replace("{attempts}", Integer.toString(Authorization.getInstance().getConfig().getInt("MAX-PASSWORD-ERRORS") - this.code_erros)));
            }
        }
    }

    public void tryLogin(String password) {
        if (this.password.equals(Authorization.getInstance().getHashManager().toHash(password))) {
            if (Logination.tologin.containsKey(this)) {
                Logination.tologin.remove(this);
            }
            this.setAuth(true);
            if (Authorization.getInstance().isVKEnable() && this.hasVKID() && this.isVKLoggin()) {
                this.setCode(Authorization.getInstance().generateCode(Authorization.getInstance().getConfig().getInt("VK-MODULE.CODE-LENGTH")));
                MessageManager.sendMessage(this.VKID, Authorization.getInstance().getSTR("BOT-MESSAGES.CODE-INFO").replace("{name}", realName).replace("{code}", this.code).replace("{ip}", this.player.getAddress().getAddress().getHostAddress()));
                this.player.sendMessage(Authorization.getInstance().getSTR("MESSAGES.VK-CODE-SEND"));
                Coding.tocode.put(this, System.currentTimeMillis());
            } else {
                this.address = this.player.getAddress().getAddress().getHostAddress();
                Authorization.getInstance().toServer(this.getPlayer(), Authorization.ServerType.SURVIVAL);
                this.player.sendMessage(Authorization.getInstance().getSTR("MESSAGES.SuccessLogin"));
            }
        } else {
            if (++this.errors >= Authorization.getInstance().getConfig().getInt("MAX-PASSWORD-ERRORS")) {
                    this.player.disconnect(Authorization.getInstance().getSTR("MESSAGES.TooManyLoginAttempts"));
                    if(Authorization.getInstance().isVKEnable())MessageManager.sendMessage(this.VKID, Authorization.getInstance().getSTR("BOT-MESSAGES.BAN-INFO").replace("{name}", realName).replace("{ip}", this.player.getAddress().getAddress().getHostAddress()));
            } else {
                this.player.sendMessage(Authorization.getInstance().getSTR("MESSAGES.WrongPassword").replace("{attempts}", Integer.toString(Authorization.getInstance().getConfig().getInt("MAX-PASSWORD-ERRORS") - this.errors)));
            }
        }
    }

    public boolean needLogin() {
        if (this.lastLogin == 0 || this.password == null || this.address == null || !this.address.equals(this.player.getAddress().getAddress().getHostAddress())) {
            return true;
        }
        return false;
    }

    public void Quit() {
        if (this.isAuth() && !this.isOnCode()) {
            if(this.logout) this.lastLogin = 0;
            else this.lastLogin = System.currentTimeMillis();
            Authorization.getInstance().getAccountBD().updateAccount(this);
        }
        this.clear();
    }

    public void clear() {
        if (Logination.tologin.containsKey(this)) {
            Logination.tologin.remove(this);
        }
        
        if (Coding.tocode.containsKey(this)) {
            Coding.tocode.remove(this);
        }
        
        this.setAuth(false);
        this.setCode(null);
    }

}

