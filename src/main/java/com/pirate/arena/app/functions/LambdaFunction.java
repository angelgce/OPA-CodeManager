package com.pirate.arena.app.functions;

import com.pirate.arena.app.requests.RequestConfirmCode;
import com.pirate.arena.app.requests.RequestCreateCode;
import com.pirate.arena.app.requests.RequestSendMail;
import com.pirate.arena.app.services.ServiceCode;
import com.pirate.arena.app.services.ServiceSendMail;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

@Configuration
@RequiredArgsConstructor
public class LambdaFunction {
    private final ServiceCode serviceCode;
    private final ServiceSendMail serviceSendMail;

    //Function to send a mail to an user
    @Bean
    public Function<RequestSendMail, ResponseEntity<Map<String, String>>> sendEmail() {
        return value -> ResponseEntity.ok()
                .body(Collections.singletonMap("data", serviceSendMail.sendMail(value)));
    }

    //Function to confirm a code
    @Bean
    public Function<RequestConfirmCode, ResponseEntity<Map<String, String>>> confirmCode() {
        return value -> ResponseEntity.ok()
                .body(Collections.singletonMap("data", serviceCode.confirmCode(value)));
    }

    //function to create a code for user
    @Bean
    public Function<RequestCreateCode, ResponseEntity<Map<String, String>>> createCode() {
        return value -> ResponseEntity.ok()
                .body(Collections.singletonMap("data", serviceCode.createUserCode(value)));
    }

}
