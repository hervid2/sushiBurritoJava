package com.restaurante.app.models;

/**
 * A product highlighted in the sales statistics (best or worst selling) together with its label.
 */
public class ProductSalesDTO {

    private String productName;
    private String description;

    public ProductSalesDTO(String productName, String description) {
        this.productName = productName;
        this.description = description;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
