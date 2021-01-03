package ua.demirug.vkbot.messages;

import ua.demirug.vkbot.messages.performance.MessageHandler;
import ua.demirug.vkbot.messages.performance.MessageManager;

public class CreatorMessage implements MessageHandler {
    
    @Override
    public void execute(Integer sender, String[] args) {
        MessageManager.sendMessage((int)sender, "> Разработчик бота @demirug 😎😎😎");
    }
}

