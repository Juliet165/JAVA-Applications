package Task_16_10_st2;

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

    
    public static void main(String[] args) {

        try {
            
            TreeConnector con = new TreeConnector(new File("trees.dat"));
            con.write(createTrees());

            GardenTree[] trees = con.read();
            for (GardenTree tree : trees) {
                System.out.println(tree);
                tree.printCreationTime();
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
