package com.arrows_tienda.Listener;

import com.arrows_tienda.Models.Usuario;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class RegistrationCompleteEvent extends ApplicationEvent {

    private Usuario user;

    private String applicationUrl;

    public RegistrationCompleteEvent(Usuario user, String applicationUrl) {
        super(user);
        this.user = user;
        this.applicationUrl = applicationUrl;
    }


}
