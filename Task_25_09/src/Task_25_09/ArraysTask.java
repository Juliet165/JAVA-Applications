package Task_25_09;

//12. Ввести с консоли n - размерность матрицы a[n][n]. Задать
//значения элементов матрицы в интервале значений от -n до n с
//помощью датчика случайных чисел. Преобразовать строки
//матрицы таким образом, чтобы элементы, равные нулю,
//располагались после всех остальных. Распечатать исходную
//матрицу и результат.

import java.util.Scanner;
import java.util.Random;
import java.util.Date;

public class ArraysTask {

	public static void main(String[] args) {
		System.out.print("Enter n: ");
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        in.close();
        if ( n <= 1 ) {
            System.err.println(
            	"Invalid n value, require n > 1");
            System.exit( 1 );
        }
        Random rnd = new Random( 
        	(new Date()).getTime() );
        int [][] arr = new int [n][n];
        System.out.println("Source values: ");
        for( int i = 0; i < n; i++ ) {
            for( int j = 0; j < n; j++ ) {
                arr[i][j] = 
                	rnd.nextInt() % ( n + 1 );
                System.out.print( 
                	arr[i][j] + " " );
            }
            System.out.println();
        }
        for (int i = 0; i < n; i++) {
        	int q = 0;
        	for (int j = 0; j < n-q; j++) {
        		if (arr[i][j] == 0) {
        			for (int k = j; k < n-1; k++) {
        				arr[i][k] = arr[i][k+1];
        			}
        			arr[i][n-1] = 0;
        			q++;
        			j--;
        		}
        	}
        }
        
        System.out.println("Final matrix: ");
        for( int i = 0; i < n; i++ ) {
            for( int j = 0; j < n; j++ ) {
                System.out.print(arr[i][j] + " " );
            }
            System.out.println();
        }

	}

}

