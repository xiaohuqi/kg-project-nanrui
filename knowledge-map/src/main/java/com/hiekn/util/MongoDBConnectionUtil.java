package com.hiekn.util;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

/**
 * @author xiaohuqi@126.com
 * @cre 2017-06-19 22:32
 **/
public class MongoDBConnectionUtil {
    private static MongoClient mongoClient = null;

    public synchronized static MongoClient getMongoClient(){
        if(mongoClient == null){
            try {
                MongoClientURI connectionString = new MongoClientURI("mongodb://centos-1:19130");
                mongoClient = new MongoClient(connectionString);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return mongoClient;
    }

    public static MongoDatabase getMongoDatabase(String dbName) {
        return getMongoClient().getDatabase(dbName);
    }

    public static MongoCollection<Document> getMongoCollection(String dbName, String collectionName) {
        return getMongoClient().getDatabase(dbName).getCollection(collectionName);
    }
}
