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
	
	//measures matrix
	double [][] M; 
	//list of tiles coordinates according to the Matrix M
	double sortedTiles[][];
	
	public StatFilter()  {
		
		System.out.println("Version: 0.1");
			
	}//end constructor
	
	
	//IMPORT ========================================================================================
	
	public void setSource(String filepath) throws IOException {
		
		this.image = new RGBHolder();
		image.setImageFromFile(filepath);
		System.out.println("IMG: " + image.getHeight()+" x "+image.getWidth());
	}
	
	public void setImage(BufferedImage img) {
		
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
		this.sortedTiles = new double [n*n][3];
		
		int t = 0;
		
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
				
				this.sortedTiles[t][0] = r;
				this.sortedTiles[t][1] = c;
				this.sortedTiles[t][2] = 0;
				
				tempR=null;
				tempG=null;
				tempB=null;
				tempA=null;
				
				t++;
			}//end rows
		}//end columns
		
		
		

	}//end setResolution
	
	
	public BufferedImage showTiles() {

		int rows = tiles.length;
		int columns = tiles[0].length;
		
		// stitching all tiles together
		for (int t=0; t<sortedTiles.length; t++){
				
				//tile coordinate
				int r = (int) sortedTiles[t][0];
				int c = (int) sortedTiles[t][1];
				
				int x = tiles[r][c].get_center_x(false);
				int y = tiles[r][c].get_center_y(false);
				tiles[r][c].add_text(String.valueOf(t), 24, Color.RED,x,y);
				
		}//for each tile
		
		
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
	
	public void rank_tiles(String measure) throws IOException{
		
		//I matrix contains the quantity of Information for each section		
	    M = new double [tiles.length][tiles[0].length];
		
	   
		//Populate the I matrix 
		for (int r=0; r<tiles.length; r++){
			for (int c=0; c<tiles[0].length; c++){
				

				switch(measure) {
				  case "entropy":
					  M[r][c]=tiles[r][c].entropy();
					  break;
				  case "mean":
					  int[] avg = tiles[r][c].mean();
					  M[r][c]=  (avg[0]+avg[1]+avg[2])/3 ;
					  break;
				  case "std.dev":
					  M[r][c]=tiles[r][c].std_dev();
					  break;
				  default:
					  break;
				}//end switch
				
			}//for columns
		}//for rows
		
		this.sortedTiles= this.createRanks();

		
	} //end 
	
	//OPERATIONS  ====================================================================================

	
	public BufferedImage apply_stat(String operation) {
		
		//for each section
		for (int r=0; r<tiles.length; r++){
			for (int c=0; c<tiles[0].length; c++){
				
				switch (operation) {
				
				case "mean":
					int[] rgb = tiles[r][c].mean();
					tiles[r][c].setMatrix("red", rgb[0]);
					tiles[r][c].setMatrix("green", rgb[1]);
					tiles[r][c].setMatrix("blue", rgb[2]);

					break;
				case "std.dev":
					double value  = tiles[r][c].std_dev();
					tiles[r][c].setMatrix("red",value);
					tiles[r][c].setMatrix("green", value);
					tiles[r][c].setMatrix("blue", value);

				default:
					break;
				}
				
				
				int[] rgb  = tiles[r][c].mean();

				
			}//end columns
		}//end rows
		
		return this.exportImage();
		
	}//end getinputlayer
	
	
	
	public BufferedImage apply_RGB(String color) {
		
		switch (color) {
		
		case "red":
			image.setMatrix("blue", 255);
			image.setMatrix("green", 255);
			break;
		case "green":
			image.setMatrix("blue", 255);
			image.setMatrix("red", 255);
			break;
		case "blue":
			image.setMatrix("green", 255);
			image.setMatrix("red", 255);
			break;
		default:
			break;
		}
		
	    return image.getBufferedImage();
			
	}
			


	
	
	public void printRanks() {
		
		int n = sortedTiles.length;
		
		// stitching all tiles together
		for (int t=0; t<n; t++){
				
				//tile coordinate
				int r = (int) sortedTiles[t][0];
				int c = (int) sortedTiles[t][1];
				
				System.out.println(sortedTiles[t][2]+"     [" + r +"]"+ "[" + c +"]");
		}//for each tile

		
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
		double max=0;
		double min=0;
		

		double[][] RGBmatrix = new double [zmatrix.length][zmatrix[0].length];
		
		for(int j=0; j<RGBmatrix.length;j++){
			for(int i=0;i<RGBmatrix[0].length;i++){			
				RGBmatrix[j][i]=(int) ((zmatrix[j][i]/(max-min))*255);
			}//i
		}//j
		
		return RGBmatrix;
		
	}// end resizeToRGB
	

	//Tools ========================================================================================
	
	private double[][]  createRanks() throws IOException {

		
		//prepare list
		int i =0;
		int n = M.length * M[0].length;
		double[][] rankedList = new double [n][3]; //   (measure, row, column)
		
		//copy I matrix and coordinates
		for (int r=0; r<M.length; r++){
			for (int c=0; c<M[0].length; c++){	
				
			    rankedList[i][0] = r;
				rankedList[i][1] = c;
				rankedList[i][2] = M[r][c];
				i++;
				
			}//for columns
		}//for rows
		
		Arrays.sort(rankedList, Comparator.comparingDouble(row -> row[2]));

		return rankedList;
		
		}
	


}//end class
