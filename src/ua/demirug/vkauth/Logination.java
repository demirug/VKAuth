package ua.demirug.vkauth;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.BungeeCord;

public class Logination {

    public static ConcurrentHashMap<Account, Long> tologin = new ConcurrentHashMap();

    public static void start() {
        BungeeCord.getInstance().getScheduler().schedule(Authorization.getInstance(), new Runnable(){
            long now;

            @Override
            public void run() {
                this.now = System.currentTimeMillis();
                for (Account acc : Logination.tologin.keySet()) {
                    if ((this.now - Logination.tologin.get(acc)) / 1000L >= 120L) {
                        if (!acc.isAuth()) {
                            acc.getPlayer().disconnect(Authorization.getInstance().getSTR("MESSAGES.RegistrationTimeOut"));
                        }
                        Logination.tologin.remove(acc);
                        continue;
                    }
                    if (acc.isAuth()) continue;
                    acc.getPlayer().sendMessage(Authorization.getInstance().getSTR("MESSAGES.Login"));
                    Authorization.getInstance().getTitle().sendLogin(acc.getPlayer());
                }
            }
        }, 0L, 6L, TimeUnit.SECONDS);
    }

}

