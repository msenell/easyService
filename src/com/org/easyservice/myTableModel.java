/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.easyservice;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Mustafa
 */
public class myTableModel 
{
    public void getTableModel(DefaultTableModel d, ResultSet rs) throws SQLException {
		d.setRowCount(0); //Satır sayısını sıfırlar :)
		ResultSetMetaData metadata = (ResultSetMetaData) rs.getMetaData(); //RS metadatası okunur.
        int columnsCount = metadata.getColumnCount()+1; //kolon sayısı bulunur.
        Vector<String> columnNames = new Vector<>();
        for (int i = 1; i < columnsCount; i++) {
            columnNames.add(metadata.getColumnName(i)); //Kolon adları alınır.
        }
        d.setColumnIdentifiers(columnNames); //Tablo modeline kolon adları ayarlanır.
        while (rs.next()) {
            Vector<Object> rowData = new Vector<>();
            for (int i = 1; i < columnsCount; i++) {
            	rowData.add(rs.getObject(i)); //Satırlar okunur
            }
            d.addRow(rowData); //Tablo modelinin içi doldurulur.
        }
        
    }
}
