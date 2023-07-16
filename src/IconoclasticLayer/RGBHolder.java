package IconoclasticLayer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;


public class RGBHolder {
	
	String imgpath;
	int height, width;
	
	//image for export
	BufferedImage img;

	double[][]redPixels;
	double[][]greenPixels;
	double[][]bluePixels;
	double[][]alphaPixels;
	
	//top left coordinates 
	int tlx;
	int tly;
		
	public RGBHolder()  {
		
		//an imageHolder can be empty
		this.height=0;
		this.width=0;
		
		//default reference system
		tlx=0;
		tly=0;
		
	}//end constructor
	
	//=== IMPORT METHODS =====================================================================
	
	//import image from another RGBHolder Object
	public void clone(RGBHolder ih){
		
		this.height=ih.getHeight();
		this.width=ih.getWidth();
		
		bluePixels= new double[height][width];
		greenPixels= new double[height][width];
		redPixels= new double[height][width];
		alphaPixels	= new double[height][width];
		
		
		redPixels = this.copyMatrix(ih.getRedMatrix());
		greenPixels = this.copyMatrix(ih.getGreenMatrix());
		bluePixels = this.copyMatrix(ih.getBlueMatrix());
		alphaPixels = this.copyMatrix(ih.getAlphaMatrix());
		

	}//end setImage
	
	//import a BufferedImage
	public void setBufferedImage(BufferedImage image){
		
		if(image ==null) {
			this.height=0;
			this.width=0;
		}else{
			
			height=image.getHeight();
			width=image.getWidth();
			
			bluePixels= new double[height][width];
			greenPixels= new double[height][width];
			redPixels= new double[height][width];
			alphaPixels	= new double[height][width];
			
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
	public void setImgFromVector(double[] array ){
		
		//linear format is R1,G1,B1, R2,G2,B2, R3..... 
		
		bluePixels= new double[height][width];
		greenPixels= new double[height][width];
		redPixels= new double[height][width];
		alphaPixels	= new double[height][width];
		
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
	
	public void subtract(RGBHolder x) {
		this.subtract(x, 0);
	}
	
	public void subtract (RGBHolder x, int threshold) {
		
		//get Image matrix
		for (int h=0; h<height;h++){
			for (int w=0; w<width;w++){
				if(redPixels[h][w]>threshold)	redPixels[h][w]=redPixels[h][w]-x.getRedMatrix()[h][w];
				if(greenPixels[h][w]>threshold)	greenPixels[h][w]=greenPixels[h][w]-x.getGreenMatrix()[h][w];
				if(bluePixels[h][w]>threshold)	bluePixels[h][w]=bluePixels[h][w]-x.getBlueMatrix()[h][w];
							
			}//end height			
		}// end width
		
	}
	
	public void add (RGBHolder x, int threshold) {
		
		//get Image matrix
		for (int h=0; h<height;h++){
			for (int w=0; w<width;w++){
				if(redPixels[h][w]<threshold)	redPixels[h][w]=redPixels[h][w]+x.getRedMatrix()[h][w];
				if(greenPixels[h][w]<threshold)	greenPixels[h][w]=greenPixels[h][w]+x.getGreenMatrix()[h][w];
				if(bluePixels[h][w]<threshold)	bluePixels[h][w]=bluePixels[h][w]+x.getBlueMatrix()[h][w];
							
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
				redPixels[j][i]= (redPixels[j][i]-avgR)/stddevR;	
				greenPixels[j][i]= (greenPixels[j][i]-avgG)/stddevG;
				bluePixels[j][i]= (bluePixels[j][i]-avgB)/stddevB;
			}//i
		}//j
		
	}//end standardize

	public double[] mean() {
			
			double[] rgbAVG = new double[3];	
			double sumR,sumG, sumB;
			
			sumR=0;
			sumG=0;
			sumB=0;
			
			double n = this.height*this.width;
			
			for (int h=0; h<this.height;h++){
				for (int w=0; w<this.width;w++){			
					sumR=sumR+redPixels[h][w];
					sumG=sumG+greenPixels[h][w];
					sumB=sumB+bluePixels[h][w];			
				}//end width
			}//end height 
			
		
			rgbAVG[0]=(sumR)/n;
			rgbAVG[1]=(sumG)/n;
			rgbAVG[2]=(sumB)/n;
			
			return rgbAVG;
			
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
       double [][] red = this.getRedMatrix();
       double [][] blue = this.getBlueMatrix();
       double [][] green = this.getGreenMatrix();


        // Iterate over each pixel and count the occurrences of each color
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
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

	
	// === IMG transform ========================================================================
	
	public void crop(double percent) {
			
			int newH = (int) Math.round(this.height*percent);
			int newW = (int) Math.round(this.width*percent);
			
			int dh = height-newH;
			int dw =width-newW;
			
			this.setTlx(Math.round(dw/2));
			this.setTly(Math.round(dh/2));
			
			
	
			double[][] newred;
			double[][] newgreen;
			double[][] newblue;
			double[][] newalpha;
			
			
			newblue		= new double[newH][newW];
			newgreen	= new double[newH][newW];
			newred		= new double[newH][newW];
			newalpha	= new double[newH][newW];
			
			
	
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
			
			this.setGreenMatrix(newgreen);
			this.setRedMatrix(newred);
			this.setBlueMatrix(newblue);
			this.setAlphaMatrix(newalpha);
	
			
			
		}
	
 	public RGBHolder resize (int newHeight, int newWidth) throws IOException{
		
		BufferedImage img = this.getBufferedImage();	
		Image newImage = img.getScaledInstance(newWidth, newHeight, Image.SCALE_DEFAULT);	 

	    // Create a buffered image with transparency
	    BufferedImage bimage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

	    // Draw the image on to the buffered image
	    Graphics2D bGr = bimage.createGraphics();
	    bGr.drawImage(newImage, 0, 0, null);
	    bGr.dispose();
	    
	    //return resized IMG
	    RGBHolder resizedIMG = new RGBHolder();
	    resizedIMG.setBufferedImage(bimage);		    
	    
	   return resizedIMG;
	  
	}
	
	
	//=== EXPORT METHODS ========================================================================
	
	public double[] getRGBArray(){
	 	//** exports 0-255 RGB array stating from top left corner R1,G1,B1, R2,G2,B2....
	
		int pixels=this.height * this.width;
		double [] lineararray = new double [pixels*3];
		
		int count=0;
	
		//linearize the matrix [ R1,G1,B1, R2,G2,B2, R3..... 
		for (int h=0; h<this.height;h++){
			for (int w=0; w<this.width;w++){			
				lineararray[count]=redPixels[h][w];
				lineararray[count+1]=greenPixels[h][w];
				lineararray[count+2]=bluePixels[h][w];
				count=count+3;
			}//end width
		}//end height
		
		return lineararray;

	}//end getlinearArray
	
	//export BufferedImage
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
	
	//export to file
	
	private BufferedImage save_BufferedImage() {
		
		BufferedImage imgBuf = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		
		for (int h=0; h<height;h++){
			for (int w=0; w<width;w++){
				int rgb = (((int)alphaPixels[h][w]<<24) | ((int)redPixels[h][w]) << 16 | ((int)greenPixels[h][w]) << 8 | ((int)bluePixels[h][w]));      
				imgBuf.setRGB(w, h, rgb);            
           }//end h
        }//end w
		
		this.img = imgBuf;

		return imgBuf;
		
	}
		
	public void saveImage(String filepath) throws IOException{
	
		if (this.img == null) this.save_BufferedImage();
		
		
        File file = new File(filepath);
        file.getParentFile().mkdirs();
	    ImageIO.write(this.img, "png", file);

	}//end write image
		
	// === OTHER get and set ===
	public void setRedMatrix(double [][] newMatrix){
		
		redPixels =new double[newMatrix.length][newMatrix[0].length];
		redPixels=this.copyMatrix(newMatrix);
		
		//if the new matrix is smaller overlaps the old one
		if(newMatrix.length>height) height=newMatrix.length;
		if(newMatrix[0].length>width) width=newMatrix[0].length;
	}
	
	public void setGreenMatrix(double [][] matrix){ 
		greenPixels =new double[matrix.length][matrix[0].length];
		
		greenPixels=this.copyMatrix(matrix);
		this.height=matrix.length;		
		this.width=matrix[0].length;
	}
	
	public void setBlueMatrix(double [][] matrix){ 
		bluePixels =new double[matrix.length][matrix[0].length];
		bluePixels=this.copyMatrix(matrix);
		if(matrix.length>height) height=matrix.length;
		if(matrix[0].length>width) width=matrix[0].length;
	}
	
	public void setAlphaMatrix(double [][] matrix){ 
		alphaPixels =new double[matrix.length][matrix[0].length];
		alphaPixels=this.copyMatrix(matrix);
		if(matrix.length>height) height=matrix.length;
		if(matrix[0].length>width) width=matrix[0].length;
	}
	
	public void setAlpha(int alpha){
		alphaPixels =new double[this.height][this.width];
		for(int h=0; h<this.height; h++){
			for(int w=0; w<this.width;w++){
				alphaPixels[h][w]=alpha;
			}//for h
		}//for w
		
	}//end setAlphaMax
		
	public double[][] getRedMatrix(){ return redPixels;}
	public double[][] getGreenMatrix(){ return greenPixels;}
	public double[][] getBlueMatrix(){ return bluePixels;}
	public double[][] getAlphaMatrix(){ return alphaPixels;}
	
	public int getHeight(){ return height;}
	public int getWidth(){ return width;}
	
	public void setHeight(int h){ this.height=h;}
	public void setWidth(int w){ this.width=w;}
	
	public int getTlx(){ return this.tlx;}
	public int getTly(){ return this.tly;}
	
	public int get_center_x(Boolean absolute){ 
		
		int offset = (absolute) ? this.tlx :  0; 
		
		return  offset + (int) ((float) width * 0.5);
		
		
		
	}
	public int get_center_y(Boolean absolute){
		
		int offset = (absolute) ? this.tly :  0; 
	
		return offset + (int) ((float) height * 0.5);
		
		
	}
	
	
	public void setTlx( int x){ this.tlx=x;}
	public void setTly(int y ){ this.tly=y;}
	
	
	//=== additional tools ========================================================================
	
	public void add_text(String text, int size, Color mycolor, int x, int y) {
		
		//input Img
		if (this.img == null) this.save_BufferedImage();

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
		
		g2d.drawImage(img, 0, 0, null);
		g2d.drawString(text, x, y);
		g2d.dispose();
        
		this.setBufferedImage(imgWithText);
	

	}//end write image
		
	private double[][] copyMatrix(double[][] matrix){
		
		double[][] output=new double[matrix.length][matrix[0].length];
		
		for(int i=0; i<matrix.length;i++){
			for(int j=0; j<matrix[0].length; j++) output[i][j]=matrix[i][j];
		}
		
		return output;
	}//end copyMatrix
		
	private double averageMatrix(double [][] matrix){
		
		double sum=0;
		
		for(int j=0; j<matrix.length;j++){
			for(int i=0;i<matrix[0].length;i++){
				
				sum=sum+matrix[j][i];
						
			}//i
		}//j
		
		int n=matrix.length*matrix[0].length;
		return sum/n;
		
	}//end average
	
	private double stdDevMatrix(double [][] matrix){
		
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
	
	
	


