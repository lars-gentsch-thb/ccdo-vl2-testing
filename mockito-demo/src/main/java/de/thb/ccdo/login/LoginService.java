package de.thb.ccdo.login;

import java.util.HashMap;
import java.util.Map;

/**
 * Einfache In-Memory-Benutzerverwaltung.
 * In einer echten Anwendung stünde hier eine Datenbank oder ein Identity Provider –
 * genau deshalb wird der Service im Unit-Test des Controllers gemockt.
 */
public class LoginService {

    private final Map<String, String> users = new HashMap<>();

    public void register(String user, String password) {
        users.put(user, password);
    }

    public boolean authenticate(String user, String password) {
        return password != null && password.equals(users.get(user));
    }
}
