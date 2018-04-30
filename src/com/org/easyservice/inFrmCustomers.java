/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.easyservice;

import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.MaskFormatter;

/**
 *
 * @author Mustafa
 */
public class inFrmCustomers extends javax.swing.JInternalFrame {

    /**
     * Creates new form inFrmCustomers
     */
    TableRowSorter<TableModel> sorter;
    private int searchCol = 2; //Arama Kolonu varsayılanı: Ad Soyad
    private int sRow=0; //selectedRow(Seçili Satır)
    DefaultTableModel d;
    boolean saveType = true;
    String fillSQL = "SELECT * FROM tblCustomers";  //JTable doldurma sorgusu
    private MaskFormatter mfTC;  //Tc alanı 11 hane maskesi
    private MaskFormatter mfPhone;  //Telefon alanı maskesi
    String maskTC = "###########";
    String maskPhone = "#### ### ####";
    
    
    public inFrmCustomers() 
    {
        try {
            mfTC = new MaskFormatter(maskTC);
            mfPhone = new MaskFormatter(maskPhone);
            //mfPhone.setPlaceholderCharacter('');
            //mfTC.setPlaceholderCharacter('');  //TC No Giriş Maskesi
        } catch (ParseException ex) {
            Logger.getLogger(inFrmCustomers.class.getName()).log(Level.SEVERE, null, ex);
        }
        initComponents();
        jTextField1.setVisible(false);
        setSearchLabelText();
        d = (DefaultTableModel) tblCustomer.getModel();
        sorter = new TableRowSorter<TableModel>(d); //Arama filtresi           
        tblCustomer.setRowSorter(sorter);
        InterfaceClass.fillTable(d, fillSQL, tblCustomer);
        tblCustomer.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                fillTextFields(sRow=tblCustomer.getSelectedRow());
            }
        });
        sorter.addRowSorterListener(new RowSorterListener() {
            @Override
            public void sorterChanged(RowSorterEvent e) {
                //tblCustomer.setRowSelectionInterval(0, 0);
            }
        });
        InterfaceClass.selectARow(tblCustomer, sRow);
        fillTextFields(sRow);
        InterfaceClass.setChildEnabled(pnlTextFields, false);
        
    }
    
    private void setSearchLabelText()  //Arama kolunu seçimine göre label texti basma
    {
        if(rbTC.isSelected())
            lblSearch.setText(rbTC.getText() + " :");
        else
            lblSearch.setText(rbName.getText() + " :");
    }
    
    private void fillTextFields(int selectedRow) //TextField'ları JTable'dan dolduran metod
    {
        if(tblCustomer.getRowCount()>0)
	{
            txtTC.setText( String.valueOf( tblCustomer.getValueAt(selectedRow, 1)) );
            txtName.setText( String.valueOf( tblCustomer.getValueAt(selectedRow, 2)) );
            txtPhone.setText( String.valueOf( tblCustomer.getValueAt(selectedRow, 3)) );
            txtMobile.setText( String.valueOf( tblCustomer.getValueAt(selectedRow, 4)) );
            txtEMail.setText( String.valueOf( tblCustomer.getValueAt(selectedRow, 5)) );
            txtAddress.setText( String.valueOf( tblCustomer.getValueAt(selectedRow, 6)) );
            txtNotes.setText( String.valueOf( tblCustomer.getValueAt(selectedRow, 7)) );
            jTextField1.setText( ""+(int)tblCustomer.getValueAt(selectedRow, 0) );
        }
        else
        {
            txtTC.setText( "Kayıt Yok!" );
            txtName.setText( "Kayıt Yok!" );
            txtPhone.setText( "Kayıt Yok!" );
            txtMobile.setText( "Kayıt Yok!" );
            txtEMail.setText( "Kayıt Yok!" );
            txtAddress.setText( "Kayıt Yok!" );
            txtNotes.setText( "Kayıt Yok!" );
            jTextField1.setText( "Kayıt Yok!" );
        }
    }
    
    private void setLabelColor(String tcNo, String name, String cepNo)  //Label renklendirme
    {
        if(tcNo.equals(""))
            lblTC.setForeground(Color.red);
        else
            lblTC.setForeground(FrmMain.captionColor);
        if(name.equals(""))
            lblName.setForeground(Color.red);
        else
            lblName.setForeground(FrmMain.captionColor);
        if(cepNo.equals(""))
            lblMobile.setForeground(Color.red);
        else
            lblMobile.setForeground(FrmMain.captionColor);
        this.revalidate();
        this.repaint();
    }
    
    private int spaceCount(String str)
    {
        int i = str.length()-1;
        int cTC = 0;  //str'deki boşluk sayısını tutacak değişken.
        while(i >= 0)  //str'deki boşluk sayısı
        {
            if(str.charAt(i) == ' ')
                cTC++;
            i--;
        }
        return cTC;
    }
    //TC No uygun giriş kontrolü
    //checkTC( TC Numarası, Müşteri Numarası, Ekleme Mi?(true)/Güncelleme Mi?(false) )
    private boolean checkTC(String tcNo, int cusNo, boolean isAdd)
    {
        boolean result = false; //Tekrar yok.
        String checkTCSql ="";
        if(spaceCount(tcNo) == 11)  //TC No alanın tamamen boş ise
            tcNo = "";
        
        if(isAdd)  //TC No ekleme işlemi için kontrol edilecekse
        {
            checkTCSql = "SELECT COUNT(*) AS count FROM tblCustomers WHERE TC = '" + tcNo + "'"; 
            //Girilen TC no ile kaç tane kayıt var?
        }
        else
        {
            checkTCSql = "SELECT COUNT(*) AS count FROM tblCustomers WHERE TC = '" + tcNo + "' AND CustomerNo != '" + cusNo + "'" ; 
            //TC No verilen müşteri nolu kayıt haricinde kaç kez kullanılmış?
        }
        DatabaseClass dc = new DatabaseClass();
        Connection con = dc.ConnectDb();
        ResultSet rs = dc.createResult(con, checkTCSql);
        try {
            rs.next();
        } catch (SQLException ex) {
            Logger.getLogger(inFrmCustomers.class.getName()).log(Level.SEVERE, null, ex);
        }
        int count = 0;  
        try {
            count = rs.getInt("count"); //Girilen TC numarasından tabloda kaç adet var?
        } catch (SQLException ex) {
            Logger.getLogger(inFrmCustomers.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(count > 0)
        {
            result = true;  //Tekrar var
        }
        try {
            rs.close();
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(inFrmCustomers.class.getName()).log(Level.SEVERE, null, ex);
        }
        boolean tcEmpty = tcNo.equals(""); //TC alanı boş ise true.
        boolean tcFail = spaceCount(tcNo) >0;  //TC No'da boşluk var ise true.
        result = result || tcFail;
        result = result && (!tcEmpty); //TC boş ise: false.
        return   result; //true: tekrar var veya hatalı, kaydedilemez; false: tekrar yok, kaydedilebilir. 
    }
    
    private boolean editCustomer()
    {
        //Textfield'lardan veri alma
        String tcNo = txtTC.getText();
        if(spaceCount(tcNo) == 11)  //TC No alanı tamamen boş ise
            tcNo = "";
        String adSoyad = txtName.getText();
        String telNo = txtPhone.getText();
        String cepNo = txtMobile.getText();
        if(spaceCount(cepNo) == 13)
            cepNo = "";
        String ePosta = txtEMail.getText();
        String adres = txtAddress.getText();
        String aciklama = txtNotes.getText();
        int cusNo=(int) tblCustomer.getValueAt(sRow, 0);  //seçili satırdaki müşteri nosu
        String sql = "UPDATE tblCustomers SET TC = ?,"
                + "Name = ?,"
                + "Phone = ?,"
                + "Mobile = ?,"
                + "EMail = ?,"
                + "Address = ?,"
                + "Notes = ? WHERE CustomerNo = ?";
        DatabaseClass dc;
        Connection con = null;
        PreparedStatement pSTM = null;
        try {
            if(checkTC(tcNo, cusNo, false))
            {
                setLabelColor("", adSoyad, cepNo);
                JOptionPane.showMessageDialog(null, "TC No Hatalı veya Tekrar Ediyor!");
            }
            else if(adSoyad.equals("") || cepNo.equals(""))
            {
                setLabelColor(tcNo, adSoyad, cepNo);
                JOptionPane.showMessageDialog(null, "Zorunlu alanları doldurun!");
            }
            else
            {
                dc = new DatabaseClass();
                con = dc.ConnectDb();
                pSTM = con.prepareStatement(sql);
                pSTM.setString(1, tcNo);
                pSTM.setString(2, adSoyad);
                pSTM.setString(3, telNo);
                pSTM.setString(4, cepNo);
                pSTM.setString(5, ePosta);
                pSTM.setString(6, adres);
                pSTM.setString(7, aciklama);
                pSTM.setInt(8, cusNo);
                if(!dc.exeQuery(pSTM))
		{
			JOptionPane.showMessageDialog(null, "Sorgu İşletilemedi!", "Database Error", JOptionPane.ERROR_MESSAGE);
		}
		else
                {
			JOptionPane.showMessageDialog(null, tblCustomer.getValueAt(sRow, 0) + " Numaralı Kayıt Güncellendi!", "Mission Completed", JOptionPane.INFORMATION_MESSAGE);
                        pSTM.close();
                        con.close();
                        return true;
                        //setLabelColor(tcNo, adSoyad, cepNo);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(inFrmCustomers.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    private void addCustomer()
    {
        String tcNo = txtTC.getText();
        String adSoyad = txtName.getText();
        String telNo = txtPhone.getText();
        String cepNo = txtMobile.getText();
        if(spaceCount(cepNo) == 13)
            cepNo = "";
        String ePosta = txtEMail.getText();
        String adres = txtAddress.getText();
        String aciklama = txtNotes.getText();
        String sql = "INSERT INTO tblCustomers(TC, Name, Phone, Mobile, EMail, Address, Notes) "
                + "VALUES(?, ?, ?, ?, ?, ?, ?)";
        DatabaseClass dc = new DatabaseClass();
        Connection con = dc.ConnectDb();
        try {
            if(checkTC(tcNo, -1, true))
            {
                setLabelColor("", adSoyad, cepNo);
                JOptionPane.showMessageDialog(null, "TC No Tekrar Edemez!");
            }
            else if(adSoyad.equals("") || cepNo.equals(""))
            {
                setLabelColor(tcNo, adSoyad, cepNo);
                JOptionPane.showMessageDialog(null, "Zorunlu alanları doldurun!");
            }
            else  //TC No tekrar etmemişse
            {
                PreparedStatement pSTM = con.prepareStatement(sql);
                pSTM.setString(1, tcNo);
                pSTM.setString(2, adSoyad);
                pSTM.setString(3, telNo);
                pSTM.setString(4, cepNo);
                pSTM.setString(5, ePosta);
                pSTM.setString(6, adres);
                pSTM.setString(7, aciklama);
                if(!dc.exeQuery(pSTM))
		{
			JOptionPane.showMessageDialog(null, "Sorgu İşletilemedi!", "Database Error", JOptionPane.ERROR_MESSAGE);
		}
		else
			JOptionPane.showMessageDialog(null, "Kayıt Eklendi!", "Mission Completed", JOptionPane.INFORMATION_MESSAGE);
            
                pSTM.close();
                con.close();
                InterfaceClass.fillTable(d, fillSQL, tblCustomer);  //JTable güncelle
                sRow = tblCustomer.getRowCount() -1;
                InterfaceClass.selectARow(tblCustomer, sRow);
                fillTextFields(sRow);
                InterfaceClass.setChildEnabled(pnlTextFields, false);
            }
        } catch (SQLException ex) {
            Logger.getLogger(inFrmCustomers.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private boolean deleteMaintenance(String productNo)
    {
        boolean _result = false;
        String sql = "DELETE FROM tblMaintenance WHERE ProductNo = ?";
        DatabaseClass dc = new DatabaseClass();
        Connection con = dc.ConnectDb();
        PreparedStatement pStm = null;
        try {
            pStm = con.prepareStatement(sql);
        } catch (SQLException ex) {
            _result = false;
            Logger.getLogger(inFrmCustomers.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            pStm.setString(1, productNo);
        } catch (SQLException ex) {
            _result = false;
            Logger.getLogger(inFrmCustomers.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(!dc.exeQuery(pStm))
        {
            _result = false;
        }
        else
        {
            _result = true;
        }
        try {
            if(pStm != null)
                pStm.close();
            if(con != null)
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(inFrmCustomers.class.getName()).log(Level.SEVERE, null, ex);
            }
        return _result;
    }

    private void deleteCustomer()
    {
        boolean _result = false;
        ArrayList<String> products = new ArrayList<String>();
        int response = JOptionPane.showConfirmDialog(null, tblCustomer.getValueAt(sRow, 0) + " nolu müşteriyi silmek istediğinize emin misiniz?\nMüşteri silindiğinde müşteriye ait ürünler, bakımlar ve KBE'ler de silinecektir.", "Are you Sure?", JOptionPane.YES_NO_OPTION);
	if(response == JOptionPane.YES_OPTION)
	{
            DatabaseClass dc = new DatabaseClass();
            Connection con = dc.ConnectDb();
            String sql = "SELECT ProductNo FROM tblProducts WHERE CustomerNo = ?"; //Müşteriye ait ürünleri çeken sorgu.
            PreparedStatement pStm = null;
            try {
                pStm = con.prepareStatement(sql);
                pStm.setInt(1, (int)tblCustomer.getValueAt(sRow, 0));
                ResultSet rs = pStm.executeQuery();
                while(rs.next())
                {
                    products.add(rs.getString("ProductNo"));
                }
		} catch (SQLException e) {
                    e.printStackTrace();
                }
            if(pStm != null)
                try {
                    pStm.close();
            } catch (SQLException ex) {
                Logger.getLogger(inFrmCustomers.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(con != null)
                try {
                    con.close();
            } catch (SQLException ex) {
                Logger.getLogger(inFrmCustomers.class.getName()).log(Level.SEVERE, null, ex);
            }
            for(String str : products) //Müşteriye ait ürünlerin bakım kayıtlarını silen sorgu.
            {
                _result = deleteMaintenance(str);
                    if(!_result)
                    {
                        JOptionPane.showMessageDialog(rootPane, str + "şasi nolu ürüne ait bakımlar silinirken hata oluştu!");
                        continue; //Hata oluşursa döngüyü sonlandır.
                    }
            }
            if(_result) //Bakımlar silinirken hata oluşmamış ise:
            {
                con = dc.ConnectDb();
                pStm = null;
                //Müşteriye ait ürünleri sil:
                sql = "DELETE FROM tblProducts WHERE CustomerNo = ?";
                try {
                    pStm = con.prepareStatement(sql);
                } catch (SQLException ex) {
                    _result = false;
                    Logger.getLogger(inFrmCustomers.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    pStm.setInt(1, (int)tblCustomer.getValueAt(sRow, 0));
                } catch (SQLException ex) {
                    _result = false;
                    Logger.getLogger(inFrmCustomers.class.getName()).log(Level.SEVERE, null, ex);
                }
                if(!dc.exeQuery(pStm))
                {
                    _result = false;
                    JOptionPane.showMessageDialog(null, "Müşteriye ait ürünler silinemedi!!", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
                else
                    _result = true;
                
            }
            if(_result)
            {
                sql = "DELETE FROM tblCustomers WHERE CustomerNo = ?";
                pStm = null;
                try {
                    pStm = con.prepareStatement(sql);
                } catch (SQLException ex) {
                    _result = false;
                    Logger.getLogger(inFrmCustomers.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    pStm.setInt(1, (int)tblCustomer.getValueAt(sRow, 0));
                } catch (SQLException ex) {
                    _result = false;
                    Logger.getLogger(inFrmCustomers.class.getName()).log(Level.SEVERE, null, ex);
                }
                if(!dc.exeQuery(pStm))
                {
                    _result = false;
                    JOptionPane.showMessageDialog(null, "Müşteriye silinemedi!!", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
                else
                {
                    _result = true;
                    JOptionPane.showMessageDialog(null, tblCustomer.getValueAt(sRow, 0) + " Nolu Müşteri Silindi!", "Mission Completed", JOptionPane.INFORMATION_MESSAGE);
                }       
                
            }  
            try {
                if(pStm != null)
                    pStm.close();
                if(con != null)
                    con.close();
            } catch (SQLException ex) {
                Logger.getLogger(inFrmCustomers.class.getName()).log(Level.SEVERE, null, ex);
            }
            InterfaceClass.fillTable(d, fillSQL, tblCustomer);  //JTable güncelle
            sRow = tblCustomer.getRowCount()-1;
            InterfaceClass.selectARow(tblCustomer, sRow);
            fillTextFields(sRow);
            
        }         
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bgSearch = new javax.swing.ButtonGroup();
        pnlTable = new javax.swing.JPanel();
        btnKBE = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCustomer = new javax.swing.JTable(new DefaultTableModel(){  //Tablo cellEditable özelliği 'false'
            public boolean isCellEditable(int row, int column) //olacak şekilde oluşturuluyor.
            {
                return false;
            }
        });
        pnlTextFields = new javax.swing.JPanel();
        lblTC = new javax.swing.JLabel();
        lblName = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        lblPhone = new javax.swing.JLabel();
        txtPhone = new javax.swing.JTextField();
        lblMobile = new javax.swing.JLabel();
        lblEMail = new javax.swing.JLabel();
        txtEMail = new javax.swing.JTextField();
        lblAddress = new javax.swing.JLabel();
        txtAddress = new javax.swing.JTextArea();
        lblNotes = new javax.swing.JLabel();
        txtNotes = new javax.swing.JTextArea();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        txtTC = new javax.swing.JFormattedTextField(mfTC);
        txtMobile = new javax.swing.JFormattedTextField(mfPhone);
        jTextField1 = new javax.swing.JTextField();
        pnlNavigator = new javax.swing.JPanel();
        btnFirst = new javax.swing.JButton();
        btnPrevious = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnAdd = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnNext = new javax.swing.JButton();
        btnLast = new javax.swing.JButton();
        pnlSearch = new javax.swing.JPanel();
        lblSearch = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        rbTC = new javax.swing.JRadioButton();
        rbName = new javax.swing.JRadioButton();

        setClosable(true);
        setResizable(true);
        setTitle("Müşteriler");
        setName(""); // NOI18N
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
                formİnternalFrameClosed(evt);
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        pnlTable.setBackground(java.awt.SystemColor.inactiveCaption);
        pnlTable.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnKBE.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnKBE.setForeground(new java.awt.Color(199, 92, 92));
        btnKBE.setText("KBE");
        btnKBE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKBEActionPerformed(evt);
            }
        });

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 204));
        jScrollPane1.setForeground(new java.awt.Color(199, 92, 92));

        tblCustomer.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tblCustomer.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblCustomer.setUpdateSelectionOnSort(false);
        tblCustomer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblCustomerMouseClicked(evt);
            }
        });
        tblCustomer.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
                tblCustomerCaretPositionChanged(evt);
            }
        });
        jScrollPane1.setViewportView(tblCustomer);

        javax.swing.GroupLayout pnlTableLayout = new javax.swing.GroupLayout(pnlTable);
        pnlTable.setLayout(pnlTableLayout);
        pnlTableLayout.setHorizontalGroup(
            pnlTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTableLayout.createSequentialGroup()
                .addComponent(btnKBE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(jScrollPane1)
        );
        pnlTableLayout.setVerticalGroup(
            pnlTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlTableLayout.createSequentialGroup()
                .addComponent(btnKBE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
                .addContainerGap())
        );

        pnlTextFields.setBackground(java.awt.SystemColor.activeCaption);
        pnlTextFields.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblTC.setFont(new java.awt.Font("Arial Black", 1, 14)); // NOI18N
        lblTC.setForeground(new java.awt.Color(199, 92, 92));
        lblTC.setText("TC No :");

        lblName.setFont(new java.awt.Font("Arial Black", 1, 14)); // NOI18N
        lblName.setForeground(new java.awt.Color(199, 92, 92));
        lblName.setText("Adı Soyadı :");

        txtName.setBackground(new java.awt.Color(255, 255, 204));
        txtName.setColumns(15);
        txtName.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtName.setText("jTextField2");
        txtName.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(199, 92, 92)));

        lblPhone.setFont(new java.awt.Font("Arial Black", 1, 14)); // NOI18N
        lblPhone.setForeground(new java.awt.Color(199, 92, 92));
        lblPhone.setText("Telefon :");

        txtPhone.setBackground(new java.awt.Color(255, 255, 204));
        txtPhone.setColumns(9);
        txtPhone.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtPhone.setText("jTextField1");
        txtPhone.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(199, 92, 92)));

        lblMobile.setFont(new java.awt.Font("Arial Black", 1, 14)); // NOI18N
        lblMobile.setForeground(new java.awt.Color(199, 92, 92));
        lblMobile.setText("Cep :");

        lblEMail.setFont(new java.awt.Font("Arial Black", 1, 14)); // NOI18N
        lblEMail.setForeground(new java.awt.Color(199, 92, 92));
        lblEMail.setText("E-Posta :");

        txtEMail.setBackground(new java.awt.Color(255, 255, 204));
        txtEMail.setColumns(9);
        txtEMail.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtEMail.setText("jTextField2");
        txtEMail.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(199, 92, 92)));

        lblAddress.setFont(new java.awt.Font("Arial Black", 1, 14)); // NOI18N
        lblAddress.setForeground(new java.awt.Color(199, 92, 92));
        lblAddress.setText("Adres :");

        txtAddress.setBackground(new java.awt.Color(255, 255, 204));
        txtAddress.setColumns(20);
        txtAddress.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtAddress.setLineWrap(true);
        txtAddress.setRows(5);
        txtAddress.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(199, 92, 92)));

        lblNotes.setFont(new java.awt.Font("Arial Black", 1, 14)); // NOI18N
        lblNotes.setForeground(new java.awt.Color(199, 92, 92));
        lblNotes.setText("Açıklama :");

        txtNotes.setBackground(new java.awt.Color(255, 255, 204));
        txtNotes.setColumns(20);
        txtNotes.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtNotes.setRows(5);
        txtNotes.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(199, 92, 92)));

        btnSave.setFont(new java.awt.Font("Arial Black", 0, 12)); // NOI18N
        btnSave.setForeground(new java.awt.Color(199, 92, 92));
        btnSave.setText("Kaydet");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnCancel.setFont(new java.awt.Font("Arial Black", 0, 12)); // NOI18N
        btnCancel.setForeground(new java.awt.Color(199, 92, 92));
        btnCancel.setText("Vazgeç");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        txtTC.setBackground(new java.awt.Color(255, 255, 204));
        txtTC.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(199, 92, 92)));
        txtTC.setColumns(11);
        txtTC.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtTC.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtTC.setPreferredSize(new java.awt.Dimension(112, 20));
        txtTC.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                txtTCMouseReleased(evt);
            }
        });

        txtMobile.setBackground(new java.awt.Color(255, 255, 204));
        txtMobile.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(199, 92, 92)));
        txtMobile.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        jTextField1.setText("jTextField1");

        javax.swing.GroupLayout pnlTextFieldsLayout = new javax.swing.GroupLayout(pnlTextFields);
        pnlTextFields.setLayout(pnlTextFieldsLayout);
        pnlTextFieldsLayout.setHorizontalGroup(
            pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTextFieldsLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblName)
                    .addComponent(lblTC)
                    .addComponent(lblPhone))
                .addGap(18, 18, 18)
                .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtName)
                    .addComponent(txtTC, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtPhone))
                .addGap(18, 18, 18)
                .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlTextFieldsLayout.createSequentialGroup()
                        .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblEMail)
                            .addComponent(lblMobile))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtMobile)
                            .addComponent(txtEMail, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)))
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblAddress)
                    .addComponent(txtAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(pnlTextFieldsLayout.createSequentialGroup()
                        .addComponent(btnSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancel))
                    .addComponent(lblNotes)
                    .addComponent(txtNotes, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        pnlTextFieldsLayout.setVerticalGroup(
            pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTextFieldsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblTC)
                    .addComponent(txtTC, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblMobile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtMobile, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblAddress)
                    .addComponent(lblNotes))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlTextFieldsLayout.createSequentialGroup()
                        .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblName)
                            .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblEMail)
                            .addComponent(txtEMail, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblPhone, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(12, 12, 12))
                    .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtNotes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSave)
                    .addComponent(btnCancel)))
        );

        txtAddress.getAccessibleContext().setAccessibleParent(pnlTextFields);

        pnlNavigator.setBackground(java.awt.SystemColor.inactiveCaption);
        pnlNavigator.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlNavigator.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        btnFirst.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/org/easyservice/Images/0.png"))); // NOI18N
        btnFirst.setMinimumSize(new java.awt.Dimension(64, 64));
        btnFirst.setOpaque(false);
        btnFirst.setPreferredSize(new java.awt.Dimension(70, 70));
        btnFirst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFirstActionPerformed(evt);
            }
        });

        btnPrevious.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/org/easyservice/Images/1.png"))); // NOI18N
        btnPrevious.setMinimumSize(new java.awt.Dimension(64, 64));
        btnPrevious.setPreferredSize(new java.awt.Dimension(70, 70));
        btnPrevious.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviousActionPerformed(evt);
            }
        });

        btnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/org/easyservice/Images/2.png"))); // NOI18N
        btnEdit.setMinimumSize(new java.awt.Dimension(64, 64));
        btnEdit.setPreferredSize(new java.awt.Dimension(70, 70));
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });

        btnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/org/easyservice/Images/3.png"))); // NOI18N
        btnAdd.setMinimumSize(new java.awt.Dimension(64, 64));
        btnAdd.setPreferredSize(new java.awt.Dimension(70, 70));
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/org/easyservice/Images/4.png"))); // NOI18N
        btnDelete.setMinimumSize(new java.awt.Dimension(64, 64));
        btnDelete.setPreferredSize(new java.awt.Dimension(70, 70));
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnNext.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/org/easyservice/Images/5.png"))); // NOI18N
        btnNext.setMinimumSize(new java.awt.Dimension(64, 64));
        btnNext.setPreferredSize(new java.awt.Dimension(70, 70));
        btnNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextActionPerformed(evt);
            }
        });

        btnLast.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/org/easyservice/Images/6.png"))); // NOI18N
        btnLast.setMinimumSize(new java.awt.Dimension(64, 64));
        btnLast.setPreferredSize(new java.awt.Dimension(70, 70));
        btnLast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLastActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlNavigatorLayout = new javax.swing.GroupLayout(pnlNavigator);
        pnlNavigator.setLayout(pnlNavigatorLayout);
        pnlNavigatorLayout.setHorizontalGroup(
            pnlNavigatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlNavigatorLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnFirst, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(btnPrevious, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(btnEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(btnNext, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(btnLast, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlNavigatorLayout.setVerticalGroup(
            pnlNavigatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlNavigatorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlNavigatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btnLast, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNext, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPrevious, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFirst, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlSearch.setBackground(java.awt.SystemColor.activeCaption);
        pnlSearch.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Ara", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(199, 92, 92))); // NOI18N
        pnlSearch.setForeground(new java.awt.Color(255, 255, 204));
        pnlSearch.setFont(new java.awt.Font("Arial Black", 0, 11)); // NOI18N

        lblSearch.setFont(new java.awt.Font("Arial Black", 1, 14)); // NOI18N
        lblSearch.setForeground(new java.awt.Color(199, 92, 92));
        lblSearch.setText("jLabel1");

        txtSearch.setBackground(new java.awt.Color(255, 255, 204));
        txtSearch.setColumns(15);
        txtSearch.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtSearch.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(199, 92, 92)));
        txtSearch.setMinimumSize(new java.awt.Dimension(2, 20));
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSearchKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchKeyReleased(evt);
            }
        });

        rbTC.setBackground(java.awt.SystemColor.activeCaption);
        bgSearch.add(rbTC);
        rbTC.setFont(new java.awt.Font("Arial Black", 1, 14)); // NOI18N
        rbTC.setForeground(new java.awt.Color(199, 92, 92));
        rbTC.setText("TC No");
        rbTC.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                rbTCStateChanged(evt);
            }
        });

        rbName.setBackground(java.awt.SystemColor.activeCaption);
        bgSearch.add(rbName);
        rbName.setFont(new java.awt.Font("Arial Black", 1, 14)); // NOI18N
        rbName.setForeground(new java.awt.Color(199, 92, 92));
        rbName.setSelected(true);
        rbName.setText("Ad Soyad");

        javax.swing.GroupLayout pnlSearchLayout = new javax.swing.GroupLayout(pnlSearch);
        pnlSearch.setLayout(pnlSearchLayout);
        pnlSearchLayout.setHorizontalGroup(
            pnlSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSearchLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblSearch)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(59, 59, 59)
                .addComponent(rbTC)
                .addGap(18, 18, 18)
                .addComponent(rbName)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlSearchLayout.setVerticalGroup(
            pnlSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSearchLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSearch)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rbTC)
                    .addComponent(rbName))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        rbTC.getAccessibleContext().setAccessibleDescription("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlTextFields, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlTable, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlNavigator, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlSearch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(pnlTable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(1, 1, 1)
                .addComponent(pnlNavigator, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(pnlTextFields, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formİnternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formİnternalFrameClosed
        // TODO add your handling code here:
    }//GEN-LAST:event_formİnternalFrameClosed

    private void rbTCStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_rbTCStateChanged
        // TODO add your handling code here:
        setSearchLabelText();
        if(rbTC.isSelected())
            searchCol = 1;
        else
            searchCol = 2;
    }//GEN-LAST:event_rbTCStateChanged

    private void txtSearchKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyPressed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_txtSearchKeyPressed

    private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyReleased
        // TODO add your handling code here:
        String text = txtSearch.getText();
        if(text.length() == 0)
        {
            sorter.setRowFilter(null);
        }
        else if(searchCol == 1)
        {
            sorter.setRowFilter(javax.swing.RowFilter.regexFilter("^" + text, searchCol));
        }
        else if(searchCol == 2)
        {
            sorter.setRowFilter(javax.swing.RowFilter.regexFilter("(?iu)" + text, searchCol)); //(?iu: unicode non-case sensitive
        }
        //sRow = tblCustomer.getSelectedRow();
        
    }//GEN-LAST:event_txtSearchKeyReleased

    private void btnFirstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFirstActionPerformed
    sRow = 0;
    InterfaceClass.selectARow(tblCustomer, sRow);
    }//GEN-LAST:event_btnFirstActionPerformed

    private void btnPreviousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousActionPerformed
        if(tblCustomer.getSelectedRow() != 0)
        {
            sRow = tblCustomer.getSelectedRow()-1;
            InterfaceClass.selectARow(tblCustomer, sRow);	
	}
    }//GEN-LAST:event_btnPreviousActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        // TODO add your handling code here:
        saveType = true;
        InterfaceClass.setChildEnabled(pnlTextFields, true);
        tblCustomer.setEnabled(false);
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        // TODO add your handling code here:
        InterfaceClass.clearText(pnlTextFields);
        saveType = false;
        InterfaceClass.setChildEnabled(pnlTextFields, true);
        tblCustomer.setEnabled(false);
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:
        deleteCustomer();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
        // TODO add your handling code here:
        if(tblCustomer.getSelectedRow() != tblCustomer.getRowCount()-1)
	{
            sRow = tblCustomer.getSelectedRow()+1;
            InterfaceClass.selectARow(tblCustomer, sRow);
	}
    }//GEN-LAST:event_btnNextActionPerformed

    private void btnLastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLastActionPerformed
        // TODO add your handling code here:
        sRow = tblCustomer.getRowCount()-1;
	InterfaceClass.selectARow(tblCustomer, sRow);
    }//GEN-LAST:event_btnLastActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        // TODO add your handling code here:
        InterfaceClass.setChildEnabled(pnlTextFields, false);
        setLabelColor(" ", " ", " ");  //Label Fontları Siyah
        if(tblCustomer.getRowCount() > 0)
            InterfaceClass.selectARow(tblCustomer, sRow);
        tblCustomer.setEnabled(true);
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        if(saveType)
        {
            if(editCustomer())
            {
                setLabelColor(" ", " ", " ");  //Label Fontları Siyah
                int _sRow = sRow;
                InterfaceClass.fillTable(d, fillSQL, tblCustomer);  //JTable güncelle
                sRow = _sRow;
                InterfaceClass.selectARow(tblCustomer, sRow);
                fillTextFields(sRow);
                
                tblCustomer.setEnabled(true);
                InterfaceClass.setChildEnabled(pnlTextFields, false);
            }
        }
        else
            addCustomer();

    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnKBEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKBEActionPerformed
        // TODO add your handling code here:
        inFrmKBE ifKBE = new inFrmKBE((int)tblCustomer.getValueAt(sRow, 0));
        sRow = tblCustomer.getSelectedRow();
        FrmMain.frm.dskPaneMain.add(ifKBE);
        ifKBE.show();
    }//GEN-LAST:event_btnKBEActionPerformed

    private void tblCustomerMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblCustomerMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2)
        {
            JTable target = (JTable)evt.getSource();
            int row = target.getSelectedRow();
            row = (int)target.getValueAt(row, 0);
            inFrmProducts ifc = new inFrmProducts(row);
            FrmMain.frm.dskPaneMain.add(ifc);
            ifc.show();
        }
    }//GEN-LAST:event_tblCustomerMouseClicked

    private void txtTCMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtTCMouseReleased
        // TODO add your handling code here:
        txtTC.setCaretPosition(0);
    }//GEN-LAST:event_txtTCMouseReleased

    private void tblCustomerCaretPositionChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_tblCustomerCaretPositionChanged
        // TODO add your handling code here:
        sRow = tblCustomer.getSelectedRow();
    }//GEN-LAST:event_tblCustomerCaretPositionChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgSearch;
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnFirst;
    private javax.swing.JButton btnKBE;
    private javax.swing.JButton btnLast;
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnPrevious;
    private javax.swing.JButton btnSave;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel lblAddress;
    private javax.swing.JLabel lblEMail;
    private javax.swing.JLabel lblMobile;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblNotes;
    private javax.swing.JLabel lblPhone;
    private javax.swing.JLabel lblSearch;
    private javax.swing.JLabel lblTC;
    private javax.swing.JPanel pnlNavigator;
    private javax.swing.JPanel pnlSearch;
    private javax.swing.JPanel pnlTable;
    private javax.swing.JPanel pnlTextFields;
    private javax.swing.JRadioButton rbName;
    private javax.swing.JRadioButton rbTC;
    private javax.swing.JTable tblCustomer;
    private javax.swing.JTextArea txtAddress;
    private javax.swing.JTextField txtEMail;
    private javax.swing.JFormattedTextField txtMobile;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextArea txtNotes;
    private javax.swing.JTextField txtPhone;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JFormattedTextField txtTC;
    // End of variables declaration//GEN-END:variables
}
