package org.teknixco.learning;

// Static member variables belong to the class, and copies are not made. 
// Static methods are accessed by the class name. Static methods cannot refer to non-static (instance) variables/methods.
// But instance methods (non-static methods) can access static data. 

class Thing {
	public String name;
	public static String description;
	
	public void getName() {
		System.out.println(description + ": " + name);
	}
	
	public static void showInfo() {
		System.out.println("Hello");
	}
	
}

public class StaticVariablesMethods {
	public static void main(String[] args) {
		Thing thing1 = new Thing();
		Thing thing2 = new Thing();
		
		Thing.description = "I am a thing";
		Thing.showInfo();
		System.out.println(Thing.description);
		
		thing1.name = "Bob";
		thing2.name = "Sue";
		thing1.getName();
		thing2.getName();
				
	}
}
