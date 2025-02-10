
package dto;

public class WishDTO {

    private int productId;
    private String productName;
    private String status;
    private double price;
    private double remaining;
    private int wishId;

    public void setRemaining(double remaining) {
        this.remaining = remaining;
    }

    public double getRemaining() {
        return remaining;
    }

    public WishDTO(int productId, int wishId, String productName, double price, double remaining, String status) {
        this.productName = productName;
        this.status = status;
        this.price = price;
        this.remaining = remaining;
        this.productId = productId;
        this.wishId = productId;
    }

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

    public void setWishId(int wishId) {
        this.wishId = wishId;
    }

    public int getWishId() {
        return wishId;
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
