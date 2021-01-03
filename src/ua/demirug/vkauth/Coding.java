package ua.demirug.vkauth;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.BungeeCord;

public class Coding {

    public static ConcurrentHashMap<Account, Long> tocode = new ConcurrentHashMap();

    public static void start() {
        BungeeCord.getInstance().getScheduler().schedule(Authorization.getInstance(), new Runnable(){
            long now;

            @Override
            public void run() {
                this.now = System.currentTimeMillis();
                for (Account acc : Coding.tocode.keySet()) {
                    if ((this.now - Coding.tocode.get(acc)) / 1000L >= 120L) {
                        if (acc.isOnCode()) {
                            acc.getPlayer().disconnect(Authorization.getInstance().getSTR("MESSAGES.CODE-TIME-OUT"));
                        }
                        Coding.tocode.remove(acc);
                        continue;
                    }
                    if (!acc.isOnCode()) continue;
                    acc.getPlayer().sendMessage(Authorization.getInstance().getSTR("MESSAGES.VK-CODE-SEND"));
                }
            }
        }, 0L, 6L, TimeUnit.SECONDS);
    }

}

