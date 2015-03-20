package com.mychatserver.db;

import com.mychatserver.entity.User;
import com.mychatserver.enums.*;
import com.mychatserver.enums.Error;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by ciprian.mare on 3/20/2015.
 */
public class UserDao {

    /**
     *
     * @param user
     * @return
     *
     * check if a user is already registered and if not registerUser, return a message for client
     */
    public String registerUser(final User user){
        if(isRegistered(user.getUsername())){
            return com.mychatserver.enums.Error.USER_EXIST.toString();
        }

        try{
            final PreparedStatement stmt = DbConnection.getInstance().getConnection().prepareStatement("INSERT INTO tbl_users(username, password) VALUES (?,?)");
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.executeUpdate();
            stmt.close();
        } catch (Exception e){
            e.printStackTrace();
            return com.mychatserver.enums.Error.COMMON_ERROR.toString();

        }
        return Error.NO_ERROR.toString();
    }

    /**
     * check if a user is already registered
     * @param username
     * @return
     */
    private boolean isRegistered(final String username){
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

    /**
     *
     * @param user
     * @return
     */
    public String authenticate(User user) {
        if(!isRegistered(user.getUsername())){
            return Error.USER_NOT_EXIST.toString();
        }

        try{
            final PreparedStatement stmt = DbConnection.getInstance().getConnection().prepareStatement("SELECT * FROM tbl_users WHERE username = ? and password = ?");
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return Error.BAD_CREDENTIALS.toString();
            }
            rs.close();
            stmt.close();
        } catch (Exception e){
            e.printStackTrace();
            return Error.COMMON_ERROR.toString();

        }
        return Error.NO_ERROR.toString();
    }
}
