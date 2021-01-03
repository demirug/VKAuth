package ua.demirug.vkbot.messages;

import ua.demirug.vkauth.Account;
import ua.demirug.vkauth.Authorization;
import ua.demirug.vkbot.messages.performance.MessageHandler;
import ua.demirug.vkbot.messages.performance.MessageManager;

public class RecoveryMessage implements MessageHandler {
    
    @Override
    public void execute(Integer sender, String[] args) {
        
        if (args.length == 0) {
            MessageManager.sendMessage((int)sender, Authorization.getInstance().getSTR("BOT-MESSAGES.RECOVERY-HELP"));
            return;
        }
        
        if (args[0].length() < 4 || args[0].length() > 16) {
            MessageManager.sendMessage((int)sender, Authorization.getInstance().getSTR("BOT-MESSAGES.NOT-FORMAT"));
            return;
        }
        
        Account acc = Authorization.getInstance().getAccountBD().getAccount(args[0]);
        
        if (acc == null) {
            MessageManager.sendMessage((int)sender, Authorization.getInstance().getSTR("BOT-MESSAGES.RECOVERY-NULL").replace("{name}", args[0]));
            return;
        }
        
        if (acc.isLocked()) {
            MessageManager.sendMessage((int)sender, Authorization.getInstance().getSTR("MESSAGES.BLOCKED").replace("{name}", args[0]));
            return;
        }      
        
        if (acc.getVKID() != sender) {
            MessageManager.sendMessage((int)sender, Authorization.getInstance().getSTR("BOT-MESSAGES.RECOVERY-NOT-YOUR").replace("{name}", args[0]));
            return;
        }
        
        if (acc.isOnline()) {
            acc.setLogOut(true);
            acc.getPlayer().disconnect();
        }
        
        String password = Authorization.getInstance().generateCode(8);
        acc.setPassword(password);
        Authorization.getInstance().getAccountBD().updateAccount(acc);
        
        MessageManager.sendMessage(sender, Authorization.getInstance().getSTR("BOT-MESSAGES.RECOVERY-DONE").replace("{name}", args[0]).replace("{pass}", password));
    }
}

