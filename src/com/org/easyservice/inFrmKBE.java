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
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class inFrmKBE extends javax.swing.JInternalFrame {

    TableRowSorter<TableModel> sorter;
    DefaultTableModel d;
    int CustomerNo = 0; //JFrame'e gelinen müşteri numarası.
    int sRow; //JTable üzerinde seçili olan satır numarası.
    boolean saveType = true;
    int searchCol;
    String fillSQL;
    private final DefaultComboBoxModel cbModelProductNo; //Müşteriye ait ürün şasilerini ekleyeceğimiz JCOmboBox modeli.
    private final DefaultComboBoxModel cbModelDealerTitle; //Bayileri listeleyecek JComboBox modeli.
    
    //Ekleme ve düzenleme değişkenleri:
    private int SAPNo = 0;
    private String productNo = "";
    private int hours = 0;
    private String date = "";
    private String landSize = "";
    private String harvest = "";
    private String liveStock = "";
    private String dealer = "";

    public inFrmKBE(int cNo) {
        initComponents();
        CustomerNo = cNo;
        //Hangi frame'den gelindiğine karar veren ve uygun sonucu görüntüleyen yapı.
        if (CustomerNo == 0) //Tüm KBE kayıtlarını getirir.
        {
            fillSQL = "SELECT K.SAPNo, K.CustomerNo, C.Name, K.ProductNo, K.Hours, K.Date, K.LandSize, K.Harvest, K.Livestock, K.Dealer"
                    + " FROM tblKBE K, tblCustomers C";
            fillSQL = fillSQL + " WHERE K.CustomerNo = C.CustomerNo";
            btnAdd.setEnabled(false);
        } else //Sadece istenilen müşteriye ait KBE kayıtlarını getirir.
        {
            fillSQL = "SELECT K.SAPNo, K.CustomerNo, C.Name, K.ProductNo, K.Hours, K.Date, K.LandSize, K.Harvest, K.Livestock, K.Dealer"
                    + " FROM tblKBE K, tblCustomers C";
            fillSQL = fillSQL + " WHERE K.CustomerNo = " + CustomerNo;
            fillSQL = fillSQL + " AND C.CustomerNo = K.CustomerNo";
            btnAdd.setEnabled(true);
        }
        cbModelProductNo = new DefaultComboBoxModel();  //Ürün seçme, ekleme ve arama işlemleri bu modeller üzerinden yapılır.
        cbModelDealerTitle = new DefaultComboBoxModel(); 
        cbProductNo.setModel(cbModelProductNo); //Ürün seçme JCombobox model ataması.
        cbDealer.setModel(cbModelDealerTitle);
        d = (DefaultTableModel) tblKBE.getModel();
        sorter = new TableRowSorter<>(d);
        tblKBE.setRowSorter(sorter);
        InterfaceClass.fillTable(d, fillSQL, tblKBE); //Verilen parametrelere göre tabloyu dolduran method.
        rbProductNo.setSelected(true);
        //Seçilen satırı yakalayıp TextField'ları dolduran listener.
        tblKBE.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            fillTextFields(tblKBE.getSelectedRow());
        });
        fillDealerComboBox();
        fillProductNoComboBox(cNo);
        InterfaceClass.selectARow(tblKBE, sRow);  //Bir satır seç.
        InterfaceClass.setChildEnabled(pnlTextFields, false);
    }

    private void fillTextFields(int selectedRow) //TextField'ları dolduran metod.
    {
        if (tblKBE.getRowCount() > 0) //Tabloda hiç kayıt var mı?
        {
            txtSAPNo.setText(String.valueOf(tblKBE.getValueAt(selectedRow, 0)));
            txtCustomerNo.setText(String.valueOf(tblKBE.getValueAt(selectedRow, 1)));
            fillProductNoComboBox(Integer.parseInt(String.valueOf(tblKBE.getValueAt(selectedRow, 1)))); //Seçilen müşterinin ürünlerini çeker.
            txtName.setText(String.valueOf(tblKBE.getValueAt(selectedRow, 2)));
            
            //Tablodaki kayıt ComboBox içinde yoksa kullanıcıyı uyar.
            if (cbModelProductNo.getIndexOf(String.valueOf(tblKBE.getValueAt(selectedRow, 3))) == -1) //Tablodan gelen bayi combo box'ta yoksa Hata göster.
            {
                JOptionPane.showMessageDialog(rootPane, Errors.getErrorText(2));
                cbModelProductNo.addElement("HATALI!");
                cbModelProductNo.setSelectedItem("HATALI!");
            }
            else //Varsa onu seç.
            {
                cbModelProductNo.setSelectedItem( String.valueOf(tblKBE.getValueAt(selectedRow, 3)) );
            }
            if( cbModelDealerTitle.getIndexOf(String.valueOf(tblKBE.getValueAt(selectedRow, 9))) == -1) //Tablodan gelen bayi combo box'ta yoksa Hata göster.
            {
                JOptionPane.showMessageDialog(rootPane, Errors.getErrorText(9));
                cbModelDealerTitle.addElement("HATALI!");
                cbModelDealerTitle.setSelectedItem("HATALI!");
            }
            else //Varsa onu seç.
            {
                cbModelDealerTitle.setSelectedItem( String.valueOf(tblKBE.getValueAt(selectedRow, 9)) );
            }
            txtHours.setText(String.valueOf(tblKBE.getValueAt(selectedRow, 4)));
            txtDateChooser.setDate(FunctionClass.getDateFromString(String.valueOf(tblKBE.getValueAt(selectedRow, 5))));
            txtLandSize.setText(String.valueOf(tblKBE.getValueAt(selectedRow, 6)));
            txtHarvest.setText(String.valueOf(tblKBE.getValueAt(selectedRow, 7)));
            txtLivestock.setText(String.valueOf(tblKBE.getValueAt(selectedRow, 8)));
           
        }
    }

    private void fillProductNoComboBox(int cNo) {
        DatabaseClass dc = new DatabaseClass();
        Connection con = dc.ConnectDb();
        //Parametre olarak gelen müşteriye ait ürün şasilerini çeken sorgu.
        ResultSet rs = dc.createResult(con, "SELECT P.ProductNo AS productNo FROM tblProducts P WHERE P.CustomerNo = '" + cNo + "'");
        cbModelProductNo.removeAllElements();
        try {
            while (rs.next()) {
                cbModelProductNo.addElement(rs.getString("productNo"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(inFrmMaintenance.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void fillDealerComboBox()
    {
        DatabaseClass dc = new DatabaseClass();
        Connection con = dc.ConnectDb();
        ResultSet rs = dc.createResult(con, "SELECT D.Name || '(' || D.Code || ')' AS title FROM tblDealers AS D");
        cbModelDealerTitle.removeAllElements();
        try {
            while (rs.next()) {
                cbModelDealerTitle.addElement(rs.getString("title"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(inFrmMaintenance.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private boolean hasProduct()
    {
        DatabaseClass dc = new DatabaseClass();
        Connection con = dc.ConnectDb();
        String sql = "SELECT COUNT(*) as count FROM tblProducts WHERE CustomerNo = '" + CustomerNo + "'";
        ResultSet rs = dc.createResult(con, sql);
        int count = -1;
        try {
            rs.next();
        } catch (SQLException ex) {
            Logger.getLogger(inFrmKBE.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            count = rs.getInt("count");
        } catch (SQLException ex) {
            Logger.getLogger(inFrmKBE.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(count <= 0)
            return false;
        else
            return true;
    }
    
    private void getValuesFromComponents()
    {
        SAPNo = 0;
        productNo = cbProductNo.getSelectedItem().toString().toUpperCase();
        hours = 0;
        date = FunctionClass.getDateString(txtDateChooser.getDate());
        landSize = txtLandSize.getText();
        harvest = txtHarvest.getText();
        liveStock = txtLivestock.getText();
        dealer = cbModelDealerTitle.getSelectedItem().toString();
    }
    
    private boolean verifyValues() //Veri Teyit Kontrolleri ve hata mesajları atamaları:
    {
        boolean _result = true;
        int[] _checkData = {-1,-1,-1,-1,-1,-1,-1};
        try { //SAPNo integer dönüşümü:
            SAPNo = Integer.parseInt(txtSAPNo.getText());
            InterfaceClass.setComponentTextColor(lblSAPNo, FrmMain.captionColor);
        } catch (NumberFormatException e) {//Dönüşüm hatalı ise:
            _checkData[0] = 0;
            InterfaceClass.setComponentTextColor(lblSAPNo, Color.red);
        }
        if(productNo.equals("") || productNo.equals("HATALI!")) //productNo hatalı ise:
        {
            _checkData[1] = 2;
            InterfaceClass.setComponentTextColor(lblProductNo, Color.red);
        }
        else //productNo uygun ise:
        {
            InterfaceClass.setComponentTextColor(lblProductNo, FrmMain.captionColor);
        }
        if(dealer.equals("") || dealer.equals("HATALI!"))  //Bayi hatalı ise:
        {
            _checkData[6] = 8;
            InterfaceClass.setComponentTextColor(lblDealer, Color.red);
        }
        else //Bayi uygun ise:
        {
            InterfaceClass.setComponentTextColor(lblProductNo, FrmMain.captionColor);
        }
        try { //hours integer dönüşümü:
            hours = Integer.parseInt(txtSAPNo.getText());
            InterfaceClass.setComponentTextColor(lblHours, FrmMain.captionColor);
        } catch (NumberFormatException e) { //dönüşüm hatalı ise:
            _checkData[2] = 4;
            InterfaceClass.setComponentTextColor(lblHours, Color.red);
        }
        
        if(!FunctionClass.checkDate(date, "dd.MM.yyyy")) //Tarih hatalı ise:
        {
            _checkData[3] = 5;
            InterfaceClass.setComponentTextColor(lblDate, Color.red);
        }
        else //Tarih uygun ise:
        {
            InterfaceClass.setComponentTextColor(lblDate, FrmMain.captionColor);
        }
        
        if(landSize.equals("")) //arazi bilgisi boş ise:
        {
            _checkData[4] = 6;
            InterfaceClass.setComponentTextColor(lblLandSize, Color.red);
        }
        else //arazi bilgisi dolu ise:
        {
            InterfaceClass.setComponentTextColor(lblLandSize, FrmMain.captionColor);
        }
        if(harvest.equals("")) //Ürün boş ise:
        {
            _checkData[5] = 7;
            InterfaceClass.setComponentTextColor(lblHarvest, Color.red);
        }
        else //Ürün dolu ise:
        {
            InterfaceClass.setComponentTextColor(lblHarvest, FrmMain.captionColor);
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
        if(!_result)
            JOptionPane.showMessageDialog(rootPane, errors);
        return _result;
    }
    private boolean addKBE() //Yeni KBE kaydı ekleyen method.
    {
        // Componentlerden veri çekme.
        getValuesFromComponents();
        int customerNo = CustomerNo;

        //Veri Teyitleri: 
        boolean _result = verifyValues();
        
        //Yeni Kayıt Sorgusu:
        String sql = "INSERT INTO tblKBE(SAPNo, CustomerNo, ProductNo, Hours, Date, LandSize, Harvest, Livestock, Dealer)"
                + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
        DatabaseClass dc = new DatabaseClass();
        Connection con = dc.ConnectDb();
        PreparedStatement pStm = null;
        if(_result) //Hata yoksa
        {
            //Şasiye ait kaç adet KBE kaydı var?
            ResultSet rsPNo = dc.createResult(con, "SELECT COUNT(*) AS count FROM tblKBE WHERE ProductNo = '" + productNo + "'");
            //SAP Noya ait kaç adet KBE kaydı var?
            ResultSet rsSAPNo = dc.createResult(con, "SELECT COUNT(*) AS count FROM tblKBE WHERE ProductNo = '" + SAPNo + "'");
            int countPNo = 0, countSAPNo = 0;
            try 
            {
                while (rsPNo.next()) 
                {
                    countPNo = rsPNo.getInt("count");
                }
                while (rsSAPNo.next()) 
                {
                    countSAPNo = rsSAPNo.getInt("count");
                }
                if (countPNo == 0 && countSAPNo == 0) //Daha önce kayıt yoksa:
                {
                    pStm = con.prepareStatement(sql);
                    pStm.setInt(1, SAPNo);
                    pStm.setInt(2, customerNo);
                    pStm.setString(3, productNo);
                    pStm.setInt(4, hours);
                    pStm.setString(5, date);
                    pStm.setString(6, landSize);
                    pStm.setString(7, harvest);
                    pStm.setString(8, liveStock);
                    pStm.setString(9, dealer);
                    if (!dc.exeQuery(pStm)) //Sorgu işletilemedi.
                    {
                        JOptionPane.showMessageDialog(null, "Sorgu İşletilemedi!", "Database Error", JOptionPane.ERROR_MESSAGE);
                        _result = false;
                    } else //Sorgu başarılı, kayıt eklendi.
                    {
                        JOptionPane.showMessageDialog(null, productNo + " Şase nolu KBE Kaydı Eklendi!", "İşlem Başarılı!", JOptionPane.INFORMATION_MESSAGE);
                        InterfaceClass.fillTable(d, fillSQL, tblKBE);  //JTable güncelle
                        sRow = tblKBE.getRowCount() - 1;
                        tblKBE.setEnabled(true);
                        _result = true;
                    }
                    rsPNo.close();
                    rsSAPNo.close();
                
                } else if (countPNo > 0) 
                {
                    JOptionPane.showMessageDialog(null, "Bu ürüne ait KBE zaten girilmiş!");
                    _result = false;
                } else if (countSAPNo > 0) 
                {
                    JOptionPane.showMessageDialog(null, "Bu SAP Numaralı kayıt daha önce girilmiş!");
                    _result = false;
                }
        } catch (SQLException ex) {
            Logger.getLogger(inFrmKBE.class.getName()).log(Level.SEVERE, null, ex);
            _result = false;
        }
        }
        else //Girilen veriler hatalı veya eksikse
        {
            _result = false;
        }
        
        if(pStm != null)
            try {
                pStm.close();
        } catch (SQLException ex) {
            Logger.getLogger(inFrmKBE.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(con != null)
            try {
                con.close();
        } catch (SQLException ex) {
            Logger.getLogger(inFrmKBE.class.getName()).log(Level.SEVERE, null, ex);
        }
        return _result;
    }

    private boolean editKBE() //Mevcut KBE kaydı düzenleyen method.
    {           
        // Componentlerden veri çekme.
        getValuesFromComponents();
        
        String _SAPNo = String.valueOf(tblKBE.getValueAt(sRow, 0)); //Tablodan çekilen SAPNo; SAPNo değişmesi durumunda gerekli.
        String _productNo = String.valueOf(tblKBE.getValueAt(sRow, 3));

        //Veri Teyitleri
        boolean _result = verifyValues();
        
        //Güncelleme sorgusu:
        String sql = "UPDATE tblKBE SET "
                + "SAPNo = ?, "
                + "ProductNo = ?, "
                + "Hours = ?, "
                + "Date = ?, "
                + "LandSize = ?, "
                + "Harvest = ?, "
                + "Livestock = ?, "
                + "Dealer = ?"
                + " WHERE SAPNo = ?"; //Tablodan çekilen SAPNo.
        DatabaseClass dc = new DatabaseClass();
        Connection con = dc.ConnectDb();
        //Şasiye ait KBE daha önceden girilmiş mi? :
        ResultSet rs = dc.createResult(con, "SELECT COUNT(*) AS count FROM tblKBE WHERE ProductNo = '" + productNo + "'");
        int count;
        int cn;
        boolean checkSAPNO = true;
        PreparedStatement pStm;
        try {
            rs.next();
            count = rs.getInt("count"); //Şasiye ait girilmiş KBE sayısı.
            if (count == 0) { //Kayıt yoksa:
                cn = -1;
            } else { //Kayıt varsa:
                rs = dc.createResult(con, "SELECT CustomerNo AS cn FROM tblKBE WHERE ProductNo = '" + productNo + "'");
                cn = rs.getInt("cn"); //KBE'si mevcut şasi hangi müşteriye ait?
            }
            rs = dc.createResult(con, "SELECT COUNT(*) as count FROM tblProducts WHERE ProductNo = '" + productNo + "'");
            int pr = rs.getInt("count"); //Güncellenen şasi nolu kayıt Ürünler tablosunda mevcut mu?
            
            if( !String.valueOf(SAPNo).equals(_SAPNo) )
            {
                rs = dc.createResult(con, "SELECT COUNT(*) as count FROM tblKBE WHERE SAPNo = '" + SAPNo + "'");
                if(rs.getInt(count) > 0)
                    checkSAPNO = false;
            }
            boolean state = ((count == 0) || (count == 1 && (CustomerNo == cn)))&&checkSAPNO; 
            if (state && (pr != 0)) //Sorun yoksa düzenlemeyi yap:
            {
                pStm = con.prepareStatement(sql);
                pStm.setInt(1, SAPNo);
                pStm.setString(2, productNo);
                pStm.setInt(3, hours);
                pStm.setString(4, date);
                pStm.setString(5, landSize);
                pStm.setString(6, harvest);
                pStm.setString(7, liveStock);
                pStm.setString(8, dealer);
                pStm.setString(9, _SAPNo);
                if (!dc.exeQuery(pStm)) {
                    JOptionPane.showMessageDialog(null, "Sorgu İşletilemedi!", "Database Error", JOptionPane.ERROR_MESSAGE);
                    _result = false;
                } else {
                    JOptionPane.showMessageDialog(null, productNo + " Şase nolu KBE Kaydı güncellendi!", "İşlem Başarılı!", JOptionPane.INFORMATION_MESSAGE);
                    _result = true;
                }

                pStm.close();
                con.close();
            } else if (pr == 0) {
                JOptionPane.showMessageDialog(null, "Girilen şasi numarası 'Ürünler' tablosunda kayıtlı değil!");
                _result = false;
            } else {
                JOptionPane.showMessageDialog(null, "Bu ürüne ait KBE zaten girilmiş!");
                _result = false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(inFrmKBE.class.getName()).log(Level.SEVERE, null, ex);
            _result = false;
        }
        InterfaceClass.fillTable(d, fillSQL, tblKBE);  //JTable güncelle
        sRow = tblKBE.getRowCount() - 1;
        tblKBE.setEnabled(true);
        return _result;
    }

    private boolean deleteKBE() //Mevcut KBE'yi silen method:
    {
        boolean result = false;
        int response = JOptionPane.showConfirmDialog(null, tblKBE.getValueAt(sRow, 2) + " şase nolu KBE kaydını silmek istediğinize emin misiniz?", "Are you Sure?", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            DatabaseClass dc = new DatabaseClass();
            Connection con = dc.ConnectDb();
            String sql = "DELETE FROM tblKBE WHERE ProductNo = ?"; //Silme sorgusu.
            PreparedStatement pStm = null;
            try {
                pStm = con.prepareStatement(sql); //Sorguyu hazırla.
            } catch (SQLException ex) {
                Logger.getLogger(inFrmKBE.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                pStm.setString(1, (String) tblKBE.getValueAt(sRow, 3)); //Sorguya parametre ata.
            } catch (SQLException ex) {
                Logger.getLogger(inFrmKBE.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (!dc.exeQuery(pStm)) {
                JOptionPane.showMessageDialog(null, "Sorgu İşletilemedi!", "Database Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, tblKBE.getValueAt(sRow, 3) + " Şase Nolu KBE kaydı Silindi!", "Mission Completed", JOptionPane.INFORMATION_MESSAGE);
                result = true;
            }
            try {
                pStm.close();
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(inFrmCustomers.class.getName()).log(Level.SEVERE, null, ex);
                result = false;
            }
            InterfaceClass.fillTable(d, fillSQL, tblKBE);
            sRow = tblKBE.getRowCount() - 1;
            if (tblKBE.getRowCount() > 0) {
                InterfaceClass.selectARow(tblKBE, sRow);
                fillTextFields(sRow);
            }
        }
        return result;
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
        tblKBE = new javax.swing.JTable(new DefaultTableModel(){  //Tablo cellEditable özelliği 'false'
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
        lblCustomerNo = new javax.swing.JLabel();
        txtCustomerNo = new javax.swing.JTextField();
        lblName = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        lblProductNo = new javax.swing.JLabel();
        lblHours = new javax.swing.JLabel();
        txtHours = new javax.swing.JTextField();
        lblDate = new javax.swing.JLabel();
        lblHarvest = new javax.swing.JLabel();
        txtHarvest = new javax.swing.JTextField();
        lblLivestock = new javax.swing.JLabel();
        txtLivestock = new javax.swing.JTextField();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        lblLandSize = new javax.swing.JLabel();
        txtLandSize = new javax.swing.JTextField();
        lblDealer = new javax.swing.JLabel();
        txtDateChooser = new org.jdesktop.swingx.JXDatePicker();
        lblSAPNo = new javax.swing.JLabel();
        txtSAPNo = new javax.swing.JTextField();
        cbProductNo = new javax.swing.JComboBox();
        cbDealer = new javax.swing.JComboBox();

        setClosable(true);
        setResizable(true);

        pnlSearch.setBackground(java.awt.SystemColor.activeCaption);
        pnlSearch.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Ara", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial Black", 0, 12), new java.awt.Color(199, 92, 92))); // NOI18N

        lblSearch.setFont(new java.awt.Font("Arial Black", 0, 14)); // NOI18N
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

        buttonGroup1.add(rbProductNo);
        rbProductNo.setFont(new java.awt.Font("Arial Black", 0, 14)); // NOI18N
        rbProductNo.setForeground(new java.awt.Color(199, 92, 92));
        rbProductNo.setText("Şase No");
        rbProductNo.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                rbProductNoStateChanged(evt);
            }
        });

        buttonGroup1.add(rbCustomerName);
        rbCustomerName.setFont(new java.awt.Font("Arial Black", 0, 14)); // NOI18N
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

        tblKBE.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblKBE.setUpdateSelectionOnSort(false);
        jScrollPane1.setViewportView(tblKBE);

        javax.swing.GroupLayout pnlTableLayout = new javax.swing.GroupLayout(pnlTable);
        pnlTable.setLayout(pnlTableLayout);
        pnlTableLayout.setHorizontalGroup(
            pnlTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
        );
        pnlTableLayout.setVerticalGroup(
            pnlTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlTableLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE)
                .addContainerGap())
        );

        pnlNavigator.setBackground(java.awt.SystemColor.inactiveCaption);
        pnlNavigator.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

        lblCustomerNo.setFont(new java.awt.Font("Arial Black", 0, 14)); // NOI18N
        lblCustomerNo.setForeground(new java.awt.Color(199, 92, 92));
        lblCustomerNo.setText("Müşteri No :");

        txtCustomerNo.setColumns(7);
        txtCustomerNo.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtCustomerNo.setText("jTextField1");
        txtCustomerNo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(199, 92, 92)));

        lblName.setFont(new java.awt.Font("Arial Black", 0, 14)); // NOI18N
        lblName.setForeground(new java.awt.Color(199, 92, 92));
        lblName.setText("Adı Soyadı :");

        txtName.setColumns(15);
        txtName.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtName.setText("jTextField2");
        txtName.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(199, 92, 92)));

        lblProductNo.setFont(new java.awt.Font("Arial Black", 0, 14)); // NOI18N
        lblProductNo.setForeground(new java.awt.Color(199, 92, 92));
        lblProductNo.setText("Şasi No :");

        lblHours.setFont(new java.awt.Font("Arial Black", 0, 14)); // NOI18N
        lblHours.setForeground(new java.awt.Color(199, 92, 92));
        lblHours.setText("Çalışma Saati :");

        txtHours.setColumns(9);
        txtHours.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtHours.setText("jTextField1");
        txtHours.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(199, 92, 92)));

        lblDate.setFont(new java.awt.Font("Arial Black", 0, 14)); // NOI18N
        lblDate.setForeground(new java.awt.Color(199, 92, 92));
        lblDate.setText("Tarih :");

        lblHarvest.setFont(new java.awt.Font("Arial Black", 0, 14)); // NOI18N
        lblHarvest.setForeground(new java.awt.Color(199, 92, 92));
        lblHarvest.setText("Ürünler :");

        txtHarvest.setColumns(10);
        txtHarvest.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtHarvest.setText("jTextField1");
        txtHarvest.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(199, 92, 92)));

        lblLivestock.setFont(new java.awt.Font("Arial Black", 0, 14)); // NOI18N
        lblLivestock.setForeground(new java.awt.Color(199, 92, 92));
        lblLivestock.setText("Hayvancılık :");

        txtLivestock.setColumns(10);
        txtLivestock.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtLivestock.setText("jTextField1");
        txtLivestock.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(199, 92, 92)));

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

        lblLandSize.setFont(new java.awt.Font("Arial Black", 0, 14)); // NOI18N
        lblLandSize.setForeground(new java.awt.Color(199, 92, 92));
        lblLandSize.setText("İşlenen Arazi :");

        txtLandSize.setColumns(9);
        txtLandSize.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtLandSize.setText("jTextField1");
        txtLandSize.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(199, 92, 92)));

        lblDealer.setFont(new java.awt.Font("Arial Black", 0, 14)); // NOI18N
        lblDealer.setForeground(new java.awt.Color(199, 92, 92));
        lblDealer.setText("Bayi :");

        lblSAPNo.setFont(new java.awt.Font("Arial Black", 0, 14)); // NOI18N
        lblSAPNo.setForeground(new java.awt.Color(199, 92, 92));
        lblSAPNo.setText("SAP No :");

        txtSAPNo.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtSAPNo.setText("jTextField1");
        txtSAPNo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(199, 92, 92)));

        cbProductNo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cbDealer.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout pnlTextFieldsLayout = new javax.swing.GroupLayout(pnlTextFields);
        pnlTextFields.setLayout(pnlTextFieldsLayout);
        pnlTextFieldsLayout.setHorizontalGroup(
            pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTextFieldsLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblName)
                    .addComponent(lblCustomerNo)
                    .addComponent(lblProductNo)
                    .addComponent(lblSAPNo))
                .addGap(18, 18, 18)
                .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(pnlTextFieldsLayout.createSequentialGroup()
                        .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCustomerNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(39, 39, 39)
                        .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblHours)
                            .addComponent(lblLandSize)
                            .addComponent(lblDate))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtLandSize)
                            .addComponent(txtHours)
                            .addComponent(txtDateChooser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(32, 32, 32)
                        .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblLivestock)
                            .addComponent(lblDealer)
                            .addComponent(lblHarvest))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cbDealer, 0, 152, Short.MAX_VALUE)
                            .addComponent(txtLivestock)
                            .addComponent(txtHarvest)))
                    .addGroup(pnlTextFieldsLayout.createSequentialGroup()
                        .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(cbProductNo, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtSAPNo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancel)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlTextFieldsLayout.setVerticalGroup(
            pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTextFieldsLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCustomerNo)
                    .addComponent(txtCustomerNo, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblHours, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtHours, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtHarvest, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblHarvest))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblName)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDate)
                    .addComponent(lblLivestock)
                    .addComponent(txtLivestock, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblLandSize)
                    .addComponent(txtLandSize, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDealer)
                    .addComponent(txtSAPNo, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSAPNo)
                    .addComponent(cbDealer, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btnSave)
                    .addComponent(btnCancel)
                    .addComponent(cbProductNo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .addGroup(pnlTextFieldsLayout.createSequentialGroup()
                .addGap(125, 125, 125)
                .addComponent(lblProductNo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(16, 16, 16))
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
        if (rbProductNo.isSelected()) {
            lblSearch.setText("Şasi No :");
            searchCol = 2;
        } else {
            lblSearch.setText("Müşteri Adı :");
            searchCol = 1;
        }
    }//GEN-LAST:event_rbProductNoStateChanged

    private void btnFirstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFirstActionPerformed
        sRow = 0;
        InterfaceClass.selectARow(tblKBE, sRow);
        fillTextFields(sRow);
    }//GEN-LAST:event_btnFirstActionPerformed

    private void btnPreviousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousActionPerformed
        if (tblKBE.getSelectedRow() != 0) {
            sRow = tblKBE.getSelectedRow() - 1;
            InterfaceClass.selectARow(tblKBE, sRow);
            fillTextFields(sRow);
        }
    }//GEN-LAST:event_btnPreviousActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        // TODO add your handling code here:
        saveType = true;
        InterfaceClass.setChildEnabled(pnlTextFields, true);
        txtCustomerNo.setEnabled(false);
        txtName.setEnabled(false);
        tblKBE.setEnabled(false);
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        // TODO add your handling code here:
        if(hasProduct())
        {
            InterfaceClass.clearText(pnlTextFields);
            saveType = false;
            InterfaceClass.setChildEnabled(pnlTextFields, true);
            txtCustomerNo.setText("" + CustomerNo);
            txtCustomerNo.setEnabled(false);
            DatabaseClass dc = new DatabaseClass();
            Connection con = dc.ConnectDb();
            ResultSet rs = dc.createResult(con, "SELECT Name FROM tblCustomers WHERE CustomerNo = " + CustomerNo);
            //InterfaceClass.fillTable(d, "SELECT Name FROM tblCustomers WHERE CustomerNo = " + CustomerNo, tblKBE);
            try {
                while (rs.next()) {
                    txtName.setText(rs.getString("Name"));
                }
            } catch (SQLException ex) {
                Logger.getLogger(inFrmKBE.class.getName()).log(Level.SEVERE, null, ex);
            }
            txtName.setEnabled(false);
            tblKBE.setEnabled(false);
        }
        else
        {
            JOptionPane.showMessageDialog(rootPane, "Müşteriye ait herhangi bir ürün kayıtlı değil. Önce bir ürün ekleyin.");
        }
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:
        deleteKBE();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
        // TODO add your handling code here:
        if (tblKBE.getSelectedRow() != tblKBE.getRowCount() - 1) {
            sRow = tblKBE.getSelectedRow() + 1;
            InterfaceClass.selectARow(tblKBE, sRow);
            fillTextFields(sRow);
        }
    }//GEN-LAST:event_btnNextActionPerformed

    private void btnLastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLastActionPerformed
        // TODO add your handling code here:
        sRow = tblKBE.getRowCount() - 1;
        InterfaceClass.selectARow(tblKBE, sRow);
        fillTextFields(sRow);
    }//GEN-LAST:event_btnLastActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        boolean result = false;
        
        if (saveType) {
            result = editKBE();
        } 
        else 
        {
            result = addKBE();
        }
        if(result)
        {
            tblKBE.setEnabled(true);
            InterfaceClass.setChildEnabled(pnlTextFields, false);
            InterfaceClass.selectARow(tblKBE, sRow);
            fillTextFields(sRow);
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        // TODO add your handling code here:
        InterfaceClass.setChildEnabled(pnlTextFields, false);
        if (tblKBE.getRowCount() > 0) {
            tblKBE.setEnabled(true);
        }
        InterfaceClass.selectARow(tblKBE, sRow);
        fillTextFields(sRow);
    }//GEN-LAST:event_btnCancelActionPerformed

    private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyReleased
        // TODO add your handling code here:
        String text = txtSearch.getText();
        if (text.length() == 0) {
            sorter.setRowFilter(null);
        } else if (searchCol == 2) {
            sorter.setRowFilter(javax.swing.RowFilter.regexFilter("(?iu)^" + text, searchCol));
        } else if (searchCol == 1) {
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
    private javax.swing.JComboBox cbDealer;
    private javax.swing.JComboBox cbProductNo;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblCustomerNo;
    private javax.swing.JLabel lblDate;
    private javax.swing.JLabel lblDealer;
    private javax.swing.JLabel lblHarvest;
    private javax.swing.JLabel lblHours;
    private javax.swing.JLabel lblLandSize;
    private javax.swing.JLabel lblLivestock;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblProductNo;
    private javax.swing.JLabel lblSAPNo;
    private javax.swing.JLabel lblSearch;
    private javax.swing.JPanel pnlNavigator;
    private javax.swing.JPanel pnlSearch;
    private javax.swing.JPanel pnlTable;
    private javax.swing.JPanel pnlTextFields;
    private javax.swing.JRadioButton rbCustomerName;
    private javax.swing.JRadioButton rbProductNo;
    private javax.swing.JTable tblKBE;
    private javax.swing.JTextField txtCustomerNo;
    private org.jdesktop.swingx.JXDatePicker txtDateChooser;
    private javax.swing.JTextField txtHarvest;
    private javax.swing.JTextField txtHours;
    private javax.swing.JTextField txtLandSize;
    private javax.swing.JTextField txtLivestock;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtSAPNo;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
