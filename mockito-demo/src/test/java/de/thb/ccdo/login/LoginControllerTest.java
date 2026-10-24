package de.thb.ccdo.login;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

    @Mock
    LoginService service;

    @InjectMocks
    LoginController controller;

    @Test
    void rejectsWrongPassword() {
        when(service.authenticate("hans", "falsch")).thenReturn(false);

        assertThrows(UserNotAuthenticatedException.class,
                () -> controller.doLogin("hans", "falsch"));
        verify(service).authenticate("hans", "falsch");
    }

    @Test
    void acceptsCorrectPassword() {
        when(service.authenticate("hans", "geheim")).thenReturn(true);

        assertDoesNotThrow(() -> controller.doLogin("hans", "geheim"));
        verify(service).authenticate("hans", "geheim");
    }
}
