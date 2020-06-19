package com.benedicta.knect.models;

public class Business {

    public String id;
    public String name;
    public String contact;
    public String location;
    public String services;
    public String delivery;
    public String category;
    public String userId;
    public String imageUrl;
    public String facebook;
    public String twitter;
    public String instagram;


    public Business(String id, String name, String contact, String location, String services, String delivery, String category, String userId, String imageUrl, String facebook, String instagram, String twitter) {
        this.name = name;
        this.id = id;
        this.contact = contact;
        this.location = location;
        this.services = services;
        this.category = category;
        this.delivery = delivery;
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.facebook = facebook;
        this.twitter = twitter;
        this.instagram = instagram;
    }

    public Business(String name, String contact, String location, String services, String delivery, String category, String userId, String imageUrl,  String facebook, String instagram, String twitter) {
        this.name = name;
        this.contact = contact;
        this.location = location;
        this.services = services;
        this.delivery = delivery;
        this.category = category;
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.facebook = facebook;
        this.twitter = twitter;
        this.instagram = instagram;
    }


    public Business() {
    }


}
