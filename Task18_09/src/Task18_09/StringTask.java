package Task18_09;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.HashSet;

//15.Каждая входная строка представляет собой слова, разделенные
//одним или несколькими пробелами. Для каждой входной строки:
//вывести без повторения те слова, у которых первая и последняя буквы совпадают

public class StringTask {

	public static void main(String[] args) {
		Scanner in = new Scanner( System.in );
        while ( in.hasNextLine() ) {
            String s = in.nextLine();
            StringTokenizer str = new StringTokenizer(s, " ");
            
            HashSet<String> elements = new HashSet<String>();

    		while(str.hasMoreTokens()) {
    			elements.add(str.nextToken());
    		}
    		 for (String el : elements) {
    			 if (el.charAt(0) == el.charAt(el.length() - 1)) {
    				 System.out.print(el + " ");
    			 }
    		 }
    		
            System.out.println();
        }
        in.close();

	}

}
