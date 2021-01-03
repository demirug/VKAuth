package ua.demirug.vkbot.messages;

import ua.demirug.vkauth.Account;
import ua.demirug.vkauth.Authorization;
import ua.demirug.vkbot.messages.performance.MessageHandler;
import ua.demirug.vkbot.messages.performance.MessageManager;

public class UnlinkMessage implements MessageHandler {
    
    @Override
    public void execute(Integer sender, String[] args) {
        
        if (args.length < 1) {
            MessageManager.sendMessage((int)sender, Authorization.getInstance().getSTR("BOT-MESSAGES.UNLINK-HELP"));
            return;
        }
        
        Account acc = Authorization.getInstance().getAccountBD().getAccount(args[0]);
    
        if(acc == null) {
            MessageManager.sendMessage((int)sender, Authorization.getInstance().getSTR("BOT-MESSAGES.UNLINK-NULL"));
            return;
        }
        
        if(acc.getVKID() != sender) {
            MessageManager.sendMessage((int)sender, Authorization.getInstance().getSTR("BOT-MESSAGES.UNLINK-NOT-YOUR").replace("{name}", acc.getRealName()));
            return;
        }
        
        acc.setVKID(-1);
        acc.setVKLogin(true);
        Authorization.getInstance().getAccountBD().updateAccount(acc);
        MessageManager.sendMessage((int)sender, Authorization.getInstance().getSTR("BOT-MESSAGES.UNLINK-DONE").replace("{name}", acc.getRealName()));
    }
}

