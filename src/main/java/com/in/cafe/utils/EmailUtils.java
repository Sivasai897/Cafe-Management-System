package com.in.cafe.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailUtils {
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendSimpleMessage(String to, String subject, String text, List<String> list) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("sivasaimudadla2002@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        if (!list.isEmpty() && list.size() > 0)
            message.setCc(getCcArray(list));
        javaMailSender.send(message);
    }

    private String[] getCcArray(List<String> list) {
        int a = list.size();
        String[] ccArray = new String[a];
        for (int i = 0; i < a; i++) {
            ccArray[i] = list.get(i);
        }
        return ccArray;
    }
}
