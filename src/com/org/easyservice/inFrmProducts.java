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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.RowFilter.ComparisonType;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author Mustafa
 */
public class inFrmProducts extends javax.swing.JInternalFrame {

    /**
     * Creates new form inFrmProducts
     */
    public int CustomerNo;
    private int sRow=0;
    private int searchCol;
    private int cbSearchTypeSelected = 0;
    private int cbSearchColumnSelected = 0;
    TableRowSorter<TableModel> sorter;
    DefaultTableModel d;
    boolean saveType = true;
    String fillSQL;
    
    String productNo;
    String engineNo;
    String customerNo;
    String model;
    String plateNo;
    String wStartDate;
    String wEndDate;
    
    public inFrmProducts(int cNo) {
        CustomerNo = cNo;
        if(CustomerNo == 0) //Müşteri No'su 0 ise tüm ürünleri getir:
        {
            fillSQL = "SELECT P.ProductNo, P.EngineNo, P.CustomerNo, C.Name, P.Model, P.LicencePlate, P.WStartDate, P.WEndDate"
                + " FROM tblProducts P, tblCustomers C";
            fillSQL = fillSQL +  " WHERE P.CustomerNo = C.CustomerNo";
        }
        else //Değilse seçilen müşterinin ürünlerini getir:
        {
            fillSQL = "SELECT P.ProductNo, P.EngineNo, P.CustomerNo, C.Name, P.Model, P.LicencePlate, P.WStartDate, P.WEndDate"
                + " FROM tblProducts P, tblCustomers C";
            fillSQL = fillSQL +  " WHERE P.CustomerNo = C.CustomerNo";
            fillSQL = fillSQL + " AND P.CustomerNo = " + CustomerNo;
        }
        initComponents();
        if(CustomerNo == 0) //Tüm ürünler listeleniyorsa ekleme yaptırma.
            btnAdd.setEnabled(false);
        d = (DefaultTableModel) tblProducts.getModel();
        sorter = new TableRowSorter<>(d);
        tblProducts.setRowSorter(sorter);
        InterfaceClass.fillTable(d, fillSQL, tblProducts); //JTable'ı doldur.
        tblProducts.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                sRow = tblProducts.getSelectedRow();
                fillTextFields(sRow); //Seçili satıra göre Text'leri doldur.
            }
        });
        InterfaceClass.selectARow(tblProducts, sRow);
        fillTextFields(sRow);
        InterfaceClass.setChildEnabled(pnlTextFields, false);
        cbColumn.setSelectedIndex(cbSearchColumnSelected);
        cbCompareType.setSelectedIndex(cbSearchTypeSelected);
        

    }
    
    private void fillTextFields(int selectedRow)
    {
        if(tblProducts.getRowCount()>0)
	{
            txtProductNo.setText( String.valueOf( tblProducts.getValueAt(selectedRow, 0)) );
            txtEngineNo.setText( String.valueOf( tblProducts.getValueAt(selectedRow, 1)) );
            txtCustomerNo.setText( String.valueOf( tblProducts.getValueAt(selectedRow, 2)) );
            txtCustomerName.setText( String.valueOf( tblProducts.getValueAt(selectedRow, 3)) );
            txtModel.setText( String.valueOf( tblProducts.getValueAt(selectedRow, 4)) );
            txtPlate.setText( String.valueOf( tblProducts.getValueAt(selectedRow, 5)) );
            dpWStart.setDate(FunctionClass.getDateFromString(String.valueOf(tblProducts.getValueAt(selectedRow, 6))));
            dpWEnd.setDate(FunctionClass.getDateFromString(String.valueOf(tblProducts.getValueAt(selectedRow, 7))));      
        }
    }
    
    private int checkProduct(String attrName, String attrValue, boolean isAdd)
    {
        /*
        1: Şasi kaydı yoksa ekleme:yapılabilir, düzenleme: yapılabilir. Sonuç olarak 0 döner.
        2: Şasi kaydı var ve aynı müşteriye ait ise; ekleme: yapılamaz, düzenleme: yapılabilir. Sonuç ekleme ise -1, düzenleme ise 0 döner.
        3: Şasi kaydı var ve farklı müşteriye ait ise; ekleme: yapılamaz, düzenleme: uyararak yapılır. Sonuç ekleme ise -1, 
            düzenleme ise mevcut müşteri no döner.
        */
        int result = -1;
        DatabaseClass dc = new DatabaseClass();
        Connection con = dc.ConnectDb();
        ResultSet rs = null;
        try {
            
            //Verilen kolon bilgisine ait kaç adet kayıt var sorgusu:
            rs = dc.createResult(con, "SELECT COUNT(*) AS count FROM tblProducts WHERE " + attrName + "= '" + attrValue + "'");
            rs.next();
            int count = rs.getInt("count");
            if(count > 0) //Kayıt mevcut ise:
            {
                //Ürün hangi müşteriye ait sorgusu:
                rs = dc.createResult(con, "SELECT CustomerNo FROM tblProducts WHERE " + attrName + "= '" + attrValue + "'");
                rs.next();
                int cNo = rs.getInt("CustomerNo");
                int _cNo = Integer.parseInt(txtCustomerNo.getText()); //Girilen müşteri numarası.
                if(cNo != _cNo) //Ürün sahip değişimi yapılıyor ise:
                {
                    /*rs = dc.createResult(con, "SELECT Name tblCustomer WHERE CustomerNo = '" + cNo + "'");
                    rs.next();
                    String cName = rs.getString("Name"); //Ürünün kayıtlı olduğu müşteri ismi.
                    int dialog = JOptionPane.showConfirmDialog(rootPane, "Girilen Ürün " + cNo + " " + cName + " adlı müşteriye aittir. Değiştirmek istiyor musunuz?", "UYARI!", JOptionPane.YES_NO_OPTION);
                    changeCustomer = JOptionPane.YES_OPTION == dialog;*/
                    result = cNo; //Ürün cNo nolu müşteriye ait.
                }
                else //Müşterinin kendi ürününü düzenleme:
                {
                    result = 0; //Aynı müşteri düzenleme : OK.
                }
            }
            else if(count == 0) //Kayıt yoksa:
                result = 0; //Ekleme: ok, düzenleme: ok.
        } catch (SQLException ex) {
            Logger.getLogger(inFrmProducts.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(rs!=null)
            try {
                rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(inFrmProducts.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(con != null)
            try {
                con.close();
        } catch (SQLException ex) {
            Logger.getLogger(inFrmProducts.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    
    private void getValuesFromComponents()//Component'lerden veri çekme:
    {
        productNo = txtProductNo.getText().toUpperCase();
        engineNo = txtEngineNo.getText();
        customerNo = txtCustomerNo.getText();
        model = txtModel.getText();
        plateNo = txtPlate.getText().toUpperCase();
        wStartDate = FunctionClass.getDateString(dpWStart.getDate());//txtWStart.getText();
        wEndDate = FunctionClass.getDateString(dpWEnd.getDate());//txtWEnd.getText();
    }
    
    private boolean verifyValues(boolean isAdd)
    {
        //Componentlerden girilen verileri kontrol eder.Uygunsuz bir giriş var ise sonuç false döner, değilse true döner.
        boolean _result = true;
        int[] _checkData = {-1,-1,-1,-1,-1,-1};
        if(FunctionClass.checkValue(productNo))
        {
            InterfaceClass.setComponentTextColor(lblProductNo, FrmMain.captionColor);
        }
        else
        {
            _checkData[0] = 2;
            InterfaceClass.setComponentTextColor(lblProductNo, Color.red);
        }
        if(FunctionClass.checkValue(engineNo))
        {
            InterfaceClass.setComponentTextColor(lblEngineNo, FrmMain.captionColor);
        }
        else
        {
            _checkData[1] = 10;
            InterfaceClass.setComponentTextColor(lblEngineNo, Color.red);
        }
        if(FunctionClass.checkValue(customerNo))
        {
            InterfaceClass.setComponentTextColor(lblCustomerName, FrmMain.captionColor);
            InterfaceClass.setComponentTextColor(lblCustomerNo, FrmMain.captionColor);
        }
        else
        {
            _checkData[2] = 11;
            InterfaceClass.setComponentTextColor(lblCustomerName, Color.red);
            InterfaceClass.setComponentTextColor(lblCustomerNo, Color.red);
        }
        if(FunctionClass.checkValue(model))
        {
            InterfaceClass.setComponentTextColor(lblModel, FrmMain.captionColor);
        }
        else
        {
            _checkData[3] = 12;
            InterfaceClass.setComponentTextColor(lblModel, Color.red);
        }
        if(!FunctionClass.checkDate(wStartDate, "dd.MM.yyyy")) //Tarih hatalı ise:
        {
            _checkData[4] = 5;
            InterfaceClass.setComponentTextColor(lblWStart, Color.red);
        }
        else //Tarih uygun ise:
        {
            InterfaceClass.setComponentTextColor(lblWStart, FrmMain.captionColor);
        }
        if(!FunctionClass.checkDate(wEndDate, "dd.MM.yyyy")) //Tarih hatalı ise:
        {
            _checkData[5] = 5;
            InterfaceClass.setComponentTextColor(lblWEnd, Color.red);
        }
        else //Tarih uygun ise:
        {
            InterfaceClass.setComponentTextColor(lblWEnd, FrmMain.captionColor);
        }
        String errors = "";
        //Hata mesajlarını gösteren ve hata sonucunu hesaplayan döngü:
        for(int i = 0; i<_checkData.length; i++)
        {
            if(_checkData[i]!=-1)
            {
                _result = _result && false; //Tek hata bile varsa son değer false olacak.
                errors = errors + Errors.ErrorList[_checkData[i]] + "\n";
            }
        }
        if(!_result) //Girilen bilgiler hatalı ise:
            JOptionPane.showMessageDialog(rootPane, errors);
        else //Girilen bilgiler uygun ise:
        {
            String[] veriler = {productNo, engineNo, plateNo};
            errors = "";
            _checkData[0] = checkProduct("ProductNo", productNo, isAdd);
            _checkData[1] = checkProduct("EngineNo", engineNo, isAdd);
            if(!plateNo.equals(""))
                _checkData[2] = checkProduct("LicencePlate", plateNo, isAdd);
            else
                _checkData[2] = 0;
            /*  Olası değerler: -1:Uygun değil, 0: Uygun, >0: Değişim var onay al.
                1- _checkData([0]==[1]==[2]) ise:
                    1.1 - == (-1) ise işlem yaptırma.
                    1.2 - == (0) ise uygun.
                    1.3 - >0 ise devir işlemi var, onay iste.
                2- != ise:
                    2.1 - (-1) olanlar için uyarı göster.
                    2.1 - >0 olanlar için uyarı göster.
            */
            boolean isEqual = (_checkData[0] == _checkData[1]) && (_checkData[0] == _checkData[2]);
            if(isEqual) //Üç değişken için de sonuç aynı ise:
            {
                if(_checkData[0] == -1)
                {
                    _result = false;
                    errors = "Şasi numarası, motor numarası ve plaka bilgileri hatalı.";
                }
                else if(_checkData[0] == 0)
                {
                    _result = true;
                }
                else
                {
                    if(!isAdd)
                    {
                    int dialog = JOptionPane.showConfirmDialog(rootPane, "Girilen Ürün " + _checkData[0] + " " + getCustomerName(_checkData[0]) + " adlı müşteriye aittir. Değiştirmek istiyor musunuz?", "UYARI!", JOptionPane.YES_NO_OPTION);
                    boolean changeCustomer = JOptionPane.YES_OPTION == dialog;
                    if(changeCustomer)
                        _result = true;
                    else
                        _result = false;
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(rootPane, "Girilen Ürün " + _checkData[0] + " " + getCustomerName(_checkData[0]) + " adlı müşteriye ait!");
                        _result = false;
                    }
                }
            }
            else
            {
                _result = _result && false;
                for(int i = 0; i<3; i++)
                {
                    if(_checkData[i] == -1)
                    {
                        errors = errors + veriler[i] + " bilgisi hatalı!\n";
                    }
                    else if(_checkData[i] > 0)
                    {
                        errors = errors + veriler[i] + " nolu kayıt " + _checkData[i] + " " + getCustomerName(_checkData[i]) + " müşterisine ait!\n";
                    }
                }
                JOptionPane.showMessageDialog(rootPane, errors);
            }
            
        }
        return _result;
    }
    
    private String getCustomerName(int cNo)
    {
        String name = "";
        DatabaseClass dc = new DatabaseClass();
        Connection con = dc.ConnectDb();
        ResultSet rs = dc.createResult(con, "SELECT Name AS name FROM tblCustomers WHERE CustomerNo = '" + cNo + "'");
        try {
            rs.next();
            name = rs.getString("name");
        } catch (SQLException ex) {
            Logger.getLogger(inFrmProducts.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(rs!=null)
            try {
                rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(inFrmProducts.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(con!= null)
            try {
                con.close();
        } catch (SQLException ex) {
            Logger.getLogger(inFrmProducts.class.getName()).log(Level.SEVERE, null, ex);
        }
        return name;
    }
    private boolean editProduct()
    {
        getValuesFromComponents();
        boolean _result = verifyValues(false);
        
        String sql = "UPDATE tblProducts SET ProductNo = ?,"
                + "EngineNo = ?,"
                + "CustomerNo = ?,"
                + "Model = ?,"
                + "LicencePlate = ?,"
                + "WStartDate = ?,"
                + "WEndDate = ? WHERE ProductNo = ?";
        
        DatabaseClass dc = new DatabaseClass();
        Connection con = dc.ConnectDb();
        if(_result)
        {
            try {
                PreparedStatement pSTM = con.prepareStatement(sql);
                pSTM = con.prepareStatement(sql);
                pSTM.setString(1, productNo);
                pSTM.setString(2, engineNo);
                pSTM.setString(3, customerNo);
                pSTM.setString(4, model);
                pSTM.setString(5, plateNo);
                pSTM.setString(6, wStartDate);
                pSTM.setString(7, wEndDate);
                pSTM.setString(8, (String)tblProducts.getValueAt(sRow, 0));
                if(!dc.exeQuery(pSTM))
                {
                    _result = false;
                    JOptionPane.showMessageDialog(null, "Sorgu İşletilemedi!", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
                else
                {
                    _result = true;
                    JOptionPane.showMessageDialog(null, tblProducts.getValueAt(sRow, 0) + " Numaralı Kayıt Güncellendi!", "Mission Completed", JOptionPane.INFORMATION_MESSAGE);
                    int sec = sRow;
                    InterfaceClass.fillTable(d, fillSQL, tblProducts);
                    sRow = sec;
                    InterfaceClass.selectARow(tblProducts, sRow);
                    fillTextFields(sRow);
                }
                if(pSTM != null)
                    pSTM.close();
                if(con != null)
                    con.close();
            } catch (SQLException ex) {
                _result = false;
                Logger.getLogger(inFrmProducts.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return _result;
    }
    
    private boolean addProduct()
    {
        getValuesFromComponents();
        String sql = "INSERT INTO tblProducts(ProductNo, EngineNo, CustomerNo, Model, LicencePlate, WStartDate, WEndDate) "
                + "VALUES(?, ?, ?, ?, ?, ?, ?)";
        
        boolean _result = verifyValues(true);
        
        if(_result)
        {
            try {
                DatabaseClass dc = new DatabaseClass();
                Connection con = dc.ConnectDb();
                PreparedStatement pSTM = con.prepareStatement(sql);
                pSTM.setString(1, productNo);
                pSTM.setString(2, engineNo);
                pSTM.setString(3, customerNo);
                pSTM.setString(4, model);
                pSTM.setString(5, plateNo);
                pSTM.setString(6, wStartDate);
                pSTM.setString(7, wEndDate);
                if(!dc.exeQuery(pSTM))
                {
                    _result = false;
                    JOptionPane.showMessageDialog(null, "Sorgu İşletilemedi!", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
                else
                {
                    _result = true;
                    JOptionPane.showMessageDialog(null, "Kayıt Eklendi!", "Mission Completed", JOptionPane.INFORMATION_MESSAGE);
                    InterfaceClass.fillTable(d, fillSQL, tblProducts);
                    sRow = tblProducts.getRowCount() -1;
                    InterfaceClass.selectARow(tblProducts, sRow);
                    fillTextFields(sRow);
                }
                if(pSTM != null)
                    pSTM.close();
                if(con != null)
                    con.close();
            } catch (SQLException ex) {
                _result = false;
                Logger.getLogger(inFrmProducts.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return _result;
    }
    
    private void deleteProduct()
    {
        int response = JOptionPane.showConfirmDialog(null, tblProducts.getValueAt(sRow, 0) + "şase nolu ürünü silmek istediğinize emin misiniz? Bu ürünü sildiğinizde ürüne ait bakım kayıtları da silinecektir!", "Are you Sure?", JOptionPane.YES_NO_OPTION);
	if(response == JOptionPane.YES_OPTION)
	{
            DatabaseClass dc = new DatabaseClass();
            Connection con = dc.ConnectDb();
            String sqlProduct = "DELETE FROM tblProducts WHERE ProductNo = ?";
            String sqlMaintenance = "DELETE FROM tblMaintenance WHERE ProductNo = ?";
            String errors = "";
            PreparedStatement pStm = null;
            try 
            {
                pStm = con.prepareStatement(sqlProduct);
                pStm.setString(1, (String)tblProducts.getValueAt(sRow, 0));
            } catch (SQLException e) 
            {
                e.printStackTrace();
            }
            if(!dc.exeQuery(pStm))
            {
                errors = tblProducts.getValueAt(sRow, 0) + " şasi nolu ürün silinirken hata oluştu!\n";
            }
            else
                errors = tblProducts.getValueAt(sRow, 0) + " şasi nolu ürün silindi!\n";
            try {
                pStm = con.prepareStatement(sqlMaintenance);
            } catch (SQLException ex) {
                Logger.getLogger(inFrmProducts.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                pStm.setString(1, (String)tblProducts.getValueAt(sRow, 0));
            } catch (SQLException ex) {
                Logger.getLogger(inFrmProducts.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(!dc.exeQuery(pStm))
                errors = errors + "Bakımlar silinirken bir hata oluştu!\n";
            else
                errors = errors + "Bakım kayıtları silindi!\n";
            try {
                pStm.close();
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(inFrmCustomers.class.getName()).log(Level.SEVERE, null, ex);
            }
            JOptionPane.showMessageDialog(null, errors, "Mission Completed", JOptionPane.INFORMATION_MESSAGE);
            InterfaceClass.fillTable(d, fillSQL, tblProducts);
            sRow = tblProducts.getRowCount()-1;
            if(tblProducts.getRowCount() > 0)
            {
                InterfaceClass.selectARow(tblProducts, sRow);
                fillTextFields(sRow);
            }
            
        }
    }
    
    private void setSearchVisible()
    {
        if(cbSearchColumnSelected<3)
        {
            btnSearch.setVisible(false);
            txtSearch1.setVisible(true);
            dpBegin.setVisible(false);
            dpEnd.setVisible(false);
            if(cbSearchTypeSelected == 3)
            {
                btnSearch.setVisible(true);
                txtSearch2.setVisible(true);
            }
            else
            {
                txtSearch2.setVisible(false);
            }
        }
        else
        {
            btnSearch.setVisible(true);
            dpBegin.setVisible(true);
            txtSearch1.setVisible(false);
            txtSearch2.setVisible(false);
            if(cbSearchTypeSelected == 3)
            {
                dpEnd.setVisible(true);
            }
            else
                dpEnd.setVisible(false);
        }
        this.repaint();
        this.revalidate();
    }
    
    private void searchTable()
    {
        
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlTable = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblProducts = new javax.swing.JTable(new DefaultTableModel(){  //Tablo cellEditable özelliği 'false'
            public boolean isCellEditable(int row, int column) //olacak şekilde oluşturuluyor.
            {
                return false;
            }
        });
        btnMaintenance = new javax.swing.JButton();
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
        txtSearch1 = new javax.swing.JTextField();
        cbCompareType = new javax.swing.JComboBox();
        cbColumn = new javax.swing.JComboBox();
        dpBegin = new org.jdesktop.swingx.JXDatePicker();
        dpEnd = new org.jdesktop.swingx.JXDatePicker();
        txtSearch2 = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        pnlTextFields = new javax.swing.JPanel();
        lblProductNo = new javax.swing.JLabel();
        txtProductNo = new javax.swing.JTextField();
        lblEngineNo = new javax.swing.JLabel();
        txtEngineNo = new javax.swing.JTextField();
        lblCustomerNo = new javax.swing.JLabel();
        txtCustomerNo = new javax.swing.JTextField();
        lblCustomerName = new javax.swing.JLabel();
        txtCustomerName = new javax.swing.JTextField();
        lblModel = new javax.swing.JLabel();
        txtModel = new javax.swing.JTextField();
        lblPlate = new javax.swing.JLabel();
        txtPlate = new javax.swing.JTextField();
        lblWStart = new javax.swing.JLabel();
        lblWEnd = new javax.swing.JLabel();
        btnCancel = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        dpWStart = new org.jdesktop.swingx.JXDatePicker();
        dpWEnd = new org.jdesktop.swingx.JXDatePicker();

        setClosable(true);
        setResizable(true);
        setTitle("Ürünler");

        pnlTable.setBackground(java.awt.SystemColor.inactiveCaption);
        pnlTable.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        tblProducts.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        tblProducts.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblProducts.setUpdateSelectionOnSort(false);
        jScrollPane1.setViewportView(tblProducts);

        btnMaintenance.setText("BAKIM");
        btnMaintenance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMaintenanceActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlTableLayout = new javax.swing.GroupLayout(pnlTable);
        pnlTable.setLayout(pnlTableLayout);
        pnlTableLayout.setHorizontalGroup(
            pnlTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addGroup(pnlTableLayout.createSequentialGroup()
                .addComponent(btnMaintenance)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        pnlTableLayout.setVerticalGroup(
            pnlTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlTableLayout.createSequentialGroup()
                .addComponent(btnMaintenance)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
                .addContainerGap())
        );

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
                .addComponent(btnFirst, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnPrevious, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnNext, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnLast, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlNavigatorLayout.setVerticalGroup(
            pnlNavigatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlNavigatorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlNavigatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnFirst, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPrevious, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNext, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLast, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pnlSearch.setBackground(java.awt.SystemColor.activeCaption);
        pnlSearch.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Ara", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial Black", 0, 12), new java.awt.Color(199, 92, 92))); // NOI18N

        lblSearch.setFont(new java.awt.Font("Arial Black", 0, 14)); // NOI18N
        lblSearch.setForeground(new java.awt.Color(199, 92, 92));
        lblSearch.setText("jLabel1");

        txtSearch1.setColumns(15);
        txtSearch1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtSearch1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(199, 92, 92)));
        txtSearch1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearch1KeyReleased(evt);
            }
        });

        cbCompareType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Eşit", "Büyük", "Küçük", "Arasında", "İçinde" }));
        cbCompareType.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbCompareTypeİtemStateChanged(evt);
            }
        });
        cbCompareType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbCompareTypeActionPerformed(evt);
            }
        });

        cbColumn.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Şasi No", "Motor No", "Müşteri", "Teslim Tarihi", "Garanti Bitiş" }));
        cbColumn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbColumnActionPerformed(evt);
            }
        });

        txtSearch2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtSearch2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(199, 92, 92)));

        btnSearch.setText("Ara");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlSearchLayout = new javax.swing.GroupLayout(pnlSearch);
        pnlSearch.setLayout(pnlSearchLayout);
        pnlSearchLayout.setHorizontalGroup(
            pnlSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSearchLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblSearch)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtSearch1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22)
                .addComponent(txtSearch2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dpBegin, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(dpEnd, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addComponent(cbColumn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cbCompareType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSearch)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlSearchLayout.setVerticalGroup(
            pnlSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSearchLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblSearch)
                    .addComponent(txtSearch1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSearch2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dpBegin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dpEnd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbColumn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbCompareType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearch))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlTextFields.setBackground(java.awt.SystemColor.activeCaption);
        pnlTextFields.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblProductNo.setFont(new java.awt.Font("Arial Black", 0, 14)); // NOI18N
        lblProductNo.setForeground(new java.awt.Color(199, 92, 92));
        lblProductNo.setText("Şasi No :");

        txtProductNo.setColumns(7);
        txtProductNo.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtProductNo.setText("jTextField1");
        txtProductNo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(199, 92, 92)));

        lblEngineNo.setFont(new java.awt.Font("Arial Black", 0, 14)); // NOI18N
        lblEngineNo.setForeground(new java.awt.Color(199, 92, 92));
        lblEngineNo.setText("Motor No :");

        txtEngineNo.setColumns(7);
        txtEngineNo.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtEngineNo.setText("jTextField1");
        txtEngineNo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(199, 92, 92)));

        lblCustomerNo.setFont(new java.awt.Font("Arial Black", 0, 14)); // NOI18N
        lblCustomerNo.setForeground(new java.awt.Color(199, 92, 92));
        lblCustomerNo.setText("Müşteri No :");

        txtCustomerNo.setColumns(3);
        txtCustomerNo.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtCustomerNo.setText("jTextField1");
        txtCustomerNo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(199, 92, 92)));

        lblCustomerName.setFont(new java.awt.Font("Arial Black", 0, 14)); // NOI18N
        lblCustomerName.setForeground(new java.awt.Color(199, 92, 92));
        lblCustomerName.setText("Müşteri Adı :");

        txtCustomerName.setColumns(15);
        txtCustomerName.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtCustomerName.setText("jTextField1");
        txtCustomerName.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(199, 92, 92)));

        lblModel.setFont(new java.awt.Font("Arial Black", 0, 14)); // NOI18N
        lblModel.setForeground(new java.awt.Color(199, 92, 92));
        lblModel.setText("Model :");

        txtModel.setColumns(8);
        txtModel.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtModel.setText("jTextField1");
        txtModel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(199, 92, 92)));

        lblPlate.setFont(new java.awt.Font("Arial Black", 0, 14)); // NOI18N
        lblPlate.setForeground(new java.awt.Color(199, 92, 92));
        lblPlate.setText("Plaka :");

        txtPlate.setColumns(8);
        txtPlate.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtPlate.setText("jTextField1");
        txtPlate.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(199, 92, 92)));

        lblWStart.setFont(new java.awt.Font("Arial Black", 0, 14)); // NOI18N
        lblWStart.setForeground(new java.awt.Color(199, 92, 92));
        lblWStart.setText("Teslim Tarihi :");

        lblWEnd.setFont(new java.awt.Font("Arial Black", 0, 14)); // NOI18N
        lblWEnd.setForeground(new java.awt.Color(199, 92, 92));
        lblWEnd.setText("Garanti Bitiş Tarihi :");

        btnCancel.setText("Vazgeç");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        btnSave.setText("Kaydet");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlTextFieldsLayout = new javax.swing.GroupLayout(pnlTextFields);
        pnlTextFields.setLayout(pnlTextFieldsLayout);
        pnlTextFieldsLayout.setHorizontalGroup(
            pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTextFieldsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlTextFieldsLayout.createSequentialGroup()
                        .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblEngineNo)
                            .addComponent(lblProductNo))
                        .addGap(18, 18, 18)
                        .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtProductNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtEngineNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(40, 40, 40)
                        .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblCustomerNo)
                            .addComponent(lblCustomerName))
                        .addGap(18, 18, 18)
                        .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCustomerNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCustomerName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(31, 31, 31)
                        .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblModel)
                            .addComponent(lblPlate))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtModel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPlate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblWStart)
                            .addComponent(lblWEnd))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(dpWEnd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dpWStart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(pnlTextFieldsLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnCancel)))
                .addContainerGap())
        );
        pnlTextFieldsLayout.setVerticalGroup(
            pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTextFieldsLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProductNo)
                    .addComponent(txtProductNo, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCustomerNo)
                    .addComponent(txtCustomerNo, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblModel)
                    .addComponent(txtModel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblWStart)
                    .addComponent(dpWStart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEngineNo)
                    .addComponent(txtEngineNo, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCustomerName)
                    .addComponent(txtCustomerName, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPlate)
                    .addComponent(txtPlate, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblWEnd)
                    .addComponent(dpWEnd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                .addGroup(pnlTextFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancel)
                    .addComponent(btnSave)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlTable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlSearch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlTextFields, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlNavigator, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addComponent(pnlTextFields, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnFirstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFirstActionPerformed
        sRow = 0;
        InterfaceClass.selectARow(tblProducts, sRow);
        fillTextFields(sRow);
    }//GEN-LAST:event_btnFirstActionPerformed

    private void btnPreviousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousActionPerformed
        if(tblProducts.getSelectedRow() != 0)
        {
            sRow = tblProducts.getSelectedRow()-1;
            InterfaceClass.selectARow(tblProducts, sRow);
            fillTextFields(sRow);
        }
    }//GEN-LAST:event_btnPreviousActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        // TODO add your handling code here:
        saveType = true;
        InterfaceClass.setChildEnabled(pnlTextFields, true);
        tblProducts.setEnabled(false);
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        // TODO add your handling code here:
        saveType = false;
        InterfaceClass.setChildEnabled(pnlTextFields, true);
        tblProducts.setEnabled(false);
        InterfaceClass.clearText(pnlTextFields);
        txtCustomerNo.setText(""+CustomerNo);
        txtCustomerNo.setEnabled(false);
        DatabaseClass dc = new DatabaseClass();
        Connection con = dc.ConnectDb();
        ResultSet rs = dc.createResult(con, "SELECT Name FROM tblCustomers WHERE CustomerNo = " + CustomerNo);
        try {
            while(rs.next())
                txtCustomerName.setText(rs.getString("Name"));
            txtCustomerName.setEnabled(false);
        } catch (SQLException ex) {
            Logger.getLogger(inFrmProducts.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:
        deleteProduct();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
        // TODO add your handling code here:
        if(tblProducts.getSelectedRow() != tblProducts.getRowCount()-1)
        {
            sRow = tblProducts.getSelectedRow()+1;
            InterfaceClass.selectARow(tblProducts, sRow);
            fillTextFields(sRow);
        }
    }//GEN-LAST:event_btnNextActionPerformed

    private void btnLastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLastActionPerformed
        // TODO add your handling code here:
        sRow = tblProducts.getRowCount()-1;
        InterfaceClass.selectARow(tblProducts, sRow);
        fillTextFields(sRow);
    }//GEN-LAST:event_btnLastActionPerformed

    private void txtSearch1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearch1KeyReleased
        // TODO add your handling code here:
        if(cbSearchTypeSelected != 3)
        {
            
        }
        String text = txtSearch1.getText();
        if(cbSearchTypeSelected == 0) //Eşit
        {
            sorter.setRowFilter(javax.swing.RowFilter.regexFilter("(?iu)^" + text, searchCol));
        }
        else if(cbSearchTypeSelected == 1) //Büyük
        {
            
            if(searchCol == 6 || searchCol == 7)
            {
                DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
                java.util.Date dt = null;
                try
                {
                    dt = df.parse(text);
                    sorter.setRowFilter(javax.swing.RowFilter.dateFilter(RowFilter.ComparisonType.EQUAL, dt, searchCol));
                }
                catch(ParseException e)
                {
                    System.out.println(dt.toString());
                }
                
            }
        }
        if(text.length() == 0)
        {
            sorter.setRowFilter(null);
        }
        else if(searchCol == 0)
        {
            
        }
        else if(searchCol == 1)
        {
            sorter.setRowFilter(javax.swing.RowFilter.regexFilter("^" + text, searchCol)); //(?iu: unicode non-case sensitive
        }
        else if(searchCol == 3)
        {
            
        }
    }//GEN-LAST:event_txtSearch1KeyReleased

    private void cbColumnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbColumnActionPerformed
        // TODO add your handling code here:
        cbSearchColumnSelected = cbColumn.getSelectedIndex();
        searchCol = cbColumn.getSelectedIndex();
        if(searchCol == 2)
        {
            searchCol = 3;
        }
        else if(searchCol == 3)
        {
            searchCol = 6;
        }
        else if(searchCol == 4)
        {
            searchCol = 7;
        }
        lblSearch.setText(cbColumn.getSelectedItem().toString() + " :");
        setSearchVisible();
    }//GEN-LAST:event_cbColumnActionPerformed

    private void cbCompareTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbCompareTypeActionPerformed
        // TODO add your handling code here:
        cbSearchTypeSelected = cbCompareType.getSelectedIndex();
        setSearchVisible();
    }//GEN-LAST:event_cbCompareTypeActionPerformed

    private void cbCompareTypeİtemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbCompareTypeİtemStateChanged
        // TODO add your handling code here:
        if(cbCompareType.getSelectedIndex() == 3)
        {
            //txtSearch2.setVisible(true);
            this.repaint();
        }
        else
        {
            //txtSearch2.setVisible(false);
            this.repaint();
        }
    }//GEN-LAST:event_cbCompareTypeİtemStateChanged

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
        boolean _result;
        if(saveType)
            _result = editProduct();
        else
            _result = addProduct();
        if(_result)
        {
            tblProducts.setEnabled(true);
            InterfaceClass.setChildEnabled(pnlTextFields, false);
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        // TODO add your handling code here:
        InterfaceClass.setChildEnabled(pnlTextFields, false);
        InterfaceClass.setChildColor(pnlTextFields, FrmMain.captionColor);
        tblProducts.setEnabled(true);
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnMaintenanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMaintenanceActionPerformed
        // TODO add your handling code here:
        inFrmMaintenance ifM = new inFrmMaintenance(tblProducts.getValueAt(sRow, 0).toString());
        FrmMain.frm.dskPaneMain.add(ifM);
        ifM.show();
    }//GEN-LAST:event_btnMaintenanceActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        // TODO add your handling code here:
        if(cbSearchTypeSelected == 3)
        {
            if(cbSearchColumnSelected>=3)
            {
                Date d1, d2;
                String s1 = FunctionClass.getDateString(dpBegin.getDate());
                String s2 = FunctionClass.getDateString(dpEnd.getDate());
                if(FunctionClass.checkDate(s1, "dd.MM.yyyy") && FunctionClass.checkDate(s2, "dd.MM.yyyy"))
                {
                    d1 = dpBegin.getDate();
                    d2 = dpEnd.getDate();
                    List<RowFilter<Object,Object>> filters = new ArrayList<RowFilter<Object,Object>>(2);
                    filters.add( RowFilter.dateFilter(ComparisonType.AFTER, d1) );
                    filters.add( RowFilter.dateFilter(ComparisonType.BEFORE, d2) );
                    sorter.setRowFilter(RowFilter.andFilter(filters));
                }
            }
        }
        DatabaseClass dc = new DatabaseClass();
        Connection con = dc.ConnectDb();
        String sql = "SELECT SAPNo, Date FROM tblMaintenance_ex";
        ResultSet rs = dc.createResult(con, sql);
        ArrayList<String[]> list = new ArrayList<String[]>();
        try {
            while(rs.next())
            {
                String[] item = {rs.getString("SAPNo"), rs.getString("Date")};
                list.add(item);
            }
        } catch (SQLException ex) {
            Logger.getLogger(inFrmProducts.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnSearchActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnFirst;
    private javax.swing.JButton btnLast;
    private javax.swing.JButton btnMaintenance;
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnPrevious;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSearch;
    private javax.swing.JComboBox cbColumn;
    private javax.swing.JComboBox cbCompareType;
    private org.jdesktop.swingx.JXDatePicker dpBegin;
    private org.jdesktop.swingx.JXDatePicker dpEnd;
    private org.jdesktop.swingx.JXDatePicker dpWEnd;
    private org.jdesktop.swingx.JXDatePicker dpWStart;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblCustomerName;
    private javax.swing.JLabel lblCustomerNo;
    private javax.swing.JLabel lblEngineNo;
    private javax.swing.JLabel lblModel;
    private javax.swing.JLabel lblPlate;
    private javax.swing.JLabel lblProductNo;
    private javax.swing.JLabel lblSearch;
    private javax.swing.JLabel lblWEnd;
    private javax.swing.JLabel lblWStart;
    private javax.swing.JPanel pnlNavigator;
    private javax.swing.JPanel pnlSearch;
    private javax.swing.JPanel pnlTable;
    private javax.swing.JPanel pnlTextFields;
    private javax.swing.JTable tblProducts;
    private javax.swing.JTextField txtCustomerName;
    private javax.swing.JTextField txtCustomerNo;
    private javax.swing.JTextField txtEngineNo;
    private javax.swing.JTextField txtModel;
    private javax.swing.JTextField txtPlate;
    private javax.swing.JTextField txtProductNo;
    private javax.swing.JTextField txtSearch1;
    private javax.swing.JTextField txtSearch2;
    // End of variables declaration//GEN-END:variables
}
