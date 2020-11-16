package com.paaril.app.cart;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.paaril.app.AppServiceRepository;
import com.paaril.app.DomainEntity;

public class ShoppingCart {

  private static final ShoppingCart INSTANCE = new ShoppingCart();
  private Map<String, String> productQtyMap;

  private ShoppingCart() {
    productQtyMap = new ConcurrentHashMap<>();

    List<DomainEntity> entityList = AppServiceRepository.getInstance()
                                                        .getWebServer()
                                                        .getDomainEntities("ShoppingCartLineItem",
                                                                           null);
    if (entityList != null) {
      for (DomainEntity shoppingCartLineItem : entityList) {

        DomainEntity productLineItem = shoppingCartLineItem.getDomainEntity("productLineItem");
        productQtyMap.put(productLineItem.getId(),
                          shoppingCartLineItem.getValue("quantity"));

      }

    }

  }

  public static ShoppingCart getShoppingCart() {
    return INSTANCE;
  }

  public void addProductLineItem(DomainEntity productLineItem,
                                 int quantity) {

    String productLineItemId = productLineItem.getId();
    String qtyStr = String.valueOf(quantity);

    DomainEntity cartLineItem = new DomainEntity();
    cartLineItem.putValue("productLineItem.id",
                          productLineItemId);
    cartLineItem.putValue("quantity",
                          qtyStr);

    AppServiceRepository.getInstance()
                        .getWebServer()
                        .postEntity("ShoppingCartLineItem",
                                    cartLineItem);

    if (quantity == 0) {
      productQtyMap.remove(productLineItemId);
    } else {
      productQtyMap.put(productLineItemId,
                        qtyStr);
    }

  }

  public String getQuantityText(String productLineItemId) {

    return productQtyMap.get(productLineItemId);

  }

  public void clear() {
    productQtyMap.clear();
  }

  public DomainEntity checkout(DomainEntity salesOrder) {

    String customerId = AppServiceRepository.getInstance()
                                            .getAppSession()
                                            .getLoggedInCustomerId();
    salesOrder.putValue("customerId",
                        customerId);

    DomainEntity resultEntity = AppServiceRepository.getInstance()
                                                    .getWebServer()
                                                    .postEntity("SalesOrder",
                                                                salesOrder);

    clear();

    return resultEntity;

  }

}