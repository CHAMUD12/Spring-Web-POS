package org.example.springwebpos.util;

import java.util.Base64;
import java.util.UUID;

public class AppUtil {
    public static String createOrderId(){
        return "ORDER-" + UUID.randomUUID();
    }
    public static String createCustomerId(){
        return "CUSTOMER-"+UUID.randomUUID();
    }
    public  static String toBase64ProfilePic(byte[] profilePic) {
        return Base64.getEncoder().encodeToString(profilePic);
    }
    public static String createItemId(){
        return "ITEM-"+UUID.randomUUID();
    }
}
