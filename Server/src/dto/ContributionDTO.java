package dto;

public class ContributionDTO {

    private int wish_id;
    private String Contributer_name;
    private double amount;
    private double remaining;
    private String friendUsername;

    public ContributionDTO(int wish_id, String Contributer_name, double amount, double remaining, String friendUsername) {
        this.wish_id = wish_id;
        this.Contributer_name = Contributer_name;
        this.amount = amount;
        this.remaining = remaining;
        this.friendUsername = friendUsername;
    }

    public void setFriendUsername(String friendUsername) {
        this.friendUsername = friendUsername;
    }

    public String getFriendUsername() {
        return friendUsername;
    }

    public double getRemaining() {
        return remaining;
    }

    public void setRemaining(double remaining) {
        this.remaining = remaining;
    }

    public int getWish_id() {
        return wish_id;
    }

    public String getContributer_name() {
        return Contributer_name;
    }

    public double getAmount() {
        return amount;
    }

    public void setWish_id(int wish_id) {
        this.wish_id = wish_id;
    }

    public void setContributer_name(String Contributer_name) {
        this.Contributer_name = Contributer_name;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

}