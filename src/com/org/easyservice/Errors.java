/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.easyservice;



/**
 *
 * @author msenell
 */
public class Errors
{
    public static String[] ErrorList = new String[20];
    
    public static void setErrors()
    {
        ErrorList[0] = "SAP numarası hatalı!";
        ErrorList[1] = "SAP numaralı kayıt daha önceden girilmiş!";
        ErrorList[2] = "Şasi numarası hatalı!";
        ErrorList[3] = "Şasi numarasına ait kayıt daha önceden girilmiş!";
        ErrorList[4] = "Çalışma saati hatalı!";
        ErrorList[5] = "Tarih Hatalı!";
        ErrorList[6] = "İşlenen arazi miktarı hatalı!";
        ErrorList[7] = "Üreticinin yetiştirdiği ürünler hatalı veya eksik!";
        ErrorList[8] = "Satışı yapan bayi girişi hatalı!";
        ErrorList[9] = "Satışı yapan bayi kayıtlarda mevcut değil!";
        ErrorList[10] = "Motor numarası hatalı!";
        ErrorList[11] = "Müşteri bilgisi hatalı!";
        ErrorList[12] = "Model bilgisi hatalı!";
        
    }
    public static String getErrorText(int index)
    {
        return ErrorList[index];
    }
    
}
