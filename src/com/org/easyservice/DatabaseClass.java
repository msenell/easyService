/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.easyservice;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

/**
 *
 * @author Mustafa
 */
public class DatabaseClass 
{
    //public String conDriver = "org.apache.derby.jdbc.EmbeddedDriver";
    public String conDriverSQLite = "org.sqlite.JDBC"; //SQLite bağlantı driverı
    //public String conPath2 = "jdbc:derby:appData/otosenel;create=true";
    //public String conPath = "jdbc:derby:appData/serviceDB;create=true";
    public String conPathSQLite = "jdbc:sqlite:appData/ssenel.db"; //SQLite path
    /*public Connection ConnectDb2() //MusteriDb baglantı olusturma(Derby için)
    {
            try {
			Class.forName(conDriver);
		} catch (ClassNotFoundException e) {
			JOptionPane.showMessageDialog(null, "JDBC Apache Derby Sürücüsü yüklenemedi!", "DatabaseClass Error", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(conPath);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Veritabanına erişilemiyor!", "DatabaseClass Error", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
                return conn;
    }*/

    public Connection ConnectDb() //MusteriDb baglantı olusturma(SQLite için)
    {
            try {
			Class.forName(conDriverSQLite);
		} catch (ClassNotFoundException e) {
			JOptionPane.showMessageDialog(null, "JDBC SQLite Sürücüsü yüklenemedi!", "DatabaseClass Error", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(conPathSQLite);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Veritabanına erişilemiyor!", "DatabaseClass Error", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
                return conn;
    }
    public ResultSet createResult(Connection conn, String sql) //ResultSet olusturma
    {
        ResultSet rs = null;
        
        Statement st = null;
        try {
            st = conn.createStatement();
        } catch (SQLException ex) {
			JOptionPane.showMessageDialog(null, "Veritabanına erişilemiyor!", "DatabaseClass Error", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
        }
        try {
            rs = st.executeQuery(sql);
        } catch (SQLException ex) {
			JOptionPane.showMessageDialog(null, "İstenen tabloya erişilemiyor!", "DatabaseClass Error", JOptionPane.ERROR_MESSAGE);
        	ex.printStackTrace();
                        System.exit(0);
        }
        return rs;
    }
	
    public boolean exeQuery(PreparedStatement pSTM) //Sorgu işletme
    {
		try {
			pSTM.executeUpdate();
			return true;
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Sorgu işletilemedi!", "Database Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			return false;
		}
		
	}
}
