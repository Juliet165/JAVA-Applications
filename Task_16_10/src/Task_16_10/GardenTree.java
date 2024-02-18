package Task_16_10;

import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.text.DateFormat;
import java.util.Locale;
import java.util.ResourceBundle;

//13. Создать базовый класс Садовое дерево и производные классы Яблоня, Вишня, Груша и
//другие. С помощью конструктора автоматически установить номер каждого дерева. Принять
//решение о пересадке каждого дерева в зависимости от возраста и плодоношения.

class GardenTree implements Serializable {
	private static final long serialVersionUID = -1234567890123456789L;

    private static int treeCount = 0;

    private int treeNumber;
    private String type;
    private int age;
    private boolean isFruitBearing;

    public final Date creationTime = new Date();

    public GardenTree(String type, int age, boolean isFruitBearing) {
        this.type = type;
        this.age = age;
        this.isFruitBearing = isFruitBearing;
        this.treeNumber = ++treeCount;
    }

    public int getTreeNumber() {
        return treeNumber;
    }

    public String getType() {
        return type;
    }

    public int getAge() {
        return age;
    }

    public boolean isFruitBearing() {
        return isFruitBearing;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void printCreationTime() {
    	Locale currentLocale = AppLocale.get();
        DateFormat dateFormatter = DateFormat.getDateTimeInstance(
                DateFormat.DEFAULT, DateFormat.DEFAULT, currentLocale);
        String dateOut = dateFormatter.format(creationTime);
        System.out.println(AppLocale.getString(AppLocale.creation_time) + ": " + dateOut);

    }
    public String toString() {
        String transplantInfo = needsTransplant() ? AppLocale.getString(AppLocale.needs_transplant)
                : AppLocale.getString(AppLocale.does_not_need_transplant);
        return new String(AppLocale.getString(AppLocale.tree) + " "
                + treeNumber + ": "
                + translateType(type) + ", " + transplantInfo + " ");
    }

    public String translateType(String type) {
    	 ResourceBundle bundle = ResourceBundle.getBundle("messages", AppLocale.get());
    	 String str = type.toLowerCase().replaceAll(" ", "");
    	 return bundle.getString(str);
    }

    public boolean needsTransplant() {
    	if (age >= 10 || !isFruitBearing) {
            return true;
        } else {
            return false;
        }
    }
}

class ObjectSerializer {
    public static void serializeObject(String fileName, Object object) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(fileName);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            objectOutputStream.writeObject(object);
            System.out.println("Object serialized and saved to " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Object deserializeObject(String fileName) {
        try (FileInputStream fileInputStream = new FileInputStream(fileName);
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            return objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}

