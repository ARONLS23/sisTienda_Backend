package com.arrows_tienda.Listener;

import com.arrows_tienda.Models.Usuario;
import com.arrows_tienda.Service.impl.AuthServiceImpl;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {

    private static final Logger log = LoggerFactory.getLogger(RegistrationCompleteEventListener.class);

    private final AuthServiceImpl userService;
    private final JavaMailSender mailSender;
    private Usuario user;

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        user = event.getUser();
        String verificationToken = UUID.randomUUID().toString();

        userService.saveUserVerificationToken(user, verificationToken);

        String url = "http://localhost:8080/api/auth/verifyEmail?token=" + verificationToken;

        try {
            sendVerificationEmail(url);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        log.info("Click en el enlace para verificar su registro : {} ", url);

    }

    public void sendVerificationEmail(String url) throws MessagingException, UnsupportedEncodingException {
        String subject = "Verificación de Correo Electronico";
        String senderName = "Servicio de Registro de Usuarios";
        String mailContent = "<p>Hola, " + user.getNombre() + "</p>"
                + "<p> Gracias por registrate con nosotros.</p>"
                + "<p> Por favor, haz click en el enlace a continuación para completar tu registro: </p>"
                + "<a href=\"" + url + "\">Verifica tu correo electrónico para activar tu cuenta</a>"
                + "<p>Gracias,<br>Servicio de Registro de Usuarios</p>";

        MimeMessage message = mailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message);

        messageHelper.setFrom("arrowstech3@gmail.com", senderName);
        messageHelper.setTo(user.getEmail());
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);

        mailSender.send(message);
    }

}
