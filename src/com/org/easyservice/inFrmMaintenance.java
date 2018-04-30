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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author Mustafa
 */
public class inFrmMaintenance extends javax.swing.JInternalFrame {

    /**
     * Creates new form inFrmKBE
     */
    
    private final int toMaintenanceNo = 0;
    private final int toProductNo = 1;
    private final int toName = 2;
    private final int toMaintenanceType = 3;
    private final int toHours = 4;
    private final int toDate = 5;
    private final int toServiceKit = 6;
    private final int toService = 7;
    private final int toSAPNo = 8;
    
    TableRowSorter<TableModel> sorter;
    DefaultTableModel d;
    int CustomerNo=0;
    int sRow;
    boolean saveType = true;
    int searchCol;
    String ProductNo = "";
    String fillSQL;
    //boolean processOK = true;
    private DefaultComboBoxModel cbModelServiceTitle; //Servisleri listeleyecek combobox için model nesnesi.
    
    private String productNo;
    private String mainType;
    private int hours;
    private int sapNo;
    private String date;
    private String serviceKit;
    private String service;
    
    public inFrmMaintenance(String prNo) {
        initComponents();
        ProductNo = "" + prNo;
        //Tüm bakımlar için SELECT sorgusu:
        if(ProductNo.equals(""))
        {
            fillSQL = "SELECT M.MaintenanceNo, P.ProductNo, C.Name, M.MaintenanceType, M.Hours, M.Date, M.ServiceKit, M.Service, M.SapNo "
                    + "FROM tblMaintenance M, tblProducts P, tblCustomers C "
                    + "WHERE M.ProductNo = P.ProductNo AND P.CustomerNo = C.CustomerNo";
            btnAdd.setEnabled(false);
        }
        //Bir ürüne ait bakımlar için SELECT sorgusu:
        else
        {
            fillSQL = "SELECT M.MaintenanceNo, P.ProductNo, C.Name, M.MaintenanceType, M.Hours, M.Date, M.ServiceKit, M.Service, M.SapNo "
                    + "FROM tblMaintenance M, tblProducts P, tblCustomers C "
                    + "WHERE M.ProductNo = '" + ProductNo + "' AND M.ProductNo = P.ProductNo AND P.CustomerNo = C.CustomerNo";
        }
        cbModelServiceTitle = new DefaultComboBoxModel();
        cbServiceBox.setModel(cbModelServiceTitle); //Servis combobox model ataması.
        //Tablo üzerinde arama yapmak için gerekli sorter yapılandırması:
        d = (DefaultTableModel) tblMaintenance.getModel();
        sorter = new TableRowSorter<>(d);
        tblMaintenance.setRowSorter(sorter);
        InterfaceClass.fillTable(d, fillSQL, tblMaintenance);
        rbProductNo.setSelected(true);
        //Seçili satır değiştikçe satır numarasını alan ve arayüzü güncelleyen event:
        tblMaintenance.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            
            @Override
            public void valueChanged(ListSelectionEvent e) {
                sRow = tblMaintenance.getSelectedRow();
                fillTextFields(sRow);
            }
        });
        InterfaceClass.selectARow(tblMaintenance, sRow); //Bir satır seç.
        fillServiceBox(); //Servis combobox'unu doldur.
        fillTextFields(sRow); //TextField'ları doldur.
        InterfaceClass.setChildEnabled(pnlTextFields, false); //TextField'ları disable yap.
    }
    
    private void fillServiceBox()
    {
        DatabaseClass dc = new DatabaseClass();
        Connection con = dc.ConnectDb();
        ResultSet rs = dc.createResult(con, "SELECT S.Name || '(' || S.Code || ')' AS title FROM tblServices S");
        cbModelServiceTitle.removeAllElements();
        try {
            while(rs.next())
            {
                cbModelServiceTitle.addElement(rs.getString("title"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(inFrmMaintenance.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //TextField'ları dolduran metod.
    private void fillTextFields(int selectedRow)
    {
        if(tblMaintenance.getRowCount()>0)//Tabloda kayıt var mı?
	{
            txtProductNo.setText( String.valueOf( tblMaintenance.getValueAt(selectedRow, toProductNo)) );
            txtName.setText( String.valueOf( tblMaintenance.getValueAt(selectedRow, toName)) );
            txtMaintenance.setText( String.valueOf( tblMaintenance.getValueAt(selectedRow, toMaintenanceType)) );
            txtHours.setText( String.valueOf( tblMaintenance.getValueAt(selectedRow, toHours)) );
            dpDate.setDate( FunctionClass.getDateFromString(String.valueOf( tblMaintenance.getValueAt(selectedRow, toDate))) );
            txtServiceKit.setText( String.valueOf( tblMaintenance.getValueAt(selectedRow, toServiceKit)) );
            String service = tblMaintenance.getValueAt(selectedRow, toService).toString();
            int index = cbModelServiceTitle.getIndexOf(service); //Seçili satırdaki servis bilgisi combobox'ta var mı?
            if(index == -1) //Yoksa, ekle
            {
                cbModelServiceTitle.addElement(service);
                cbModelServiceTitle.setSelectedItem(service);
            }
            else //Varsa, o servisi seçili hale getir.
                cbModelServiceTitle.setSelectedItem(service);
            txtSapNo.setText( String.valueOf( tblMaintenance.getValueAt(selectedRow, toSAPNo)) );
        }
    }
   
    private void getValuesFromComponents()
    {
        productNo = txtProductNo.getText();
        sapNo = 0;
        mainType = txtMaintenance.getText();
        hours = 0;
        date = FunctionClass.getDateString(dpDate.getDate());
        serviceKit = txtServiceKit.getText();
        service = cbServiceBox.getSelectedItem().toString();
    }
    
    private boolean verifyValues(boolean isAdd)
    {
        boolean _result = true;
        int[] _checkData = {-1,-1,-1,-1};
        
        if(FunctionClass.checkValue(productNo)) //Şasi numarası uygun ise:
        {
            InterfaceClass.setComponentTextColor(lblProductNo, FrmMain.captionColor);
        }
        else  //Şasi numarası uygun değil ise:
        {
            _checkData[0] = 2;
            InterfaceClass.setComponentTextColor(lblProductNo, Color.red);
        }
        
        try //Çalışma saati uygun ise(tamsayı ise):
        {
            hours = Integer.parseInt( txtHours.getText());
            InterfaceClass.setComponentTextColor(lblHours, FrmMain.captionColor);
        }catch(Exception e) //Çalışma saati hatalı ise:
        {
            hours = 0;
            _checkData[1] = 4;
            InterfaceClass.setComponentTextColor(lblHours, Color.red);
        }
        if(!FunctionClass.checkDate(date, "dd.MM.yyyy")) //Tarih hatalı ise:
        {
            _checkData[2] = 5;
            InterfaceClass.setComponentTextColor(lblDate, Color.red);
        }
        else //Tarih uygun ise:
        {
            InterfaceClass.setComponentTextColor(lblDate, FrmMain.captionColor);
        }
        try { //SapNo uygun ise:
            sapNo = Integer.parseInt(txtSapNo.getText());
            InterfaceClass.setComponentTextColor(lblSapNo, FrmMain.captionColor);
        } catch (Exception e) { //SapNo hatalı ise:
            sapNo = 0;
            _checkData[3] = 0;
            InterfaceClass.setComponentTextColor(lblSapNo, Color.red);
        }
        String errors = "";
        //Hata mesajlarını gösteren ve hata sonucunu hesaplayan döngü:
        for(int i = 0; i<_checkData.length; i++)
        {
            if(_checkData[i]!=-1)
            {
                _result = _result && false; //Tek hata bile varsa son değer false olacak.
                errors =errors + Errors.ErrorList[_checkData[i]] + "\n";
            }
        }
        if(!_result) //Girilen bilgiler hatalı ise:
            JOptionPane.showMessageDialog(rootPane, errors);
        else
        {
            _result = checkSAPNo(sapNo, isAdd);
            if(!_result)
                JOptionPane.showMessageDialog(rootPane, Errors.ErrorList[1]);
        }
        return _result;
    }
    
    private boolean checkSAPNo(int _sapNo, boolean isAdd)
    {
        /*Ekleme ise:
            1- SapNo veritabanında mevcut değilse : true.
            2- SapNo veritabanında mevcut ise     : false.
          Düzenleme ise:
            1-SapNo mevcut değil ise                             : true.
            2-SapNo veritabanında mevcut ise:
                2.1-Başka bir kaydın SapNo'su ile çakışma varsa     : false.
                2.2-Mevcut SapNo kendisi ise                        : true.
        */
        int countMaintenance=0;
        boolean _result = false;
        try {
            DatabaseClass dc = new DatabaseClass();
            Connection con = dc.ConnectDb();
            ResultSet rs = dc.createResult(con, "SELECT COUNT(*) AS count FROM tblMaintenance WHERE SapNo = " + _sapNo);
            while(rs.next())
                countMaintenance = rs.getInt("count");
        } catch (SQLException ex) {
            Logger.getLogger(inFrmMaintenance.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(isAdd) //Ekleme için kontrol:
        {
            if(countMaintenance == 0) //#1
            {
                _result = true;
            }
            else //#2
            {
                _result = false;
            }
        }
        else //Düzenleme için kontrol:
        {
            if(countMaintenance == 0) //#1
            {
                _result = true;
            }
            else //#2
            {
                int exSapNo = Integer.parseInt( tblMaintenance.getValueAt(sRow, toSAPNo).toString() );
                int exMaintenanceNo = getMaintenanceNo(exSapNo);
                int newMaintenanceNo = getMaintenanceNo(_sapNo);
                if(exMaintenanceNo == newMaintenanceNo)//#2.1
                {
                    _result = true;
                }
                else //#2.2
                {
                    _result = false;
                }
            }
        }
        return _result;
    }
    
    private int getMaintenanceNo(int sapNo)
    {
        int maintenanceNo=0;
        DatabaseClass dc = new DatabaseClass();
        Connection con = dc.ConnectDb();
        ResultSet rs = dc.createResult(con, "SELECT MaintenanceNo AS no FROM tblMaintenance WHERE SapNo = " + sapNo);
        try {
            while(rs.next())
                maintenanceNo = rs.getInt("no");
        } catch (SQLException ex) {
            Logger.getLogger(inFrmMaintenance.class.getName()).log(Level.SEVERE, null, ex);
        }
        return maintenanceNo;
    }
    
    private boolean addMaintenance()
    {
        //Componentlerden verilerin çekilmesi:
        getValuesFromComponents();
        
        boolean _result = false;
        Connection con = null;
        PreparedStatement pStm = null;
        ResultSet rs = null;
        DatabaseClass dc = new DatabaseClass();
        con = dc.ConnectDb();
         
        if(verifyValues(true)) //Çekilen değerlerde hata yoksa :
        {
            String sql = "INSERT INTO tblMaintenance(ProductNo, MaintenanceType, Hours, Date, ServiceKit, Service, SapNo)"
                + "VALUES(?, ?, ?, ?, ?, ?, ?)";
            try 
            {
                pStm = con.prepareStatement(sql);
                pStm.setString(1, productNo);
                pStm.setString(2, mainType);
                pStm.setInt(3, hours);
                pStm.setString(4, date);
                pStm.setString(5, serviceKit);
                pStm.setString(6, service);
                pStm.setInt(7, sapNo);
                if(!dc.exeQuery(pStm))
                {
                    _result = false;
                    JOptionPane.showMessageDialog(null, "Sorgu İşletilemedi!", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
                else
                {
                    _result = true;
                    JOptionPane.showMessageDialog(null, sapNo + " SAP nolu bakım kaydı eklendi!", "İşlem Başarılı!", JOptionPane.INFORMATION_MESSAGE);
                    InterfaceClass.fillTable(d, fillSQL, tblMaintenance);  //JTable güncelle
                    sRow = tblMaintenance.getRowCount() -1;
                    tblMaintenance.setEnabled(true);
                }   
            } catch (SQLException ex) {
                Logger.getLogger(inFrmKBE.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else
            _result = false;
          
        try {
            if(pStm != null)
                pStm.close();
            if(!con.isClosed())
                con.close();
        } catch (SQLException ex) {
            Logger.getLogger(inFrmMaintenance.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return _result;
    }
    
    private boolean editMaintenance()
    {
        //Componentlerden verilerin çekilmesi.
        getValuesFromComponents();
        
        boolean _result = false;
        String sql = "UPDATE tblMaintenance SET "
                + "MaintenanceType = ?,"
                + "Hours = ?, "
                + "Date = ?, "
                + "ServiceKit = ?, "
                + "Service = ?, "
                + "SapNo = ? "
                + "WHERE SapNo = ?";
        DatabaseClass dc = new DatabaseClass();
        Connection con = dc.ConnectDb();
        
        PreparedStatement pStm = null;
        
        if(verifyValues(false)) //Çekilen değerlerde hata yoksa:
        {
            int _sapNo = Integer.parseInt( tblMaintenance.getValueAt(sRow, toSAPNo).toString() );
            try {
                pStm = con.prepareStatement(sql);
                pStm.setString(1, mainType);
                pStm.setInt(2, hours);
                pStm.setString(3, date);
                pStm.setString(4, serviceKit);
                pStm.setString(5, service);
                pStm.setInt(6, sapNo);
                pStm.setInt(7, _sapNo);
                if(!dc.exeQuery(pStm))
                {
                    _result = false;
                    JOptionPane.showMessageDialog(null, "Sorgu İşletilemedi!", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
                else
                {
                    _result = true;
                    JOptionPane.showMessageDialog(null, sapNo + " SAP nolu Bakım Kaydı güncellendi!", "İşlem Başarılı!", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (SQLException ex) {
                Logger.getLogger(inFrmMaintenance.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else
            _result = false;
        
        if(pStm != null)
            try {
                pStm.close();
        } catch (SQLException ex) {
            Logger.getLogger(inFrmMaintenance.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(con != null)
            try {
                con.close();
        } catch (SQLException ex) {
            Logger.getLogger(inFrmMaintenance.class.getName()).log(Level.SEVERE, null, ex);
        }
        InterfaceClass.fillTable(d, fillSQL, tblMaintenance);  //JTable güncelle
        sRow = tblMaintenance.getRowCount() -1;
        tblMaintenance.setEnabled(true);
        
        return _result;
    }
    
    private void deleteKBE()
    {
        int response = JOptionPane.showConfirmDialog(null, tblMaintenance.getValueAt(sRow, toMaintenanceNo) + " nolu bakım kaydını silmek istediğinize emin misiniz?", "Are you Sure?", JOptionPane.YES_NO_OPTION);
	if(response == JOptionPane.YES_OPTION)
	{
            DatabaseClass dc = new DatabaseClass();
            Connection con = dc.ConnectDb();
            String sql = "DELETE FROM tblMaintenance WHERE MaintenanceNo = ?";
            PreparedStatement pStm = null;
            try {
                pStm = con.prepareStatement(sql);
                pStm.setInt(1, (int)tblMaintenance.getValueAt(sRow, toMaintenanceNo));
		} catch (SQLException e) {
                    e.printStackTrace();
                }
            if(!dc.exeQuery(pStm))
            {
                JOptionPane.showMessageDialog(null, "Sorgu İşletilemedi!", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
            else
                JOptionPane.showMessageDialog(null, "Kayıt Silindi!", "Mission Completed", JOptionPane.INFORMATION_MESSAGE);
            try {
                pStm.close();
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(inFrmCustomers.class.getName()).log(Level.SEVERE, null, ex);
            }
            InterfaceClass.fillTable(d, fillSQL, tblMaintenance);
            sRow = tblMaintenance.getRowCount()-1;
            if(tblMaintenance.getRowCount() > 0)
            {
                InterfaceClass.selectARow(tblMaintenance, sRow);
                fillTextFields(sRow);
            } 
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        pnlSearch = new javax.swing.JPanel();
        lblSearch = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        rbProductNo = new javax.swing.JRadioButton();
        rbCustomerName = new javax.swing.JRadioButton();
        pnlTable = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblMaintenance = new javax.swing.JTable(new DefaultTableModel(){  //Tablo cellEditable özelliği 'false'
            public boolean isCellEditable(int row, int column) //olacak şekilde oluşturuluyor.
            {
                return false;
            }
        });
        pnlNavigator = new javax.swing.JPanel();
        btnFirst = new javax.swing.JButton();
        btnPrevious = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnAdd = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnNext = new javax.swing.JButton();
        btnLast = new javax.swing.JButton();
        pnlTextFields = new javax.swing.JPanel();
        lblProductNo = new javax.swing.JLabel();
        txtProductNo = new javax.swing.JTextField();
        lblName = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        lblMaintenance = new javax.swing.JLabel();
        txtMaintenance = new javax.swing.JTextField();
        lblHours = new javax.swing.JLabel();
        txtHours = new javax.swing.JTextField();
        lblDate = new javax.swing.JLabel();
        lblService = new javax.swing.JLabel();
        lblSapNo = new javax.swing.JLabel();
        txtSapNo = new javax.swing.JTextField();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        lblServiceKit = new javax.swing.JLabel();
        txtServiceKit = new javax.swing.JTextField();
        cbServiceBox = new javax.swing.JComboBox();
        dpDate = new org.jdesktop.swingx.JXDatePicker();

        setClosable(true);
        setResizable(true);

        pnlSearch.setBackground(java.awt.SystemColor.activeCaption);
        pnlSearch.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Ara", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(199, 92, 92))); // NOI18N

        lblSearch.setFont(new java.awt.Font("Arial Black", 1, 14)); // NOI18N
        lblSearch.setForeground(new java.awt.Color(199, 92, 92));
        lblSearch.setText("jLabel1");

        txtSearch.setColumns(20);
        txtSearch.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtSearch.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(199, 92, 92)));
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchKeyReleased(evt);
            }
        });

        buttonGroup1.add(rbProductNo);
        rbProductNo.setFont(new java.awt.Font("Arial Black", 1, 14)); // NOI18N
        rbProductNo.setForeground(new java.awt.Color(199, 92, 92));
        rbProductNo.setText("Şase No");
        rbProductNo.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                rbProductNoStateChanged(evt);
            }
        });

        buttonGroup1.add(rbCustomerName);
        rbCustomerName.setFont(new java.awt.Font("Arial Black", 1, 14)); // NOI18N
        rbCustomerName.setForeground(new java.awt.Color(199, 92, 92));
        rbCustomerName.setText("Müşteri Adı");

        javax.swing.GroupLayout pnlSearchLayout = new javax.swing.GroupLayout(pnlSearch);
        pnlSearch.setLayout(pnlSearchLayout);
        pnlSearchLayout.setHorizontalGroup(
            pnlSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSearchLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(lblSearch)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addComponent(rbProductNo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rbCustomerName)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlSearchLayout.setVerticalGroup(
            pnlSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSearchLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSearch)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rbProductNo)
                    .addComponent(rbCustomerName))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlTable.setBackground(java.awt.SystemColor.activeCaption);
        pnlTable.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        tblMaintenance.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblMaintenance.setUpdateSelectionOnSort(false);
        jScrollPane1.setViewportView(tblMaintenance);

        javax.swing.GroupLayout pnlTableLayout = new javax.swing.GroupLayout(pnlTable);
        pnlTable.setLayout(pnlTableLayout);
        pnlTableLayout.setHorizontalGroup(
            pnlTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
        );
        pnlTableLayout.setVerticalGroup(
            pnlTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTableLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
                .addContainerGap())
        );

        pnlNavigator.setBackground(java.awt.SystemColor.inactiveCaption);
        pnlNavigator.setBorder(javax.swing.BorderFactory.createEtchedBorder());

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
                .addContainerGap(15, Short.MAX_VALUE)
                .addGroup(pnlNavigatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlNavigatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnFirst, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnPrevious, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnNext, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnLast, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        pnlTextFields.setBackground(java.awt.SystemColor.activeCaption);
        pnlTextFields.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblProductNo.setFont(new java.awt.Font("Arial Black", 1, 14)); // NOI18N
        lblProductNo.setForeground(new java.awt.Color(199, 92, 92));
        lblProductNo.setText("Şase No :");

        txtProductNo.setColumns(7);
        txtProductNo.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtProductNo.setText("jTextField1");
        txtProductNo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(199, 92, 92)));

        lblName.setFont(new java.awt.Font("Arial Black", 1, 14)); // NOI18N
        lblName.setForeground(new java.awt.Color(199, 92, 92));
        lblName.setText("Adı Soyadı :");

        txtName.setColumns(15);
        txtName.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtName.setText("jTextField2");
        txtName.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(199, 92, 92)));

        lblMaintenance.setFont(new java.awt.Font("Arial Black", 1, 14)); // NOI18N
        lblMaintenance.setForeground(new java.awt.Color(199, 92, 92));
        lblMaintenance.setText("Bakım Tipi :");

        txtMaintenance.setColumns(9);
        txtMaintenance.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtMaintenance.setText("jTextField1");
        txtMaintenance.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(199, 92, 92)));

        lblHours.setFont(new java.awt.Font("Arial Black", 1, 14)); // NOI18N
        lblHours.setForeground(new java.awt.Color(199, 92, 92));
        lblHours.setText("Çalışma Saati :");

        txtHours.setColumns(9);
        txtHours.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtHours.setText("jTextField1");
        txtHours.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(199, 92, 92)));

        lblDate.setFont(new java.awt.Font("Arial Black", 1, 14)); // NOI18N
        lblDate.setForeground(new java.awt.Color(199, 92, 92));
        lblDate.setText("Tarih :");

        lblService.setFont(new java.awt.Font("Arial Black", 1, 14)); // NOI18N
        lblService.setForeground(new java.awt.Color(199, 92, 92));
        lblService.setText("Servis :");

        lblSapNo.setFont(new java.awt.Font("Arial Black", 1, 14)); // NOI18N
        lblSapNo.setForeground(new java.awt.Color(199, 92, 92));
        lblSapNo.setText("SAP No :");

        txtSapNo.setColumns(10);
        txtSapNo.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtSapNo.setText("jTextField1");
        txtSapNo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(199, 92, 92)));

        btnSave.setFont(new java.awt.Font("Arial Black", 0, 14)); // NOI18N
        btnSave.setForeground(new java.awt.Color(199, 92, 92));
        btnSave.setText("Kaydet");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnCancel.setFont(new java.awt.Font("Arial Black", 0, 14)); // NOI18N
        btnCancel.setForeground(new java.awt.Color(199, 92, 92));
        btnCancel.setText("Vazgeç");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        lblServiceKit.setFont(new java.awt.Font("Arial Black", 1, 14)); // NOI18N
        lblServiceKit.setForeground(new java.awt.Color(199, 92, 92));
        lblServiceKit.setText("Servis Kiti :");

        txtServiceKit.setColumns(9);
        txtServiceKit.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtServiceKit.setText("jTextField1");
        txtServiceKit.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(199, 92, 92)));

        cbServiceBox.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(199, 92, 92)));

        dpDate.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(199, 92, 92)));

        javax.swing.GroupLayout pnlTextFieldsLayout = new javax.swing.GroupLayout(pnlTextFields);
        pnlTextFields.setLayout(pnlTextFieldsLayout);
        pnlTextFieldsLayout.setHorizontalGroup(
            pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTextFieldsLayout.createSequentialGroup()
                .addContainerGap(47, Short.MAX_VALUE)
                .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblName)
                    .addComponent(lblProductNo)
                    .addComponent(lblMaintenance))
                .addGap(18, 18, 18)
                .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMaintenance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtProductNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblDate)
                    .addComponent(lblHours)
                    .addComponent(lblServiceKit))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(dpDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtHours)
                    .addComponent(txtServiceKit))
                .addGap(18, 18, 18)
                .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblService)
                    .addComponent(lblSapNo)
                    .addComponent(btnSave))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtSapNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancel)
                    .addComponent(cbServiceBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(47, Short.MAX_VALUE))
        );
        pnlTextFieldsLayout.setVerticalGroup(
            pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTextFieldsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblProductNo)
                    .addComponent(txtProductNo, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblHours, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                    .addComponent(txtHours, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblService)
                    .addComponent(cbServiceBox, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblName)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDate)
                    .addComponent(lblSapNo)
                    .addComponent(txtSapNo, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dpDate, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblMaintenance, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                    .addComponent(txtMaintenance, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblServiceKit)
                    .addComponent(txtServiceKit, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSave)
                    .addComponent(btnCancel))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlSearch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlTable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlNavigator, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlTextFields, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(pnlTable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(1, 1, 1)
                .addComponent(pnlNavigator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(pnlTextFields, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void rbProductNoStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_rbProductNoStateChanged
        // TODO add your handling code here
        if(rbProductNo.isSelected())
        {
            lblSearch.setText("Şasi No :");
            searchCol = 2;
        }
        else
        {
            lblSearch.setText("Müşteri Adı :");
            searchCol = 1;
        }
    }//GEN-LAST:event_rbProductNoStateChanged

    private void btnFirstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFirstActionPerformed
        sRow = 0;
        InterfaceClass.selectARow(tblMaintenance, sRow);
        fillTextFields(sRow);
    }//GEN-LAST:event_btnFirstActionPerformed

    private void btnPreviousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousActionPerformed
        if(tblMaintenance.getSelectedRow() != 0)
        {
            sRow = tblMaintenance.getSelectedRow()-1;
            InterfaceClass.selectARow(tblMaintenance, sRow);
            fillTextFields(sRow);
        }
    }//GEN-LAST:event_btnPreviousActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        // TODO add your handling code here:
        saveType = true;
        InterfaceClass.setChildEnabled(pnlTextFields, true);
        txtProductNo.setEnabled(false);
        txtName.setEnabled(false);
        tblMaintenance.setEnabled(false);
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        // TODO add your handling code here:
        InterfaceClass.clearText(pnlTextFields);
        saveType = false;
        InterfaceClass.setChildEnabled(pnlTextFields, true);
        txtProductNo.setText(""+ProductNo);
        txtProductNo.setEnabled(false);
        DatabaseClass dc = new DatabaseClass();
        Connection con = dc.ConnectDb();
        ResultSet rs = dc.createResult(con, "SELECT C.Name AS name FROM tblCustomers C, tblProducts P WHERE C.CustomerNo = P.CustomerNo AND P.ProductNo = '" + ProductNo + "'");
        try {
            while(rs.next())
                txtName.setText(rs.getString("name"));
        } catch (SQLException ex) {
            Logger.getLogger(inFrmKBE.class.getName()).log(Level.SEVERE, null, ex);
        }
        txtName.setEnabled(false);
        tblMaintenance.setEnabled(false);
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:
        deleteKBE();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
        // TODO add your handling code here:
        if(tblMaintenance.getSelectedRow() != tblMaintenance.getRowCount()-1)
        {
            sRow = tblMaintenance.getSelectedRow()+1;
            InterfaceClass.selectARow(tblMaintenance, sRow);
            fillTextFields(sRow);
        }
    }//GEN-LAST:event_btnNextActionPerformed

    private void btnLastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLastActionPerformed
        // TODO add your handling code here:
        sRow = tblMaintenance.getRowCount()-1;
        InterfaceClass.selectARow(tblMaintenance, sRow);
        fillTextFields(sRow);
    }//GEN-LAST:event_btnLastActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        boolean _result = false;
        
        if(saveType)
            _result = editMaintenance();
        else
            _result = addMaintenance();
        if(_result)
        {
            tblMaintenance.setEnabled(true);
            InterfaceClass.setChildEnabled(pnlTextFields, false);
            InterfaceClass.selectARow(tblMaintenance, sRow);
            fillTextFields(sRow);
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        // TODO add your handling code here:
        InterfaceClass.setChildEnabled(pnlTextFields, false);
        if(tblMaintenance.getRowCount() > 0)
        tblMaintenance.setEnabled(true);
        InterfaceClass.selectARow(tblMaintenance, sRow);
        fillTextFields(sRow);
        InterfaceClass.setChildColor(pnlTextFields, FrmMain.captionColor);
    }//GEN-LAST:event_btnCancelActionPerformed

    private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyReleased
        // TODO add your handling code here:
        String text = txtSearch.getText();
        if(text.length() == 0)
        {
            sorter.setRowFilter(null);
        }
        else if(searchCol == 2)
        {
            sorter.setRowFilter(javax.swing.RowFilter.regexFilter("(?iu)^" + text, searchCol));
        }
        else if(searchCol == 1)
        {
            sorter.setRowFilter(javax.swing.RowFilter.regexFilter("(?iu)" + text, searchCol)); //(?iu: unicode non-case sensitive
        }
    }//GEN-LAST:event_txtSearchKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnFirst;
    private javax.swing.JButton btnLast;
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnPrevious;
    private javax.swing.JButton btnSave;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox cbServiceBox;
    private org.jdesktop.swingx.JXDatePicker dpDate;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblDate;
    private javax.swing.JLabel lblHours;
    private javax.swing.JLabel lblMaintenance;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblProductNo;
    private javax.swing.JLabel lblSapNo;
    private javax.swing.JLabel lblSearch;
    private javax.swing.JLabel lblService;
    private javax.swing.JLabel lblServiceKit;
    private javax.swing.JPanel pnlNavigator;
    private javax.swing.JPanel pnlSearch;
    private javax.swing.JPanel pnlTable;
    private javax.swing.JPanel pnlTextFields;
    private javax.swing.JRadioButton rbCustomerName;
    private javax.swing.JRadioButton rbProductNo;
    private javax.swing.JTable tblMaintenance;
    private javax.swing.JTextField txtHours;
    private javax.swing.JTextField txtMaintenance;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtProductNo;
    private javax.swing.JTextField txtSapNo;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtServiceKit;
    // End of variables declaration//GEN-END:variables
}
