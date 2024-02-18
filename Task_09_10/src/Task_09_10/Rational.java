package Task_09_10;

import java.util.Comparator;
import java.util.Iterator;
import java.util.*;

//8) Рациональная (несократимая) дробь представляется парой целых чисел (a,b),
//где a - числитель, b - знаменатель. Создать класс Rational для работы с рациональными
//дробями. Необходимо реализовать 4 арифметические операции: +-*/.
//Класс должен реализовать:
//-интерфейсы Comparable и Comparator с возможностью выбора одного из полей
//для сравнения
//-интерфейс Iterable - индексатор по всем полям объекта
//-метод для сохранения значений всех полей в строке текста (переопределить
//toString())
//-конструктор или метод для инициализации объекта из строки текста,
//соответствующий реализации метода toString()
//Создать консольное приложение, демонстрирующее использование класса. Создать
//небольшой массив объектов и напечатать отсортированными по выбранному полю.

public class Rational implements Comparable<Rational>, Comparator<Rational>, Iterable<Integer> {
    private int numerator;   
    private int denominator; 
    private String comparisonField = "value";

    public Rational(int numerator, int denominator) {
        if (denominator == 0) {
            throw new IllegalArgumentException("Знаменатель не может быть равен нулю");
        }

        int gcd = gcd(Math.abs(numerator), Math.abs(denominator));
        this.numerator = numerator / gcd;
        this.denominator = denominator / gcd;

        if (this.denominator < 0) {
            this.numerator = -this.numerator;
            this.denominator = -this.denominator;
        }
    }

    public void setComparisonField(String field) {
        if ("numerator".equals(field) || "denominator".equals(field)) {
            comparisonField = field;
        } else {
            comparisonField = "value";
        }
    }

    public String getComparisonField() {
        return comparisonField;
    }
    
    private int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    public Rational add(Rational other) {
        int newNumerator = this.numerator * other.denominator + other.numerator * this.denominator;
        int newDenominator = this.denominator * other.denominator;
        return new Rational(newNumerator, newDenominator);
    }

    public Rational subtract(Rational other) {
        int newNumerator = this.numerator * other.denominator - other.numerator * this.denominator;
        int newDenominator = this.denominator * other.denominator;
        return new Rational(newNumerator, newDenominator);
    }

    public Rational multiply(Rational other) {
        int newNumerator = this.numerator * other.numerator;
        int newDenominator = this.denominator * other.denominator;
        return new Rational(newNumerator, newDenominator);
    }

    public Rational divide(Rational other) {
        if (other.numerator == 0) {
            throw new ArithmeticException("Деление на ноль");
        }
        int newNumerator = this.numerator * other.denominator;
        int newDenominator = this.denominator * other.numerator;
        return new Rational(newNumerator, newDenominator);
    }

    @Override
    public int compareTo(Rational other) {
    	if ("numerator".equals(comparisonField)) {
            return Integer.compare(this.numerator, other.numerator);
        } else if ("denominator".equals(comparisonField)) {
            return Integer.compare(this.denominator, other.denominator);
        } else {
            double thisValue = (double) this.numerator / this.denominator;
            double otherValue = (double) other.numerator / other.denominator;
            return Double.compare(thisValue, otherValue);
        }
    }

    @Override
    public int compare(Rational r1, Rational r2) {
    	if ("numerator".equals(r1.comparisonField) && "numerator".equals(r2.comparisonField)) {
            return Integer.compare(r1.numerator, r2.numerator);
        } else if ("denominator".equals(r1.comparisonField) && "denominator".equals(r2.comparisonField)) {
            return Integer.compare(r1.denominator, r2.denominator);
        } else {
            double value1 = (double) r1.numerator / r1.denominator;
            double value2 = (double) r2.numerator / r2.denominator;
            return Double.compare(value1, value2);
        }
    }

    @Override
    public String toString() {
        return numerator + "/" + denominator;
    }

    public static Rational fromString(String s) {
        String[] parts = s.split("/");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Неправильный формат строки");
        }
        int numerator = Integer.parseInt(parts[0]);
        int denominator = Integer.parseInt(parts[1]);
        return new Rational(numerator, denominator);
    }

    @Override
    public java.util.Iterator<Integer> iterator() {
        return new java.util.Iterator<Integer>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < 2;
            }

            @Override
            public Integer next() {
                if (index == 0) {
                    index++;
                    return numerator;
                } else if (index == 1) {
                    index++;
                    return denominator;
                } else {
                    throw new java.util.NoSuchElementException();
                }
            }
        };
    }
}
