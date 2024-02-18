package Task_16_10;

import java.util.*;
import java.io.*;

public class TestTree {

    public static GardenTree[] createTrees() {
        GardenTree[] trees = new GardenTree[4];

        trees[0] = new AppleTree(6, true);
        trees[1] = new CherryTree(4, false);
        trees[2] = new PearTree(5, true);
        trees[3] = new PlumTree(12, true);

        return trees;
    }

    static Locale createLocale(String[] args) {
        if (args.length == 2) {
            return new Locale(args[0], args[1]);
        } else if (args.length == 4) {
            return new Locale(args[2], args[3]);
        }
        return null;
    }

    static void setupConsole(String[] args) {
        if (args.length == 2) {
            if (args[0].equals("-encoding")) {
                try {
                    System.setOut(new PrintStream(System.out, true, args[1]));
                } catch (UnsupportedEncodingException ex) {
                    System.err.println("Unsupported encoding: " + args[1]);
                    System.exit(1);
                }
            }
        }
    }

    public static void main(String[] args) {

        try {
            setupConsole(args);
            Locale loc = createLocale(args);
            if (loc == null) {
                System.err.println("Invalid argument(s)\n"
                        + "Syntax: [-encoding ENCODING_ID] language country\n"
                        + "Example: -encoding Cp855 be BY");
                System.exit(1);
            }
            AppLocale.set(loc);

            TreeConnector con = new TreeConnector(new File("trees.dat"));
            con.write(createTrees());

            GardenTree[] trees = con.read();
            System.out.println(AppLocale.getString(AppLocale.the_trees) + ":");
            for (GardenTree tree : trees) {
                System.out.println(tree);
                tree.printCreationTime();
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
