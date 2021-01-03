package ua.demirug.vkbot.messages;

import ua.demirug.vkauth.Account;
import ua.demirug.vkauth.Authorization;
import ua.demirug.vkbot.messages.performance.MessageHandler;
import ua.demirug.vkbot.messages.performance.MessageManager;

public class AuthorizationMessage implements MessageHandler {
    
    @Override
    public void execute(Integer sender, String[] args) {
        
        if (args.length < 2) {
            MessageManager.sendMessage((int)sender, Authorization.getInstance().getSTR("BOT-MESSAGES.AUTHORIZATION-HELP"));
            return;
        }
        
        boolean status = true, error = false;
        
        switch(args[1].toLowerCase()) {
            case "вкл":
            case "включить":
            case "on":
                status = true;
                break;
            case "выкл":
            case "выключить":
            case "off":
                status = false;
                break;
            default:
                error = true;
                break;
        }
        
        if(error) {
            MessageManager.sendMessage((int)sender, Authorization.getInstance().getSTR("BOT-MESSAGES.AUTHORIZATION-INCORRECT-TYPE"));
            return;
        }
        
        Account acc = Authorization.getInstance().getAccountBD().getAccount(args[0]);
    
        if(acc == null) {
            MessageManager.sendMessage((int)sender, Authorization.getInstance().getSTR("BOT-MESSAGES.AUTHORIZATION-ACCOUNT-NOT-FOUND"));
            return;
        }
        
        if(acc.getVKID() != sender) {
            MessageManager.sendMessage((int)sender, Authorization.getInstance().getSTR("BOT-MESSAGES.AUTHORIZATION-NOT-YOUR").replace("{name}", acc.getRealName()));
            return;
        }
        
        acc.setVKLogin(status);
        Authorization.getInstance().getAccountBD().updateAccount(acc);
        MessageManager.sendMessage((int)sender, Authorization.getInstance().getSTR("BOT-MESSAGES.AUTHORIZATION-CHANGED").replace("{name}", acc.getRealName()).replace("{status}", status == true ? "включена" : "отключена"));
    }
}

