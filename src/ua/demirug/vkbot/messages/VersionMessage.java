package ua.demirug.vkbot.messages;

import ua.demirug.vkauth.Authorization;
import ua.demirug.vkbot.messages.performance.MessageHandler;
import ua.demirug.vkbot.messages.performance.MessageManager;

public class VersionMessage implements MessageHandler {
    
    @Override
    public void execute(Integer sender, String[] args) {
        MessageManager.sendMessage((int)sender, "> Версия бота " +  Authorization.getInstance().getDescription().getVersion());
    }
}

