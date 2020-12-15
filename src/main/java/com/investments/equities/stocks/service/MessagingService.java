package com.investments.equities.stocks.service;

import com.twilio.Twilio;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import static com.investments.equities.stocks.common.CommonConstants.TWILIO_ACCOUNT_SID;
import static com.investments.equities.stocks.common.CommonConstants.TWILIO_AUTH_TOKEN;

@Service
public class MessagingService {

    private static Logger logger = LogManager.getLogger(MessagingService.class);

    public MessagingService() {
        Twilio.init(TWILIO_ACCOUNT_SID, TWILIO_AUTH_TOKEN);

    }

    public void sendMessage(String messageBody) {
//        Message message = Message.creator(
//                new com.twilio.type.PhoneNumber("whatsapp:+14049407776"),
//                new com.twilio.type.PhoneNumber("whatsapp:+14155238886"),
//                messageBody)
//                .create();
    }
}
