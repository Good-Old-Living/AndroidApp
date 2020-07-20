package com.paaril.app;

public interface AppConstants {

  String SMS_SENDER_ID = "PAARIL";
  String SMS_SENDER_ID_BY_PROVIDER = "INFSMS";
  
  String ARG_SUBSCRIPTION_ID = "subscriptionId";
  String ARG_SUBSCRIPTION_LINE_ITEM_ID = "subscriptionLineItemId";
  String ARG_SUBSCRIPTION_ADDRESS = "subscriptionAddress";
  
  
  String SUBSCRIPTION_STATE_ACTIVE = "1";

  public static int PAYMENT_MODE_COD = 251;
  public static int PAYMENT_MODE_GPAY = 252;
  public static int PAYMENT_MODE_ONLINE = 253;
    public static int PAYMENT_MODE_WALLET = 254;

  public static String STATE_PENDING_PAYMENT_CODE = "3";
}
