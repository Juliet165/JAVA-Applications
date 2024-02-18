package Task_09_10;

import java.util.Arrays;

public class Tests {

	public static void main(String[] args) {
		Rational[] rationals = {
	            new Rational(1, 2),
	            new Rational(3, 4),
	            new Rational(1, 3),
	            new Rational(2, 5),
	    };

		System.out.println("Исходный массив:");
        for (Rational r : rationals) {
            System.out.println(r);
        }

        Arrays.sort(rationals); 
        System.out.println("\nОтсортированный по значениям массив:");
        for (Rational r : rationals) {
            System.out.println(r);
        }
        
        for (Rational r : rationals) {
        	r.setComparisonField("denominator");
        }
        Arrays.sort(rationals); 
        System.out.println("\nОтсортированный по знаменателям массив:");
        for (Rational r : rationals) {
            System.out.println(r);
        }
        
        String rationalString = "5/6";
        Rational r = Rational.fromString(rationalString);
        System.out.println("\nДробь из строки: " + r);
        

	}

}
