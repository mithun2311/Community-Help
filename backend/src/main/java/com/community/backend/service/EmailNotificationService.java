package com.community.backend.service;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import com.community.backend.event.HelpRequestCreatedEvent;
import com.community.backend.event.SosTriggeredEvent;
import com.community.backend.event.SafetyCheckInEscalatedEvent;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.community.backend.entity.HelpRequest;
import com.community.backend.entity.SOSIncident;
import com.community.backend.entity.TrustedContact;
@Service
public class EmailNotificationService {
    private static final Logger logger=LoggerFactory.getLogger(EmailNotificationService.class);
    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final boolean enabled;
    private final String alertRecipient;
    private final String fromAddress;
    public EmailNotificationService(ObjectProvider<JavaMailSender> mailSenderProvider,@Value("${app.notifications.email-enabled:false}") boolean enabled,@Value("${app.notifications.recipient:}") String alertRecipient,@Value("${spring.mail.username:}") String fromAddress) {
        this.mailSenderProvider=mailSenderProvider; this.enabled=enabled; this.alertRecipient=alertRecipient.trim(); this.fromAddress=fromAddress.trim();
    }
    @TransactionalEventListener(phase=TransactionPhase.AFTER_COMMIT)
    public void onHelpRequestCreated(HelpRequestCreatedEvent event) { notifyHighUrgency(event.helpRequest()); }
    @TransactionalEventListener(phase=TransactionPhase.AFTER_COMMIT)
    public void onSosTriggered(SosTriggeredEvent event) { notifySos(event.incident(),event.contacts()); }
    @TransactionalEventListener(phase=TransactionPhase.AFTER_COMMIT)
    public void onSafetyCheckInEscalated(SafetyCheckInEscalatedEvent event) {
        String subject="Community Help safety check-in escalation #"+event.checkIn().getId();
        String body="A safety check-in was not acknowledged for help request #"+event.checkIn().getHelpRequest().getId()+". Please attempt to contact the participant and follow your agreed safety plan. Community Help is not an emergency-services replacement.";
        send(alertRecipient,subject,body);
        for(TrustedContact contact:event.contacts()) if(contact.getEmail()!=null && !contact.getEmail().isBlank()) send(contact.getEmail(),subject,body);
    }
    public void notifyHighUrgency(HelpRequest request) {
        if(request.getUrgency()!=com.community.backend.entity.HelpUrgency.HIGH) return;
        send(alertRecipient,"High-urgency Community Help request #"+request.getId(),"A high-urgency help request has been created.\nTitle: "+request.getTitle()+"\nCategory: "+request.getCategory()+"\nPlease review the Community Help dashboard.");
    }
    public void notifySos(SOSIncident incident,List<TrustedContact> contacts) {
        String subject="Community Help SOS incident #"+incident.getId();
        String body="An SOS was triggered for help request #"+incident.getHelpRequest().getId()+". Please contact the participant through the platform and follow your agreed safety plan. Community Help is not an emergency-services replacement.";
        send(alertRecipient,subject,body);
        for(TrustedContact contact:contacts) if(contact.getEmail()!=null && !contact.getEmail().isBlank()) send(contact.getEmail(),subject,body);
    }
    private void send(String recipient,String subject,String body) {
        if(!enabled || recipient==null || recipient.isBlank()) return;
        JavaMailSender sender=mailSenderProvider.getIfAvailable();
        if(sender==null) { logger.warn("Email notification skipped because mail is not configured"); return; }
        try {
            SimpleMailMessage message=new SimpleMailMessage();
            if(!fromAddress.isBlank()) message.setFrom(fromAddress);
            message.setTo(recipient);
            message.setSubject(subject);
            message.setText(body);
            sender.send(message);
        } catch(RuntimeException exception) {
            logger.error("Unable to send Community Help notification",exception);
        }
    }
}
