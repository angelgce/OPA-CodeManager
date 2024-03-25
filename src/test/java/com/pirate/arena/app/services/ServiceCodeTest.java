package com.pirate.arena.app.services;

import com.pirate.arena.app.dynamoDB.ServiceDynamoDB;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ServiceCodeTest {
    ServiceCode serviceCode = new ServiceCode(null);

    @Test
    void generateCode() {
        List<String> listCodes = new ArrayList<>();
        boolean isDuplicated = false;
        for (int i = 0; i < 100; i++) {
            String code = serviceCode.createCode();
            if (listCodes.contains(code)) {
                isDuplicated = true;
            }
            listCodes.add(code);
        }
        System.out.println(listCodes);
        assertFalse(isDuplicated);
    }

    @Test
    void isCodeMatch() {
       assertTrue(serviceCode.isCodeMatch("123456","123456"));
       assertFalse(serviceCode.isCodeMatch("123456","123"));
    }


}