package dto;

public class ContributionDTO {

    private int wish_id;
    private String Contributer_name;
    private double balance;
    private double remaining;

    public ContributionDTO(int wish_id, String Contributer_name, double amount, double remaining) {
        this.wish_id = wish_id;
        this.Contributer_name = Contributer_name;
        this.balance = amount;
         this.remaining = remaining;
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

    public double getBalance() {
        return balance;
    }

    public void setWish_id(int wish_id) {
        this.wish_id = wish_id;
    }

    public void setContributer_name(String Contributer_name) {
        this.Contributer_name = Contributer_name;
    }

    public void setBalance(double amount) {
        this.balance = amount;
    }

}
