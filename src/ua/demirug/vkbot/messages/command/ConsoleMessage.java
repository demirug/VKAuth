package ua.demirug.vkbot.messages.command;

import java.util.List;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import ua.demirug.vkbot.messages.performance.MessageHandler;
import ua.demirug.vkbot.messages.performance.MessageManager;

public class ConsoleMessage implements MessageHandler {
    
    private List<Integer> allow;

    public ConsoleMessage(List<Integer> allowed) {
        this.allow = allowed;
    }

    @Override
    public void execute(Integer sender, String[] args) {
        if (!this.allow.contains(sender)) {
            MessageManager.sendMessage((int)sender, "\u0423 \u0432\u0430\u0441 \u043d\u0435\u0442 \u0434\u043e\u0441\u0442\u0443\u043f\u0430 \u043a \u0434\u0430\u043d\u043d\u043e\u0439 \u043a\u043e\u043c\u0430\u043d\u0434\u0435!");
            return;
        }
        if (args.length == 0) {
            MessageManager.sendMessage((int)sender, "\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /command [\u041a\u043e\u043c\u0430\u043d\u0434\u0430]");
            return;
        }
        StringBuilder builder = new StringBuilder();
        for (String str : args) {
            builder.append(str).append(" ");
        }
        String command = builder.toString().substring(0, builder.toString().length() - 1);
        MessageManager.sendMessage((int)sender, "\u041a\u043e\u043c\u0430\u043d\u0434\u0430 " + command + " \u043e\u0442\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u0430 \u043d\u0430 \u0432\u044b\u043f\u043e\u043b\u043d\u0435\u043d\u0438\u0435. \u041e\u0442\u0432\u0435\u0442:");
        ConsoleSender consoleSender = new ConsoleSender();
        BungeeCord.getInstance().getPluginManager().dispatchCommand((CommandSender)consoleSender, command);
        MessageManager.sendMessage((int)sender, ChatColor.stripColor((String)consoleSender.getBuffer().toString()));
    }
}

