/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dto;


public class WishDTO {
    private int productId;
    private String productName;
    private String status;
    private double price;

    public WishDTO(int productId, String productName, String status) {
        this.productId = productId;
        this.productName = productName;
        this.status = status;
    }

    public WishDTO(int productId, String productName, String status, double price) {
        this.productId = productId;
        this.productName = productName;
        this.status = status;
        this.price = price;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getStatus() {
        return status;
    }

    public double getPrice() {
        return price;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPrice(double price) {
        this.price = price;
    }
    public void setProductId(int productId) {
        this.productId = productId;
    }



}
