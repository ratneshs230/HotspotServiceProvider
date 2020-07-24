package com.hotspot.hotspotserviceprovider;

public class ProductsModel {
    String productName,ProductImage,productDescription,productPrice,productPushkey;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImage() {
        return ProductImage;
    }

    public void setProductImage(String productImage) {
        ProductImage = productImage;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductPushkey() {
        return productPushkey;
    }

    public void setProductPushkey(String productPushkey) {
        this.productPushkey = productPushkey;
    }
}
