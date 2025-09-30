package com.example.ecommerce_backend.Service;

import org.springframework.stereotype.Service;

import com.example.ecommerce_backend.Config.TwilioProperties;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

@Service
public class WhatsAppService {

    private final TwilioProperties twilioProperties;

    public WhatsAppService(TwilioProperties twilioProperties) {
        this.twilioProperties = twilioProperties;
    }

    public void sendWhatsAppMessage(String to, String message) {
        Twilio.init(twilioProperties.getAccountSid(), twilioProperties.getAuthToken());
        Message.creator(
        		new com.twilio.type.PhoneNumber("whatsapp:"+to),
                new com.twilio.type.PhoneNumber(twilioProperties.getWhatsappNumber()),
                message
        ).create();
    }
}
