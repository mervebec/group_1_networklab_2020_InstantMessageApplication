/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ınstantmessageapplication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author HP
 */
public class connectionToDataBase {
    public static void main(String[] args) {
        Connection con ;
        Statement st;
        ResultSet rs;
        try{
           Class.forName("com.mysql.jdbc.Driver"); //load the driver for particular database 
            con= DriverManager.getConnection("jdbc:mysql://localhost?3306/client", "root", "") ;     // establish connection with the database 
            st= con.createStatement();// obtain connection to that particular database
           rs=st.executeQuery("select * from client_info");
           while(rs.next()){
               System.out.println("ID: "+rs.getInt(1)+" Name: " +rs.getString(2));
           }
           // now i will add jar file to the pro
        }catch(Exception e){
            System.out.println("Eror:"+e.getMessage());
        }
    }
}
