/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.easyservice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class inFrmServices extends javax.swing.JInternalFrame {

    TableRowSorter<TableModel> sorter;
    DefaultTableModel d;
    int CustomerNo=0;
    int sRow;
    boolean saveType = true;
    int searchCol;
    String ProductNo = "";
    String fillSQL;
    
    
    public inFrmServices() {
        initComponents();
        
        fillSQL = "SELECT S.Code, S.Name, S.Phone, S.Mobile, S.EMail, S.Province, S.County "
                    + "FROM tblServices S";
        
        d = (DefaultTableModel) tblServices.getModel();
        sorter = new TableRowSorter<>(d);
        tblServices.setRowSorter(sorter);
        InterfaceClass.fillTable(d, fillSQL, tblServices);
        rbProductNo.setSelected(true);
        
        tblServices.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                sRow = tblServices.getSelectedRow();
                fillTextFields(sRow);
            }
        });
        InterfaceClass.selectARow(tblServices, sRow);
        fillTextFields(sRow);
        InterfaceClass.setChildEnabled(pnlTextFields, false);
    }
    
    private void fillTextFields(int selectedRow)
    {
        if(tblServices.getRowCount()>0)
	{
            txtServiceCode.setText( String.valueOf( tblServices.getValueAt(selectedRow, 0)) );
            txtName.setText( String.valueOf( tblServices.getValueAt(selectedRow, 1)) );
            txtPhone.setText( String.valueOf( tblServices.getValueAt(selectedRow, 2)) );
            txtMobile.setText( String.valueOf( tblServices.getValueAt(selectedRow, 3)) );
            txtEMail.setText( String.valueOf( tblServices.getValueAt(selectedRow, 4)) );
            txtProvince.setText( String.valueOf( tblServices.getValueAt(selectedRow, 5)) );
            txtCounty.setText( String.valueOf( tblServices.getValueAt(selectedRow, 6)) );
        }
    }

    private void editService()
    {
        
    }   
    
    private void addService()
    {
        int code;
        code = Integer.parseInt(txtServiceCode.getText());
        String name = txtName.getText(); 
        String province = txtProvince.getText();
        String county = txtCounty.getText();
        String phone = txtPhone.getText();
        String mobile = txtMobile.getText();
        String email = txtEMail.getText();
        Connection con = null;
        PreparedStatement pStm = null;
        ResultSet rs = null;
        DatabaseClass dc = new DatabaseClass();
        con = dc.ConnectDb();
        
        if(name.equals("") ||  code <= 0)
        {
            JOptionPane.showMessageDialog(null, "Tüm zorunlu alanları doldurun!", "Zorunlu Alanlar", JOptionPane.ERROR_MESSAGE);
        }
        else
        {
            String sql = "INSERT INTO tblServices(Name, Code, Province, County, Phone, Mobile, Email)"
                + "VALUES(?, ?, ?, ?, ?, ?, ?)";
        
        rs = dc.createResult(con, "SELECT COUNT(*) AS count FROM tblServices WHERE Code = " + code + "");
        int count = 0;
        try {
            while(rs.next())
                count = rs.getInt("count");
            rs.close();
            if(count == 0)
            {
                
                pStm = con.prepareStatement(sql);
                pStm.setString(1, name);
                pStm.setInt(2, code);
                pStm.setString(3, province);
                pStm.setString(4, county);
                pStm.setString(5, phone);
                pStm.setString(6, mobile);
                pStm.setString(7, email);
                if(!dc.exeQuery(pStm))
                {
                    JOptionPane.showMessageDialog(null, "Sorgu İşletilemedi!", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
                else
                {
                    JOptionPane.showMessageDialog(null, code + " nolu servis kaydı eklendi!", "İşlem Başarılı!", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            else
                JOptionPane.showMessageDialog(null, code + " nolu kayıt zaten girilmiş!");
        } catch (SQLException ex) {
            Logger.getLogger(inFrmKBE.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
        
        try {
            pStm.close();
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(inFrmMaintenance.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        InterfaceClass.fillTable(d, fillSQL, tblServices);  //JTable güncelle
        sRow = tblServices.getRowCount() -1;
        tblServices.setEnabled(true);
        try {
                    finalize();
                } catch (Throwable ex) {
                    Logger.getLogger(inFrmMaintenance.class.getName()).log(Level.SEVERE, null, ex);
                }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    
    private void deleteService()
    {
        int response = JOptionPane.showConfirmDialog(null, tblServices.getValueAt(sRow, 0) + " kodlu servis kaydını silmek istediğinize emin misiniz?", "Are you Sure?", JOptionPane.YES_NO_OPTION);
	if(response == JOptionPane.YES_OPTION)
	{
            DatabaseClass dc = new DatabaseClass();
            Connection con = dc.ConnectDb();
            String sql = "DELETE FROM tblServices WHERE Code = ?";
            PreparedStatement pStm = null;
            try {
                pStm = con.prepareStatement(sql);
                pStm.setInt(1, (int)tblServices.getValueAt(sRow, 0));
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
            InterfaceClass.fillTable(d, fillSQL, tblServices);
            sRow = tblServices.getRowCount()-1;
            if(tblServices.getRowCount() > 0)
            {
                InterfaceClass.selectARow(tblServices, sRow);
                fillTextFields(sRow);
            } 
        }
    }
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlSearch = new javax.swing.JPanel();
        lblSearch = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        rbProductNo = new javax.swing.JRadioButton();
        rbCustomerName = new javax.swing.JRadioButton();
        pnlTable = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblServices = new javax.swing.JTable(new DefaultTableModel(){  //Tablo cellEditable özelliği 'false'
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
        lblServiceCode = new javax.swing.JLabel();
        txtServiceCode = new javax.swing.JTextField();
        lblName = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        lblPhone = new javax.swing.JLabel();
        txtPhone = new javax.swing.JTextField();
        lblMobile = new javax.swing.JLabel();
        txtMobile = new javax.swing.JTextField();
        lblProvince = new javax.swing.JLabel();
        txtProvince = new javax.swing.JTextField();
        lblCounty = new javax.swing.JLabel();
        txtCounty = new javax.swing.JTextField();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        lblEMail = new javax.swing.JLabel();
        txtEMail = new javax.swing.JTextField();

        setClosable(true);
        setResizable(true);

        pnlSearch.setBackground(java.awt.SystemColor.activeCaption);
        pnlSearch.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Ara", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial Black", 0, 12), new java.awt.Color(199, 92, 92))); // NOI18N

        lblSearch.setFont(new java.awt.Font("Arial Black", 1, 14)); // NOI18N
        lblSearch.setForeground(new java.awt.Color(199, 92, 92));
        lblSearch.setText("jLabel1");

        txtSearch.setColumns(20);
        txtSearch.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtSearch.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(199, 92, 92)));
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchKeyReleased(evt);
            }
        });

        rbProductNo.setFont(new java.awt.Font("Arial Black", 1, 14)); // NOI18N
        rbProductNo.setForeground(new java.awt.Color(199, 92, 92));
        rbProductNo.setText("Şase No");
        rbProductNo.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                rbProductNoStateChanged(evt);
            }
        });

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

        pnlTable.setBackground(java.awt.SystemColor.inactiveCaption);
        pnlTable.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        tblServices.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblServices.setUpdateSelectionOnSort(false);
        jScrollPane1.setViewportView(tblServices);

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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE)
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
                .addContainerGap()
                .addGroup(pnlNavigatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlNavigatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnFirst, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnPrevious, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnNext, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnLast, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlTextFields.setBackground(java.awt.SystemColor.activeCaption);
        pnlTextFields.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblServiceCode.setFont(new java.awt.Font("Arial Black", 1, 14)); // NOI18N
        lblServiceCode.setForeground(new java.awt.Color(199, 92, 92));
        lblServiceCode.setText("Servis Kodu :");

        txtServiceCode.setColumns(7);
        txtServiceCode.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtServiceCode.setText("jTextField1");
        txtServiceCode.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(199, 92, 92)));

        lblName.setFont(new java.awt.Font("Arial Black", 1, 14)); // NOI18N
        lblName.setForeground(new java.awt.Color(199, 92, 92));
        lblName.setText("Servis Adı :");

        txtName.setColumns(15);
        txtName.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtName.setText("jTextField2");
        txtName.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(199, 92, 92)));

        lblPhone.setFont(new java.awt.Font("Arial Black", 1, 14)); // NOI18N
        lblPhone.setForeground(new java.awt.Color(199, 92, 92));
        lblPhone.setText("Telefon :");

        txtPhone.setColumns(9);
        txtPhone.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtPhone.setText("jTextField1");
        txtPhone.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(199, 92, 92)));

        lblMobile.setFont(new java.awt.Font("Arial Black", 1, 14)); // NOI18N
        lblMobile.setForeground(new java.awt.Color(199, 92, 92));
        lblMobile.setText("Cep :");

        txtMobile.setColumns(9);
        txtMobile.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtMobile.setText("jTextField2");
        txtMobile.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(199, 92, 92)));

        lblProvince.setFont(new java.awt.Font("Arial Black", 1, 14)); // NOI18N
        lblProvince.setForeground(new java.awt.Color(199, 92, 92));
        lblProvince.setText("İl :");

        txtProvince.setColumns(10);
        txtProvince.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtProvince.setText("jTextField1");
        txtProvince.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(199, 92, 92)));

        lblCounty.setFont(new java.awt.Font("Arial Black", 1, 14)); // NOI18N
        lblCounty.setForeground(new java.awt.Color(199, 92, 92));
        lblCounty.setText("İlçe :");

        txtCounty.setColumns(10);
        txtCounty.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtCounty.setText("jTextField1");
        txtCounty.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(199, 92, 92)));

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

        lblEMail.setFont(new java.awt.Font("Arial Black", 1, 14)); // NOI18N
        lblEMail.setForeground(new java.awt.Color(199, 92, 92));
        lblEMail.setText("E-Posta :");

        txtEMail.setColumns(9);
        txtEMail.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtEMail.setText("jTextField1");
        txtEMail.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(199, 92, 92)));

        javax.swing.GroupLayout pnlTextFieldsLayout = new javax.swing.GroupLayout(pnlTextFields);
        pnlTextFields.setLayout(pnlTextFieldsLayout);
        pnlTextFieldsLayout.setHorizontalGroup(
            pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTextFieldsLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblServiceCode)
                    .addComponent(lblName))
                .addGap(18, 18, 18)
                .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtServiceCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(53, 53, 53)
                .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblMobile)
                    .addComponent(lblPhone)
                    .addComponent(lblEMail))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtEMail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMobile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(43, 43, 43)
                .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblProvince)
                    .addComponent(lblCounty)
                    .addComponent(btnSave))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCounty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtProvince, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlTextFieldsLayout.setVerticalGroup(
            pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTextFieldsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblServiceCode)
                    .addComponent(txtServiceCode, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPhone, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtProvince, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblProvince))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblName)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblMobile)
                    .addComponent(txtMobile, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCounty)
                    .addComponent(txtCounty, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblEMail)
                    .addComponent(txtEMail, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSave)
                    .addComponent(btnCancel))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlSearch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlNavigator, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlTextFields, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlTable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(pnlTable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(pnlNavigator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(pnlTextFields, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
        InterfaceClass.selectARow(tblServices, sRow);
        fillTextFields(sRow);
    }//GEN-LAST:event_btnFirstActionPerformed

    private void btnPreviousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousActionPerformed
        if(tblServices.getSelectedRow() != 0)
        {
            sRow = tblServices.getSelectedRow()-1;
            InterfaceClass.selectARow(tblServices, sRow);
            fillTextFields(sRow);
        }
    }//GEN-LAST:event_btnPreviousActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        // TODO add your handling code here:
        saveType = true;
        InterfaceClass.setChildEnabled(pnlTextFields, true);
        txtServiceCode.setEnabled(false);
        txtName.setEnabled(false);
        tblServices.setEnabled(false);
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        // TODO add your handling code here:
        InterfaceClass.clearText(pnlTextFields);
        saveType = false;
        InterfaceClass.setChildEnabled(pnlTextFields, true);
        tblServices.setEnabled(false);
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:
        deleteService();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
        // TODO add your handling code here:
        if(tblServices.getSelectedRow() != tblServices.getRowCount()-1)
        {
            sRow = tblServices.getSelectedRow()+1;
            InterfaceClass.selectARow(tblServices, sRow);
            fillTextFields(sRow);
        }
    }//GEN-LAST:event_btnNextActionPerformed

    private void btnLastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLastActionPerformed
        // TODO add your handling code here:
        sRow = tblServices.getRowCount()-1;
        InterfaceClass.selectARow(tblServices, sRow);
        fillTextFields(sRow);
    }//GEN-LAST:event_btnLastActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        if(saveType)
        editService();
        else
        addService();
        tblServices.setEnabled(true);
        InterfaceClass.setChildEnabled(pnlTextFields, false);
        InterfaceClass.selectARow(tblServices, sRow);
        fillTextFields(sRow);
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        // TODO add your handling code here:
        InterfaceClass.setChildEnabled(pnlTextFields, false);
        if(tblServices.getRowCount() > 0)
        tblServices.setEnabled(true);
        InterfaceClass.selectARow(tblServices, sRow);
        fillTextFields(sRow);
    }//GEN-LAST:event_btnCancelActionPerformed


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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblCounty;
    private javax.swing.JLabel lblEMail;
    private javax.swing.JLabel lblMobile;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblPhone;
    private javax.swing.JLabel lblProvince;
    private javax.swing.JLabel lblSearch;
    private javax.swing.JLabel lblServiceCode;
    private javax.swing.JPanel pnlNavigator;
    private javax.swing.JPanel pnlSearch;
    private javax.swing.JPanel pnlTable;
    private javax.swing.JPanel pnlTextFields;
    private javax.swing.JRadioButton rbCustomerName;
    private javax.swing.JRadioButton rbProductNo;
    private javax.swing.JTable tblServices;
    private javax.swing.JTextField txtCounty;
    private javax.swing.JTextField txtEMail;
    private javax.swing.JTextField txtMobile;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtPhone;
    private javax.swing.JTextField txtProvince;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtServiceCode;
    // End of variables declaration//GEN-END:variables
}
