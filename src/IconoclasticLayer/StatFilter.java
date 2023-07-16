 package IconoclasticLayer;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;



public class StatFilter {

	RGBHolder image;
	RGBHolder[][] tiles; //[rows], [columns]  c and r are the coordinates in the picture
	RGBHolder features[];
	
	//entropy matrix
	double [][] I; 
		
	public StatFilter()  {
		
		System.out.println("Version: 0.09");
			
	}//end constructor
	
	
	//IMPORT ========================================================================================
	
	public void set_source(String filepath) throws IOException {
		
		this.image = new RGBHolder();
		image.setImageFromFile(filepath);
		System.out.println("IMG: " + image.getHeight()+" x "+image.getWidth());
	}
	
	public void set_image(BufferedImage img) {
		
		this.image = new RGBHolder();
		image.setBufferedImage(img);
		
	}
	
	//TILES   ==============================================================================

	public void divide_image(int n){
		
		//height and width of sub matrixes
		int hs,ws;
				
		hs= (int) Math.floor(image.getHeight()/n);
		ws= (int) Math.floor(image.getWidth()/n);
		
		tiles = new RGBHolder [(int) n][(int) n];

		//for each  section  c=columns r=rows
		for (int r=0; r<n; r++){
			for (int c=0; c<n; c++){
				
				int [][] tempR = new int[hs][ws];
				int [][] tempG = new int[hs][ws];
				int [][] tempB = new int[hs][ws];
				int [][] tempA = new int[hs][ws];
				
				for (int h=0; h<hs; h++){
					for (int w=0; w<ws; w++){
						
						int offsetH = (r)*hs;
						int offsetW = (c)*ws;		
						
						tempR[h][w]= image.getMatrix("red")[offsetH+h][offsetW+w];
						tempG[h][w]= image.getMatrix("green")[offsetH+h][offsetW+w];
						tempB[h][w]= image.getMatrix("blue")[offsetH+h][offsetW+w];
						tempA[h][w]= image.getMatrix("alpha")[offsetH+h][offsetW+w];
						
					}	//end height
				}//end width
				
				tiles[r][c]=new RGBHolder();		
				tiles[r][c].setMatrix("red", tempR);
				tiles[r][c].setMatrix("green", tempG);
				tiles[r][c].setMatrix("blue", tempB);
				tiles[r][c].setMatrix("alpha", tempA);
				
				tiles[r][c].setTlx((c)*ws); //-1 because the matrix is indexed from 0
				tiles[r][c].setTly((r)*hs);
				
				
				tempR=null;
				tempG=null;
				tempB=null;
				tempA=null;
					
			}//end rows
		}//end columns
		
		
		

	}//end setResolution
	
	
	public BufferedImage showTiles() {

		int rows = tiles.length;
		int columns = tiles[0].length;
		
		int t = 0;
		
		
		// stitching all tiles together
		for (int r=0; r<rows; r++){
			for (int c=0; c<columns; c++){				
				
				int x = tiles[r][c].get_center_x(false);
				int y = tiles[r][c].get_center_y(false);
				tiles[r][c].add_text(String.valueOf(t), 24, Color.RED,x,y);
				t++;
				
			}//for columns
		}//for rows
		
		
		return this.exportImage();
		
	}//end ShowTiles
	
	
	public RGBHolder getTile (int x, int y) {
		
		return tiles[x][y];
		
	}
	
	
	public BufferedImage [] getTiles() {
		
		BufferedImage [] output;
		
		int rows = tiles.length;
		int columns = tiles[0].length;
		
		output = new BufferedImage[rows*columns];
		
		int t = 0;
		
		// stitching all tiles together
		for (int r=0; r<rows; r++){
			for (int c=0; c<columns; c++){
				
				output[t]= tiles[r][c].getBufferedImage();
				t++;
				
			}
		}
		
		return output;
		
	}
	
	public double[][] rank_tiles() throws IOException{
		
		//I matrix contains the quantity of Information for each section		
	    I= new double [tiles.length][tiles[0].length];
		
	   
		//Populate the I matrix 
		for (int r=0; r<tiles.length; r++){
			for (int c=0; c<tiles[0].length; c++){					
				I[r][c]=tiles[r][c].entropy();				
			}//for columns
		}//for rows
		
	    //sort all tiles by information and get the list	
		
		double sortedList [][]= this.create_ranks();

		
		return sortedList;
		
	} //end 
	
	//OPERATIONS  ====================================================================================

	
	public BufferedImage apply_mean() {
		
		//linear RGB array size: tiles(W) * tiles(H)  * 3 colors
		int size = (int) Math.pow(tiles.length, 2) *3; 
		
		int i = 0;
		
		//for each section
		for (int r=0; r<tiles.length; r++){
			for (int c=0; c<tiles[0].length; c++){
				
				int[] rgb  = tiles[r][c].mean();
				tiles[r][c].setMatrix("red", rgb[0]);
				tiles[r][c].setMatrix("green", rgb[1]);
				tiles[r][c].setMatrix("blue", rgb[2]);
				
			}//end columns
		}//end rows
		
		return this.exportImage();
		
	}//end getinputlayer
	
	
	public double[] getMaxValues() {
		
		//linear RGB array size: tiles(W) * tiles(H)  * 3 colors
		int size = (int) Math.pow(tiles.length, 2) *3; 
		double[] inputLayer = new double[size];
		
		int i = 0;
		
		//for each section
		for (int r=0; r<tiles.length; r++){
			for (int c=0; c<tiles[0].length; c++){
				
				
				inputLayer[i]=this.findMax(tiles[r][c].getMatrix("red"), null)[0];
				inputLayer[i+1]=this.findMax(tiles[r][c].getMatrix("green"), null)[0];
				inputLayer[i+2]=this.findMax(tiles[r][c].getMatrix("blue"), null)[0];
				
				i=i+3;
				
			}//end columns
		}//end rows
		
		return inputLayer;
		
	}//end getinputlayer
	
	//normalization
	public void localNormalisation() {
		
		//standardized values in each section

		for (int r=0; r<tiles.length; r++){
			for (int c=0; c<tiles[0].length; c++){			
				tiles[r][c].standardise();		
			}//end col
		}//end rows
		
	}//end localstd
	
	
	//IMAGE TRANSFORM  ====================================================================================
	private RGBHolder optimiseSection(RGBHolder section, int direction) {
		
		
		double info,infoL, infoS;
		double gradL, gradS;

		RGBHolder larger;
		RGBHolder smaller;
		
		larger=null;
		smaller=null;
		
		//speed of change
		int delta;
		delta =(int) Math.round(0.05*Math.min(section.getHeight(), section.getWidth()));
		
		if(delta%2!=0) delta=delta-1; //if odd
		
		gradL=0;
		gradS=0;
		
		
		//calculate direction gradient (if direction=0 calculate both) 
		info=section.entropy();
		if(direction>=0){
			larger=this.resizeSection(section, delta);
			infoL=larger.entropy();
			gradL=infoL-info;
		}
		if(direction<=0){
			smaller=this.resizeSection(section, (-1)*delta);
			infoS=smaller.entropy();
			gradS=infoS-info;
		}
		
		
		if(gradL>=gradS){
			//increase the section
			if(gradL>0){
				return this.optimiseSection(larger, 1);
				
			}else{
				//optimal (largest gradient is negative)
				return section;
			}
		}else {
			//shrink the section
			if(gradS>0){
				//System.out.println("shrinking..");
				return this.optimiseSection(smaller, -1);
			}else{
				//optimal (largest gradient is negative)
				return section;
			}

		}

		
	}//end optimise section
	
	private RGBHolder resizeSection(RGBHolder section, int delta){
		
		
		RGBHolder resSection = new RGBHolder();


		//calculate new coordinates
		int offsetH = Math.max(section.getTly()- (int) (delta/2),0);
		int offsetW = Math.max(section.getTlx()-(int) (delta/2),0);	
		
		int maxh = Math.min(section.getHeight()+delta, image.height-offsetH-1);
		int maxw = Math.min(section.getHeight()+delta, image.width-offsetW-1);
		
		
		int [][] tempR = new int[maxh][maxw];
		int [][] tempG = new int[maxh][maxw];
		int [][] tempB = new int[maxh][maxw];
		int [][] tempA = new int[maxh][maxw];
		
				
		for (int h=0; h<maxh; h++){
			for (int w=0; w<maxw; w++){
								
				tempR[h][w]= image.getMatrix("red")[offsetH+h][offsetW+w];
				tempG[h][w]= image.getMatrix("green")[offsetH+h][offsetW+w];
				tempB[h][w]= image.getMatrix("blue")[offsetH+h][offsetW+w];
				tempA[h][w]= image.getMatrix("alpha")[offsetH+h][offsetW+w];

			}	//end height
		}//end width
		
		resSection.setMatrix("red", tempR);
		resSection.setMatrix("green", tempG);
		resSection.setMatrix("blue", tempB);
		resSection.setMatrix("alpha", 255);
		
		resSection.setTly(offsetH);
		resSection.setTlx(offsetW);
		
		tempR=null;
		tempG=null;
		tempB=null;
		tempA=null;
		
		return resSection;
		
	}
	
	
	
	//debugging: used to display the standardised image (resize to 0-255 then stitch tiles together)
	public BufferedImage exportImage() {
		
		int rows = tiles.length;
		int columns = tiles[0].length;
				
		int subh= tiles[0][0].getHeight();
		int subw = tiles[0][0].getWidth();
		int offsetH=0;
		int offsetW=0;
		
		//final image
		int [][] redPixels = new int [subh*rows][subw*columns];
		int [][] greenPixels = new int [subh*rows][subw*columns];
		int [][] bluePixels = new int [subh*rows][subw*columns];
		

			
		// stitching all tiles together
		for (int r=0; r<rows; r++){
			for (int c=0; c<columns; c++){
				
				int [][] RMatrix=tiles[r][c].getMatrix("red");
				int [][] GMatrix=tiles[r][c].getMatrix("green");
				int [][] BMatrix=tiles[r][c].getMatrix("blue");
				
				for (int h=0; h<subh; h++){
					for (int w=0; w<subw; w++){
						
						//place section in the final image
						redPixels[h+offsetH][w+offsetW]= RMatrix [h][w];
						greenPixels[h+offsetH][w+offsetW]= GMatrix [h][w];
						bluePixels[h+offsetH][w+offsetW]= BMatrix [h][w];
												
						offsetH=subh*r;
						offsetW=subw*c;
				
					}//width
				}//height 
			}//columns
		}//rows
		
		RGBHolder imgout = new RGBHolder();
		
		imgout.setMatrix("red", redPixels);
		imgout.setMatrix("green", greenPixels);
		imgout.setMatrix("blue", bluePixels);
		imgout.setMatrix("alpha", 255);
		
		
		redPixels=null;
		greenPixels=null;
		bluePixels=null;
		
		return imgout.getBufferedImage();
	
	}
	
	private double [][] resizeToRGB(int[][] zmatrix){
		
			
		//resize z variable to 0-255
		double max=this.findMax(zmatrix,null)[0];
		double min=this.findMin(zmatrix);
		

		double[][] RGBmatrix = new double [zmatrix.length][zmatrix[0].length];
		
		for(int j=0; j<RGBmatrix.length;j++){
			for(int i=0;i<RGBmatrix[0].length;i++){			
				RGBmatrix[j][i]=(int) ((zmatrix[j][i]/(max-min))*255);
			}//i
		}//j
		
		return RGBmatrix;
		
	}// end resizeToRGB
	

	//Tools ========================================================================================
	
	private double[][]  create_ranks() throws IOException {

		
		//prepare list
		int i =0;
		int n = I.length * I[0].length;
		double[][] rankedList = new double [n][3]; //   (measure, row, column)
		
		//copy I matrix and coordinates
		for (int r=0; r<I.length; r++){
			for (int c=0; c<I[0].length; c++){	
				
				rankedList[i][0] = I[r][c];
			    rankedList[i][1] = r;
				rankedList[i][2] = c;
				i++;
				
			}//for columns
		}//for rows
		
		Arrays.sort(rankedList, Comparator.comparingDouble(row -> row[0]));

		return rankedList;
		
		}
	
	private double[] findMax(int [][] matrix, int excludedElements[][]){
		
		//returns [maxValue,r,c] ... row/column  where the max value was found in the original matrix
		// excluded elements is a list of pairs of coordinates that 
		double [] max = new double [3];
		
		//initialise
		max[0]=0;
		max[1]=-1;
		max[2]=-1;
		
		
		//for each element in the matrix
		for(int r=0; r<matrix.length;r++){
			for(int c=0;c<matrix[0].length;c++){			
				
				//found new max
				if(matrix[r][c]>=max[0]) {
					
					boolean excluded=false;
					
					//check if any exclusion is defined
					if(excludedElements!=null) {
						
						//check if coordinates are excluded
						for(int i=0; i< excludedElements.length; i++) {
							if( excludedElements[i][0]==r && excludedElements[i][1]==c ) excluded=true; 	
						}
					}// end if exclusion
					
					if(!excluded) {
						max[0]=matrix[r][c];
						max[1]=r;
						max[2]=c;
					}//end if
					
				}//if found new max
			}//for columns
		}//for rows
		
		return max;
	}//find max end
	
	private double findMin(int [][] matrix){
		//used in resizing to 0-255
		
		double min=matrix[0][0];
		
		//calculate z variable
		for(int j=0; j<matrix.length;j++){
			for(int i=0;i<matrix[0].length;i++){			
				if(matrix[j][i]<min) min=matrix[j][i];
			}//i
		}//j
		
		return min;
	}//find max end


}//end class
