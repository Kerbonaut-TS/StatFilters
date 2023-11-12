 package filters;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Dictionary;



public class StatFilter {
	
	BufferedImage original;
	Tile image;
	Tile[][] tiles; 		//[rows], [columns]  c and r are the coordinates in the picture
	int[][] indexList; 		// the tile index displayed
		
	int sortedTiles[];		//list of tiles coordinates ranked by values in Matrix M	
	public StatFilter()  {
		
			
	}//end constructor
	
	
	//IMPORT ========================================================================================
	public void setSource(String filepath) throws IOException {
		
		this.image = new Tile();
		image.setImageFromFile(filepath);
		this.original = image.getBufferedImage();
		
		this.createTiles(1, 1);
			
		
	}
	
	public void setImage(BufferedImage img) {
		this.original = img;
		this.image = new Tile();
		image.setBufferedImage(img);
		this.createTiles(1, 1);

		
	}
	
	public BufferedImage reset() {
		
		
		this.image = new Tile();
		image.setBufferedImage(this.original);
		this.createTiles(1, 1);
		return this.showImage();
		
	}
	
	
	//CREATE TILES & SORTING  ==============================================================================

	public void createTiles(int rows, int columns ){
		
		//height and width of sub matrixes
		int hs,ws;
				
		hs= (int) Math.floor(image.getHeight()/rows);
		ws= (int) Math.floor(image.getWidth()/columns);
		
		tiles = new Tile [rows][columns];
		this.sortedTiles = new int [rows*columns];
		
		int t = 0;
		
		//for each  tile  c=columns r=rows
		for (int r=0; r<rows; r++){
			for (int c=0; c<columns; c++){
				
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
	}
	
	public void createTiles(String pixelWindow){
		
		
		String[] dimensions = pixelWindow.split("x");
		  try {
			  
			  
              int n1 = Integer.parseInt(dimensions[0]);
              int n2 = Integer.parseInt(dimensions[1]);
              
              if (n1 != n2){
            	  
            	  System.out.println("Error: Invalid size. Only square windows suported");
            	  
              } else {
            	  
            	  int rows = (int) Math.floor(image.getHeight()/n1);
            	  int columns = (int) Math.floor(image.getWidth()/n1);
           	  
            	  this.createTiles(rows,columns);
              }
              
              
          } catch (NumberFormatException e) {
        	  
              System.out.println("Error: Invalid input format");
          }
		  
		  

		
		

	}//end setResolution


	//SORT TILES  ==============================================================================
	
	public int[] sortTiles(String measure, Boolean ascending) throws IOException{
		
		int k =  ( ascending == true) ? 1 : -1;
		
		// storing tile index, measure in array
	    double[][] measures = new double [sortedTiles.length][2]; 
	    int avgs[]= null;
	   
		//create a list of indexes and measures
		for (int t = 0; t<sortedTiles.length; t++){
				
				switch(measure) {
				
				  case "entropy":
					  measures[t][0] = t;
					  measures[t][1] = this.getTile(t).entropy();
					  break;
					  
				  case "mean":
					  measures[t][0] = t;
					  avgs = this.getTile(t).mean();
					  measures[t][1] = (double) avgs[0]+avgs[1]+avgs[2]/3;
					  break;
					  
				  case "brightness":
					  measures[t][0]=t;
					  measures[t][1]=this.getTile(t).brightness();
					  break;
					  
				  case "saturation":
					  measures[t][0]=t;
					  measures[t][1]=this.getTile(t).saturation();
					  break;
					  
				  case "hue":
					  measures[t][0]=t;
					  measures[t][1]=this.getTile(t).hue();
					  break;
					  
					  
				  case "std.dev":
					  measures[t][0]=t;
					  measures[t][1]=this.getTile(t).std_dev();
					  break;
					  
				  case "red":
					  measures[t][0] = t;
					  avgs = this.getTile(t).mean();
					  measures[t][1] = (double) avgs[0];
					  break;
					  
				  case "green":
					  measures[t][0] = t;
					  avgs = this.getTile(t).mean();
					  measures[t][1] = (double) avgs[1];
					  break;
					  
				  case "blue":
					  measures[t][0] = t;
					  avgs = this.getTile(t).mean();
					  measures[t][1] = (double) avgs[2];
					  
				  default:
					  break;
				}//end switch
		}		

		
		// Sorting based on the second column (index 1)
		Arrays.sort(measures, Comparator.comparingDouble(row -> row[1]*k));
		for (int i=0; i<this.sortedTiles.length; i++)  this.sortedTiles[i] =  (int) measures[i][0];
		
		return this.sortedTiles;
		
	} //end 

	public BufferedImage percentileMask(String metric, double[] intervals) throws IOException {
		
		int[] id_array = this.sortTiles(metric, false);
		double I = intervals.length; //number of intervals
		double T = id_array.length; //number of Tiles
		
		int interval_begins = 0;
		int interval_end = 0;
		
		
		for (int i=0 ; i<I; i++) {
			
			int value =(int)  Math.floor(intervals[i]*255);
			interval_end = (int)  Math.floor(intervals[i]*T);
			
			System.out.println("Checking Interval "+intervals[i]+" from "+interval_begins+" to " + interval_end+ " of "+T +" setting values of " + value);
			for (int t = interval_begins; t<interval_end; t++ ) {
				
				Tile tile = this.getTile(id_array[t]);
				tile.setMatrix("red", 255-value);
				tile.setMatrix("blue", 255-value);
				tile.setMatrix("green", 255-value);
				
				
			}//for each tile in interval
			
			interval_begins = interval_end;
			
			
		}//for each interval
		
		
		Tile composedImg = this.composeImage(false, true);		
		
		return composedImg.getBufferedImage();
		
		
	}
	
	
	//POOLING OPERATIONS  ====================================================================================
	
	public void applyOperation(String operation, int tile) {
		
		 int r = this.getTileCoordinates(tile)[0];
		 int c = this.getTileCoordinates(tile)[1];
		
		 double value;
		 
		switch (operation) {
			
		//pooling operations
		
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
				
		
		
		//transformations				
			case "log":
				tiles[r][c].log();
				break;				
				
			case "sobel":
				tiles[r][c].sobel();
				break;
								
			case "sqrt":
				tiles[r][c].sqrt();
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
		
		Tile composedImg = this.composeImage(false, true);		
	
		return composedImg.getBufferedImage();

		
	
	}
	
	public BufferedImage applyOperation(String operation) {
		
		if(operation.contains("sobel")) this.createTiles("3x3");

	    return this.applyOperation(operation, sortedTiles);				
	    
	
	}
	

	

	//IMAGE OPERATIONS (EXP) ====================================================================================
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
	
	public BufferedImage merge(BufferedImage img, String operation) {
		
		Tile t1 = this.composeImage(false, false);
		Tile t2 = new Tile();
		t2.setBufferedImage(img);
		
		t1.merge_with(t2, operation );
		
		return t1.getBufferedImage();
	    
	}//end merge
	
	
	//EXPORT  ====================================================================================
	
	
	public BufferedImage showTiles() {
		
		return this.composeImage(true, true).getBufferedImage();
		
	}//end ShowTiles
	
	public BufferedImage showImage() {
		
		return this.composeImage(false, true).getBufferedImage();
		
	}//end ShowTiles
	
	public Tile getTile (int tileIndex) {
		
		
		int r = this.getTileCoordinates(tileIndex)[0];
		int c =  this.getTileCoordinates(tileIndex)[1];
		
		return tiles[r][c];
		
	}
			
	public void  saveImage (String filepath, String format) throws IOException {
		
		this.composeImage(false, true).savetoFile(filepath, format);
		
		//json File
		String json_path = filepath.replace(".jpeg", ".json").replace(".jpg", ".json").replace(".png", ".json");
		
		this.saveJson(json_path, 0);
		
	}
	
	public void  saveTiles (String filepath,  int[] listTiles ) throws IOException {
		
		File file = new File(filepath);
		String path = file.getParent();
		String name = this.getFilename(filepath);
		
		
		//cycle through tiles
		for (int i : listTiles){	
			
			int r = (int) this.getTileCoordinates(i)[0];
			int c = (int) this.getTileCoordinates(i)[1];
		
			tiles[r][c].savetoFile(path+"\\"+name+"-"+r+"-"+c+".png", "PNG");
			this.saveJson(filepath, i );
					
		}
		
				

			
	}//end saveTiles
		
	public void saveJson(String filepath) throws IOException{
		
		this.saveJson(filepath, 0);
		
		
	}
	
	public void saveJson(String filepath, int i) throws IOException{
		
		int r = (int) this.getTileCoordinates(i)[0];
		int c = (int) this.getTileCoordinates(i)[1];
		
		File file = new File(filepath);
		String path = file.getParent();
		String filename = file.getName();
		String name = this.getFilename(filepath);
	
		String content = "[";
							
					//tile coordinates
					content = content +"{ \"img\":\""+filename+"\", \"Rank\":"+i+", \"Y\":"+ r + ", \"X\":"+ c;
					
					// statistics to String
					Dictionary stats = tiles[r][c].getStats();
					String stats_string = stats.toString().replace("=", "\":").replace(", ", ", \"").replace("{", "\"").replace("}", "");			
					
					//tile stats
					content = content  + ", \"height\":"+tiles[r][c].getHeight()+", \"width\":"+tiles[r][c].getWidth() +","+stats_string;
					content = content + "}";
					
						
		content = content + "]";


		this.writeFile(path+"\\"+name+"-"+r+"-"+c+".json", content);
		
		
	}
	
	
	 	
	public Tile composeImage (Boolean drawTiles, Boolean RGBrescale) {
		
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
				render_tile.clone(tiles[r][c]);
				
				if  (drawTiles) {	
					int x = render_tile.get_center_x(false);
					int y = render_tile.get_center_y(false);
					render_tile.add_text(String.valueOf(i), 24, Color.RED,x,y);
					render_tile.drawSquare();				
				}
				
				
				for (int h=0; h<subh; h++){
					for (int w=0; w<subw; w++){
						
						//place section in the final image
						redPixels[h+offsetH][w+offsetW]= render_tile.getMatrix("red") [h][w];
						greenPixels[h+offsetH][w+offsetW]= render_tile.getMatrix("green") [h][w];
						bluePixels[h+offsetH][w+offsetW]= render_tile.getMatrix("blue") [h][w];
												
						offsetH=subh*r;
						offsetW=subw*c;
				
					}//width
				}//height 
				
				i++;
			}//columns
		}//rows
		
		Tile imgout = new Tile();
		
		if(RGBrescale) {
			redPixels = this.rescaleRGB(redPixels, 0, 255);
			greenPixels = this.rescaleRGB(greenPixels, 0, 255);
			bluePixels  = this.rescaleRGB(bluePixels, 0, 255);
			
		}
		
		imgout.setMatrix("red", redPixels);
		imgout.setMatrix("green", greenPixels);
		imgout.setMatrix("blue", bluePixels);
		imgout.setMatrix("alpha", 255);
		
		
		redPixels=null;
		greenPixels=null;
		bluePixels=null;
		
		return imgout;
	
	}
	
	
	private int [][] rescaleRGB (int[][] zmatrix, int newmin, int newmax){
		
			
		//resize z variable to 0-255
		int max = getMaxValue(zmatrix);
		int min = getMinValue(zmatrix);
		
		double range = max-min ==0 ? 1 : max-min;
		

		int[][] RGBmatrix = new int [zmatrix.length][zmatrix[0].length];
		
		for(int j=0; j<RGBmatrix.length;j++){
			for(int i=0;i<RGBmatrix[0].length;i++){			
				RGBmatrix[j][i]=(int) (newmin + ((zmatrix[j][i]-min)/range)* (newmax - newmin));
			}//i
		}//j
		
		return RGBmatrix;
		
	}// end resizeToRGB
	

	
	
	private void writeFile(String filepath, String content) {
		
        int lastSeparatorIndex = filepath.lastIndexOf(File.separator);
        String folder = filepath.substring(0, lastSeparatorIndex);
        String filename = filepath.substring(lastSeparatorIndex + 1);

		
        File directory = new File(folder);
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                System.out.println("Directory created successfully.");
            } else {
                System.out.println("Failed to create the directory.");
                return;
            }
        }
		
		try {
			 FileWriter myWriter = new FileWriter(filepath);
		     myWriter.write(content);
		     myWriter.close();
		     
			} catch (IOException e) {
			      System.out.println("An error occurred.");
			      System.out.println(folder);
			      System.out.println(filename);

			      
			      e.printStackTrace();
			}
		
		
	}
	
	private String getFilename(String filepath) {
		
				File f = new File(filepath);
				String fn = f.getName();
				String [] name = fn.split("\\.");
				return name[0];
		
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

    private int[] getTileCoordinates(int index) {
				
		int[] rc = new int[2];
		
		int cols = this.tiles[0].length;
	
		int r = (int) Math.floor( index/cols ) ;
		int c = index-r*cols;
		
		
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
