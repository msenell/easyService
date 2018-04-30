/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.easyservice;

import java.awt.Color;
import java.awt.Rectangle;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.swingx.JXDatePicker;


/**
 *
 * @author Mustafa
 */
public class InterfaceClass 
{
    protected static void setChildEnabled(JComponent Parent, boolean en)
    {
        int n = Parent.getComponentCount();
        for(int i = 0; i<n; i++)
        {
            if((Parent.getComponent(i) instanceof JTextField || Parent.getComponent(i) instanceof JTextArea || Parent.getComponent(i) instanceof JComboBox || Parent.getComponent(i) instanceof JXDatePicker)) 
            {
                Parent.getComponent(i).setEnabled(en);
            }
            else if( Parent.getComponent(i) instanceof JButton)
            {
                Parent.getComponent(i).setVisible(en);
            }
        }
    }
    
    protected static void clearText(JComponent Parent)
    {
        int n = Parent.getComponentCount();
        for(int i = 0; i<n; i++)
        {
            if((Parent.getComponent(i) instanceof JTextField )) 
            {
                JTextField txt = (JTextField)Parent.getComponent(i);
                txt.setText("");
            }
            else if(Parent.getComponent(i) instanceof JTextArea)
            {
                JTextArea txt = (JTextArea)Parent.getComponent(i);
                txt.setText("");
            }
            else if(Parent.getComponent(i) instanceof JXDatePicker)
            {
                JXDatePicker txt = (JXDatePicker)Parent.getComponent(i);
                txt.setDate(null);
            }
        }
    }
 
    protected static void selectARow(JTable table, int sRow)
    {
        if(table.getRowCount() > sRow)
	{
            table.setRowSelectionInterval(sRow, sRow);
	        table.scrollRectToVisible(new Rectangle(table.getCellRect(table.getSelectedRow(), table.getSelectedRow(), true)));
		}
    }

    protected static void fillTable(DefaultTableModel d, String sql, JTable table)
    {
        Connection con;
        ResultSet res;
        DatabaseClass dc = new DatabaseClass(); //Veritabanı işlemlerini sağlayacak.
	myTableModel myTM = new myTableModel();
	con = dc.ConnectDb(); // Veritabanına bağlantı yapma.
	res = dc.createResult(con, sql);
        
        
	try {
            //Tablo dolduruluyor.
            d = (DefaultTableModel) table.getModel();
            myTM.getTableModel(d,res);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            Statement st = res.getStatement();
            res.close();
            st.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            con.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected static int getItemIndex(JComboBox cb, String s)
    {
        int size = cb.getItemCount();
        if( size > 0 )
        {
            for(int i = 0; i<size; i++)
            {
                String item = cb.getItemAt(i).toString();
                item = item.toUpperCase();
                s = s.toUpperCase();
                if(item.contains(s))
                    return i;
            }
            return -1;
        }
        else
            return -1;
    }
    
    protected static void addToComboBox(JComboBox cb, String s)
    {
        cb.addItem(s);
    }
    
    protected static void setComponentTextColor(JComponent c, Color cl)
    {
        c.setForeground(cl);
        c.repaint();
    }
    
    protected static void setChildColor(JComponent Parent, Color cl)
    {
        int n = Parent.getComponentCount();
        for(int i = 0; i<n; i++)
        {
            if( Parent.getComponent(i) instanceof JLabel)
            {
                Parent.getComponent(i).setForeground(cl);
                Parent.getComponent(i).repaint();
            }
        }
    }
}
