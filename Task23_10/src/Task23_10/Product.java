package Task23_10;

//2. Запись о товаре на складе имеет атрибуты: номер склада, код товара, наименование
//товара, дата поступления, срок хранения в днях, количество единиц товара, цена за единицу.
//Индексировать по: номеру склада, коду товара, дате поступления, сроку хранения

import java.io.Serializable;
import java.io.*;
import java.util.*;
import java.util.Date;
import java.text.DateFormat;  
import java.text.ParseException;
import java.text.SimpleDateFormat; 
import java.io.IOException;
import java.io.PrintStream; 

public class Product implements Serializable {
    // Class release version:
    private static final long serialVersionUID = 1L;

    public String warehouseNumber;
    public String productCode;
    public String productName;
    public Date receiptDate;
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


    public Product(String warehouseNumber, String productCode, String productName, Date receiptDate,
                   String storageDays, int quantity, double pricePerUnit) {
        this.warehouseNumber = warehouseNumber;
        this.productCode = productCode;
        this.productName = productName;
        this.receiptDate = receiptDate;
        this.storageDays = storageDays;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
    }

    public String getProductCode() {
        return productCode;
    }

    public String getReceiptDate() {
    	DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");  
    	String strDate = dateFormat.format(receiptDate);
        return strDate;
    }

    public String getStorageDate() {
        return storageDays;
    }
    
    public String getWarehouseNumber() {
		return warehouseNumber;
	}

    @Override
    public String toString() {
        return "Warehouse Number: " + warehouseNumber + "\n" +
               "Product Code: " + productCode + "\n" +
               "Product Name: " + productName + "\n" +
               "Receipt Date: " + receiptDate + "\n" +
               "Storage Days: " + storageDays + "\n" +
               "Quantity: " + quantity + "\n" +
               "Price Per Unit: " + pricePerUnit;
    }

    private static GregorianCalendar curCalendar = new GregorianCalendar();
    
	static Boolean validYear(int year) {
		return year > 0 && year <= curCalendar.get(Calendar.YEAR);
	}

	public static boolean nextRead(Scanner fin, PrintStream out) {
		return nextRead(P_warehouseNumber, fin, out);
	}

	static boolean nextRead(final String prompt, Scanner fin, PrintStream out) {
		out.print(prompt);
		out.print(": ");
		return fin.hasNextLine();
	}

	//public static final String authorDel = ",";

	public static Product read(Scanner fin, PrintStream out) throws IOException,
			NumberFormatException, ParseException {
		String str;
		Product product = new Product();
		product.warehouseNumber = fin.nextLine().trim();
		
		if (!nextRead(P_productCode, fin, out)) {
			return null;
		}
		product.productCode = fin.nextLine();
		if (!nextRead(P_productName, fin, out)) {
			return null;
		}
		product.productName = fin.nextLine();
		
		
		if (!nextRead(P_receiptDate, fin, out)) {
			return null;
		}
		String str3 = fin.nextLine();
		product.receiptDate = new SimpleDateFormat("yyyy-mm-dd").parse(str3);
		//Date date1=new SimpleDateFormat("yyyy-mm-dd").parse(sDate1);  
		
		if (!nextRead(P_storageDays, fin, out)) {
			return null;
		}
		product.storageDays = fin.nextLine();
		if (!nextRead(P_quantity, fin, out)) {
			return null;
		}
		String str2 = fin.nextLine();
		product.quantity = Integer.parseInt(str2);
		
		if (!nextRead(P_pricePerUnit, fin, out)) {
			return null;
		}
		str = fin.nextLine();
		product.pricePerUnit = Double.parseDouble(str);
		return product;
	}

	public Product() {
	}

	public static final String areaDel = "\n";

}
