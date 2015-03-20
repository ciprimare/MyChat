package com.mychatserver.db;

import com.mychatserver.entity.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by ciprian.mare on 3/19/2015.
 */
public class UserDao {

    /**
     *
     * @param user
     * @return
     *
     * check if a user is already registered and if not register, return a message for client
     */
    public String register(final User user){
        if(isRegistered(user.getUsername())){
            return "user is already registered";
        }

        try{
            final PreparedStatement stmt = DbConnection.getInstance().getConnection().prepareStatement("INSERT INTO tbl_users(username, password) VALUES (?,?)");
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.executeUpdate();
            stmt.close();
        } catch (Exception e){

        }
        return "user registered";
    }

    /**
     * check if a user is already registered
     * @param username
     * @return
     */
    public boolean isRegistered(final String username){
        boolean bRet = false;

        try {
            final PreparedStatement stmt = DbConnection.getInstance().getConnection().prepareStatement("SELECT * FROM tbl_users WHERE username = ?");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
               bRet = true;
            }
            rs.close();
            stmt.close();
        } catch (Exception e){
            e.printStackTrace();
        }
        return bRet;
    }
}
