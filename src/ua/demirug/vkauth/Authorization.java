package ua.demirug.vkauth;

import ua.demirug.vkauth.commands.LiveCommand;
import ua.demirug.vkauth.commands.AuthCommand;
import ua.demirug.vkauth.database.DataBase;
import com.petersamokhin.bots.sdk.clients.Group;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.varia.NullAppender;
import ua.demirug.vkauth.commands.AChangePassCommand;
import ua.demirug.vkauth.commands.BlockCommand;
import ua.demirug.vkauth.commands.ChangePasswordCommand;
import ua.demirug.vkauth.commands.CodeCommand;
import ua.demirug.vkauth.commands.LogOutCommand;
import ua.demirug.vkauth.commands.LoginCommand;
import ua.demirug.vkauth.commands.RegisterCommand;
import ua.demirug.vkauth.commands.ReloadCFGCommand;
import ua.demirug.vkauth.commands.UnBlockCommand;
import ua.demirug.vkauth.commands.VKCommand;
import ua.demirug.vkauth.database.AccountBD;
import ua.demirug.vkauth.database.PermissionsBD;
import ua.demirug.vkbot.messages.AccountsMessage;
import ua.demirug.vkbot.messages.AuthorizationMessage;
import ua.demirug.vkbot.messages.BindMessage;
import ua.demirug.vkbot.messages.ConfirmMessage;
import ua.demirug.vkbot.messages.CreatorMessage;
import ua.demirug.vkbot.messages.RecoveryMessage;
import ua.demirug.vkbot.messages.UnlinkMessage;
import ua.demirug.vkbot.messages.VersionMessage;
import ua.demirug.vkbot.messages.command.ConsoleMessage;
import ua.demirug.vkbot.messages.performance.MessageManager;

public class Authorization extends Plugin {
    
    private static Authorization manager;
    private Thread purge, autobackup;
    private DataBase accountBD, permissionsBD;
    private Configuration config;
    private HashManager hash;
    private Title title;
    private ConcurrentHashMap<ServerInfo, Boolean> serverData;
    private List<ServerInfo> auth = new ArrayList<>(), survival = new ArrayList<>();
    private SecureRandom rad = new SecureRandom();
    private Group group;
    private final Comparator sortMethod = new Comparator<ServerInfo>() {
        @Override
        public int compare(ServerInfo o1, ServerInfo o2) {
        
            if(serverData.containsKey(o1) && serverData.get(o1) == false) {
                return 1;
            }
            
            if(serverData.containsKey(o2) && serverData.get(o2) == false) {
                return -1;
            }
            
            if(o1.getPlayers().size() < o2.getPlayers().size()) return -1;
            else if(o1.getPlayers().size() > o2.getPlayers().size()) return 1;
            else return 0;
       
        }
    };
    
    @Override
    public void onEnable() {
        BasicConfigurator.configure(new NullAppender());
        manager = this;
        serverData = new ConcurrentHashMap();
        this.config = this.loadConfig();
        this.hash = new HashManager(this.config.getString("HASH"));
        this.loadServers();
        this.accountBD = new AccountBD(this.config.getString("MySQL.url"), this.config.getString("MySQL.username"), this.config.getString("MySQL.password"));
        this.title = new Title(this.config.getBoolean("TITLES.ENABLE"));
        Logination.start();
        Registration.start();
        Coding.start();
        if (this.isVKEnable()) {
            Coding.start();
        }
        
        this.getProxy().getPluginManager().registerCommand(this, new LoginCommand());
        this.getProxy().getPluginManager().registerCommand(this, new RegisterCommand());
        this.getProxy().getPluginManager().registerCommand(this, new ChangePasswordCommand());
        this.getProxy().getPluginManager().registerCommand(this, new LogOutCommand());
        this.getProxy().getPluginManager().registerCommand(this, new AuthCommand());
        this.getProxy().getPluginManager().registerCommand(this, new AChangePassCommand());
        this.getProxy().getPluginManager().registerCommand(this, new BlockCommand());
        this.getProxy().getPluginManager().registerCommand(this, new UnBlockCommand());
        this.getProxy().getPluginManager().registerCommand(this, new ReloadCFGCommand());
        
        if (this.isVKEnable()) {
            this.getProxy().getPluginManager().registerCommand(this, new VKCommand());
            this.getProxy().getPluginManager().registerCommand(this, new LiveCommand());
            this.getProxy().getPluginManager().registerCommand(this, new CodeCommand());
        }
        
        this.getProxy().getPluginManager().registerListener(this, new Listener());
        MessageManager.registerMessage(new CreatorMessage(), "developer", "creator", "создатель", "хозяин", "разраб", "разработчик");
        MessageManager.registerMessage(new VersionMessage(), "версия", "version");
        MessageManager.registerMessage(new BindMessage(), "привязать");
        MessageManager.registerMessage(new ConfirmMessage(), "подтвердить");
        MessageManager.registerMessage(new RecoveryMessage(), "восстановить");
        MessageManager.registerMessage(new AccountsMessage(), "аккаунты");
        MessageManager.registerMessage(new UnlinkMessage(), "отвязать");
        MessageManager.registerMessage(new AuthorizationMessage(), "авторизация");
        MessageManager.registerMessage(new ConsoleMessage(this.config.getIntList("VK-MODULE.ADMINS")), "console", "консоль");
        
        for (String string : this.config.getSection("VK-MODULE.CUSTOM-MESSAGES").getKeys()) {
            MessageManager.registerSubMessage(string, this.config.getString("VK-MODULE.CUSTOM-MESSAGES." + string).replace("{new}", "\n"));
        }
        
        for (String string : this.config.getSection("VK-MODULE.KEYS-ANSWER").getKeys()) {
            MessageManager.registerKeys(this.config.getStringList("VK-MODULE.KEYS-ANSWER." + string + ".KEYS"), this.config.getString("VK-MODULE.KEYS-ANSWER." + string + ".ANSWER").replace("{new}", "\n"));
        }
        
        if (this.isVKEnable()) {
            this.initGroup();
            this.startAutoStart();
        }
        
        if(this.config.getBoolean("PURGE")) {
            this.permissionsBD = new PermissionsBD(this.config.getString("PermissionsBD.url"), this.config.getString("PermissionsBD.username"), this.config.getString("PermissionsBD.password"));
            this.startPurge();
        }
        
        if(this.isOnlineChecker()) {
            this.startOnlineChecker();
        }
        
    }

    public boolean isOnlineChecker() {
        return this.config.getBoolean("CHECK-FOR-ONLINE.ENABLE");
    }
    
    private void startOnlineChecker() {
        BungeeCord.getInstance().getScheduler().schedule(this, new Runnable(){
            @Override
            public void run() {
                for(ServerInfo sv : auth) {
                    sv.ping((sinfo, trouble) -> {
                        serverData.put(sv, trouble == null);
                    });
                } 
                
                for(ServerInfo sv : survival) {
                    sv.ping((sinfo, trouble) -> {
                       serverData.put(sv, trouble == null);
                    }); 
                } 
                
            }
        }, 0L, this.config.getInt("CHECK-FOR-ONLINE.TIME"), TimeUnit.SECONDS); 
    }
    
    public void initGroup() {
            this.group = new Group(this.config.getInt("VK-MODULE.ID"), this.config.getString("VK-MODULE.TOKEN"));
            this.group.onSimpleTextMessage(message -> MessageManager.handle(message));
    }
    
    public void startAutoStart() {
        
        this.autobackup = new Thread(new Runnable() {
            @Override
            public void run() {
            
                while(true) {
                    try {
                         getGroup().longPoll().off();
                         initGroup();
                        TimeUnit.HOURS.sleep(1);
                    } catch (InterruptedException ex) {}
                }
                
            }
        });
        this.autobackup.setName("START-THREAD");
        this.autobackup.start();
    }
    
    public void startPurge() {
        
        this.purge = new Thread(new Runnable() {
            @Override
            public void run() {
            
                while(true) {
                    try {
                        TimeUnit.HOURS.sleep(8);
                        ((AccountBD)Authorization.getInstance().accountBD).purgeUnuse();
                    } catch (InterruptedException ex) {
                       ex.printStackTrace();
                    }
                }
                
            }
        });
        this.purge.setName("PURGE-THREAD");
        this.purge.start();
        
    }
    
    public static Authorization getInstance() {
        return manager;
    }

    public boolean isVKEnable() {
        return this.config.getBoolean("VK-MODULE.ENABLE");
    }

    public Configuration getConfig() {
        return this.config;
    }

    public List<ServerInfo> getAuthServer() {
        return this.auth;
    }

    public Title getTitle() {
        return this.title;
    }

    public HashManager getHashManager() {
        return this.hash;
    }

    private void loadServers() {
        ServerInfo server = null;
        for (String auths : this.config.getStringList("AUTH")) {
            server = BungeeCord.getInstance().getServerInfo(auths);
            if (server == null) {
                System.out.println(" ");
                System.out.println("§6Authorization §7> §fСервер §c" + auths + " §fотсутствует! (AUTH)");
                System.out.println("§6Authorization §7> §fНастройте конфигурацию!");
                System.out.println(" ");
                continue;
            }
            this.auth.add(server);
            this.serverData.put(server, Boolean.TRUE);
        }
        for (String survivals : this.config.getStringList("SURVIVAL")) {
            server = BungeeCord.getInstance().getServerInfo(survivals);
            if (server == null) {
                System.out.println(" ");
                System.out.println("§6Authorization §7> §fСервер §c" + survivals + " §fотсутствует! (SURVIVAL)");
                System.out.println("§6Authorization §7> §fНастройте конфигурацию!");
                System.out.println(" ");
                continue;
            }
            this.survival.add(server);
            this.serverData.put(server, Boolean.TRUE);
        }
    }

    public String generateCode(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            sb.append("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(this.rad.nextInt("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".length())));
        }
        return sb.toString();
    }

    public void reloadConfig() {
        try {
            this.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(this.getDataFolder(), "config.yml"));
        } catch (IOException ex) {
           ex.printStackTrace();
        }
        
        MessageManager.clear();
        for (String string : this.config.getSection("VK-MODULE.CUSTOM-MESSAGES").getKeys()) {
            MessageManager.registerSubMessage(string, this.config.getString("VK-MODULE.CUSTOM-MESSAGES." + string).replace("{new}", "\n"));
        }
        
        for (String string : this.config.getSection("VK-MODULE.KEYS-ANSWER").getKeys()) {
            MessageManager.registerKeys(this.config.getStringList("VK-MODULE.KEYS-ANSWER." + string + ".KEYS"), this.config.getString("VK-MODULE.KEYS-ANSWER." + string + ".ANSWER").replace("{new}", "\n"));
        }
        
        this.title = new Title(this.config.getBoolean("TITLES.ENABLE"));
        this.hash = new HashManager(this.config.getString("HASH"));
    }
    
    private Configuration loadConfig() {
        try {
            File file;
            if (!this.getDataFolder().exists()) {
                this.getDataFolder().mkdir();
            }
            if (!(file = new File(this.getDataFolder(), "config.yml")).exists()) {
                InputStream in = this.getResourceAsStream("config.yml");
                Files.copy(in, file.toPath(), new CopyOption[0]);
            }
            return ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(this.getDataFolder(), "config.yml"));
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public AccountBD getAccountBD() {
        return (AccountBD)this.accountBD;
    }
    
    public PermissionsBD getPermissionsBD() {
        return (PermissionsBD) this.permissionsBD;
    }

    public Group getGroup() {
        return this.group;
    }

    public boolean checkString(String allow, String check) {
        for (char c : check.toLowerCase().toCharArray()) {
            if (allow.contains(String.valueOf(c))) continue;
            return true;
        }
        return false;
    }

    public String getSTR(String path) {
        return ChatColor.translateAlternateColorCodes((char)'&', (String)this.config.getString(path).replace("{new}", "\n"));
    }

    public void toServer(ProxiedPlayer player, ServerType type) {
        List<ServerInfo> servers = type == ServerType.AUTH ? this.auth : this.survival;
        Collections.sort(servers, sortMethod);
        
        ServerInfo now = null;
        
        if(!servers.isEmpty()) now = servers.get(0);
        
        if (now == null || (this.isOnlineChecker() && this.serverData.get(now) == false)) {
            player.disconnect(this.getSTR("MESSAGES.ConnectError").replace("{type}", type.toString()));
            return;
        }
        
        if (player == null || player.getServer() != null && player.getServer().getInfo().equals(now)) {
            return;
        }
        player.connect(now);
    }

    public static enum ServerType {
        SURVIVAL,
        AUTH;
    }

}

