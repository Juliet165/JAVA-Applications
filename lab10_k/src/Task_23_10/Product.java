package Task_23_10;

//2. Запись о товаре на складе имеет атрибуты: номер склада, код товара, наименование
//товара, дата поступления, срок хранения в днях, количество единиц товара, цена за единицу.
//Индексировать по: номеру склада, коду товара, дате поступления, сроку хранения

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.io.IOException;
import java.io.PrintStream;

public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    public String warehouseNumber;
    public String productCode;
    public String productName;
    LocalDate receiptDate;
    public String storageDays;
    private int quantity;
    private double pricePerUnit;
    public static final String P_warehouseNumber = "Warehouse Number";
    public static final String P_productCode = "Product Code";
    public static final String P_productName = "Product Name";
    public static final String P_receiptDate = "Receipt Date";
    public static final String P_storageDays = "Storage Days";
    public static final String P_quantity = "Quantity";
    public static final String P_pricePerUnit = "Price Per Unit";


    public String getProductCode() {
        return productCode;
    }

    public String getReceiptDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy");
        return receiptDate.format(formatter);
    }

    public String getStorageDate() {
        return storageDays;
    }

    public String getWarehouseNumber() {
        return warehouseNumber;
    }


    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy");
        String formattedDate = receiptDate.format(formatter);
        return new String( //P_warehouseNumber +
                 warehouseNumber + AREA_DEL+
               // P_productCode +
                productCode + AREA_DEL +
               // P_productName +
                         productName + AREA_DEL +
               // P_receiptDate +
                         formattedDate + AREA_DEL +
             //  P_storageDays +
                         storageDays +AREA_DEL +
               // P_quantity+
                         quantity + AREA_DEL +
                //P_pricePerUnit +
                         pricePerUnit);
    }


    public static boolean nextRead(Scanner fin, PrintStream out) {
        return nextRead(P_warehouseNumber, fin, out);
    }

    static boolean nextRead(final String prompt, Scanner fin, PrintStream out) {
        out.print(prompt);
        out.print(": ");
        return fin.hasNextLine();
    }



    public static Product read(Scanner fin, PrintStream out) throws IOException,
            NumberFormatException, ParseException {
        String str;
        Product product = new Product();
        product.warehouseNumber = fin.nextLine();

        if(!nextRead(P_productCode,fin,out))
        {
            return null;
        }
        product.productCode = fin.nextLine();

        if(!nextRead(P_productName,fin,out))
        {
            return null;
        }
        product.productName = fin.nextLine();

        if (!nextRead(P_receiptDate, fin, out)) {
            return null;
        }

        str = fin.nextLine();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy");
        LocalDate date = null;
        try {
            date = LocalDate.parse(str, formatter);
        } catch (DateTimeParseException e) {
            e.printStackTrace();
        }

        product.setReceiptDate(String.valueOf(date));

        if(!nextRead(P_storageDays,fin,out))
        {
            return null;
        }
        product.storageDays = fin.nextLine();

        if (!nextRead(P_quantity, fin, out)) {
            return null;
        }
        str = fin.nextLine();
        product.quantity =  Integer.parseInt(str);


        if (!nextRead( P_pricePerUnit, fin, out)) {
            return null;
        }
        str = fin.nextLine();
        product.pricePerUnit = Double.parseDouble(str);
        return product;

    }

    public Product() {
    }

    public Product(String strBook) {
        String [] rows = strBook.split(Product.AREA_DEL);
        if (rows.length != 7) {
            throw new IllegalArgumentException("Illegal source string for Book");
        }
        setWarehouseNumber(rows[0]);
        setProductCode(rows[1]);
        setProductName(rows[2]);
        setReceiptDate(rows[3]);
        setStorageDays(rows[4]);
        setQuantity(rows[5]);
        setPricePerUnit(rows[6]);
    }

    public static final String AREA_DEL = "\n";

    public void setWarehouseNumber(String warehouseNumber) {
        this.warehouseNumber = warehouseNumber;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setReceiptDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            throw new IllegalArgumentException("Illegal date");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy");
        try {
            this.receiptDate = LocalDate.parse(dateString, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Use dd.MM.yy");
        }
    }

    public void setStorageDays(String storageDays) {
        this.storageDays = storageDays;
    }

    public void setQuantity(String quantity) {
        this.quantity = Integer.parseInt(quantity);
    }

    public void setPricePerUnit(String pricePerUnit) {
        this.pricePerUnit = Double.parseDouble(pricePerUnit);
    }
}
