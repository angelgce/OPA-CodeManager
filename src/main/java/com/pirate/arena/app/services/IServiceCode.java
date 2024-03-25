package com.pirate.arena.app.services;

import com.pirate.arena.app.requests.RequestConfirmCode;

public interface IServiceCode {

    String createCode();
    String createUserCode();
    String confirmCode(RequestConfirmCode confirmCode);

    boolean isCodeMatch(String expect, String actual);

    void validateInputs(RequestConfirmCode confirmCode);
}
