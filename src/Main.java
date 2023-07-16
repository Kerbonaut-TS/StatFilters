import java.awt.image.BufferedImage;
import java.io.IOException;

import IconoclasticLayer.StatFilter;






public class Main {

	public static void main(String[] args) throws IOException {

		final String path= "C:\\Users\\Riccardo\\Desktop\\drone pics iceland\\DJI_0128.jpg";
		

		StatFilter filter= new StatFilter();
		
		filter.setSource(path);
		filter.divide_image(2);
		
		BufferedImage  img;

		img = filter.showTiles();
		
		System.out.println("end test");
		

	}//end main


}//end class
