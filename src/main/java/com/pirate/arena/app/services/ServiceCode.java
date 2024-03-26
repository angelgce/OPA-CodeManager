package com.pirate.arena.app.services;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.pirate.arena.app.dynamoDB.ServiceDynamoDB;
import com.pirate.arena.app.exceptions.BadRequestException;
import com.pirate.arena.app.requests.RequestConfirmCode;
import com.pirate.arena.app.requests.RequestCreateCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceCode extends ServiceValidateRequest implements IServiceCode {
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
    public String createUserCode(RequestCreateCode createCode) {
        validateInputs(Optional.ofNullable(createCode));
        List<Item> list = serviceDynamoDB.getItemByIndex("codes", "email", createCode.email(), "email-index");
        list.forEach(item -> {
            String type = (String) item.get("type");
            if (type.equals(createCode.type()))
                throw new BadRequestException("The user [".concat(createCode.email()).concat("] already has a code for ["
                        .concat(createCode.type()).concat("]")));
        });
        String code = createCode();
        Item item = new Item()
                .withPrimaryKey("code", code)
                .withString("email", createCode.email())
                .withString("type", createCode.type());
        serviceDynamoDB.putItem("codes",item);
        log.info("[Code added] request:[{}] code:[{}]", createCode,code);
        return code;
    }

    @Override
    public String confirmCode(RequestConfirmCode confirmCode) {
        validateInputs(Optional.ofNullable(confirmCode));
        Item item = serviceDynamoDB.getItemByKey("codes", "code", confirmCode.code());
        if (!isCodeMatch((String) item.get("email"), confirmCode.email())
            || !isCodeMatch((String) item.get("code"), confirmCode.code()))
            throw new BadRequestException("Error: Invalid code [".concat(confirmCode.toString()).concat("]"));
        serviceDynamoDB.deleteItem("codes", "code", confirmCode.code());
        log.info("[Code validated] request:[{}] code:[{}]", confirmCode,item);
        return "success";
    }

    @Override
    public boolean isCodeMatch(String expect, String actual) {
        return expect.equals(actual);
    }


}

