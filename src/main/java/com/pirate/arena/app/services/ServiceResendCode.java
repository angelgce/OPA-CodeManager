package com.pirate.arena.app.services;


import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.pirate.arena.app.exceptions.BadRequestException;
import com.pirate.arena.app.requests.RequestSendMail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServiceResendCode {

    private final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
    private final DynamoDB dynamoDB = new DynamoDB(client);
    private final ServiceMail serviceMail;
    private final ServiceCode serviceCode;

    public String resendCode(RequestSendMail user) {
        //validate email
        validateInputs(user);
        Table table = dynamoDB.getTable("users");
        Item item = table.getItem("email", user.emailReceiver());
        if (item == null)
            throw new BadRequestException("Error: user not founded.");
        if ((boolean) item.get("verified"))
            throw new BadRequestException("Error: user is already verified.");
        //update code
        String code = serviceCode.createCode();
        UpdateItemSpec updateItemSpec = new UpdateItemSpec()
                .withPrimaryKey("email", user.emailReceiver())
                .withUpdateExpression("set code = :code")
                .withValueMap(new ValueMap()
                        .withString(":code", code));
        int response = (table.updateItem(updateItemSpec)
                .getUpdateItemResult().getSdkHttpMetadata().getHttpStatusCode());
        if (response != 200)
            throw new BadRequestException("Error: code not updated.");
        //send email
        serviceMail.sendWelcomeMail(user, code);
        return "Code sent!";
    }

    private void validateInputs(RequestSendMail sendCode) {
        if (sendCode.emailReceiver() == null)
            throw new BadRequestException("Error in the sendCode, some mandatory fields are missing "
                    .concat(sendCode.toString()));
    }

}
