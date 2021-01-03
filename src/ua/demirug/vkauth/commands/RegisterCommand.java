package ua.demirug.vkauth.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import ua.demirug.vkauth.Account;
import ua.demirug.vkauth.AccountManager;
import ua.demirug.vkauth.Authorization;
import ua.demirug.vkauth.Registration;

public class RegisterCommand extends Command {
    
    public RegisterCommand() {
        super("register", null, new String[]{"reg"});
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage("§6Authorization §7> §fТолько для игроков");
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer)sender;
        Account user = AccountManager.getAccount(player.getName());
        if (user != null) {
            player.sendMessage(Authorization.getInstance().getSTR("MESSAGES.AlreadyRegistered"));
            return;
        }
        if (args.length < 1) {
            player.sendMessage(Authorization.getInstance().getSTR("MESSAGES.RegisterInfo"));
            return;
        }
        if (args[0].replace("[", "").replace("]", "").length() < 4) {
            player.sendMessage(Authorization.getInstance().getSTR("MESSAGES.PasswordMinLenght"));
            return;
        }
        if (args[0].replace("[", "").replace("]", "").length() > 16) {
            player.sendMessage(Authorization.getInstance().getSTR("MESSAGES.PasswordMaxLenght"));
            return;
        }
        if (Authorization.getInstance().checkString("abcdefghijklmnopqrstuvwxyz_0123456789", args[0].replace("[", "").replace("]", ""))) {
            player.sendMessage(Authorization.getInstance().getSTR("MESSAGES.PasswordIncorrectSymbols"));
            return;
        }
        
            if(Authorization.getInstance().getAccountBD().IPregAmmount(player.getAddress().getAddress().getHostAddress()) >= Authorization.getInstance().getConfig().getInt("MAX-ACCOUTS-PER-IP")) {
                 player.disconnect(Authorization.getInstance().getSTR("MESSAGES.MaxAccoutsPerIP"));
                 return;
             }
        
        Account acc = AccountManager.createAccount(player.getName(),player.getName(), Authorization.getInstance().getHashManager().toHash(args[0].replace("[", "").replace("]", "")), -1, true, System.currentTimeMillis(), player.getAddress().getAddress().getHostAddress(), false);
        acc.setAuth(true);
        acc.setPlayer(player);
        Authorization.getInstance().getAccountBD().insertAccount(acc);
        Authorization.getInstance().toServer(player, Authorization.ServerType.SURVIVAL);
        Registration.toregister.remove(player);
        player.sendMessage(Authorization.getInstance().getSTR("MESSAGES.SuccessRegister"));
        if (Authorization.getInstance().isVKEnable()) {
            player.sendMessage(Authorization.getInstance().getSTR("MESSAGES.VKRecomended"));
        }
    }
}

