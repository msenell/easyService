/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.easyservice;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import java.awt.image.BufferedImage;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.text.MaskFormatter;

/**
 *
 * @author Mustafa
 */
public class FrmMain extends javax.swing.JFrame {

    /**
     * Creates new form MainFrame
     */
    public static FrmMain frm;
    public static String dateMask = "##.##.####";
    public static String dateFormat = "dd.MM.yyyy";
    public static DateFormat dF;
    public static MaskFormatter mF;
    public static MaskFormatter _mF;
    public static Color captionColor;
    private BufferedImage desktopPaneBackground;
    public FrmMain() {
            initComponents();
            captionColor = new Color(199, 92, 92);
            jButton1.setVisible(false);
            btnExportToSQLite.setVisible(false);
            Errors.setErrors();
            try {
            desktopPaneBackground = ImageIO.read(getClass().getResource( "/com/org/easyservice/Images/sapBackgroundImage.jpeg" ));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
            dF = new SimpleDateFormat(dateFormat);
        try {
            mF = new MaskFormatter(dateMask);
            mF.setPlaceholderCharacter('_');
            _mF = new MaskFormatter(dateMask);
            _mF.setPlaceholderCharacter('_');
        } catch (ParseException ex) {
            Logger.getLogger(FrmMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }

    public double getScaleFactor(int iMasterSize, int iTargetSize) {

    double dScale = 1;
    if (iMasterSize > iTargetSize) {

        dScale = (double) iTargetSize / (double) iMasterSize;

    } else {

        dScale = (double) iTargetSize / (double) iMasterSize;

    }

    return dScale;

}

public double getScaleFactorToFit(Dimension original, Dimension toFit) {

    double dScale = 1d;

    if (original != null && toFit != null) {

        double dScaleWidth = getScaleFactor(original.width, toFit.width);
        double dScaleHeight = getScaleFactor(original.height, toFit.height);

        dScale = Math.min(dScaleHeight, dScaleWidth);

    }

    return dScale;

}
    
    private void createTables()
    {
                /*DatabaseClass dc = new DatabaseClass();
        Connection con = dc.ConnectDb2();
        Statement st = null;
        try {
            st = con.createStatement();
        } catch (SQLException ex) {
            Logger.getLogger(FrmMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            
            st.execute("CREATE TABLE tblServices"
                    + "(ServiceNo INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY(START WITH 1, INCREMENT BY 1),"
                    + "Name VARCHAR(50) NOT NULL,"
                    + "Code INTEGER UNIQUE,"
                    + "Province VARCHAR(50),"
                    + "County VARCHAR(50),"
                    + "Phone VARCHAR(25),"
                    + "Mobile VARCHAR(25),"
                    + "EMail VARCHAR(50),"
                    + "PRIMARY KEY(ServiceNo) )");
            st.execute("CREATE TABLE tblClaim"
                    + "(ClaimNo INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY(START WITH 1, INCREMENT BY 1), "
                    + "SAPNo INTEGER UNIQUE, "
                    + "ProductNo VARCHAR(20) NOT NULL, "
                    + "Hours INTEGER NOT NULL, "
                    + "Date DATE NOT NULL, "
                    + "FaultCode VARCHAR(20) NOT NULL, "
                    + "Notes VARCHAR(50), "
                    + "PRIMARY KEY(ClaimNo))");
            
            st.execute("CREATE TABLE tblCustomers"
                    + "(CustomerNo INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY(START WITH 1, INCREMENT BY 1),"
                    + "TC VARCHAR(11), "
                    + "Name VARCHAR(100), "
                    + "Phone VARCHAR(25), "
                    + "Mobile VARCHAR(25), "
                    + "EMail VARCHAR(50), "
                    + "Address VARCHAR(200), "
                    + "Notes  VARCHAR(200), "
                    + "PRIMARY KEY(CustomerNo))");
            st.execute("CREATE TABLE tblKBE"
                    + "(KBENo INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY(START WITH 1, INCREMENT BY 1), "
                    + "CustomerNo INTEGER NOT NULL, "
                    + "ProductNo VARCHAR(20) NOT NULL, "
                    + "Hours INTEGER NOT NULL, "
                    + "Date DATE NOT NULL, "
                    + "LandSize INTEGER NOT NULL, "
                    + "Harvest VARCHAR(50) NOT NULL, "
                    + "Livestock VARCHAR(50) NOT NULL, "
                    + "Dealer VARCHAR(50), "
                    + "PRIMARY KEY(KBENo))");
            
            st.execute("CREATE TABLE tblMaintenance"
                    + "(MaintenanceNo INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY(START WITH 1, INCREMENT BY 1), "
                    + "ProductNo VARCHAR(20), "
                    + "MaintenanceType VARCHAR(10), "
                    + "Hours INTEGER, "
                    + "Date DATE, "
                    + "ServiceKit VARCHAR(20), "
                    + "Service VARCHAR(50), "
                    + "SapNo INTEGER, "
                    + "PRIMARY KEY(MaintenanceNo))");
            st.execute("CREATE TABLE tblProducts"
                    + "(ProductNo VARCHAR(20) NOT NULL, "
                    + "EngineNo INTEGER NOT NULL UNIQUE, "
                    + "CustomerNo INTEGER NOT NULL, "
                    + "Model VARCHAR(50), "
                    + "LicencePlate VARCHAR(20), "
                    + "WStartDate DATE, "
                    + "WEndDate DATE, "
                    + "PRIMARY KEY(ProductNo))");
        } catch (SQLException ex) {
            Logger.getLogger(FrmMain.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlGoToFrames = new javax.swing.JPanel();
        btnCustomers = new javax.swing.JButton();
        btnProducts = new javax.swing.JButton();
        btnKBE = new javax.swing.JButton();
        btnMaintenance = new javax.swing.JButton();
        btnServices = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        btnExportToSQLite = new javax.swing.JButton();
        dskPaneMain = new javax.swing.JDesktopPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(0, 0, 0));
        setPreferredSize(new java.awt.Dimension(1024, 668));

        pnlGoToFrames.setBackground(java.awt.SystemColor.activeCaption);

        btnCustomers.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/org/easyservice/Images/user-male-icon.png"))); // NOI18N
        btnCustomers.setToolTipText("MÜŞTERİLER");
        btnCustomers.setBorder(null);
        btnCustomers.setContentAreaFilled(false);
        btnCustomers.setMaximumSize(new java.awt.Dimension(129, 129));
        btnCustomers.setMinimumSize(new java.awt.Dimension(129, 129));
        btnCustomers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCustomersActionPerformed(evt);
            }
        });

        btnProducts.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/org/easyservice/Images/tractor-icon.png"))); // NOI18N
        btnProducts.setToolTipText("ÜRÜNLER");
        btnProducts.setBorder(null);
        btnProducts.setContentAreaFilled(false);
        btnProducts.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProductsActionPerformed(evt);
            }
        });

        btnKBE.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/org/easyservice/Images/forms-icon.png"))); // NOI18N
        btnKBE.setToolTipText("KBE");
        btnKBE.setBorder(null);
        btnKBE.setContentAreaFilled(false);
        btnKBE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKBEActionPerformed(evt);
            }
        });

        btnMaintenance.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/org/easyservice/Images/Maintenance-icon.png"))); // NOI18N
        btnMaintenance.setToolTipText("BAKIMLAR");
        btnMaintenance.setBorder(null);
        btnMaintenance.setContentAreaFilled(false);
        btnMaintenance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMaintenanceActionPerformed(evt);
            }
        });

        btnServices.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/org/easyservice/Images/9-128.png"))); // NOI18N
        btnServices.setBorder(null);
        btnServices.setContentAreaFilled(false);
        btnServices.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnServicesActionPerformed(evt);
            }
        });

        jButton1.setText("CreateSQLiteDb");
        jButton1.setEnabled(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        btnExportToSQLite.setText("Export To SQLite");
        btnExportToSQLite.setEnabled(false);
        btnExportToSQLite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportToSQLiteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlGoToFramesLayout = new javax.swing.GroupLayout(pnlGoToFrames);
        pnlGoToFrames.setLayout(pnlGoToFramesLayout);
        pnlGoToFramesLayout.setHorizontalGroup(
            pnlGoToFramesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGoToFramesLayout.createSequentialGroup()
                .addGroup(pnlGoToFramesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlGoToFramesLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pnlGoToFramesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(btnCustomers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnProducts)
                            .addComponent(btnKBE)))
                    .addGroup(pnlGoToFramesLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(btnExportToSQLite))
                    .addGroup(pnlGoToFramesLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jButton1))
                    .addGroup(pnlGoToFramesLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(btnServices))
                    .addGroup(pnlGoToFramesLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(btnMaintenance)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlGoToFramesLayout.setVerticalGroup(
            pnlGoToFramesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGoToFramesLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(btnCustomers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnProducts)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnKBE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnMaintenance)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnServices)
                .addGap(34, 34, 34)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnExportToSQLite)
                .addContainerGap(214, Short.MAX_VALUE))
        );

        dskPaneMain.setBackground(new java.awt.Color(255, 255, 204));
        dskPaneMain.setPreferredSize(new java.awt.Dimension(800, 0));

        javax.swing.GroupLayout dskPaneMainLayout = new javax.swing.GroupLayout(dskPaneMain);
        dskPaneMain.setLayout(dskPaneMainLayout);
        dskPaneMainLayout.setHorizontalGroup(
            dskPaneMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1184, Short.MAX_VALUE)
        );
        dskPaneMainLayout.setVerticalGroup(
            dskPaneMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlGoToFrames, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(dskPaneMain, javax.swing.GroupLayout.DEFAULT_SIZE, 1184, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlGoToFrames, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(dskPaneMain, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 888, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCustomersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCustomersActionPerformed
        inFrmCustomers ifCustomers = new inFrmCustomers(); //Müşteri işlem arayüzü
        dskPaneMain.add(ifCustomers);
        ifCustomers.show();
    }//GEN-LAST:event_btnCustomersActionPerformed

    private void btnProductsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProductsActionPerformed
        inFrmProducts ifProducts = new inFrmProducts(0); //Ürün işlem arayüzü
        dskPaneMain.add(ifProducts);
        ifProducts.show();
    }//GEN-LAST:event_btnProductsActionPerformed

    private void btnKBEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKBEActionPerformed
        // TODO add your handling code here:
        inFrmKBE ifKBE = new inFrmKBE(0);
        dskPaneMain.add(ifKBE);
        ifKBE.show();
    }//GEN-LAST:event_btnKBEActionPerformed

    private void btnMaintenanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMaintenanceActionPerformed
        // TODO add your handling code here:
        inFrmMaintenance ifM = new inFrmMaintenance("");
        dskPaneMain.add(ifM);
        ifM.show();
    }//GEN-LAST:event_btnMaintenanceActionPerformed

    private void btnServicesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnServicesActionPerformed
        // TODO add your handling code here:
        inFrmServices ifM = new inFrmServices();
        dskPaneMain.add(ifM);
        ifM.show();
    }//GEN-LAST:event_btnServicesActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        //createSQLiteTable();
        JOptionPane.showMessageDialog(rootPane, "OK!");
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnExportToSQLiteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportToSQLiteActionPerformed
        // TODO add your handling code here:
        //createCopy();
    }//GEN-LAST:event_btnExportToSQLiteActionPerformed

    /*private void createCopy()
    {
        DatabaseClass dc = new DatabaseClass();
        Connection con = dc.ConnectDb();
        Connection con2 = dc.ConnectDb2();
        
        Statement st;
        Statement st2;
        ResultSet res;
        ResultSet res12;
        ResultSet rs2;
        try {
            st = con.createStatement();
            st2 = con2.createStatement();
            PreparedStatement pSTM = null;
            //res = st.executeQuery("SELECT CustomerNo, ProductNo, Hours, toFormatDate(Date) AS Date, LandSize, Harvest, LiveStock, Dealer FROM tblKBE");
            
                /*int SAPNo;
                Connection con12 = dc.ConnectDb2();
                PreparedStatement pst = null;
                con12 = dc.ConnectDb2();
                res.next();
                String ProductNo = res.getString("ProductNo");
                pst = con12.prepareStatement("SELECT SAPNo FROM tblMaintenance WHERE ProductNo = ? AND MaintenanceType = ?");
                pst.setString(1, String.valueOf(ProductNo));
                pst.setString(2, "KBE");
                ResultSet rs = pst.executeQuery();
                rs.next();
                SAPNo = rs.getInt(1);
                rs.close();
                pst.close();
                con12.close();
            System.out.println(SAPNo);*/
            /*while(res.next())
            {
                
                
                //  *Müşteriler Tablosu Kopyalama Başlangıcı*
                /*String TC = res.getString("TC");
                String Name = res.getString("Name");
                String Phone = res.getString("Phone");
                String Mobile = res.getString("Mobile");
                String EMail = res.getString("EMail");
                String Address = res.getString("Address");
                String Notes = res.getString("CustomerNo"); //Derby CustomerNo
                
                String insertSQL = "INSERT INTO tblCustomers(TC, Name, Phone, Mobile, EMail, Address, Notes) "
                + "VALUES(?, ?, ?, ?, ?, ?, ?)";
                pSTM = con2.prepareStatement(insertSQL);
                pSTM.setString(1, TC);
                pSTM.setString(2, Name);
                pSTM.setString(3, Phone);
                pSTM.setString(4, Mobile);
                pSTM.setString(5, EMail);
                pSTM.setString(6, Address);
                pSTM.setString(7, Notes);*/
                //  *Müşteriler Tablosu Kopyalama Sonu*
                
                //  *Ürünler Tablosu Kopyalama Başlangıcı*
                /*String ProductNo = res.getString("ProductNo");
                int EngineNo = res.getInt("EngineNo");
                int CustomerNo = res.getInt("CustomerNo");
                String Model = res.getString("Model");
                String LicencePlate = res.getString("LicencePlate");
                String WStartDate = res.getString("WStartDate");
                String WEndDate = res.getString("WEndDate");
                Connection con12 = dc.ConnectDb2();
                PreparedStatement pst = null;
                pst = con12.prepareStatement("SELECT CustomerNo FROM tblCustomers WHERE Notes = ?");
                pst.setString(1, String.valueOf(CustomerNo));
                ResultSet rs = pst.executeQuery();
                rs.next();
                CustomerNo = rs.getInt(1);
                rs.close();
                pst.close();
                con12.close();
                String insertSQL = "INSERT INTO tblProducts(ProductNo, EngineNo, CustomerNo, Model, LicencePlate, WStartDate, WEndDate) "
                + "VALUES(?, ?, ?, ?, ?, ?, ?)";
                pSTM = con2.prepareStatement(insertSQL);
                pSTM.setString(1, ProductNo);
                pSTM.setInt(2, EngineNo);
                pSTM.setInt(3, CustomerNo);
                pSTM.setString(4, Model);
                pSTM.setString(5, LicencePlate);
                pSTM.setString(6, WStartDate);
                pSTM.setString(7, WEndDate);*/
                
                //   *KBE Tablosu Kopyalama Başlangıcı*
                /*int CustomerNo = res.getInt("CustomerNo");
                String ProductNo = res.getString("ProductNo");
                int Hours = res.getInt("Hours");
                String Date = res.getString("Date");
                int LandSize = res.getInt("LandSize");
                String Harvest = res.getString("Harvest");
                String Livestock = res.getString("Livestock");
                String Dealer = res.getString("Dealer");
                
                Connection con12 = dc.ConnectDb2();
                PreparedStatement pst = null;
                pst = con12.prepareStatement("SELECT CustomerNo FROM tblCustomers WHERE Notes = ?");
                pst.setString(1, String.valueOf(CustomerNo));
                ResultSet rs = pst.executeQuery();
                rs.next();
                CustomerNo = rs.getInt(1);
                rs.close();
                pst.close();
                con12.close();
                
                int SAPNo;
                con12 = dc.ConnectDb2();
                PreparedStatement pst2 = null;
                pst2 = con12.prepareStatement("SELECT SAPNo FROM tblMaintenance WHERE ProductNo = ? AND MaintenanceType = ?");
                pst2.setString(1, String.valueOf(ProductNo));
                pst2.setString(2, "KBE");
                rs2 = pst2.executeQuery();
                rs2.next();
                System.out.println(ProductNo);
                SAPNo = rs2.getInt(1);
                rs2.close();
                pst2.close();
                con12.close();
                String insertSQL = "INSERT INTO tblKBE(SAPNo, CustomerNo, ProductNo, Hours, Date, LandSize, Harvest, Livestock, Dealer)"
                + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
                pSTM = con2.prepareStatement(insertSQL);
                pSTM.setInt(1, SAPNo);
                pSTM.setInt(2, CustomerNo);
                pSTM.setString(3, ProductNo);
                pSTM.setInt(4, Hours);
                pSTM.setString(5, Date);
                pSTM.setInt(6, LandSize);
                pSTM.setString(7, Harvest);
                pSTM.setString(8, Livestock);
                pSTM.setString(9, Dealer);*/
                //   *KBE Tablosu Kopyalama Sonu*
                   
                //  *Servisler Tablosu Kopyalama Başlangıcı*
                /*String Name = res.getString("Name");
                int Code = res.getInt("Code");
                String Province = res.getString("Province");
                String County = res.getString("County");
                String Phone = res.getString("Phone");
                String Mobile = res.getString("Mobile");
                String EMail = res.getString("EMail");
                String insertSQL = "INSERT INTO tblServices(Code, Name, Province, County, Phone, Mobile, Email)"
                + "VALUES(?, ?, ?, ?, ?, ?, ?)";
                pSTM = con2.prepareStatement(insertSQL);
                pSTM.setInt(1, Code);
                pSTM.setString(2, Name);
                pSTM.setString(3, Province);
                pSTM.setString(4, County);
                pSTM.setString(5, Phone);
                pSTM.setString(6, Mobile);
                pSTM.setString(7, EMail);*/
                //  *Servisler Tablosu Kopyalama Sonu*
                
                //  *Bakımlar Tablosu Kopyalama Başlangıcı*
                /*String ProductNo = res.getString("ProductNo");
                String MaintenanceType = res.getString("MaintenanceType");
                int Hours = res.getInt("Hours");
                String Date = res.getString("Date");
                String ServiceKit = res.getString("ServiceKit");
                String Service = res.getString("Service");
                int SapNo = res.getInt("SapNo");
                
                String insertSQL = "INSERT INTO tblMaintenance(SAPNo, ProductNo, MaintenanceType, Hours, Date, ServiceKit, Service)"
                + "VALUES(?, ?, ?, ?, ?, ?, ?)";
                
                pSTM = con2.prepareStatement(insertSQL);
                pSTM.setInt(1, SapNo);
                pSTM.setString(2, ProductNo);
                pSTM.setString(3, MaintenanceType);
                pSTM.setInt(4, Hours);
                pSTM.setString(5, Date);
                pSTM.setString(6, ServiceKit);
                pSTM.setString(7, Service);*/
                //   *Bakımlar Tablosu Kopyalama Sonu*
                
                /*if(!dc.exeQuery(pSTM))
		{
			JOptionPane.showMessageDialog(null, "Sorgu İşletilemedi!", "Database Error", JOptionPane.ERROR_MESSAGE);
		}*//*
            }
            
            System.out.println("OK!");
            st.close();
            st2.close();
            //pSTM.close();
            con.close();
            con2.close();
            
            
        } catch (SQLException ex) {
            Logger.getLogger(FrmMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }*/
    
    /*private void createSQLiteTable()
    {
        DatabaseClass dc = new DatabaseClass();
        Connection con = dc.ConnectDb2();
        Statement st = null;
        try {
            st = con.createStatement();
        } catch (SQLException ex) {
            Logger.getLogger(FrmMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            
            st.execute("CREATE TABLE tblServices"
                    + "(Code INTEGER PRIMARY KEY NOT NULL,"
                    + "Name VARCHAR NOT NULL,"
                    + "Province VARCHAR,"
                    + "County VARCHAR,"
                    + "Phone VARCHAR,"
                    + "Mobile VARCHAR,"
                    + "EMail VARCHAR)");
            st.execute("CREATE TABLE tblClaim"
                    + "(SAPNo INTEGER PRIMARY KEY NOT NULL, "
                    + "ProductNo VARCHAR NOT NULL, "
                    + "Hours INTEGER NOT NULL, "
                    + "Date DATE NOT NULL, "
                    + "FaultCode VARCHAR NOT NULL, "
                    + "Notes VARCHAR)");
            
            st.execute("CREATE TABLE tblCustomers"
                    + "(CustomerNo INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                    + "TC VARCHAR, "
                    + "Name VARCHAR, "
                    + "Phone VARCHAR, "
                    + "Mobile VARCHAR, "
                    + "EMail VARCHAR, "
                    + "Address VARCHAR, "
                    + "Notes  VARCHAR )");
            st.execute("CREATE TABLE tblKBE"
                    + "(SAPNo INTEGER PRIMARY KEY NOT NULL, "
                    + "CustomerNo INTEGER NOT NULL, "
                    + "ProductNo VARCHAR NOT NULL, "
                    + "Hours INTEGER NOT NULL, "
                    + "Date DATE NOT NULL, "
                    + "LandSize INTEGER NOT NULL, "
                    + "Harvest VARCHAR NOT NULL, "
                    + "Livestock VARCHAR NOT NULL, "
                    + "Dealer VARCHAR )");
            
            st.execute("CREATE TABLE tblMaintenance"
                    + "(SAPNo INTEGER PRIMARY KEY NOT NULL, "   //SapNo -> SAPNo
                    + "ProductNo VARCHAR, "
                    + "MaintenanceType VARCHAR, "
                    + "Hours INTEGER, "
                    + "Date DATE, "
                    + "ServiceKit VARCHAR, "
                    + "Service VARCHAR)");
            st.execute("CREATE TABLE tblProducts"
                    + "(ProductNo VARCHAR PRIMARY KEY NOT NULL, "
                    + "EngineNo INTEGER NOT NULL UNIQUE, "
                    + "CustomerNo INTEGER NOT NULL, "
                    + "Model VARCHAR, "
                    + "LicencePlate VARCHAR, "
                    + "WStartDate DATE, "
                    + "WEndDate DATE)");
        } catch (SQLException ex) {
            Logger.getLogger(FrmMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }*/
    /*private void createFunctions()
    {
        String sql = "CREATE FUNCTION toFormatDate(DT DATE) RETURNS VARCHAR(10) " +
                     "PARAMETER STYLE JAVA NO SQL LANGUAGE JAVA " +
                     "EXTERNAL NAME 'com.org.easyservice.DerbyFunctions.toFormatDate'";
        DatabaseClass dc = new DatabaseClass();
        Connection con = dc.ConnectDb2();
        Statement st = null;
        try {
            st = con.createStatement();
        } catch (SQLException ex) {
            Logger.getLogger(FrmMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            st.execute(sql);
        } catch (SQLException ex) {
            Logger.getLogger(FrmMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            st.close();
        } catch (SQLException ex) {
            Logger.getLogger(FrmMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(FrmMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }*/
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FrmMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrmMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrmMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
               /* try {
                    
                    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                } catch (UnsupportedLookAndFeelException ex) {
                    Logger.getLogger(FrmMain.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(FrmMain.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InstantiationException ex) {
                    Logger.getLogger(FrmMain.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(FrmMain.class.getName()).log(Level.SEVERE, null, ex);
                }*/
                /*Toolkit tk = Toolkit.getDefaultToolkit();
                int h = (int)tk.getScreenSize().getHeight();
                int w = (int)tk.getScreenSize().getWidth();*/
                frm = new FrmMain();
                frm.setExtendedState(JFrame.MAXIMIZED_BOTH);
                //frm.setSize(w, h);
                //frm.setUndecorated(true);
                frm.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCustomers;
    private javax.swing.JButton btnExportToSQLite;
    private javax.swing.JButton btnKBE;
    private javax.swing.JButton btnMaintenance;
    private javax.swing.JButton btnProducts;
    private javax.swing.JButton btnServices;
    public javax.swing.JDesktopPane dskPaneMain;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel pnlGoToFrames;
    // End of variables declaration//GEN-END:variables
}
