package OOP_usage;

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
	
   public static void main(String [] args ) {
	   
	   //To call a method or function, we use only method name, if we are not creating object of it, we can call a function multiple times
	   Method_1();
	   
	   //To call a Method with a parameter
	   MethodParam("Tom", 12);
	   MethodParam("Rick", 11);
	   MethodParam("Sam",12);
	   
	   
   }


}
