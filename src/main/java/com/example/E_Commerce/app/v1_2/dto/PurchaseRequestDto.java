package com.example.E_Commerce.app.v1_2.dto;


public class PurchaseRequestDto {
    private String productId; // დამჭირდება პროდუქტის პირდაპირ შესაძენად, ბარათზე რომ აღარ ამატებ რა

    private Integer quantity; // დამჭირდება პირდაპირ შეძენისას რომ მივუთითო სასურველი პროდუქტის რაოდენობა


    // Constructors
    public PurchaseRequestDto() {
    }

    public PurchaseRequestDto(String productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    // Getters and Setters
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    // Helper შეამოწმებს პირდაპირ შეძენის რექვესტი არის თუ არა ნამდვილად
    public boolean isDirectPurchase() {
        return productId != null && quantity != null && quantity > 0;
    }

    // Helper შეამოწმებს უკვე პირდაპირ შეძენის რექვესტი თუ არის ოღონდ ბარათზე დამატებული ნივთისა.
    // აქ ასევე ვამოწმებ ბარათზე დამატებული ნივთებიდან კონკრეტულად რომელს ყიდულობს და ამიტომ გადავცემ ID და რაოდენობასაც
    public boolean isSingleCartItemPurchase() {

        return productId != null && quantity != null && quantity > 0;
    }
}
