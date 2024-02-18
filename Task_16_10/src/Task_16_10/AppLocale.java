package Task_16_10;

import java.util.*;

public class AppLocale {
    private static final String strMsg = "messages";
    private static Locale loc = Locale.getDefault();
    private static ResourceBundle res = ResourceBundle.getBundle(AppLocale.strMsg, AppLocale.loc);

    static Locale get() {
        return AppLocale.loc;
    }

    static void set(Locale loc) {
        AppLocale.loc = loc;
        res = ResourceBundle.getBundle(AppLocale.strMsg, AppLocale.loc);
    }

    static ResourceBundle getBundle() {
        return AppLocale.res;
    }

    static String getString(String key) {
        return AppLocale.res.getString(key);
    }

    // Resource keys:

    public static final String tree = "tree";
    public static final String needs_transplant = "needs_transplant";
    public static final String does_not_need_transplant = "does_not_need_transplant";
    public static final String creation_time = "creation_time";
    public static final String the_trees = "the_trees";
    public static final String cherry_tree = "cherry_tree";
    public static final String apple_tree = "apple_tree";
    public static final String pear_tree = "pear_tree";
    public static final String plum_tree = "plum_tree";
    // Добавьте другие ключи, если необходимо, чтобы они соответствовали вашей программе.
}
