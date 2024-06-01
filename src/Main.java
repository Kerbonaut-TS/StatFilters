import java.io.IOException;
import com.babelcoding.StatFilters.StatFilter;

public class Main {

	public static void main(String[] args) throws IOException {

		final String filepath= "C:\\path\\to\\image\\input";
		

		StatFilter f1= new StatFilter();
		f1.setSource(filepath);
		System.out.println(f1.getTile(0).getStats());

		String [] operations = new String[]{"mean", "std.dev", "sobel", "sqrt", "entropy", "red", "green", "blue"};

		for (String operation: operations) {
		    System.out.println("Applying filer: "+operation);
		    f1.subdivide("5x5");
		    f1.apply(operation);
		    f1.saveImage("test//"+operation+".jpg", "jpg");
		    f1.reset();

		} 



	}//end main


}//end class
