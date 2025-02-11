
package dto;

import java.sql.Date;

public class UserDTO 
{
    private String username; 
    private String full_name;
    private String password;
    private String gender;
    private String phone;
    private float balance;
    private Date dob;  

    public UserDTO(String username, String full_name, String password, String gender, String phone, float balance, Date dob) {
        this.username = username;
        this.full_name = full_name;
        this.password = password;
        this.gender = gender;
        this.phone = phone;
        this.balance = balance;
        this.dob = dob;
    }
    public UserDTO(String username, String full_name, String gender, String phone, float balance, Date dob) {
        this.username = username;
        this.full_name = full_name;
        this.gender = gender;
        this.phone = phone;
        this.balance = balance;
        this.dob = dob;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }
}
