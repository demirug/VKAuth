package ua.demirug.vkauth;

import java.util.concurrent.ConcurrentHashMap;

public class AccountManager {
    public static ConcurrentHashMap<String, Account> accounts = new ConcurrentHashMap();

    public static Account createAccount(String name, String realName, String password, Integer vk_id, boolean vklogin, long lastlogin, String address, boolean locked) {
        Account account = new Account(name, realName, password, vk_id,vklogin, lastlogin, address, locked);
        accounts.put(name.toLowerCase(), account);
        return account;
    }

    public static Account getAccount(String name) {
        return accounts.get(name.toLowerCase());
    }
}

