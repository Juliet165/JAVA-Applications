package Task_23_10;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.zip.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
//
//class KeyComp implements Comparator<String> {
//    public int compare(String o1, String o2) {
//        // right order:
//        return o1.compareTo(o2);
//    }
//}
//
//class KeyCompReverse implements Comparator<String> {
//    public int compare(String o1, String o2) {
//        // reverse order:
//        return o2.compareTo(o1);
//    }
//}
//
//class KeyCompDate implements Comparator<String> {
//    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
//    public int compare(String o1, String o2) {
//        try {
//            Date date1 = dateFormat.parse(o1);
//            Date date2 = dateFormat.parse(o2);
//            return date1.compareTo(date2);
//        } catch (ParseException e) {
//            e.printStackTrace();
//            return 0;
//        }
//    }
//}
//
//class ReverseKeyCompDate implements Comparator<String> {
//    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
//
//    public int compare(String o1, String o2) {
//        try {
//            Date date1 = dateFormat.parse(o1);
//            Date date2 = dateFormat.parse(o2);
//            return date2.compareTo(date1);
//        } catch (ParseException e) {
//            e.printStackTrace();
//            return 0;
//        }
//    }
//}
class KeyComp implements Comparator<String> {
    @Override
    public int compare(String o1, String o2) {
        boolean isNumber1 = isNumber(o1);
        boolean isNumber2 = isNumber(o2);

        if (isNumber1 && isNumber2) {
            int num1 = Integer.parseInt(o1);
            int num2 = Integer.parseInt(o2);
            return Integer.compare(num1, num2);
        } else if (isDate(o1) && isDate(o2)) {
            try {
                Date date1 = parseDate(o1);
                Date date2 = parseDate(o2);
                return date1.compareTo(date2);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (isNumber1) {
            return -1;
        } else if (isNumber2) {
            return 1;
        }

        return o1.compareTo(o2);
    }

    private boolean isNumber(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isDate(String str) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(str);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private Date parseDate(String str) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
        return dateFormat.parse(str);
    }
}

class KeyCompReverse implements Comparator<String> {
    @Override
    public int compare(String o1, String o2) {
        boolean isNumber1 = isNumber(o1);
        boolean isNumber2 = isNumber(o2);

        if (isNumber1 && isNumber2) {
            int num1 = Integer.parseInt(o1);
            int num2 = Integer.parseInt(o2);
            return Integer.compare(num2, num1);
        } else if (isDate(o1) && isDate(o2)) {
            try {
                Date date1 = parseDate(o1);
                Date date2 = parseDate(o2);
                return date2.compareTo(date1);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (isNumber1) {
            return 1;
        } else if (isNumber2) {
            return -1;
        }

        return o2.compareTo(o1);
    }

    private boolean isNumber(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isDate(String str) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(str);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private Date parseDate(String str) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
        return dateFormat.parse(str);
    }
}


interface IndexBase {
    String[] getKeys(Comparator<String> comp);

    void put(String key, long value);

    boolean contains(String key);

    Long[] get(String key);
}

class IndexOne2One implements Serializable, IndexBase {
    // Unique keys
    // class release version:
    private static final long serialVersionUID = 1L;

    private TreeMap<String, Long> map;

    public IndexOne2One() {
        map = new TreeMap<String, Long>();
    }

    public String[] getKeys(Comparator<String> comp) {
        String[] result = map.keySet().toArray(new String[0]);
        Arrays.sort(result, comp);
        return result;
    }

    public void put(String key, long value) {
        map.put(key, new Long(value));
    }

    public boolean contains(String key) {
        return map.containsKey(key);
    }

    public Long[] get(String key) {
        long pos = map.get(key).longValue();
        return new Long[] { pos };
    }
}

class IndexOne2N implements Serializable, IndexBase {
    // Not unique keys
    // class release version:
    private static final long serialVersionUID = 1L;

    private TreeMap<String, Vector<Long>> map;

    public IndexOne2N() {
        map = new TreeMap<String, Vector<Long>>();
    }

    public String[] getKeys(Comparator<String> comp) {
        String[] result = map.keySet().toArray(new String[0]);
        Arrays.sort(result, comp);
        return result;
    }

    public void put(String key, long value) {
        Vector<Long> arr = map.get(key);
        if (arr == null) {
            arr = new Vector<Long>();
        }
        arr.add(new Long(value));
        map.put(key, arr);
    }

    public void put(String keys, // few keys in one string
                    String keyDel, // key delimiter
                    long value) {
        StringTokenizer st = new StringTokenizer(keys, keyDel);
        int num = st.countTokens();
        for (int i = 0; i < num; i++) {
            String key = st.nextToken();
            key = key.trim();
            put(key, value);
        }
    }

    public boolean contains(String key) {
        return map.containsKey(key);
    }

    public Long[] get(String key) {
        return map.get(key).toArray(new Long[0]);
    }
}

public class Index implements Serializable, Closeable {
    // class release version:
    private static final long serialVersionUID = 1L;

    IndexOne2N warehouseNumbers;
    IndexOne2One productCodes;
    IndexOne2N receiptDates;
    IndexOne2N storageDates;

    public void test(Product product) throws KeyNotUniqueException {
        assert (product != null);
        if (productCodes.contains(product.getProductCode())) {
            throw new KeyNotUniqueException(product.getProductCode());
        }
    }

    public void put(Product product, long value) throws KeyNotUniqueException {
        test(product);
        productCodes.put(product.getProductCode(), value);
        warehouseNumbers.put(product.getWarehouseNumber(), value);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy");
        String dateStr = product.receiptDate.format(formatter);
        receiptDates.put(dateStr, value);
        storageDates.put(product.getStorageDate(), value);
    }

    public Index() {
        warehouseNumbers = new IndexOne2N();
        productCodes = new IndexOne2One();
        receiptDates = new IndexOne2N();
        storageDates = new IndexOne2N();
    }

    public static Index load(String name) throws IOException,
            ClassNotFoundException {
        Index obj = null;
        try {
            FileInputStream file = new FileInputStream(name);
            try (ZipInputStream zis = new ZipInputStream(file)) {
                ZipEntry zen = zis.getNextEntry();
                if (zen.getName().equals(Buffer.zipEntryName) == false) {
                    throw new IOException("Invalid block format");
                }
                try (ObjectInputStream ois = new ObjectInputStream(zis)) {
                    obj = (Index) ois.readObject();
                }
            }
        } catch (FileNotFoundException e) {
            obj = new Index();
        }
        if (obj != null) {
            obj.save(name);
        }
        return obj;
    }

    private transient String filename = null;

    public void save(String name) {
        filename = name;
    }

    public void saveAs(String name) throws IOException {
        FileOutputStream file = new FileOutputStream(name);
        try (ZipOutputStream zos = new ZipOutputStream(file)) {
            zos.putNextEntry(new ZipEntry(Buffer.zipEntryName));
            zos.setLevel(ZipOutputStream.DEFLATED);
            try (ObjectOutputStream oos = new ObjectOutputStream(zos)) {
                oos.writeObject(this);
                oos.flush();
                zos.closeEntry();
                zos.flush();
            }
        }
    }

    public void close() throws IOException {
        saveAs(filename);
    }
}
