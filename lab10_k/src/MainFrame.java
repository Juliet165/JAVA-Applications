
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import Task_23_10.*;
import java.awt.Cursor;
import java.util.ArrayList;
import javax.swing.JDialog;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class MainFrame extends javax.swing.JFrame {


    protected java.io.File fileData = new java.io.File("product.dat");

    final boolean chooseFile() {
        JFileChooser file = new JFileChooser();
        file.setMultiSelectionEnabled(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Data files", "dat");
        file.setFileFilter(filter);
        file.setSelectedFile(fileData);
        if (file.showOpenDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {
            fileData = file.getSelectedFile();
            Commands.setFile(fileData);
            return true;
        }
        return false;
    }

    final Product getProduct() {
        Product p = new  Product();
        p.setWarehouseNumber(werehouseNumberText.getText().trim());
        p.setProductCode(productCodeText.getText().trim());
        p.setProductName(productNameText.getText().trim());
        p.setReceiptDate(receiptDateText.getText().trim());
        p.setStorageDays(storageDaysText.getText().trim());
        p.setQuantity(quantityText.getText().trim());
        p.setPricePerUnit(pricePerText.getText().trim());
        return p;
    }

    final void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, this.getTitle() + ": error",
                JOptionPane.ERROR_MESSAGE);
    }

    final void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, this.getTitle() + ": error",
                JOptionPane.INFORMATION_MESSAGE);
    }

    static final String ABOUT_TEXT = "Written by Julia Kanoplich";
    static final String STATUS_TEXT_DEFAULT = "Enter Alt+x to exit...";
    static final String STATUS_TEXT_FILE_OPEN = "Choose a file to work with...";
    static final String STATUS_TEXT_FILE_EXIT = "Exit application...";
    static final String STATUS_TEXT_HELP_ABOUT = "Show information about the application...";
    static final String STATUS_TEXT_COMMAND_ADD = "Add a new product...";
    static final String STATUS_TEXT_COMMAND_REMOVE = "Remove existing product by key...";
    static final String STATUS_TEXT_COMMAND_SHOW = "Show all products...";
    static final String STATUS_TEXT_COMMAND_SHOW_SORTED = "Show all products sorted by key...";
    static final String STATUS_TEXT_COMMAND_FIND = "Find and show products by key...";

    final void setStatusTextDefault() {
        statusBarText.setText(STATUS_TEXT_DEFAULT);
        statusBarText.repaint();
    }

    static final int ROW_WHEREHOUSENUMBER = 0;
    static final int ROW_PRODUCTCODE = 1;
    static final int ROW_RECEIPDATE = 2;
    static final int ROW_STORAGEDATE = 3;

    static final Object[] TABLE_HEADER = {
           Product.P_warehouseNumber,Product.P_productCode,Product.P_productName,Product.P_receiptDate,
            Product.P_storageDays,Product.P_quantity,Product.P_pricePerUnit
    };
    static final int[] TABLE_SIZE = {
            150, 150, 150, 150, 150, 150, 150
    };

    final DefaultTableModel createTableModel() {
        DefaultTableModel tm = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tm.setColumnIdentifiers(TABLE_HEADER);
        return tm;
    }

    final void fillTable(JTable tbl, List<String> src) {
        int i, n;
        DefaultTableModel tm = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        if (src != null) {
            tm.setColumnIdentifiers(TABLE_HEADER);
            for (i = 0, n = src.size(); i < n; i++) {
                Object[] rows = src.get(i).split(Product.AREA_DEL);
                if (rows.length != TABLE_HEADER.length) {
                    showError("Invalid data at position " + i);
                    continue;
                }
                //swap price/annotation:
//                Object o = rows[6];
//                rows[6] = rows[5];
//                rows[5] = o;
                tm.addRow(rows);
            }
        }
        tbl.setModel(tm);
        if (src != null) {
            TableColumnModel cm = tbl.getColumnModel();
            for (i = 0; i < TABLE_SIZE.length; i++) {
                cm.getColumn(i).setPreferredWidth(TABLE_SIZE[i]);
            }
            tbl.setEnabled(true);
            tbl.setVisible(true);
        } else {
            tbl.setEnabled(false);
            tbl.setVisible(false);
        }
    }

    static final String[][] TEST_DATA = {
            {"123","93930","Phone","05.12.23","100","10","1000"},
            {"290","20343","TV", "06.10.22","23","23","1200"},
            {"220","20323","Laptop", "02.03.23","24","93","1800"}
    };

    final List<String> getTestData() {
        ArrayList lst = new ArrayList(TEST_DATA.length);
        for (int i = 0; i < TEST_DATA.length; i++) {
            String[] r = TEST_DATA[i];
            String str = r[0];
            for (int j = 1; j < r.length; j++) {
                str += Product.AREA_DEL + r[j];
            }
            lst.add(i, str);
        }
        return lst;
    }

    final void fillTable(JTable tbl) {
        fillTable(tbl, getTestData());
    }

    final void clearTable(JTable tbl) {
        fillTable(tbl, null);
    }

    // target of chooseKeyDialog -
    private boolean chooseKeyDialogTargetRemove = false;
    // if false - use for find by key command
    // if true - use for remove by key command

    //--Save last veiw command here:
    static class ViewOptions {

        static enum Command {
            None,
            Show,
            ShowSorted,
            Find
        };
        static Command what;

        // Params for all commands:
        static String keyType;
        static String keyValue;
        static int comp; //0 -==, 1 -<, 2 ->
        static boolean reverse;
        static String delKeyType;
        static String delKeyValue;
    };

    static final String RESULT_TEXT_NONE = " ";
    static final String RESULT_TEXT_SHOW = "All products, unordered:";
    static final String RESULT_TEXT_SHOW_SORTED = "All products, ordered by ";
    static final String RESULT_TEXT_SHOW_REVERSE_SORTED = "All products, reverse ordered by ";
    static final String RESULT_TEXT_FIND = "Find product(s) by ";
    final void setOptions(ViewOptions.Command cmd) {

        String str, val;
        switch (cmd) {
            case None:
                ViewOptions.keyType = null;
                ViewOptions.keyValue = null;
                ViewOptions.comp = 0;
                ViewOptions.reverse = false;
                resultLabel.setText(RESULT_TEXT_NONE);
                break;
            case Show:
                ViewOptions.keyType = null;
                ViewOptions.keyValue = null;
                ViewOptions.comp = 0;
                ViewOptions.reverse = false;
                resultLabel.setText(RESULT_TEXT_SHOW);
                break;
            case ShowSorted:
                ViewOptions.keyType = sortedKeyComboBox.getItemAt(sortedKeyComboBox.getSelectedIndex());
                ViewOptions.reverse = sortedReverseCheckBox.isSelected();
                str = ViewOptions.reverse ? RESULT_TEXT_SHOW_REVERSE_SORTED : RESULT_TEXT_SHOW_SORTED;
                resultLabel.setText(str + ViewOptions.keyType + ":");
                break;
            case Find:
                str = chooseKeyTypeComboBox.getItemAt(
                        chooseKeyTypeComboBox.getSelectedIndex());
                val = chooseKeyValueField.getText();
                if (chooseKeyDialogTargetRemove) {
                    ViewOptions.delKeyType = str;
                    ViewOptions.delKeyValue = val;
                    // do not change ViewOptions.what
                    return;
                }
                ViewOptions.keyType = str;
                ViewOptions.keyValue = val;
                ViewOptions.comp = chooseKeyCompComboBox.getSelectedIndex();
                str = chooseKeyCompComboBox.getItemAt(ViewOptions.comp);
                resultLabel.setText(RESULT_TEXT_FIND + ViewOptions.keyType + str + val + ":");
                break;
        }
        resultLabel.repaint();
        ViewOptions.what = cmd;
    }
    //--
    //-- View Commands:
    boolean viewLast(JDialog dlg) {
        switch (ViewOptions.what) {
            case None:
            default:
                return false;
            case Show:
                return viewShow(dlg);
            case ShowSorted:
                return viewShowSorted(dlg);
            case Find:
                return viewFind(dlg);
        }
    }

    void viewSetCursor(JDialog dlg, Cursor cur) {
        if (dlg == null) {
            setCursor(cur);
        }
        else {
            dlg.setCursor(cur);
        }
    }

    boolean viewShow(JDialog dlg) {
        boolean isError = false;
        String errorMessage = null;
        viewSetCursor(dlg, Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        {
            try {
                List<String> result = Commands.readFile();
                fillTable(viewTable, result);
            } catch (Error | Exception e) {
                isError = true;
                errorMessage = e.getMessage();
            }
        }
        viewSetCursor(dlg, Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        if (isError) {
            showError(errorMessage);
            setOptions(ViewOptions.Command.None);
        }
        return isError;
    }

    boolean viewShowSorted(JDialog dlg) {
        boolean isError = false;
        String errorMessage = null;
        viewSetCursor(dlg, Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        {
            try {
                clearTable(viewTable);
                List<String> result = Commands.readFile(ViewOptions.keyType, ViewOptions.reverse);
                fillTable(viewTable, result);
            } catch (Error | Exception e) {
                isError = true;
                errorMessage = e.getMessage();
            }
        }
        viewSetCursor(dlg, Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        if (isError) {
            showError(errorMessage);
            setOptions(ViewOptions.Command.None);
        }
        return isError;
    }

    boolean viewFind(JDialog dlg) {
        boolean isError = false;
        String errorMessage = null;
        viewSetCursor(dlg, Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        {
            try {
                clearTable(viewTable);
                if (chooseKeyDialogTargetRemove) {
                    Commands.deleteFile(ViewOptions.delKeyType, ViewOptions.delKeyValue);
                } else {
                    List<String> result = (ViewOptions.comp == 0)
                            ? Commands.findByKey(ViewOptions.keyType, ViewOptions.keyValue)
                            : Commands.findByKey(ViewOptions.keyType, ViewOptions.keyValue, ViewOptions.comp);
                    fillTable(viewTable, result);
                }
            } catch (Error | Exception e) {
                isError = true;
                errorMessage = e.getMessage();
            }
        }
        viewSetCursor(dlg, Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        if (isError) {
            showError(errorMessage);
            setOptions(ViewOptions.Command.None);
        }
        return isError;
    }

    boolean viewAdd(JDialog dlg) {
        // Add new book:
        boolean isError = false;
        String errorMessage = null;
        viewSetCursor(dlg, Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        {
            try {
                clearTable(viewTable);
                Product p = getProduct();
                Commands.appendFile(true, p);
            } catch (Error | Exception e) {
                isError = true;
                errorMessage = e.getMessage();
            }
        }
        viewSetCursor(dlg, Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        if (isError) {
            showError(errorMessage);
        }
        return isError;
    }

    //--
    /*---*/

    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        initComponents();
        // Code by hands:
        Commands.setFile(fileData);
        setLocationRelativeTo(null);
        statusBar.setFloatable(false);
        statusBarText.setText(STATUS_TEXT_DEFAULT);
        clearTable(viewTable);
        setOptions(ViewOptions.Command.None);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
   // @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pDialog = new javax.swing.JDialog();

        werehouseNumberLabel = new javax.swing.JLabel();
        werehouseNumberText = new javax.swing.JTextField();
        productCodeLabel = new javax.swing.JLabel();
        productCodeText = new javax.swing.JTextField();
        productNameLabel = new javax.swing.JLabel();
       productNameText = new javax.swing.JTextField();
        receiptDateLabel = new javax.swing.JLabel();
       receiptDateText = new javax.swing.JTextField();
        storageDaysLabel = new javax.swing.JLabel();
        storageDaysText = new javax.swing.JTextField();
        quantityLable = new javax.swing.JLabel();
        pSeparator = new javax.swing.JSeparator();
        quantityText = new javax.swing.JTextField();
        pricePerLabel = new javax.swing.JLabel();
       pricePerText = new javax.swing.JTextField();

        pOK = new javax.swing.JButton();
        pClose = new javax.swing.JButton();
        sortedDialog = new javax.swing.JDialog();
        sortedLabelTitle = new javax.swing.JLabel();
        sortedKeyComboBox = new javax.swing.JComboBox<>();
        sortedReverseCheckBox = new javax.swing.JCheckBox();
        jSeparator4 = new javax.swing.JSeparator();
        sortedButtonOK = new javax.swing.JButton();
        sortedButtonCancel = new javax.swing.JButton();
        chooseKeyDialog = new javax.swing.JDialog();
        chooseKeyLabelTitle = new javax.swing.JLabel();
        chooseKeyTypeLabel = new javax.swing.JLabel();
        chooseKeyTypeComboBox = new javax.swing.JComboBox<>();
        chooseKeyValueLabel = new javax.swing.JLabel();
        chooseKeyValueField = new javax.swing.JTextField();
        chooseKeyCompLabel = new javax.swing.JLabel();
        chooseKeyCompComboBox = new javax.swing.JComboBox<>();
        jSeparator5 = new javax.swing.JSeparator();
        chooseKeyOK = new javax.swing.JButton();
        chooseKeyCancel = new javax.swing.JButton();
        statusBar = new javax.swing.JToolBar();
        statusBarText = new javax.swing.JLabel();
        viewPane = new javax.swing.JScrollPane();
        viewTable = new javax.swing.JTable();
        resultLabel = new javax.swing.JLabel();
        mainMenuBar = new javax.swing.JMenuBar();
        menuFile = new javax.swing.JMenu();
        menuFileOpen = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        menuFileExit = new javax.swing.JMenuItem();
        menuCommand = new javax.swing.JMenu();
        menuCommandAddBook = new javax.swing.JMenuItem();
        menuCommandRemove = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        menuCommandShowBooks = new javax.swing.JMenuItem();
        menuCommandShowSorted = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        menuCommandFind = new javax.swing.JMenuItem();
        menuHelp = new javax.swing.JMenu();
        menuHelpAbout = new javax.swing.JMenuItem();

        pDialog.setTitle("Add new product");
        pDialog.setAlwaysOnTop(true);
        pDialog.setMinimumSize(new java.awt.Dimension(420, 470));
        pDialog.setModalityType(java.awt.Dialog.ModalityType.DOCUMENT_MODAL);

        werehouseNumberLabel.setText("Warehouse Number: ");

        productCodeLabel.setText("Product Code:");

        productNameLabel.setText("Product Name:");

        receiptDateLabel.setText("Receipt Date: ");

        storageDaysLabel.setText("Storage Days:");

        quantityLable.setText("Quantity:");


        pricePerLabel.setText("Price Per Unit:");
//
//        bookAnnoArea.setColumns(20);
//        bookAnnoArea.setRows(5);
//        bookAnnoScroll.setViewportView(bookAnnoArea);

        pOK.setMnemonic('d');
        pOK.setText("Add product");
        pOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bookOKActionPerformed(evt);
            }
        });

        pClose.setMnemonic('c');
        pClose.setText("Close");
        pClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bookCloseActionPerformed(evt);
            }
        });
        javax.swing.GroupLayout pDialogLayout = new javax.swing.GroupLayout(pDialog.getContentPane());
        pDialog.getContentPane().setLayout(pDialogLayout);
        pDialogLayout.setHorizontalGroup(
                pDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pDialogLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(pDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(pSeparator, javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(pDialogLayout.createSequentialGroup()
                                                .addGroup(pDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                        .addComponent(storageDaysLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(receiptDateLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(productNameLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(productCodeLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(werehouseNumberLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(pricePerLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(quantityLable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(pDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(werehouseNumberText)
                                                        .addComponent(productCodeText)
                                                        .addComponent(productNameText)
                                                        .addComponent(receiptDateText)
                                                        .addComponent(storageDaysText)
                                                        .addComponent(quantityText)
                                                        .addComponent(pricePerText, javax.swing.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pDialogLayout.createSequentialGroup()
                                                .addGap(0, 258, Short.MAX_VALUE)
                                                .addComponent(pOK)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(pClose)))
                                .addContainerGap())
        );
        pDialogLayout.setVerticalGroup(
                pDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pDialogLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(pDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(werehouseNumberLabel)
                                        .addComponent(werehouseNumberText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(productCodeLabel)
                                        .addComponent(productCodeText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(productNameLabel)
                                        .addComponent(productNameText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(receiptDateLabel)
                                        .addComponent(receiptDateText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(storageDaysLabel)
                                        .addComponent(storageDaysText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(quantityText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(quantityLable))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(pricePerText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(pricePerLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(pDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(pClose)
                                        .addComponent(pOK))
                                .addContainerGap())
        );


        sortedDialog.setTitle("Show sorted");
        sortedDialog.setAlwaysOnTop(true);
        sortedDialog.setMaximumSize(new java.awt.Dimension(340, 160));
        sortedDialog.setMinimumSize(new java.awt.Dimension(340, 160));
        sortedDialog.setModalityType(java.awt.Dialog.ModalityType.DOCUMENT_MODAL);
        sortedDialog.setPreferredSize(new java.awt.Dimension(340, 160));

        sortedLabelTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        sortedLabelTitle.setText("Choose a key:");

        sortedKeyComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Warehouse Number", "Product Code", "Receipt Date","Storage Date" }));

        sortedReverseCheckBox.setMnemonic('r');
        sortedReverseCheckBox.setText("Reverse");

        sortedButtonOK.setText("OK");
        sortedButtonOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sortedButtonOKActionPerformed(evt);
            }
        });

        sortedButtonCancel.setText("Cancel");
        sortedButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sortedButtonCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout sortedDialogLayout = new javax.swing.GroupLayout(sortedDialog.getContentPane());
        sortedDialog.getContentPane().setLayout(sortedDialogLayout);
        sortedDialogLayout.setHorizontalGroup(
                sortedDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(sortedDialogLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(sortedDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(sortedLabelTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sortedDialogLayout.createSequentialGroup()
                                                .addComponent(sortedButtonOK)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(sortedButtonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(jSeparator4)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sortedDialogLayout.createSequentialGroup()
                                                .addGap(16, 16, 16)
                                                .addComponent(sortedKeyComboBox, 0, 96, Short.MAX_VALUE)
                                                .addGap(106, 106, 106)
                                                .addComponent(sortedReverseCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                                                .addGap(18, 18, 18)))
                                .addContainerGap())
        );
        sortedDialogLayout.setVerticalGroup(
                sortedDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(sortedDialogLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(sortedLabelTitle)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(sortedDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(sortedKeyComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(sortedReverseCheckBox))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
                                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(sortedDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(sortedButtonCancel)
                                        .addComponent(sortedButtonOK))
                                .addGap(12, 12, 12))
        );

        chooseKeyDialog.setAlwaysOnTop(true);
        chooseKeyDialog.setMinimumSize(new java.awt.Dimension(320, 230));
        chooseKeyDialog.setModalityType(java.awt.Dialog.ModalityType.DOCUMENT_MODAL);

        chooseKeyLabelTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        chooseKeyLabelTitle.setText("Choose a key:");

        chooseKeyTypeLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        chooseKeyTypeLabel.setLabelFor(chooseKeyTypeComboBox);
        chooseKeyTypeLabel.setText("Key type:");

        chooseKeyTypeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Warehouse Number", "Product Code", "Receipt Date","Storage Date" }));

        chooseKeyValueLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        chooseKeyValueLabel.setLabelFor(chooseKeyValueField);
        chooseKeyValueLabel.setText("Key value:");

        chooseKeyValueField.setHorizontalAlignment(javax.swing.JTextField.LEFT);

        chooseKeyCompLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        chooseKeyCompLabel.setLabelFor(chooseKeyCompComboBox);
        chooseKeyCompLabel.setText("Comparison type:");

        chooseKeyCompComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "==", ">", "<" }));

        chooseKeyOK.setText("OK");
        chooseKeyOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chooseKeyOKActionPerformed(evt);
            }
        });

        chooseKeyCancel.setText("Cancel");
        chooseKeyCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chooseKeyCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout chooseKeyDialogLayout = new javax.swing.GroupLayout(chooseKeyDialog.getContentPane());
        chooseKeyDialog.getContentPane().setLayout(chooseKeyDialogLayout);
        chooseKeyDialogLayout.setHorizontalGroup(
                chooseKeyDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(chooseKeyDialogLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(chooseKeyDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(chooseKeyDialogLayout.createSequentialGroup()
                                                .addGroup(chooseKeyDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                        .addComponent(chooseKeyValueLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(chooseKeyCompLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)
                                                        .addComponent(chooseKeyTypeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                .addGap(12, 12, 12)
                                                .addGroup(chooseKeyDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(chooseKeyValueField)
                                                        .addComponent(chooseKeyCompComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(chooseKeyTypeComboBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, 126, Short.MAX_VALUE)))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, chooseKeyDialogLayout.createSequentialGroup()
                                                .addGap(0, 0, Short.MAX_VALUE)
                                                .addComponent(chooseKeyOK)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(chooseKeyCancel))
                                        .addComponent(jSeparator5)
                                        .addComponent(chooseKeyLabelTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );
        chooseKeyDialogLayout.setVerticalGroup(
                chooseKeyDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(chooseKeyDialogLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(chooseKeyLabelTitle)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(chooseKeyDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(chooseKeyTypeLabel)
                                        .addComponent(chooseKeyTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(chooseKeyDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(chooseKeyValueLabel)
                                        .addComponent(chooseKeyValueField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(chooseKeyDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(chooseKeyCompComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(chooseKeyCompLabel))
                                .addGap(13, 13, 13)
                                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                                .addGroup(chooseKeyDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(chooseKeyCancel)
                                        .addComponent(chooseKeyOK))
                                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("GUI Frame Application");
        setName("mainFrame"); // NOI18N
        setResizable(false);

        statusBar.setRollover(true);

        statusBarText.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusBarText.setText("...");
        statusBar.add(statusBarText);

        viewPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        viewPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        viewPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        viewTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        viewTable.setEnabled(false);
        viewTable.getTableHeader().setResizingAllowed(false);
        viewTable.getTableHeader().setReorderingAllowed(false);
        viewPane.setViewportView(viewTable);

        resultLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        resultLabel.setLabelFor(viewPane);
        resultLabel.setText("No results");
        resultLabel.setToolTipText("");
        resultLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        menuFile.setMnemonic('f');
        menuFile.setText("File");

        menuFileOpen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.ALT_DOWN_MASK));
        menuFileOpen.setMnemonic('o');
        menuFileOpen.setText("Open");
        menuFileOpen.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                menuFileOpenMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                menuFileOpenMouseExited(evt);
            }
        });
        menuFileOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuFileOpenActionPerformed(evt);
            }
        });
        menuFile.add(menuFileOpen);
        menuFile.add(jSeparator1);

        menuFileExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.ALT_DOWN_MASK));
        menuFileExit.setMnemonic('x');
        menuFileExit.setText("Exit");
        menuFileExit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                menuFileExitMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                menuFileExitMouseExited(evt);
            }
        });
        menuFileExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuFileExitActionPerformed(evt);
            }
        });
        menuFile.add(menuFileExit);

        mainMenuBar.add(menuFile);

        menuCommand.setMnemonic('c');
        menuCommand.setText("Command");

        menuCommandAddBook.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.ALT_DOWN_MASK));
        menuCommandAddBook.setMnemonic('a');
        menuCommandAddBook.setText("Add product");
        menuCommandAddBook.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                menuCommandAddBookMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                menuCommandAddBookMouseExited(evt);
            }
        });
        menuCommandAddBook.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuCommandAddBookActionPerformed(evt);
            }
        });
        menuCommand.add(menuCommandAddBook);

        menuCommandRemove.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.ALT_DOWN_MASK));
        menuCommandRemove.setMnemonic('r');
        menuCommandRemove.setText("Remove product");
        menuCommandRemove.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                menuCommandRemoveMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                menuCommandRemoveMouseExited(evt);
            }
        });
        menuCommandRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuCommandRemoveActionPerformed(evt);
            }
        });
        menuCommand.add(menuCommandRemove);
        menuCommand.add(jSeparator2);

        menuCommandShowBooks.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.ALT_DOWN_MASK));
        menuCommandShowBooks.setMnemonic('s');
        menuCommandShowBooks.setText("Show");
        menuCommandShowBooks.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                menuCommandShowBooksMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                menuCommandShowBooksMouseExited(evt);
            }
        });
        menuCommandShowBooks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuCommandShowBooksActionPerformed(evt);
            }
        });
        menuCommand.add(menuCommandShowBooks);

        menuCommandShowSorted.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.ALT_DOWN_MASK));
        menuCommandShowSorted.setMnemonic('h');
        menuCommandShowSorted.setText("Show sorted");
        menuCommandShowSorted.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                menuCommandShowSortedMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                menuCommandShowSortedMouseExited(evt);
            }
        });
        menuCommandShowSorted.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuCommandShowSortedActionPerformed(evt);
            }
        });
        menuCommand.add(menuCommandShowSorted);
        menuCommand.add(jSeparator3);

        menuCommandFind.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.ALT_DOWN_MASK));
        menuCommandFind.setMnemonic('f');
        menuCommandFind.setText("Find product");
        menuCommandFind.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                menuCommandFindMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                menuCommandFindMouseExited(evt);
            }
        });
        menuCommandFind.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuCommandFindActionPerformed(evt);
            }
        });
        menuCommand.add(menuCommandFind);

        mainMenuBar.add(menuCommand);

        menuHelp.setMnemonic('h');
        menuHelp.setText("Help");

        menuHelpAbout.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.ALT_DOWN_MASK));
        menuHelpAbout.setMnemonic('b');
        menuHelpAbout.setText("About");
        menuHelpAbout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                menuHelpAboutMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                menuHelpAboutMouseExited(evt);
            }
        });
        menuHelpAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuHelpAboutActionPerformed(evt);
            }
        });
        menuHelp.add(menuHelpAbout);

        mainMenuBar.add(menuHelp);

        setJMenuBar(mainMenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(viewPane, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
                                        .addComponent(statusBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(resultLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(resultLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(viewPane, javax.swing.GroupLayout.PREFERRED_SIZE, 317, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(statusBar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(3, 3, 3))
        );

        statusBar.getAccessibleContext().setAccessibleDescription("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void menuFileExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuFileExitActionPerformed
        setStatusTextDefault();
        System.exit(0);
    }//GEN-LAST:event_menuFileExitActionPerformed

    private void menuFileOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuFileOpenActionPerformed
        setStatusTextDefault();
        if (chooseFile()) {
            viewLast(null);
        }
    }//GEN-LAST:event_menuFileOpenActionPerformed

    private void menuFileOpenMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menuFileOpenMouseEntered
        statusBarText.setText(STATUS_TEXT_FILE_OPEN);
        statusBarText.repaint();
    }//GEN-LAST:event_menuFileOpenMouseEntered

    private void menuFileOpenMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menuFileOpenMouseExited
        setStatusTextDefault();
    }//GEN-LAST:event_menuFileOpenMouseExited

    private void menuFileExitMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menuFileExitMouseEntered
        statusBarText.setText(STATUS_TEXT_FILE_EXIT);
        statusBarText.repaint();
    }//GEN-LAST:event_menuFileExitMouseEntered

    private void menuFileExitMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menuFileExitMouseExited
        setStatusTextDefault();
    }//GEN-LAST:event_menuFileExitMouseExited

    private void menuHelpAboutMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menuHelpAboutMouseEntered
        statusBarText.setText(STATUS_TEXT_HELP_ABOUT);
        statusBarText.repaint();
    }//GEN-LAST:event_menuHelpAboutMouseEntered

    private void menuHelpAboutMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menuHelpAboutMouseExited
        setStatusTextDefault();
    }//GEN-LAST:event_menuHelpAboutMouseExited

    private void menuHelpAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuHelpAboutActionPerformed
        setStatusTextDefault();
        JOptionPane.showMessageDialog(this, ABOUT_TEXT, this.getTitle(),
                JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_menuHelpAboutActionPerformed

    private void menuCommandAddBookMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menuCommandAddBookMouseEntered
        statusBarText.setText(STATUS_TEXT_COMMAND_ADD);
        statusBarText.repaint();
    }//GEN-LAST:event_menuCommandAddBookMouseEntered

    private void menuCommandAddBookMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menuCommandAddBookMouseExited
        setStatusTextDefault();
    }//GEN-LAST:event_menuCommandAddBookMouseExited

    private void menuCommandAddBookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuCommandAddBookActionPerformed
        setStatusTextDefault();
        pDialog.setLocationRelativeTo(this);
        pDialog.setVisible(true);
    }//GEN-LAST:event_menuCommandAddBookActionPerformed

    private void bookOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bookOKActionPerformed

        boolean isError = viewAdd(pDialog);
        pDialog.setVisible(isError);
        if (!isError) {
            viewLast(null);
        }
    }//GEN-LAST:event_bookOKActionPerformed

    private void bookCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bookCloseActionPerformed
        pDialog.setVisible(false);
    }//GEN-LAST:event_bookCloseActionPerformed

    private void menuCommandShowBooksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuCommandShowBooksActionPerformed
        setStatusTextDefault();
        setOptions(ViewOptions.Command.Show);
        viewShow(null);
    }//GEN-LAST:event_menuCommandShowBooksActionPerformed

    private void menuCommandShowBooksMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menuCommandShowBooksMouseEntered
        statusBarText.setText(STATUS_TEXT_COMMAND_SHOW);
        statusBarText.repaint();
    }//GEN-LAST:event_menuCommandShowBooksMouseEntered

    private void menuCommandShowBooksMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menuCommandShowBooksMouseExited
        setStatusTextDefault();
    }//GEN-LAST:event_menuCommandShowBooksMouseExited

    private void menuCommandShowSortedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuCommandShowSortedActionPerformed
        setStatusTextDefault();
        sortedDialog.setLocationRelativeTo(this);
        sortedDialog.setVisible(true);
    }//GEN-LAST:event_menuCommandShowSortedActionPerformed

    private void menuCommandShowSortedMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menuCommandShowSortedMouseEntered
        statusBarText.setText(STATUS_TEXT_COMMAND_SHOW_SORTED);
        statusBarText.repaint();
    }//GEN-LAST:event_menuCommandShowSortedMouseEntered

    private void menuCommandShowSortedMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menuCommandShowSortedMouseExited
        setStatusTextDefault();
    }//GEN-LAST:event_menuCommandShowSortedMouseExited

    private void menuCommandRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuCommandRemoveActionPerformed
        setStatusTextDefault();
        chooseKeyDialogTargetRemove = true;
        chooseKeyDialog.setTitle("Remove Product");
        chooseKeyDialog.setLocationRelativeTo(this);
        chooseKeyCompLabel.setEnabled(false);
        chooseKeyCompComboBox.setEnabled(false);
        chooseKeyDialog.setVisible(true);
    }//GEN-LAST:event_menuCommandRemoveActionPerformed

    private void menuCommandRemoveMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menuCommandRemoveMouseEntered
        statusBarText.setText(STATUS_TEXT_COMMAND_REMOVE);
        statusBarText.repaint();
    }//GEN-LAST:event_menuCommandRemoveMouseEntered

    private void menuCommandRemoveMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menuCommandRemoveMouseExited
        setStatusTextDefault();
    }//GEN-LAST:event_menuCommandRemoveMouseExited

    private void menuCommandFindActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuCommandFindActionPerformed
        setStatusTextDefault();
        chooseKeyDialogTargetRemove = false;
        chooseKeyDialog.setTitle("Find Product");
        chooseKeyDialog.setLocationRelativeTo(this);
        chooseKeyCompLabel.setEnabled(true);
        chooseKeyCompComboBox.setEnabled(true);
        chooseKeyDialog.setVisible(true);
    }//GEN-LAST:event_menuCommandFindActionPerformed

    private void menuCommandFindMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menuCommandFindMouseEntered
        statusBarText.setText(STATUS_TEXT_COMMAND_FIND);
        statusBarText.repaint();
    }//GEN-LAST:event_menuCommandFindMouseEntered

    private void menuCommandFindMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menuCommandFindMouseExited
        setStatusTextDefault();
    }//GEN-LAST:event_menuCommandFindMouseExited

    private void sortedButtonOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sortedButtonOKActionPerformed
        setOptions(ViewOptions.Command.ShowSorted);
        viewShowSorted(sortedDialog);
        sortedDialog.setVisible(false);
    }//GEN-LAST:event_sortedButtonOKActionPerformed

    private void sortedButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sortedButtonCancelActionPerformed
        sortedDialog.setVisible(false);
    }//GEN-LAST:event_sortedButtonCancelActionPerformed

    private void chooseKeyOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chooseKeyOKActionPerformed
        setOptions(ViewOptions.Command.Find);
        boolean isError = viewFind(chooseKeyDialog);
        if (chooseKeyDialogTargetRemove) {
            chooseKeyDialog.setVisible(isError);
            if (!isError) {
                viewLast(null);
            }
        }
        else {
            chooseKeyDialog.setVisible(false);
        }
    }//GEN-LAST:event_chooseKeyOKActionPerformed

    private void chooseKeyCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chooseKeyCancelActionPerformed
        chooseKeyDialog.setVisible(false);
    }//GEN-LAST:event_chooseKeyCancelActionPerformed

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
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    //private javax.swing.JTextArea bookAnnoArea;
    private javax.swing.JLabel werehouseNumberLabel;
    private javax.swing.JTextField werehouseNumberText;
    private javax.swing.JLabel productCodeLabel;
    private javax.swing.JTextField  productCodeText;
    private javax.swing.JButton pClose;
    private javax.swing.JDialog pDialog;
    private javax.swing.JLabel productNameLabel;
    private javax.swing.JTextField productNameText;
    private javax.swing.JLabel receiptDateLabel;
    private javax.swing.JTextField receiptDateText;
    private javax.swing.JButton pOK;
    private javax.swing.JLabel storageDaysLabel;
    private javax.swing.JTextField  storageDaysText;
    private javax.swing.JLabel quantityLable;
    private javax.swing.JTextField quantityText;
    private javax.swing.JSeparator pSeparator;
    private javax.swing.JLabel pricePerLabel;
    private javax.swing.JTextField pricePerText;





    private javax.swing.JButton chooseKeyCancel;
    private javax.swing.JComboBox<String> chooseKeyCompComboBox;
    private javax.swing.JLabel chooseKeyCompLabel;
    private javax.swing.JDialog chooseKeyDialog;
    private javax.swing.JLabel chooseKeyLabelTitle;
    private javax.swing.JButton chooseKeyOK;
    private javax.swing.JComboBox<String> chooseKeyTypeComboBox;
    private javax.swing.JLabel chooseKeyTypeLabel;
    private javax.swing.JTextField chooseKeyValueField;
    private javax.swing.JLabel chooseKeyValueLabel;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JMenuBar mainMenuBar;
    private javax.swing.JMenu menuCommand;
    private javax.swing.JMenuItem menuCommandAddBook;
    private javax.swing.JMenuItem menuCommandFind;
    private javax.swing.JMenuItem menuCommandRemove;
    private javax.swing.JMenuItem menuCommandShowBooks;
    private javax.swing.JMenuItem menuCommandShowSorted;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenuItem menuFileExit;
    private javax.swing.JMenuItem menuFileOpen;
    private javax.swing.JMenu menuHelp;
    private javax.swing.JMenuItem menuHelpAbout;
    private javax.swing.JLabel resultLabel;
    private javax.swing.JButton sortedButtonCancel;
    private javax.swing.JButton sortedButtonOK;
    private javax.swing.JDialog sortedDialog;
    private javax.swing.JComboBox<String> sortedKeyComboBox;
    private javax.swing.JLabel sortedLabelTitle;
    private javax.swing.JCheckBox sortedReverseCheckBox;
    private javax.swing.JToolBar statusBar;
    private javax.swing.JLabel statusBarText;
    private javax.swing.JScrollPane viewPane;
    private javax.swing.JTable viewTable;
    // End of variables declaration//GEN-END:variables
}

