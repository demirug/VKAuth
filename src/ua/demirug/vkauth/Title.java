package ua.demirug.vkauth;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Title {

    private net.md_5.bungee.api.Title registerTitle, loginTitle;

    public Title(boolean bln) {
        if (bln) {
            this.registerTitle = Title.createTitle("REGISTER");
            this.loginTitle = Title.createTitle("LOGIN");
        }
    }

    private static net.md_5.bungee.api.Title createTitle(String type) {
        net.md_5.bungee.api.Title title = BungeeCord.getInstance().createTitle();
        title.title(TextComponent.fromLegacyText((String)ChatColor.translateAlternateColorCodes((char)'&', (String)Authorization.getInstance().getConfig().getString("TITLES." + type + ".TITLE"))));
        title.subTitle(TextComponent.fromLegacyText((String)ChatColor.translateAlternateColorCodes((char)'&', (String)Authorization.getInstance().getConfig().getString("TITLES." + type + ".SUBTITLE"))));
        title.fadeIn(0);
        title.fadeOut(0);
        title.stay(120);
        return title;
    }

    public void sendLogin(ProxiedPlayer player) {
        if (this.loginTitle != null) {
            this.loginTitle.send(player);
        }
    }

    public void sendRegister(ProxiedPlayer player) {
        if (this.registerTitle != null) {
            this.registerTitle.send(player);
        }
    }
}

