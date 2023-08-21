import java.awt.image.BufferedImage;
import java.io.IOException;

import filters.StatFilter;






public class Main {

	public static void main(String[] args) throws IOException {

		final String path= "C:\\path\\to\\image\\input";
		

		StatFilter filter= new StatFilter();
		
		filter.setSource(path);
		filter.divideImage(5);
	
			
		BufferedImage  img;

		img = filter.showTiles();
		
		filter.applyOperation("mean");
		
		System.out.println("end test");
		
		filter.savefile("C:\\path\\to\\image\\output", "png");


	}//end main


}//end class
