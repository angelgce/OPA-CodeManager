package com.pirate.arena.app.services;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.pirate.arena.app.dynamoDB.ServiceDynamoDB;
import com.pirate.arena.app.exceptions.BadRequestException;
import com.pirate.arena.app.requests.RequestConfirmCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceCode implements IServiceCode {
    private final ServiceDynamoDB serviceDynamoDB;

    @Override
    public String createCode() {
        StringBuilder output = new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i < 6; i++)
            output.append(rand.nextInt(9));
        return output.toString();
    }

    @Override
    public String createUserCode() {
        return null;
    }

    @Override
    public String confirmCode(RequestConfirmCode confirmCode) {
        validateInputs(confirmCode);
        Item item = serviceDynamoDB.getItemByKey("codes", "code", confirmCode.code());
        if (!isCodeMatch((String) item.get("email"), confirmCode.email())
            || !isCodeMatch((String) item.get("code"), confirmCode.code()))
            throw new BadRequestException("Error: invalid code");
        serviceDynamoDB.deleteItem("codes", "code", confirmCode.code());
        log.info("user {} validated an code. {}", confirmCode.email(), item.toJSONPretty());
        return "success";
    }

    @Override
    public boolean isCodeMatch(String expect, String actual) {
        return expect.equals(actual);
    }

    @Override
    public void validateInputs(RequestConfirmCode confirmCode) {
        if (confirmCode.toString().contains("null"))
            throw new BadRequestException("Error in the requests, some mandatory fields are missing "
                    .concat(confirmCode.toString()));
    }
}

