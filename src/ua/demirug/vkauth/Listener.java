package ua.demirug.vkauth;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.event.EventHandler;

public class Listener implements net.md_5.bungee.api.plugin.Listener {
    
    @EventHandler
    public void ServerConnectEvent(ServerConnectEvent e) {
        if (e.getPlayer().getServer() == null) {
            return;
        }
        Account acc = AccountManager.getAccount(e.getPlayer().getName());
        if (acc == null || !acc.isAuth() || (acc.hasVKID() && acc.isOnCode())) {
            if(!Authorization.getInstance().getAuthServer().contains(e.getTarget()))
                e.setCancelled(true);
        }
    }

    @EventHandler(priority=-64)
    public void ServerSwitchEvent(ServerSwitchEvent e) {
        if (!e.getPlayer().isConnected()) {
            return;
        }
        ProxiedPlayer player = e.getPlayer();
        
        Authorization.getInstance().getAccountBD().loadAccount(player.getName());
        Account acc = AccountManager.getAccount(player.getName());
        
        if (acc != null && acc.getPlayer() == null) {
            acc.setPlayer(player);
        }
        
        if (acc != null) {
            if (acc.isAuth()) {
                return;
            }
            
            if(acc.isLocked()) {
                e.getPlayer().disconnect(Authorization.getInstance().getSTR("MESSAGES.BLOCKED"));
                return;
            }
            
            if(acc.getRealName() == null) {
                acc.setRealName(e.getPlayer().getName());
            } else {
                if(!acc.getRealName().equals(e.getPlayer().getName())) {
                e.getPlayer().disconnect(Authorization.getInstance().getSTR("MESSAGES.Not-RealName").replace("{name}", e.getPlayer().getName()).replace("{realname}", acc.getRealName()));
                return;  
                }
            }
            
            
            if (acc.needLogin()) {
                if(!Logination.tologin.containsKey(acc)) {
                    Authorization.getInstance().toServer(player, Authorization.ServerType.AUTH);
                    Logination.tologin.put(acc, System.currentTimeMillis());
                    player.sendMessage(Authorization.getInstance().getSTR("MESSAGES.Login"));
                }
            } else {
                acc.setAuth(true);
                Authorization.getInstance().toServer(acc.getPlayer(), Authorization.ServerType.SURVIVAL);
                if (Authorization.getInstance().isVKEnable() && !acc.hasVKID()) {
                    player.sendMessage(Authorization.getInstance().getSTR("MESSAGES.VKRecomended"));
                }
            }
        } else {
            if (Authorization.getInstance().checkString("абвгдеёжзийклмнопрстуфхцчшщъыьэюяabcdefghijklmnopqrstuvwxyz_0123456789", e.getPlayer().getName())) {
                e.getPlayer().disconnect(Authorization.getInstance().getSTR("MESSAGES.NameIncorrectSymbols"));
                return;
            }
            Registration.toregister.put(player, System.currentTimeMillis());
            Authorization.getInstance().toServer(player, Authorization.ServerType.AUTH);
            player.sendMessage(Authorization.getInstance().getSTR("MESSAGES.Register"));
        }
    }

    @EventHandler
    public void Chat(ChatEvent e) {
        if (e.isCancelled()) {
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer)e.getSender();
        Account acc = AccountManager.getAccount(player.getName());
        if (!(acc != null && acc.isAuth() && (!acc.hasVKID() || !acc.isOnCode()) || e.getMessage().toLowerCase().startsWith("/l ") || e.getMessage().toLowerCase().startsWith("/login ") || e.getMessage().toLowerCase().startsWith("/reg ") || e.getMessage().toLowerCase().startsWith("/register ") || e.getMessage().toLowerCase().startsWith("/code "))) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void Disconnect(PlayerDisconnectEvent e) {
        ProxiedPlayer player = e.getPlayer();
        Account acc = AccountManager.getAccount(player.getName());
        if (acc == null) {
           if(Registration.toregister.contains(player)) Registration.toregister.remove(player);
        } else {
            acc.Quit();
            AccountManager.accounts.remove(e.getPlayer().getName().toLowerCase());
        }
    }

}

