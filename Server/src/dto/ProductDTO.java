/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dto;

public class ProductDTO {

    public static ProductDTO add;

    private int productId;
    private String productName;
    private int productPrice;
    private String manufacture_date;

    public ProductDTO(int productId, String productName, int productPrice, String manufacture_date) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.manufacture_date = manufacture_date;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String Product_name) {
        this.productName = productName;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public void setProduct_price(int Product_price) {
        this.productPrice = Product_price;
    }

    public String getmanufacture_date() {
        return manufacture_date;
    }

    public void setmanufacture_date(String manufacture_date) {
        this.manufacture_date = manufacture_date;
    }

}
