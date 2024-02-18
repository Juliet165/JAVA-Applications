package Task23_10;

import java.io.*;
import java.text.ParseException;
import java.util.*;

public class Products {
    public static void main(String[] args) {
        try {
            if (args.length >= 1) {
                if (args[0].equals("-?") || args[0].equals("-h")) {
                    System.out.println(
                            "Syntax:\n" +
                                    "\t-a  [file [encoding]] - append data (*)\n" +
                                    "\t-az [file [encoding]] - append data (*), compress every record\n" +
                                    "\t-d                    - clear all data\n" +
                                    "\t-dk  {w|p|r|s} key    - clear data by key (warehouse, code, receipt date, storage days)\n" +
                                    "\t-p                    - print data unsorted\n" +
                                    "\t-ps  {w|p|r|s}        - print data sorted by warehouse/code/receipt date/storage days\n" +
                                    "\t-psr {w|p|r|s}        - print data reverse sorted by warehouse/code/receipt date/storage days\n" +
                                    "\t-f   {w|p|r|s} key    - find record by key\n" +
                                    "\t-fr  {w|p|r|s} key    - find records > key\n" +
                                    "\t-fl  {w|p|r|s} key    - find records < key\n" +
                                    "\t-?, -h                - command line syntax\n" +
                                    "   (*) if not specified, encoding for file is utf8\n"
                    );
                } else if (args[0].equals("-a")) {
                    // Append file with new object from System.in
                    // -a [file [encoding]]
                    appendFile(args, false);
                } else if (args[0].equals("-az")) {
                    // Append file with compressed new object from System.in
                    // -az [file [encoding]]
                    appendFile(args, true);
                } else if (args[0].equals("-p")) {
                    // Prints data file
                    printFile();
                } else if (args[0].equals("-ps")) {
                    Comparator<String> comp = getComparatorByArg(args);
                    if (comp != null && printFile(args, comp, false) == false) {
                        System.exit(1);
                    }
                } else if (args[0].equals("-psr")) {
                    Comparator<String> comp = getComparatorReverseByArg(args);
                    if (comp != null && printFile(args, comp, true) == false) {
                        System.exit(1);
                    }
                } else if (args[0].equals("-d")) {
                    // delete files
                    if (args.length != 1) {
                        System.err.println("Invalid number of arguments");
                        System.exit(1);
                    }
                    deleteFile();
                } else if (args[0].equals("-dk")) {
                    // Delete records by key
                    if (deleteFile(args) == false) {
                        System.exit(1);
                    }
                } else if (args[0].equals("-f")) {
                    // Find record(s) by key
                    if (findByKey(args) == false) {
                        System.exit(1);
                    }
                } else if (args[0].equals("-fr")) {
                    // Find record(s) by key large then key
                    if (findByKey(args, new KeyCompReverse()) == false) {
                        System.exit(1);
                    }
                } else if (args[0].equals("-fl")) {
                    // Find record(s) by key less then key
                    if (findByKey(args, new KeyComp()) == false) {
                        System.exit(1);
                    }
                } else {
                    System.err.println("Option is not realized: " + args[0]);
                    System.exit(1);
                }
            } else {
                System.err.println("Products: Nothing to do! Enter -? for options");
            }
        } catch (Exception e) {
            System.err.println("Run/time error: " + e);
            System.exit(1);
        }
        System.err.println("Products finished...");
        System.exit(0);
    }

    static final String filename = "Products.dat";
    static final String filenameBak = "Products.~dat";
    static final String idxname = "Products.idx";
    static final String idxnameBak = "Products.~idx";

    // input file encoding:
    private static String encoding = "utf8";
    private static PrintStream productOut = System.out;
    
    static boolean printFile(String[] args, Comparator<String> comp, boolean reverse)
            throws ClassNotFoundException, IOException {
        if (args.length != 2) {
            System.err.println("Invalid number of arguments");
            return false;
        }

        try (Index idx = Index.load(idxname);
             RandomAccessFile raf = new RandomAccessFile(filename, "rw")) {
            IndexBase pidx = indexByArg(args[1], idx);
            if (pidx == null) {
                return false;
            }

            String[] keys = pidx.getKeys(comp);
            for (String key : keys) {
                printRecord(raf, key, pidx);
            }
        }
        return true;
    }
    static Comparator<String> getComparatorByArg(String[] args) {
        if (args.length > 2 && args[2].equals("d")) {
            return new KeyCompDate();
        }
        return new KeyComp();
    }
    static Comparator<String> getComparatorReverseByArg(String[] args) {
        if (args.length > 2 && args[2].equals("d")) {
              return new ReverseKeyCompDate();
          }
          return new KeyCompReverse();
      }
    static Product readProd( Scanner fin ) throws IOException, NumberFormatException, ParseException {	
        return Product.nextRead( fin, productOut ) 
                ? Product.read( fin, productOut ) : null;
    }

    private static void deleteBackup() {
        new File( filenameBak ).delete();
        new File( idxnameBak ).delete();				
    }
	
    static void deleteFile() {
        deleteBackup();
        new File( filename ).delete();
        new File( idxname ).delete();
    }
	
    private static void backup() {
        deleteBackup();
        new File( filename ).renameTo( new File( filenameBak ));
        new File( idxname ).renameTo( new File( idxnameBak ));
    }

    static boolean deleteFile( String[] args ) 
            throws ClassNotFoundException, IOException, KeyNotUniqueException {
        //-dk  {i|a|n} key      - clear data by key
        if ( args.length != 3 ) {
            System.err.println( "Invalid number of arguments" );
            return false;
        }
        Long[] poss = null;
        try ( Index idx = Index.load( idxname )) {
            IndexBase pidx = indexByArg( args[1], idx );
            if ( pidx == null ) {
                return false;
            }
            if ( pidx.contains(args[2])== false ) {
                System.err.println( "Key not found: " + args[2] );
                return false;				
            }
            poss = pidx.get(args[2]);
        }
        backup();
        Arrays.sort( poss );
        try ( Index idx = Index.load( idxname ); 
              RandomAccessFile fileBak= new RandomAccessFile(filenameBak, "rw");
              RandomAccessFile file = new RandomAccessFile( filename, "rw")) {
            boolean[] wasZipped = new boolean[] {false};
            long pos;
            while (( pos = fileBak.getFilePointer()) < fileBak.length() ) {
                Product product = (Product) 
                Buffer.readObject( fileBak, pos, wasZipped );
                if ( Arrays.binarySearch(poss, pos) < 0 ) { // if not found in deleted
                    long ptr = Buffer.writeObject( file, product, wasZipped[0] );
                    idx.put( product, ptr );
                }
             }
         }
         return true;
    }
	
    static void appendFile( String[] args, Boolean zipped ) 
            throws FileNotFoundException, IOException, ClassNotFoundException,
                KeyNotUniqueException, NumberFormatException, ParseException {
        if ( args.length >= 2 ) {
            FileInputStream stdin = new FileInputStream( args[1] );
            System.setIn( stdin );
            if (args.length == 3) {
                encoding = args[2];
            }
            // hide output:
            productOut = new PrintStream("nul");
        }
        appendFile( zipped );
    }
	
    static void appendFile( Boolean zipped ) 
            throws FileNotFoundException, IOException, ClassNotFoundException, 
                KeyNotUniqueException, NumberFormatException, ParseException {
        Scanner fin = new Scanner( System.in, encoding );
        productOut.println( "Enter product data: " );
        try ( Index idx = Index.load( idxname ); 
              RandomAccessFile raf = new RandomAccessFile( filename, "rw" )) {
            for(;;) {
                Product product = readProd( fin );
                if ( product == null )
                    break;
                idx.test( product );
                long pos = Buffer.writeObject( raf, product, zipped );
                idx.put( product, pos );
            }
        }
    }
	
    private static void printRecord( RandomAccessFile raf, long pos ) 
            throws ClassNotFoundException, IOException {
        boolean[] wasZipped = new boolean[] {false};
        Product product = (Product) Buffer.readObject( raf, pos, wasZipped );
        if ( wasZipped[0] == true ) {
            System.out.print( " compressed" );
        }
        System.out.println( " record at position "+ pos + ": \n" + product );
        System.out.println();
    }
	
    private static void printRecord( RandomAccessFile raf, String key, 
            IndexBase pidx ) throws ClassNotFoundException, IOException {
        Long[] poss = pidx.get( key );
        for ( long pos : poss ) {
            System.out.print( "*** Key: " +  key + " points to" );
            printRecord( raf, pos );
        }		
    }
	
    static void printFile() 
            throws FileNotFoundException, IOException, ClassNotFoundException {
        long pos;
        int rec = 0;
        try ( RandomAccessFile raf = new RandomAccessFile( filename, "rw" )) {
            while (( pos = raf.getFilePointer()) < raf.length() ) {
                System.out.print( "#" + (++rec ));
                printRecord( raf, pos );
            }
            System.out.flush();
        }		
    }
	
    private static IndexBase indexByArg( String arg, Index idx ) {
        IndexBase pidx = null;
        if ( arg.equals("w")) {
            pidx = idx.warehouseNumbers;
        } 
        else if ( arg.equals("p")) {
            pidx = idx.productCodes;
        } 
        else if ( arg.equals("r")) {
            pidx = idx.receiptDates;
        } 
        else if ( arg.equals("s")) {
            pidx = idx.storageDates;
        } 
        else {
            System.err.println( "Invalid index specified: " + arg );
        }
        return pidx;
    }
	
    static boolean printFile( String[] args, boolean reverse ) 
            throws ClassNotFoundException, IOException {
        if ( args.length != 2 ) {
            System.err.println( "Invalid number of arguments" );
            return false;
        }
        try ( Index idx = Index.load( idxname ); 
              RandomAccessFile raf = new RandomAccessFile( filename, "rw" )) {
            IndexBase pidx = indexByArg( args[1], idx );
            if ( pidx == null ) {
                return false;
            }
            String[] keys = 
                pidx.getKeys( reverse ? new KeyCompReverse() : new KeyComp() );
            for ( String key : keys ) {
                printRecord( raf, key, pidx );
            }
        }
        return true;
    }
	
    static boolean findByKey( String[] args ) 
            throws ClassNotFoundException, IOException {
        if ( args.length != 3 ) {
            System.err.println( "Invalid number of arguments" );
            return false;
        }
        try ( Index idx = Index.load( idxname ); 
              RandomAccessFile raf = new RandomAccessFile( filename, "rw" )) {
            IndexBase pidx = indexByArg( args[1], idx );
            if ( pidx.contains(args[2])== false ) {
                System.err.println( "Key not found: " + args[2] );
                return false;				
            }
            printRecord( raf, args[2], pidx );
        }
        return true;	
    }
	
    static boolean findByKey( String[] args, Comparator<String> comp ) 
            throws ClassNotFoundException, IOException {
        if ( args.length != 3 ) {
            System.err.println( "Invalid number of arguments" );
            return false;
        }
        try ( Index idx = Index.load( idxname ); 
              RandomAccessFile raf = new RandomAccessFile( filename, "rw" )) {
            IndexBase pidx = indexByArg( args[1], idx );
            if ( pidx.contains(args[2])== false ) {
                System.err.println( "Key not found: " + args[2] );
                return false;				
            }
            String[] keys = pidx.getKeys( comp );
            for ( int i = 0; i < keys.length; i++ ) {
                String key = keys[i];
                if ( key.equals( args[2] )) {
                    break;
                }
                printRecord( raf, key, pidx );
            }
        }
        return true;
    }
    
}

