/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package schooleventregistration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;


public class AccountManager {
    
    private static final String FILE_NAME = "accounts.csv";
    
    public static boolean registerAccount(String username , String password , String accountType){
        username = username.trim();
        
        if(username.isEmpty() || password.isEmpty()){
            return false;
        }
        if(!accountType.equals("Student") && !accountType.equals("Teacher")){
            return false;
        }
        
        File file = new File(FILE_NAME);
        
        try{
            if(!file.exists()){
                try(PrintWriter writer = new PrintWriter(
                    new OutputStreamWriter(
                    new FileOutputStream(file),
                    StandardCharsets.UTF_8))){
                        writer.println("username,password,accountType");
                    }
                }
            try(BufferedReader reader = Files.newBufferedReader(
                    file.toPath(), StandardCharsets.UTF_8)){
                
                String line;
                boolean firstLine = true;
                
                while((line = reader.readLine()) != null){
                    if(firstLine){
                        firstLine = false;
                        continue;
                    }
                    String [] data = line.split(",");
                    
                    if(data.length >= 3 && data [0].equalsIgnoreCase(username)){
                        return false;
                    }
                }
            }
            
                try(PrintWriter writer = new PrintWriter(
                    new OutputStreamWriter(
                    new FileOutputStream(file , true),
                    StandardCharsets.UTF_8))){
                    writer.println(username + "," + password + "," + accountType);
                }
                return true;
                }catch(IOException e){
                    e.printStackTrace();
                    return false;
                }
            
        }
            
     public static String login(String username , String password){
         username = username.trim();
         
         File file = new File(FILE_NAME);
         
         if(!file.exists()){
             return null;
         }
         
         try(BufferedReader reader = Files.newBufferedReader(
            file.toPath(), StandardCharsets.UTF_8)){
             
             String line;
             boolean Firstline = true;
             
             while((line = reader.readLine()) !=null){
                 
                 if(Firstline){
                     Firstline = false;
                     continue;
                 }
                 
                 String [] data = line.split(",");
                 if(data.length >= 3 &&
                         data[0].equalsIgnoreCase(username)){
                     if(data[1].equals(password)){
                         return data[2];
                     }
                     return null;
                 }
             }
             
         }catch(IOException e){
             e.printStackTrace();
         }
         return null;
     }   
    }

