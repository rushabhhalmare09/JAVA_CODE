package OOP_usage;

import java.util.*;

public class function_1 {

//Methods are used to perform certain actions, and they are also known as functions.
//How to create a function without parameter (Parameter which we give while creating method. argument we give values to it)

	//PARAMETERLESS METHOD
	static void Method_1() {
       
		System.out.println("Method without Parameter called ... ");
    }
	
	//METHOD WITH PARAMETERS
	static void MethodParam(String name, int age) {
		System.out.println(" Method with parameter");
		System.out.println("Name: - "+ name+ ", " + "Age: - "+ age);
		
	}
	
	//Method_3 -- using return type as int and hence if donot use void so we need to use return 
	static int addition(int num1, int num2) {
		
        int sum;
        sum = num1 + num2;
        
        return sum;
		
	}
	
   public static void main(String [] args ) {
	   
	   // Method_1 -- To call a method or function, we use only method name, if we are not creating object of it, we can call a function multiple times
	   Method_1();
	   
	   // Method_2 -- To call a Method with a parameter
	   MethodParam("Tom", 12);
	   MethodParam("Rick", 11);
	   MethodParam("Sam",12);
	   
	   // Method_3 --
	   Scanner sc = new Scanner(System.in);
	   
	   System.out.print("Enter num1 : - ");
	   int num1 = sc.nextInt();

	   System.out.print("Enter num2 : - ");
	   int num2 = sc.nextInt();
	   
	   int summation = addition(num1, num2);
	   
	   System.out.println("Ths sum is : - " + summation);
	   
   }

}
