package ua.demirug.vkbot.messages;

import java.util.List;
import ua.demirug.vkauth.Authorization;
import ua.demirug.vkbot.messages.performance.MessageHandler;
import ua.demirug.vkbot.messages.performance.MessageManager;

public class AccountsMessage implements MessageHandler {
    
    @Override
    public void execute(Integer sender, String[] args) {
        
       List<String> names = Authorization.getInstance().getAccountBD().getAccountsByVKID(sender);
       
       if(names.isEmpty()) {
           MessageManager.sendMessage((int)sender, Authorization.getInstance().getSTR("BOT-MESSAGES.ACCOUNTS-NULL"));
       } else {
           StringBuilder builder = new StringBuilder();
           
           for(int i = 0; i < names.size(); i++) {
               builder.append(names.get(i));
               if(i + 1 != names.size()) {
                   builder.append(", ");
               }
           }
           
           MessageManager.sendMessage((int)sender, Authorization.getInstance().getSTR("BOT-MESSAGES.ACCOUTS").replace("{accounts}", builder.toString()));
       }
       
        
    }
}

