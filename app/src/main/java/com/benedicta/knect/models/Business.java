package com.benedicta.knect.models;

public class Business {
    private int id;
    private String image;
    private String description;
    private String userId;
    private String categoryId;
    private String ratings;

    public Business(int id, String image, String description, String userId, String categoryId, String ratings) {
        this.id = id;
        this.image = image;
        this.description = description;
        this.userId = userId;
        this.categoryId = categoryId;
        this.ratings = ratings;
    }

    public Business() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getRatings() {
        return ratings;
    }

    public void setRatings(String ratings) {
        this.ratings = ratings;
    }
}
