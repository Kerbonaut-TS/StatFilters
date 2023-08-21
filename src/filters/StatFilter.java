 package filters;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;



public class StatFilter {
	
	
	Tile image;
	Tile[][] tiles; 		//[rows], [columns]  c and r are the coordinates in the picture
	int[][] indexList; 		// the tile index displayed
		
	double [][] M; 			//measures matrix	
	int sortedTiles[];		//list of tiles coordinates ranked by values in Matrix M
	
	public StatFilter()  {
		
		System.out.println("Version: 0.1");
			
	}//end constructor
	
	
	//IMPORT ========================================================================================
	public void setSource(String filepath) throws IOException {
		
		this.image = new Tile();
		image.setImageFromFile(filepath);
		System.out.println("IMG: " + image.getHeight()+" x "+image.getWidth());
		
		
		
	}
	
	public void setImage(BufferedImage img) {
		
		this.image = new Tile();
		image.setBufferedImage(img);
		
	}
	
	//CREATE TILES & SORTING  ==============================================================================

	public void divideImage(int n){
		
		//height and width of sub matrixes
		int hs,ws;
				
		hs= (int) Math.floor(image.getHeight()/n);
		ws= (int) Math.floor(image.getWidth()/n);
		
		tiles = new Tile [(int) n][(int) n];
		this.sortedTiles = new int [n*n];
		
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
				
				tiles[r][c]=new Tile();		
				tiles[r][c].setMatrix("red", tempR);
				tiles[r][c].setMatrix("green", tempG);
				tiles[r][c].setMatrix("blue", tempB);
				tiles[r][c].setMatrix("alpha", tempA);
				
				tiles[r][c].setTlx((c)*ws); //-1 because the matrix is indexed from 0
				tiles[r][c].setTly((r)*hs);
							
				this.sortedTiles[t] = t;
				
				tempR=null;
				tempG=null;
				tempB=null;
				tempA=null;
				
				t++;
			}//end rows
		}//end columns
		
		
		

	}//end setResolution
	
	public BufferedImage showTiles() {
		
		return this.composeImage(true).getBufferedImage();
		
	}//end ShowTiles
	
	//SORT TILES  ==============================================================================
	
	public int[] sortTilesBy(String measure, Boolean ascending) throws IOException{
		
		//I matrix contains the quantity of Information for each section		
	    M = new double [tiles.length][tiles[0].length];
		
	   
		//Populate the I matrix 
		for (int r=0; r<tiles.length; r++){
			for (int c=0; c<tiles[0].length; c++){
				
				 int[] avg  = tiles[r][c].mean();

				switch(measure) {
				  case "entropy":
					  M[r][c]=tiles[r][c].entropy();
					  break;
				  case "mean":
					  M[r][c]=  (avg[0]+avg[1]+avg[2])/3 ;
					  break;
				  case "std.dev":
					  M[r][c]=tiles[r][c].std_dev();
					  break;
					  
				  case "red":
					  M[r][c]=  avg[0] ;
					  break;
					  
				  case "green":
					  M[r][c]=  avg[1] ;
					  break;
					  
				  case "blue":
					  M[r][c]=  avg[2] ;
					  break;
					  
				  default:
					  break;
				}//end switch
				
			}//for columns
		}//for rows
		
		this.sortedTiles= this.sortTiles(ascending);
		this.log(this.sortedTiles);
		return  this.sortedTiles;
		
	} //end 

	public BufferedImage [] getSortedTiles() {
		
		BufferedImage [] output;
		int H = tiles.length;
		int W = tiles[0].length;
		output = new BufferedImage[H*W];
		
		
		int n = sortedTiles.length;
		
		// stitching all tiles together
		for (int t=0; t<n; t++){
				
				//tile coordinate
				int r = (int) this.calculateRC(sortedTiles[t])[0];
				int c = (int) this.calculateRC(sortedTiles[t])[1];

				output[t] = tiles[r][c].getBufferedImage();
				
		}
		
		return output;
		
	}
	

	//APPLY OPERATIONS  ====================================================================================
	public void applyOperation(String operation, int tile) {
		
		 int r = this.calculateRC(tile)[0];
		 int c = this.calculateRC(tile)[1];
		
		 double value;
		 
		switch (operation) {
			
			case "mean":
				int[] rgb = tiles[r][c].mean();
				tiles[r][c].setMatrix("red", rgb[0]);
				tiles[r][c].setMatrix("green", rgb[1]);
				tiles[r][c].setMatrix("blue", rgb[2]);
				break;
				
			case "std.dev":
				value  = tiles[r][c].std_dev();
				tiles[r][c].setMatrix("red",value);
				tiles[r][c].setMatrix("green", value); 
				tiles[r][c].setMatrix("blue", value);	
				break;
				
			case "entropy":
				value  = tiles[r][c].entropy();
				tiles[r][c].setMatrix("red",value);
				tiles[r][c].setMatrix("green", value); 
				tiles[r][c].setMatrix("blue", value);	
				break;
				
			case "red":
				tiles[r][c].setMatrix("blue", 0);
				tiles[r][c].setMatrix("green", 0);
				break;
				
			case "green":
				tiles[r][c].setMatrix("blue", 0);
				tiles[r][c].setMatrix("red", 0);
				break;
				
			case "blue":
				tiles[r][c].setMatrix("green", 0);
				tiles[r][c].setMatrix("red", 0);
				break;
				
			default:
				break;
				
			}
				
				
	}//end getinputlayer
	
	public BufferedImage applyOperation(String operation, int[] tileList) {
		
		int n = tileList.length;
				
		//for each tile
		for (int t=0; t<n; t++) this.applyOperation(operation, tileList[t]);
		
		Tile composedImg = this.composeImage(false);		

		
		if(operation.contains("std.dev") || operation.contains("entropy")) {
		
			composedImg.setMatrix("red", this.normaliseRGB(composedImg.getMatrix("red")));
			composedImg.setMatrix("green", this.normaliseRGB(composedImg.getMatrix("green")));
			composedImg.setMatrix("blue", this.normaliseRGB(composedImg.getMatrix("blue")));
		}
		
		return composedImg.getBufferedImage();

		
	
	}
	
	public BufferedImage applyOperation(String operation) {
				
	    return this.applyOperation(operation, sortedTiles);				
	    
	
	}
	

	//IMAGE TRANSFORM (EXP) ====================================================================================
	private Tile optimiseSection(Tile section, int direction) {
		
		
		double info,infoL, infoS;
		double gradL, gradS;

		Tile larger;
		Tile smaller;
		
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
	
	private Tile resizeSection(Tile section, int delta){
		
		
		Tile resSection = new Tile();


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
	
	//normalization
	public void localNormalisation() {
		
		//standardized values in each section

		for (int r=0; r<tiles.length; r++){
			for (int c=0; c<tiles[0].length; c++){			
				tiles[r][c].standardise();		
			}//end col
		}//end rows
		
	}//end localstd
	
	//EXPORT  ====================================================================================
		
	public Tile getTile (int tileIndex) {
		
		
		int r = this.calculateRC(tileIndex)[0];
		int c =  this.calculateRC(tileIndex)[1];
		
		return tiles[r][c];
		
	}
			
	public void  savefile (String filepath, String format) throws IOException {
		
		this.composeImage(false).savetoFile(filepath, format);
		
	} 
	
	public Tile composeImage (Boolean showIndex) {
		/*** Buffered Image stitching all tiles together in one image ***/ 
		
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
		
		int i=0;
			
		// stitching all tiles together
		for (int r=0; r<rows; r++){
			for (int c=0; c<columns; c++){
				
				Tile render_tile = new Tile();
				render_tile.setBufferedImage(tiles[r][c].getBufferedImage());
				
				if  (showIndex) {
				
					int x = render_tile.get_center_x(false);
					int y = render_tile.get_center_y(false);
					render_tile.add_text(String.valueOf(i), 24, Color.RED,x,y);
					
				}
				
				
				int [][] RMatrix=render_tile.getMatrix("red");
				int [][] GMatrix=render_tile.getMatrix("green");
				int [][] BMatrix=render_tile.getMatrix("blue");
				
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
				
				i++;
			}//columns
		}//rows
		
		Tile imgout = new Tile();
		
		imgout.setMatrix("red", redPixels);
		imgout.setMatrix("green", greenPixels);
		imgout.setMatrix("blue", bluePixels);
		imgout.setMatrix("alpha", 255);
		
		
		redPixels=null;
		greenPixels=null;
		bluePixels=null;
		
		return imgout;
	
	}
	
	private int [][] normaliseRGB(int[][] zmatrix){
		
			
		//resize z variable to 0-255
		int max=this.getMaxValue(zmatrix);
		int min=this.getMinValue(zmatrix);
		
		double range = max-min ==0 ? 1 : max-min;
		

		int[][] RGBmatrix = new int [zmatrix.length][zmatrix[0].length];
		
		for(int j=0; j<RGBmatrix.length;j++){
			for(int i=0;i<RGBmatrix[0].length;i++){			
				RGBmatrix[j][i]=(int) (((zmatrix[j][i]-min)/range)*255);
			}//i
		}//j
		
		return RGBmatrix;
		
	}// end resizeToRGB
	

	//Tools ========================================================================================
	
	private int[] sortTiles(Boolean ascending) throws IOException {

		
		//prepare list
		int i =0;
		int n = M.length * M[0].length;
		double[][] rankedList = new double [n][3]; //   (measure, row, column)
		
		int m =  ( ascending == true) ? 1 : -1;
		
		
		//copy I matrix and coordinates
		for (int r=0; r<M.length; r++){
			for (int c=0; c<M[0].length; c++){	
				
			    rankedList[i][0] = r;
				rankedList[i][1] = c;
				rankedList[i][2] = M[r][c] * m;
				i++;
				
			}//for columns
		}//for rows
		
		Arrays.sort(rankedList, Comparator.comparingDouble(row -> row[2]));
		
		
		for (int t=0; t<n; t++) this.sortedTiles[t] =  this.getTileIndex( (int) rankedList[t][0], (int) rankedList[t][1] );

		return sortedTiles;
		
		}
	
    private static int getMaxValue(int[][] numbers) {
    	int maxValue = numbers[0][0];
        for (int j = 0; j < numbers.length; j++) {
            for (int i = 0; i < numbers[j].length; i++) {
                if (numbers[j][i] > maxValue) {
                    maxValue = numbers[j][i];
                }
            }
        }
        return maxValue;
    }

    private static int getMinValue(int[][] numbers) {
    	int minValue = numbers[0][0];
        
        for (int j = 0; j < numbers.length; j++) {
            for (int i = 0; i < numbers[j].length; i++) {
                if (numbers[j][i] < minValue ) {
                    minValue = numbers[j][i];
                }
            }
        }
        return minValue ;
    
    }

	private int[] calculateRC(int index) {
		
		int rows = this.tiles.length;
		int cols = this.tiles[0].length;
		
		int[] rc = new int[2];
		
		int r = (int) Math.floor( index/rows ) ;
		int c = index - (r*cols);
		
		
		rc[0] = r;
		rc[1] = c;
		
		return rc;
		
	}
	
	private int getTileIndex(int r, int c) {
		
		int cols = this.tiles[0].length;
				
		int index = r * cols + c;
		
		return index;
	}
	
	private void log(int[] list) {
		
		System.out.print("{");

		for (int i=0; i<list.length; i++)  System.out.print(list[i]+",");
		
		System.out.println("}");

	}

}//end class
