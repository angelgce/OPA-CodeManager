package com.pirate.arena.app.services;

import com.pirate.arena.app.requests.RequestConfirmCode;
import com.pirate.arena.app.requests.RequestSendMail;

public interface IServiceSendMail {

    String createEmail(String title, String body, String email, boolean isCode);
    String sendMail(RequestSendMail requestSendMail);

    void validateInputs(RequestSendMail requestSendMail);
}
