package Task_02_10;

public class TestInterval {

	public static void main(String[] args) {
		Interval i1 = new Interval(0, 10);
		Interval i2 = new Interval(5, 20);
		
	    System.out.println("First length : " + i1.getLength());
	    System.out.println("Expected length : 10");
	    System.out.println("");
	    
	    System.out.println("Second length : " + i2.getLength());
	    System.out.println("Expected length : 15");
	    System.out.println("");

	    Interval i3 = i1.sum(i2);
	    System.out.println("First + second length: " + i3.getLength());
	    System.out.println("Expected length : 25");
	    System.out.println("");
	    
	    System.out.println("Sum left : " + i3.getLeft());
	    System.out.println("Expected sum left : 5");
	    System.out.println("");
	    
	    System.out.println("Sum right : " + i3.getRight());
	    System.out.println("Expected sum right : 30");
	    System.out.println("");

	    Interval i4 = i1.difference(i2);
	    System.out.println("Difference left : " + i4.getLeft());
	    System.out.println("Expected difference left : -20");
	    System.out.println("");
	    
	    System.out.println("Difference right : " + i4.getRight());
	    System.out.println("Expected difference right : 5");
	    System.out.println("");

	    i1.shiftLeft(2.5);
	    System.out.println("ShiftLeft left : " + i1.getLeft());
	    System.out.println("Expected ShiftLeft left : -2.5");
	    System.out.println("");
	    
	    System.out.println("ShiftLeft right : " + i1.getRight());
	    System.out.println("Expected ShiftLeft right : 7.5");
	    System.out.println("");

	    i2.shiftRight(3.0);
	    System.out.println("ShiftRight left : " + i2.getLeft());
	    System.out.println("Expected ShiftRight left : 8");
	    System.out.println("");
	    
	    System.out.println("ShiftRight right : " + i2.getRight());
	    System.out.println("Expected ShiftRight right : 23");
	    System.out.println("");

	    i3.stretch(2.0);
	    System.out.println("Stretch left : " + i3.getLeft());
	    System.out.println("Expected stretch left : -7.5");
	    System.out.println("");
	    
	    System.out.println("Stretch right : " + i3.getRight());
	    System.out.println("Expected stretch right : 42.5");

	    //Interval i5 = new Interval(10, 5); 
	}

}
