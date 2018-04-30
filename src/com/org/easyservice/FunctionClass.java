/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.easyservice;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author msenell
 */
public class FunctionClass 
{
    protected static boolean checkDate(String date, String dateFormat)
    {
        DateFormat df = new SimpleDateFormat(dateFormat);
                Date dt = null;
                try{
                    dt = df.parse(date);
                }catch(Exception e)
                {
                    return false;
                }
                return true;
    }
    
    protected static String getDateString(Date d)
    {
        String date = "";
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        try
        {
            date = dateFormat.format(d);
            
        }catch(Exception e)
        {
            return null;
        }
        return date;
    }
    
    protected static Date getDateFromString(String dateString)
    {
        Date date=null;
        try {
            DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
            date = format.parse(dateString);
        } catch (ParseException ex) {
            Logger.getLogger(FunctionClass.class.getName()).log(Level.SEVERE, null, ex);
        }
        return date;
    }
    
    protected static boolean checkValue(String productNo)
    {
        if(productNo.equals(""))
            return false;
        else if(productNo.split("\\s").length == 0)
        {
            JOptionPane.showConfirmDialog(null, "Şasi No boşluk içeremez!");
            return false;
        }
        else
        {
            return true;
        
        }
    }
}
