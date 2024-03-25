package com.pirate.arena.app.dynamoDB;


import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.DeleteItemRequest;
import com.pirate.arena.app.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ServiceDynamoDB implements IServiceDynamoDB {

    private final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
    private final DynamoDB dynamoDB = new DynamoDB(client);

    @Override
    public Item getItemByKey(String tableName, String key, String value) {
        Table table = dynamoDB.getTable(tableName);
        Item item = table.getItem(key, value);
        if (item == null)
            throw new BadRequestException("Error: Key [".concat(key).concat("] doesn't exists "
                    .concat("or doesn't has the value [".concat(value).concat("]"
                            .concat(" on table [".concat(tableName.concat("].")))))));
        return item;
    }

    @Override
    public List<Item> getItemByIndex(String tableName, String key, String value, String indexKey) {
        Table table = dynamoDB.getTable(tableName);
        QuerySpec spec = new QuerySpec()
                .withKeyConditionExpression(key + " = :v_id")
                .withValueMap(new ValueMap()
                        .withString(":v_id", value));
        ItemCollection<QueryOutcome> items = table.getIndex(indexKey).query(spec);
        List<Item> itemList = new ArrayList<>();
        items.iterator().forEachRemaining(itemList::add);
        if (itemList.isEmpty())
            throw new BadRequestException("Error: index [".concat(indexKey).concat("] doesn't exists"
                    .concat("or doesn't has values")));
        return itemList;
    }

    @Override
    public int updateItem(String tableName, String primaryKey, String primaryKeyValue, String key, String value) {
        Table table = dynamoDB.getTable(tableName);
        UpdateItemSpec updateItemSpec = new UpdateItemSpec()
                .withPrimaryKey(primaryKey, primaryKeyValue)
                .withUpdateExpression("set " + key + " = :" + key)
                .withValueMap(new ValueMap()
                        .withString(":" + key, value));
        return table.updateItem(updateItemSpec)
                .getUpdateItemResult().getSdkHttpMetadata().getHttpStatusCode();
    }

    @Override
    public void putItem(String tableName, Item item) {
        Table table = dynamoDB.getTable(tableName);
        table.putItem(item);
    }

    @Override
    public void deleteItem(String tableName, String key, String value) {
        Table table = dynamoDB.getTable(tableName);
        DeleteItemOutcome outcome = table.deleteItem(key, value);
        String code = String.valueOf(outcome.getDeleteItemResult().getSdkHttpMetadata().getHttpStatusCode());
    }

}
