package com.example.alex.tcgapp.model;

import java.io.Serializable;

public class CardItem implements Serializable {
    private String name, Uid;
    private String url;
    private String user;
    private String description, rarity, store, stock, price;


    public String getName() {
        return name;
    }
    public String getPrice() {
        return price;
    }
    public String getUrl() {
        return url;
    }
    public String getUser() {
        return user;
    }
    public String getDescription() {
        return description;
    }
    public String getRarity() {
        return rarity;
    }
    public String getStore() {
        return store;
    }
    public String getStock() {
        return stock;
    }
    public String getUid() {
        return Uid;
    }

    public void setName(String name) {
         this.name=name;
    }
    public void setPrice(String price) {
         this.price=price;
    }
    public void setUrl(String url) {
         this.url=url;
    }
    public void setUser(String user) {
        this.user=user;
    }
    public void setDescription(String description) {
        this.description=description;
    }
    public void setRarity(String rarity) {
        this.rarity= rarity;
    }
    public void setStore(String store) {
        this.store=store;
    }
    public void setStock(String stock) {
        this.stock=stock;
    }
    public void setUid(String uid ) {
        this.Uid=uid;
    }


    public CardItem(String name, String url, String user, String description, String rarity, String store, String stock, String price, String Uid) {
        this.name = name;
        this.url = url;
        this.user=user;
        this.description=description;
        this.price=price;
        this.rarity=rarity;
        this.stock=stock;
        this.store=store;
        this.Uid=Uid;
    }

    public CardItem(){}
}
