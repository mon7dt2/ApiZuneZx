package com.ptit.edu.store.product.models.view;

import com.ptit.edu.store.product.models.data.Product;

public class ProductPreview {
    private String id;
    private String name;
    private double price;
    private String category;
    private String logoUrl;
    private String size;
    private int numberSave;
    private int quantity;
    private int isSale;
    private float salePercent;

    public ProductPreview() {
    }

    public ProductPreview(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.logoUrl = product.getAvatarUrl();
        this.numberSave = product.getTotalSave();
        this.category= product.getCategory().getTitle();
        this.size = product.getSize();
        this.quantity = product.getQuantity();
        this.isSale = product.getIsSale();
        this.salePercent = product.getSalePercent();
    }

    public int getIsSale() {
        return isSale;
    }

    public void setIsSale(int isSale) {
        this.isSale = isSale;
    }

    public float getSalePercent() {
        return salePercent;
    }

    public void setSalePercent(float salePercent) {
        this.salePercent = salePercent;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public int getNumberSave() {
        return numberSave;
    }

    public void setNumberSave(int numberSave) {
        this.numberSave = numberSave;
    }
}
