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
    int telphone_no;
    String name;
    ArrayList <Client> history=new ArrayList<>();
    boolean state;
    ObjectOutputStream  outputstream;
    ObjectInputStream   inputStream;
    public Client(int telphone_no, String name) {
        this.telphone_no = telphone_no;
        this.name = name;
    }
    
 
    
    
    
}
