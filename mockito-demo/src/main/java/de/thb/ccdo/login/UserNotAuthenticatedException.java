package de.thb.ccdo.login;

public class UserNotAuthenticatedException extends RuntimeException {

    public UserNotAuthenticatedException(String user) {
        super("Login fehlgeschlagen für Benutzer: " + user);
    }
}
