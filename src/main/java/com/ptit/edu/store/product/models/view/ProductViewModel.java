package com.ptit.edu.store.product.models.view;

import com.ptit.edu.store.product.models.data.Category;
import com.ptit.edu.store.product.models.data.Product;
import com.ptit.edu.store.product.models.data.RateClothes;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class ProductViewModel {
    private String id;
    private String name;
    private double price;
    private String description;
    private Date createdDate;
    private String avatarUrl;
    private String coverUrl;
    private String categoryTitle;
    private String categoryID;
    private String size;
    private int quantity;
    private int isSale;
    private float salePercent;
    private int numberSave;
    private boolean isSaved;
    private float avarageOfRate = 0;

    public ProductViewModel() {
    }

    public ProductViewModel(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.description = product.getDescription();
        this.createdDate = product.getCreatedDate();
        this.avatarUrl = product.getAvatarUrl();
        this.coverUrl = product.getCoverUrl();
        this.categoryTitle = product.getCategory().getTitle();
        this.categoryID = product.getCategory().getId();
        this.quantity = product.getQuantity();
        this.isSaved = false;
        this.numberSave = product.getTotalSave();
        this.size = product.getSize();
        this.isSale = product.getIsSale();
        this.salePercent = product.getSalePercent();
        setAvarageOfRate(getAvarageOfRate(product));
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

    public float getAvarageOfRate() {
        return avarageOfRate;
    }

    public float getAvarageOfRate(Product product) {
        if(product.getRateClothes().size()==0){
            return 0;
        }
        int sum = 0;
        for (RateClothes rateClothes : product.getRateClothes()) {
            sum += rateClothes.getRating();
        }

        return (float) sum / product.getRateClothes().size();
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setAvarageOfRate(float avarageOfRate) {
        this.avarageOfRate = avarageOfRate;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNumberSave() {
        return numberSave;
    }

    public void setNumberSave(int numberSave) {
        this.numberSave = numberSave;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public boolean getIsSaved() {
        return isSaved;
    }

    public void setIsSaved(boolean saved) {
        isSaved = saved;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

}
