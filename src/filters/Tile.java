package filters;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.imageio.ImageIO;


public class Tile {
	
	String imgpath;
	int height, width;
	
	//image for export
	BufferedImage img;

	int[][]redPixels;
	int[][]greenPixels;
	int[][]bluePixels;
	int[][]alphaPixels;
	
	//top left coordinates 
	int tlx;
	int tly;
		
	public Tile()  {
		
		//an imageHolder can be empty
		this.height=0;
		this.width=0;
		
		//default reference system
		tlx=0;
		tly=0;
		
	}//end constructor
	
	//=== IMPORT METHODS =====================================================================
	
	//import image from another RGBHolder Object
	public void clone(Tile ih){
		
		this.height=ih.getHeight();
		this.width=ih.getWidth();
		
		bluePixels= new int[height][width];
		greenPixels= new int[height][width];
		redPixels= new int[height][width];
		alphaPixels	= new int[height][width];
		
		
		redPixels = this.copyMatrix(ih.getMatrix("red"));
		greenPixels = this.copyMatrix(ih.getMatrix("green"));
		bluePixels = this.copyMatrix(ih.getMatrix("blue"));
		alphaPixels = this.copyMatrix(ih.getMatrix("alpha"));
		

	}//end setImage
	
	//import a BufferedImage
	public void setBufferedImage(BufferedImage image){
		
		if(image ==null) {
			this.height=0;
			this.width=0;
		}else{
			
			height=image.getHeight();
			width=image.getWidth();
			
			bluePixels= new int[height][width];
			greenPixels= new int[height][width];
			redPixels= new int[height][width];
			alphaPixels	= new int[height][width];
			
			//get Image matrix
			for (int h=0; h<height;h++){
				for (int w=0; w<width;w++){
					redPixels[h][w]=this.getPixelColour(image,w, h).getRed();
					greenPixels[h][w]=this.getPixelColour(image,w, h).getGreen();
					bluePixels[h][w]=this.getPixelColour(image,w, h).getBlue();
					alphaPixels[h][w]=this.getPixelColour(image,w, h).getAlpha();			
				}//end height			
			}// end width
		}//end else	
	}//end setbufferedimage
	
	//import Image from file
	public void setImageFromFile(String imgpath) throws IOException{
		
		File myImg = new File(imgpath);
		BufferedImage image = ImageIO.read(myImg);
		
		this.setBufferedImage(image);
			
	} //end getImageFrom
	
	//import image from a linear RGB array
	public void setImgFromVector(int[] array ){
		
		//linear format is R1,G1,B1, R2,G2,B2, R3..... 
		
		bluePixels= new int[height][width];
		greenPixels= new int[height][width];
		redPixels= new int[height][width];
		alphaPixels	= new int[height][width];
		
		int count =0;
		
		for (int h=0; h<this.getHeight();h++){
			for (int w=0; w<this.getWidth();w++){
				redPixels[h][w]=array [count];
				greenPixels[h][w]=array[count+1];
				bluePixels[h][w]=array[count+2];
				alphaPixels[h][w]=255;
				count=count+3;
			}//end width
		}//end height
		
	}
	
	
	//=== OPERATIONS ========================================================================
	
	public void subtract(Tile x) {
		this.subtract(x, 0);
	}
	
	public void subtract (Tile x, int threshold) {
		
		//get Image matrix
		for (int h=0; h<height;h++){
			for (int w=0; w<width;w++){
				if(redPixels[h][w]>threshold)	redPixels[h][w]=redPixels[h][w]-x.getMatrix("red")[h][w];
				if(greenPixels[h][w]>threshold)	greenPixels[h][w]=greenPixels[h][w]-x.getMatrix("green")[h][w];
				if(bluePixels[h][w]>threshold)	bluePixels[h][w]=bluePixels[h][w]-x.getMatrix("blue")[h][w];
							
			}//end height			
		}// end width
		
	}
	
	public void add (Tile x, int threshold) {
		
		//get Image matrix
		for (int h=0; h<height;h++){
			for (int w=0; w<width;w++){
				if(redPixels[h][w]<threshold)	redPixels[h][w]=redPixels[h][w]+x.getMatrix("red")[h][w];
				if(greenPixels[h][w]<threshold)	greenPixels[h][w]=greenPixels[h][w]+x.getMatrix("green")[h][w];
				if(bluePixels[h][w]<threshold)	bluePixels[h][w]=bluePixels[h][w]+x.getMatrix("blue")[h][w];
							
			}//end height			
		}// end width
		
	}
		
	public void invert( ){
		
		
		//get Image matrix
		for (int h=0; h<height;h++){
			for (int w=0; w<width;w++){
				redPixels[h][w]=255-redPixels[h][w];
				greenPixels[h][w]=255-greenPixels[h][w];
				bluePixels[h][w]=255-bluePixels[h][w];
							
			}//end height			
		}// end width
		
		
	}
	
	public void standardise(){
		
		//calculate z variable
		double avgR = this.averageMatrix(redPixels);
		double stddevR = this.stdDevMatrix(redPixels);
		
		double avgG = this.averageMatrix(greenPixels);
		double stddevG = this.stdDevMatrix(greenPixels);
		
		double avgB = this.averageMatrix(bluePixels);
		double stddevB = this.stdDevMatrix(bluePixels);
		
		
		for(int j=0; j<height;j++){
			for(int i=0;i<width;i++){							
				redPixels[j][i]= (int) Math.round((redPixels[j][i]-avgR)/stddevR);	
				greenPixels[j][i]= (int) Math.round((greenPixels[j][i]-avgG)/stddevG);
				bluePixels[j][i]= (int) Math.round((bluePixels[j][i]-avgB)/stddevB);
			}//i
		}//j
		
	}//end standardize

	public void log() {
		
		
		for (int h=0; h<this.height;h++){
			for (int w=0; w<this.width;w++){			
				redPixels[h][w] = (int) Math.log(redPixels[h][w]);
				greenPixels[h][w] =(int) Math.log(greenPixels[h][w]);
				bluePixels[h][w]=(int) Math.log(bluePixels[h][w]);
			}//end width
		}//end height 
		
		
	}

	public void sqrt() {
		
		
		for (int h=0; h<this.height;h++){
			for (int w=0; w<this.width;w++){			
				redPixels[h][w] = (int) Math.sqrt(redPixels[h][w]);
				greenPixels[h][w] =(int) Math.sqrt(greenPixels[h][w]);
				bluePixels[h][w]=(int) Math.sqrt(bluePixels[h][w]);
			}//end width
		}//end height 
		
	}
		
	public int[] mean() {
			
			int[] rgbAVG = new int[3];	
			
			double sumR=0;
			double sumG=0;
			double sumB=0;
			
			double n = this.height*this.width;
			
			for (int h=0; h<this.height;h++){
				for (int w=0; w<this.width;w++){			
					sumR=sumR+redPixels[h][w];
					sumG=sumG+greenPixels[h][w];
					sumB=sumB+bluePixels[h][w];			
				}//end width
			}//end height 
			
		
			rgbAVG[0] = (int) Math.round((sumR)/n);
			rgbAVG[1] = (int) Math.round((sumG)/n);
			rgbAVG[2] = (int) Math.round((sumB)/n);
			
			return rgbAVG;
			
		}
    
	public double hue() {
		
		int[] rgbAVG = this.mean();
		
		float rp,gp,bp, max,min,hue;
		
		rp = rgbAVG[0]/255f;
		gp = rgbAVG[1]/255f;
		bp = rgbAVG[2]/255f;
		
		max = Math.max(Math.max(rp,gp),bp);
		min = Math.min(Math.min(rp,gp),bp);
		
		// each colour takes a 60 degree segment
		hue = 360 * (max -min)/6f + rp-gp/(max - min);
		
		
		return Math.floor(hue);
		
	}
	
	
	public double saturation(){
		
		int[] rgbAVG = this.mean();
		
		float rp,gp,bp, max,min,saturation;
		
		rp = rgbAVG[0]/255f;
		gp = rgbAVG[1]/255f;
		bp = rgbAVG[2]/255f;
		
		max = Math.max(Math.max(rp,gp),bp);
		min = Math.min(Math.min(rp,gp),bp);
		
		saturation = (max == 0) ? 0 : ((max - min) / max);
		
		saturation = saturation*100;
		
		return Math.floor(saturation);
		
		
	}
	
	
	public double brightness() {
		
		int[] rgbAVG = this.mean();
		
		float  max, brightness;

		
		max = Math.max(Math.max(rgbAVG[0],rgbAVG[1]),rgbAVG[2]);
		
		brightness = max/255f *100 ;
		
		
		return Math.floor(brightness);
		
		
	}
	
	public double std_dev(){
		
		double sigmar, sigmag, sigmab;
		sigmar=this.stdDevMatrix(redPixels);
		sigmag=this.stdDevMatrix(greenPixels);
		sigmab=this.stdDevMatrix(bluePixels);
		
		return sigmar+sigmag+sigmab;
	
	}//end getinfo
	
	public  double entropy() {
        // Create a map to count the frequency of each color
        Map<Color, Integer> colorCounts = new HashMap<>();
        int [][] red = this.getMatrix("red");
        int [][] blue = this.getMatrix("blue");
        int [][] green = this.getMatrix("green");


        // Iterate over each pixel and count the occurrences of each color
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width ; j++) {
                Color pixel = new Color((int) red[i][j], (int)green[i][j], (int)blue[i][j]);
                colorCounts.put(pixel, colorCounts.getOrDefault(pixel, 0) + 1);
            }
        }

        // Calculate the entropy
        double totalPixels = width * height;
        double entropy = 0.0;

        for (int count : colorCounts.values()) {
            double probability = count / totalPixels;
            entropy -= probability * Math.log(probability) / Math.log(2);
        }

        return entropy;
    }

	public void sobel() {
	
			
			int sx[][] = new int[][]
			{ 
				  {-1,0,1},
		          {-2,0,2},
		          {-1,0,1}
			};
			
			int sy[][] = new int[][]
			{ 
				  {-1,-2,-1},
		          {0,0,0},
		          {1,2,1}
		    };
		    
		    int gx,gy,G;
		    
		    gx = 0;
		    gy = 0;
		    G = 0;
		    
		    //iterate in the 3x3 tile
			for (int h=0; h<this.height;h++){
				for (int w=0; w<this.width;w++){			
					int pixel = (int) Math.floor((redPixels[h][w]+greenPixels[h][w] +bluePixels[h][w])/3) ;
					
				      gx = gx + pixel *sx[h][w];
				      gy = gy + pixel *sy[h][w];

				}//end width
			}//end height 
			
		     G = Math.min( (int) Math.sqrt( Math.pow((double) gx, 2) + Math.pow((double) gy, 2)), 255);

		     
			//set pixels equal to gradient
			for (int h=0; h<this.height;h++){
				for (int w=0; w<this.width;w++){			

				     redPixels[h][w] = G;
				     greenPixels[h][w] = G;
				     bluePixels[h][w] = G;
					
				}//end width
			}//end height 
			
		    
	}	   
		
	
	// === IMG transform ======================================================================
	
	public void crop(double percent) {
			
			int newH = (int) Math.round(this.height*percent);
			int newW = (int) Math.round(this.width*percent);
			
			int dh = height-newH;
			int dw =width-newW;
			
			this.setTlx(Math.round(dw/2));
			this.setTly(Math.round(dh/2));
			
			
	
			int[][] newred;
			int[][] newgreen;
			int[][] newblue;
			int[][] newalpha;
			
			
			newblue		= new int[newH][newW];
			newgreen	= new int[newH][newW];
			newred		= new int[newH][newW];
			newalpha	= new int[newH][newW];
			
			
	
			for(int j=tly; j<(height-dh/2);j++){
				for(int i=tlx;i<(width-dw/2);i++){							
					newred[j-dh/2][i-dw/2]= redPixels[j][i];	
					newgreen[j-dh/2][i-dw/2]= greenPixels[j][i];
					newblue[j-dh/2][i-dw/2]= bluePixels[j][i];
					newalpha[j-dh/2][i-dw/2]= alphaPixels[j][i];
	
				}//i
			}//j
			
			this.setHeight(newH);
			this.setWidth(newW);
			
			this.setMatrix("green", newgreen);
			this.setMatrix("red", newred);
			this.setMatrix("blue", newblue);
			this.setMatrix("alpha",newalpha);
	
			
			
		}
	
 	public Tile resize (int newHeight, int newWidth) throws IOException{
		
		BufferedImage img = this.getBufferedImage();	
		Image newImage = img.getScaledInstance(newWidth, newHeight, Image.SCALE_DEFAULT);	 

	    // Create a buffered image with transparency
	    BufferedImage bimage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

	    // Draw the image on to the buffered image
	    Graphics2D bGr = bimage.createGraphics();
	    bGr.drawImage(newImage, 0, 0, null);
	    bGr.dispose();
	    
	    //return resized IMG
	    Tile resizedIMG = new Tile();
	    resizedIMG.setBufferedImage(bimage);		    
	    
	   return resizedIMG;
	  
	}
	
 	public void drawSquare() {
 		
 		 for (int h=0; h<this.height;h++) {
 			 
 			redPixels[h][0] = 255;
 		    redPixels[h][this.width-1] = 255;
 		    
 		    greenPixels[h][0] = 0;
 		    greenPixels[h][this.width-1] = 0;
		    
	 		bluePixels[h][0] = 0;
	 		bluePixels[h][this.width-1] = 0;
 		 }
 		 
 		 
 		 for (int w=0; w<this.width;w++) {
 			 
 			redPixels[0][w] = 255;
 		    redPixels[this.height-1][w] = 255;
 		    
 		    greenPixels[0][w] = 0;
 		    greenPixels[this.height-1][w] = 0;
		    
	 		bluePixels[0][w] = 0;
	 		bluePixels[this.height-1][w] = 0;
 		 }
 		 
 		
 	}
	
	//=== EXPORT  ==============================================================================
	
	public int[] getPixels(){
	 	//** exports 0-255 RGB array stating from top left corner R1,G1,B1, R2,G2,B2....
	
		int pixels= this.height * this.width;
		int [] RGBarray = new int [pixels*3];
		
		int count=0;
	
		//linearize the matrix [ R1,G1,B1, R2,G2,B2, R3..... 
		for (int h=0; h<this.height;h++){
			for (int w=0; w<this.width;w++){			
				RGBarray[count]=redPixels[h][w];
				RGBarray[count+1]=greenPixels[h][w];
				RGBarray[count+2]=bluePixels[h][w];
				count=count+3;
			}//end width
		}//end height
		
		return RGBarray;

	}//end getlinearArray
	
	public Dictionary getStats() {
		
		   Dictionary stats = new Hashtable();
		   
		   double mean = (this.mean()[0]+this.mean()[1]+this.mean()[2])/3;
		   
           // put() method
		   stats.put("mean", mean);
		   stats.put("std.dev", this.std_dev());
		   stats.put("entropy", this.entropy());
		   stats.put("avg red", this.mean()[0]);
		   stats.put("avg green", this.mean()[1]);
		   stats.put("avg blue", this.mean()[2]);
		   stats.put("hue", this.hue());
		   stats.put("saturation", this.saturation());
		   stats.put("brightness", this.brightness());


           //print out Hashtable out
           //System.out.println(stats);
           
           
		return stats;
	}
	
	public BufferedImage getBufferedImage() {
		
		
		//prepare the buffered output image
		BufferedImage imgBuf = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);;
		
		for (int h=0; h<height;h++){
			for (int w=0; w<width;w++){
				int rgb = (((int)alphaPixels[h][w]<<24) | ((int)redPixels[h][w]) << 16 | ((int)greenPixels[h][w]) << 8 | ((int)bluePixels[h][w]));      
				imgBuf.setRGB(w, h, rgb);            
           }//end h
        }//end w
		
       return imgBuf;

	}//end write image
	
	public void savetoFile(String filepath, String format) throws IOException{
		
		
        File file = new File(filepath);
        file.getParentFile().mkdirs();
	    ImageIO.write(this.getBufferedImage(), format, file);

	}//end write image
		

	
	//=== GET/SET  ========================================================================

	public int[][] getMatrix(String color){
		
		switch(color) {
		  case "red":
			  return redPixels;		
		    
		  case "green":
			  return greenPixels;
			
		  case "blue":
			  return bluePixels;
			  
		  case "alpha":
			  return alphaPixels;
			  
		  default:
			  System.out.println("Invalid: set red, green, blue or alpha");
			  return null;
		
		}
				
	}
	
	public void setMatrix(String color, double constant){
				

		switch(color) {
		  case "red":
			   redPixels = new int [height][width];
				for(int i=0; i<redPixels.length;i++){
					for(int j=0; j<redPixels[0].length; j++) redPixels[i][j]=(int)constant;
				}
			  break;
		    
		  case "green":
			  greenPixels = new int [height][width];
				for(int i=0; i<greenPixels.length;i++){
					for(int j=0; j<greenPixels[0].length; j++) greenPixels[i][j]=(int)constant;
				}
			  break;
			
		  case "blue":
			  bluePixels = new int [height][width];
				for(int i=0; i<bluePixels.length;i++){
					for(int j=0; j<bluePixels[0].length; j++) bluePixels[i][j]=(int)constant;
				}
			  break;
			  
		  case "alpha":
			  alphaPixels = new int [height][width];
				for(int i=0; i<alphaPixels.length;i++){
					for(int j=0; j<alphaPixels[0].length; j++) alphaPixels[i][j]=(int)constant;
				}
			  break;
			  
		  default:
			  System.out.println("Invalid: set red, green, blue or alpha");
		
		}
	
		
	}
		
	public void setMatrix(String color, int [][] RGBmatrix) {
		
		this.setHeight(RGBmatrix.length);
		this.setWidth(RGBmatrix[0].length);
		
		switch(color) {
		  case "red":
			  redPixels=this.copyMatrix(RGBmatrix);
			  break;
		    
		  case "green":
			  greenPixels=this.copyMatrix(RGBmatrix);
			  break;
			
		  case "blue":
			  bluePixels=this.copyMatrix(RGBmatrix);
			  break;
			  
		  case "alpha":
			  alphaPixels=this.copyMatrix(RGBmatrix);
			  break;
			  
		  default:
			  System.out.println("Invalid: set red, green, blue or alpha");
		
		}
		
	
	
		
	}
	

	public int getHeight(){ return height;}
	
	public int getWidth(){ return width;}
	
	public void setHeight(int h){ this.height=h;}
	
	public void setWidth(int w){ this.width=w;}
	
	
	
	public int getTlx(){ return this.tlx;}
	
	public int getTly(){ return this.tly;}
	
	public void setTlx( int x){ this.tlx=x;}
	
	public void setTly(int y ){ this.tly=y;}
	
	
	public int get_center_x(Boolean absolute){ 
		
		int offset = (absolute) ? this.tlx :  0; 
		
		return  offset + (int) ((float) width * 0.5);
		
		
		
	}
	public int get_center_y(Boolean absolute){
		
		int offset = (absolute) ? this.tly :  0; 
	
		return offset + (int) ((float) height * 0.5);
		
		
	}
	
	
	//=== additional tools ========================================================================
	
	public void add_text(String text, int size, Color mycolor, int x, int y) {
		

		//output Img		
		BufferedImage imgWithText = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = imgWithText.createGraphics();

		// draw Text
		Font font = new Font("Arial", Font.BOLD, size);
		g2d.setFont(font);
		g2d.setColor(mycolor);
		
		Rectangle2D bounds;
		bounds=font.getStringBounds(text, g2d.getFontRenderContext());
		int stringWidth= (int) bounds.getWidth();
		
		//offset if exceeding margin
		if(x+stringWidth>=width) x= x-((x+stringWidth)-width);
		if(x<0) x= 0;
		
		g2d.drawImage(this.getBufferedImage(), 0, 0, null);
		g2d.drawString(text, x, y);
		g2d.dispose();
        
		this.setBufferedImage(imgWithText);
	

	}//end write image
		
	private int[][] copyMatrix(int[][] matrix){
		
		int[][] output=new int[matrix.length][matrix[0].length];
		
		for(int i=0; i<matrix.length;i++){
			for(int j=0; j<matrix[0].length; j++) output[i][j]=matrix[i][j];
		}
		
		return output;
	}//end copyMatrix
		
	private double averageMatrix(int [][] matrix){
		
		double sum=0;
		
		for(int j=0; j<matrix.length;j++){
			for(int i=0;i<matrix[0].length;i++){
				
				sum=sum+matrix[j][i];
						
			}//i
		}//j
		
		int n=matrix.length*matrix[0].length;
		return sum/n;
		
	}//end average
	
	private double stdDevMatrix(int [][] matrix){
		
		double avg= this.averageMatrix(matrix);
		double sum=0;
		
		for(int j=0; j<matrix.length;j++){
			for(int i=0;i<matrix[0].length;i++){
				
				sum=sum + Math.pow(matrix[j][i]-avg, 2);
						
			}//i
		}//j
		
		double n = matrix[0].length*matrix.length;
		
		return Math.sqrt(sum/n);
		
	
	}//end standard deviation
		
	private Color getPixelColour(BufferedImage image, int x, int y){
		
		Color colour = new Color(image.getRGB(x, y));
		return colour;
		
	}//end getpixelcolour
	
	
}//end class
	
	
	


