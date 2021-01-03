package ua.demirug.vkbot.messages;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import net.jodah.expiringmap.ExpiringMap;
import ua.demirug.vkauth.Account;
import ua.demirug.vkauth.Authorization;
import ua.demirug.vkbot.messages.performance.MessageHandler;
import ua.demirug.vkbot.messages.performance.MessageManager;

public class ConfirmMessage implements MessageHandler {
    
    public static Map<Integer, Object[]> confirms = ExpiringMap.builder().expiration(5, TimeUnit.MINUTES).build();
    
    @Override
    public void execute(Integer sender, String[] args) {
        if (args.length == 0) {
            MessageManager.sendMessage((int)sender, Authorization.getInstance().getSTR("BOT-MESSAGES.CONFIRM-HELP"));
            return;
        }
        if (!confirms.containsKey(sender)) {
            MessageManager.sendMessage((int)sender, Authorization.getInstance().getSTR("BOT-MESSAGES.CONFIRM-ERROR"));
            return;
        }
        
        Object[] data = confirms.get(sender);
        
        if(!data[0].equals(args[0])) {
            MessageManager.sendMessage((int)sender, Authorization.getInstance().getSTR("BOT-MESSAGES.CONFIRM-ERROR"));
            return;
        }
        
        if (Authorization.getInstance().getAccountBD().getAccountByVKAmmount(sender) >= Authorization.getInstance().getConfig().getInt("VK-MODULE.MAX-USERS-PER-ACOOUNT")) {
            MessageManager.sendMessage(sender, Authorization.getInstance().getSTR("BOT-MESSAGES.BIND-USED").replace("{name}", args[0]));
            return;
        }
        
        Account acc = (Account) data[1];
        confirms.remove(sender);
        acc.setVKID(sender);
        Authorization.getInstance().getAccountBD().updateAccount(acc);
        
        MessageManager.sendMessage((int)sender, Authorization.getInstance().getSTR("BOT-MESSAGES.CONFIRM-DONE").replace("{name}", acc.getPlayerName()));
    }
}

