package org;



import ptolemy.actor.TypedIOPort;
import ptolemy.actor.lib.LimitedFiringSource;
import ptolemy.data.expr.Parameter;
import ptolemy.kernel.CompositeEntity;
import ptolemy.kernel.util.IllegalActionException;
import ptolemy.kernel.util.NameDuplicationException;

public class Gth extends LimitedFiringSource {
	
	public TypedIOPort input;
	
	static {
		System.load("C:/Users/Altec/source/repos/Projet3/x64/Debug/Projet3.dll");  // Load native library at runtime
		                             // hello.dll (Windows) or libhello.so (Unixes)
		}
	// Declare a native method grassmanTH() that receives 2 arguments (Filename and extension ) and returns a file
	private native void grassmanTH(String A, String B);
	//-Djava.library.path="C:\Users\Altec\source\repos\Projet3\x64\Debug"
	
	public Gth(CompositeEntity container, String name) throws NameDuplicationException, IllegalActionException {
		super(container, name);
		_firingCountLimit=1;
		// TODO Auto-generated constructor stub
		input = new TypedIOPort(this, "input", true, false);
		
	}
	public Parameter username;
	@Override
	
	
	public void fire() throws IllegalActionException {
		// TODO Auto-generated method stub
		   String a="C:/Users/Altec/source/repos/Projet3/x64/Debug/1000-12-1.Rii";
		   String b="C:/Users/Altec/source/repos/Projet3/x64/Debug/1000-12-1.sz";
	      try {
			new Gth(_container, "GTH").grassmanTH(a,b);
		} catch (NameDuplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  // invoke the native method
		
		
		super.fire();
	}

	
	
	/*static {
		System.loadLibrary("Projet3");  // Load native library at runtime
		                             // hello.dll (Windows) or libhello.so (Unixes)
		}


		// Declare a native method grassmanTH() that receives 2 arguments (Filename and extension ) and returns a file
		private native void grassmanTH(String A, String B);
		 // Test Driver
		   public static void main(String[] args) {
			   String a="C:/Users/Altec/source/repos/Projet3/x64/Debug/1000-12-1.Rii";
			   String b="C:/Users/Altec/source/repos/Projet3/x64/Debug/1000-12-1.sz";
		      new Gth().grassmanTH(a,b);  // invoke the native method
		   }*/
	}
