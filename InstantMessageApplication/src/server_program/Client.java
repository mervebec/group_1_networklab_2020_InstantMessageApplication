/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server_program;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author HP
 */
public class Client  implements Serializable{
    String username;
    String name;
    ArrayList <Client> history=new ArrayList<>();
    String state;//it could be :null (while singing up) log-in (if client not siged-out ) log out (if client registered but signed-out)
    ObjectOutputStream  outputstream;
    ObjectInputStream   inputStream;
    String password;
    public Client(String telphone_no, String name , String password) {
        this.username = telphone_no;
        this.name = name;
        this.password=password;
    }
    
 
    
    
    
}
