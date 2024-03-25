package com.pirate.arena.app.services;

import com.pirate.arena.app.requests.RequestConfirmCode;
import com.pirate.arena.app.requests.RequestCreateCode;

import java.util.Optional;

public interface IServiceCode {

    String createCode();
    String createUserCode(RequestCreateCode createCode);
    String confirmCode(RequestConfirmCode confirmCode);

    boolean isCodeMatch(String expect, String actual);

}
