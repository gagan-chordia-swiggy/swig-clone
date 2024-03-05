package com.example.fulfillmentservice.constants;

public final class Constants {
    private Constants() {}

    // Users
    public static final String USER_REGISTERED = "User registered";
    public static final String USER_ALREADY_EXISTS = "User already exists with the given username";
    public static final String USER_NOT_FOUND = "User not found";

    // Deliveries
    public static final String DELIVERY_FACILITATED = "Delivery has been facilitated";
    public static final String STATUS_UPDATED = "Delivery status updated to ";
    public static final String DELIVERY_NOT_FOUND = "Delivery not found";
    public static final String NO_EXECUTIVE_NEARBY = "No delivery executive nearby";
    public static final String ORDER_ALREADY_FACILITATED = "Order has already been facilitated";
    public static final String ORDER_ALREADY_DELIVERED = "Order has already been delivered";
    public static final String UNAUTHORIZED_STATUS_UPDATE = "Executive trying to update some other executive's order";
}
