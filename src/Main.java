import java.awt.image.BufferedImage;
import java.io.IOException;

import filters.StatFilter;






public class Main {

	public static void main(String[] args) throws IOException {

		final String filepath= "C:\\path\\to\\image\\input";
		

		StatFilter f1= new StatFilter();
		f1.setSource(filepath);

		String [] operations = new String[]{"mean", "std.dev", "sobel", "sqrt", "entropy", "red", "green", "blue", ""};

		for (String operation: operations) {
		    System.out.println("Applying filer: "+operation);
		    f1.createTiles("5x5");
		    f1.applyOperation(operation);
		    f1.saveImage("test//"+operation+".jpg", "jpg");
		    f1.reset();

		} 



	}//end main


}//end class
