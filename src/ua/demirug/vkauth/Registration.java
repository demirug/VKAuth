package ua.demirug.vkauth;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Registration {

    public static ConcurrentHashMap<ProxiedPlayer, Long> toregister = new ConcurrentHashMap();

    public static void start() {
        BungeeCord.getInstance().getScheduler().schedule(Authorization.getInstance(), new Runnable(){
            long now;

            @Override
            public void run() {
                this.now = System.currentTimeMillis();
                for (ProxiedPlayer player : Registration.toregister.keySet()) {
                    if ((this.now - Registration.toregister.get(player)) / 1000L >= 120L) {
                        player.disconnect(Authorization.getInstance().getSTR("MESSAGES.RegistrationTimeOut"));
                        Registration.toregister.remove(player);
                        continue;
                    }
                    if (AccountManager.getAccount(player.getName()) != null) continue;
                    player.sendMessage(Authorization.getInstance().getSTR("MESSAGES.Register"));
                    Authorization.getInstance().getTitle().sendRegister(player);
                }
            }
        }, 0L, 6L, TimeUnit.SECONDS);
    }

}

