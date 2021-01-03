package ua.demirug.vkbot.messages;

import ua.demirug.vkauth.Account;
import ua.demirug.vkauth.AccountManager;
import ua.demirug.vkauth.Authorization;
import ua.demirug.vkbot.messages.performance.MessageHandler;
import ua.demirug.vkbot.messages.performance.MessageManager;

public class BindMessage implements MessageHandler {
    
    @Override
    public void execute(Integer sender, String[] args) {
        
        if (args.length == 0) {
            MessageManager.sendMessage((int)sender, Authorization.getInstance().getSTR("BOT-MESSAGES.BIND-HELP"));
            return;
        }
        if (args[0].length() < 4 || args[0].length() > 16) {
            MessageManager.sendMessage((int)sender, Authorization.getInstance().getSTR("BOT-MESSAGES.NOT-FORMAT"));
            return;
        }

        Account acc = AccountManager.getAccount(args[0]);
        
        if (acc == null) {
            MessageManager.sendMessage((int)sender, Authorization.getInstance().getSTR("BOT-MESSAGES.BIND-NULL").replace("{name}", args[0]));
            return;
        }
        if (acc.hasVKID()) {
            MessageManager.sendMessage((int)sender, Authorization.getInstance().getSTR("BOT-MESSAGES.BIND-HAS").replace("{name}", args[0]));
            return;
        }
        
        if (Authorization.getInstance().getAccountBD().getAccountByVKAmmount(sender) >= Authorization.getInstance().getConfig().getInt("VK-MODULE.MAX-USERS-PER-ACOOUNT")) {
            MessageManager.sendMessage((int)sender, Authorization.getInstance().getSTR("BOT-MESSAGES.BIND-USED").replace("{name}", args[0]));
            return;
        }
            
        if (!acc.isAuth()) {
            MessageManager.sendMessage((int)sender, Authorization.getInstance().getSTR("BOT-MESSAGES.BIND-NO-LOGIN").replace("{name}", args[0]));
            return;
        }
           
        String code = Authorization.getInstance().generateCode(Authorization.getInstance().getConfig().getInt("VK-MODULE.CODE-LENGTH"));
        ConfirmMessage.confirms.put(sender, new Object[] {code, acc});
        acc.getPlayer().sendMessage(Authorization.getInstance().getSTR("MESSAGES.Code").replace("{code}", code));
        MessageManager.sendMessage((int)sender, Authorization.getInstance().getSTR("BOT-MESSAGES.BIND-SEND").replace("{name}", args[0]));
        }
    }