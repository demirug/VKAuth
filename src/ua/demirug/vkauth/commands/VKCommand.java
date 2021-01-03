package ua.demirug.vkauth.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import ua.demirug.vkauth.Authorization;

public class VKCommand extends Command {
    
    public VKCommand() {
        super("vk", null, new String[] {"vkontakte"});
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage("§6Authorization §7> §fТолько для игроков");
            return;
        }
        sender.sendMessage(Authorization.getInstance().getSTR("MESSAGES.VkCommandOut").replace("{playername}", sender.getName()));
    }
}

