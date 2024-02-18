package Task_02_10;

//7. Определить класс «Интервал» - хранит левую и правую границы интервала.
//Определить несколько конструкторов и методы: длина интервала, смещение интервала
//(влево, вправо), сжатие (растяжение) интервала на заданный коэффициент, сравнение
//двух интервалов, сумма, разность двух интервалов.

public class Interval {
	private double left;
	private double right;

	public Interval() {
		left = 0;
		right = 0;
	}

	public Interval(int n, int m) {
		assert (m > n);
		if (n > m) {
			throw new IllegalArgumentException();
		}
		left = n;
		right = m;
	}
	
	public double getLeft() {
		return this.left;
	}
	
	public double getRight() {
		return this.right;
	}

	public double getLength() {
		double res = this.getRight() - this.getLeft();
		return res;
	}
	
	public void shiftLeft(double offset) {
	    left -= offset;
	    right -= offset;
	}
	
	public void shiftRight(double offset) {
	    left += offset;
	    right += offset;
	}
	public void stretch(double factor) {
	    double center = (left + right) / 2.0;
	    double halfLength = (right - left) / 2.0;
	    double newHalfLength = halfLength * factor;
	    left = center - newHalfLength;
	    right = center + newHalfLength;
	}
	  
	public boolean equal(Interval other) {
	    return this.left == other.getLeft() && this.right == other.getRight();
	}
	  
	public Interval sum(Interval other) {
	    Interval result = new Interval();
	    result.set(this.left + other.getLeft(), this.right + other.getRight());
	    return result;
	}
	  
	public Interval difference(Interval other) {
	    Interval result = new Interval();
	    result.set(this.left - other.getRight(), this.right - other.getLeft());
	    return result;
	}
	
	public void set(double i, double j) {
		assert (j > i);
		if (i > j) {
			throw new IllegalArgumentException();
		}
		left = i;
		right = j;
	}
	
}
