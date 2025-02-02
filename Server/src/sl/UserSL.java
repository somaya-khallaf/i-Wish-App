package sl;

import dal.UserDAO;
import entities.User;
import dto.UserDTO;
import dto.LoginDTO;

import java.sql.SQLException;

public class UserSL {
    static public boolean logIn(LoginDTO logindto) throws SQLException {
    String password = UserDAO.getPassword(logindto.username);
    if (password !=null && password.equals(logindto.password))
        return true;
    return false; 
     
    } 
    static public int register(User user) throws SQLException {return 0;}
    static public UserDTO getUserData(String userName) throws SQLException {return new UserDTO();}
    public double getBalance(String userName) throws SQLException {return 0;}
    public int updateBalance(String userName, double amount) throws SQLException {return 0;}

}
