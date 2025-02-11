/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dal;

import dto.HomeUserDTO;
import dto.UserDTO;
import entities.User;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import oracle.jdbc.OracleDriver;


/**
 *
 * @author Ahmed
 */
public class UserDAO {


    static public String getPassword(String userName) throws SQLException{
        Database db = new Database();
        Connection con = db.getConnection();
        PreparedStatement stmt  = con.prepareStatement("select password from users where username = ?");
        stmt.setString(1, userName);
        ResultSet rs = stmt.executeQuery();
        String password= null;
        if (rs.next()) {
            password = rs.getString("password");
        }
        db.close();
        stmt.close();
        return password;
    }


    static public int addUser(UserDTO user) throws SQLException 
    {
        Database db = new Database();
        Connection con = db.getConnection();
        String Check_user_exsistance = "select count(*) from users where username = ? ";
        PreparedStatement user_exist = con.prepareStatement(Check_user_exsistance);
        user_exist.setString(1, user.getUsername());
        ResultSet rs = user_exist.executeQuery();
        rs.next();
        if(rs.getInt(1) > 0 )
        {
            System.out.println(" That User Exsists");
            return 1;
        }else
        {
            String Query = "insert into users (username,full_name,password,gender,phone,balance,dob) values (?,?,?,?,?,?,?)";
            PreparedStatement statement = con.prepareStatement(Query);
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getFull_name());
            statement.setString(3, user.getPassword());
            statement.setString(4, user.getGender());
            statement.setString(5, user.getPhone());
            statement.setFloat(6, user.getBalance());
            statement.setDate(7, new java.sql.Date(user.getDob().getTime()));
            statement.executeUpdate();
            con.commit();
            return 0;    
        }
        
    }
    static public HomeUserDTO getHomeUser(String userName) throws SQLException {
        Database db = new Database();
        Connection con = db.getConnection();
        PreparedStatement stmt  = con.prepareStatement("select username, full_name, balance from users where username = ?");
        stmt.setString(1, userName);
        ResultSet rs = stmt.executeQuery();
        HomeUserDTO homeUserData = null;
        if (rs.next()) {
            homeUserData = new HomeUserDTO( rs.getString("username"),  rs.getString("full_name"), rs.getDouble("balance"));
        }
        db.close();
        stmt.close();
        return homeUserData;
    }
    static public UserDTO getUserData(String userName) throws SQLException {
        Database db = new Database();
        Connection con = db.getConnection();
        PreparedStatement stmt  = con.prepareStatement("select username, full_name,gender,phone, balance,dob from users where username = ?");
        stmt.setString(1, userName);
        ResultSet rs = stmt.executeQuery();
        UserDTO UserData = null;
        if (rs.next()) {
            UserData = new UserDTO( rs.getString("username"),  rs.getString("full_name"),   
                    rs.getString("gender"),rs.getString("phone"), rs.getFloat("balance"),rs.getDate("dob"));
        }
        db.close();
        stmt.close();
        return UserData;
    }
    static public int updateBalance(String userName) throws SQLException {return 0;}
}
