package de.thb.ccdo.login;

public class LoginController {

    private final LoginService service;

    public LoginController(LoginService service) {
        this.service = service;
    }

    public void doLogin(String user, String password) {
        if (!service.authenticate(user, password)) {
            throw new UserNotAuthenticatedException(user);
        }
    }
}
