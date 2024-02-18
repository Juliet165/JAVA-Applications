package Task_16_10_st2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class TreeConnector {
    private File file;

    public TreeConnector(String filename) {
        this.file = new File(filename);
    }

    public TreeConnector(File file) {
        this.file = file;
    }

    public void write(GardenTree[] trees) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        try (ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeInt(trees.length);
            for (int i = 0; i < trees.length; i++) {
                oos.writeObject(trees[i]);
            }
            oos.flush();
        }
    }

    public GardenTree[] read() throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(file);
        try (ObjectInputStream oin = new ObjectInputStream(fis)) {
            int length = oin.readInt();
            GardenTree[] result = new GardenTree[length];
            for (int i = 0; i < length; i++) {
                result[i] = (GardenTree) oin.readObject();
            }
            return result;
        }
    }
}


