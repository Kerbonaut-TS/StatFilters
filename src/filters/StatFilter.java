 package filters;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Dictionary;



public class StatFilter {
	
	BufferedImage original;
	Tile image;
	public Tile[][] tiles; 		//[rows], [columns]  c and r are the coordinates in the picture
	int[][] indexList; 		// the tile index displayed
	int sortedTiles[];		//list of tiles coordinates ranked by values in Matrix M	

	public StatFilter()  {

		System.out.println("StatFilter: loops v9");

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

		/*** creates matrix of tiles using the source image ***/

		int hs,ws;

		hs= (int) Math.floor(image.getHeight()/rows);
		ws= (int) Math.floor(image.getWidth()/columns);

		tiles = new Tile [rows][columns];
		this.sortedTiles = new int [rows*columns];

		int t = 0;

		//for each  tile  c=columns r=rows
		for (int r=0; r<rows; r++){
			for (int c=0; c<columns; c++){

				tiles[r][c] = select_tile_by_coordinates((c) * ws, (r) * hs, ws, hs);
				this.sortedTiles[t] = t;
				t++;

			}//end rows
		}//end columns
	}
	
	public void createTiles(String pixelWindow){
		
			  int[] dimensions  = this.calculateDimensions(pixelWindow);
              
              if (dimensions[0] != dimensions[1]){
            	  
            	  System.out.println("Error: Invalid size. Only square windows suported");
            	  
              } else {
            	  
            	  int rows = (int) Math.floor(image.getHeight()/dimensions[0]);
            	  int columns = (int) Math.floor(image.getWidth()/dimensions[0]);
           	  
            	  this.createTiles(rows,columns);
              }


	}//end create Tiles




	public Tile select_tile_by_coordinates(int x, int y, int width, int height){

		/*** selects a width X height tile with its top left corner at coordinates X,Y ***/

		Tile tile = new Tile();
		tile.setTlx(x);
		tile.setTly(y);
		tile.setHeight(height);
		tile.setWidth(width);


		for (int h=0; h<height;h++) {
			for (int w = 0; w < width; w++) {
				for (String c : this.image.channels) {
					int value = this.image.getMatrix(c)[h+y][w+x];
					tile.setPixel(c,  h,  w, value);

				}
			}
		}

		return tile;
	}



 	public BufferedImage findTile(String measure, String operator,  String size, int stride) {

		/*** Finds the tile that is  maximising/minimizing  a particular Stats
		 * the convolution operation will use a pixel window and stride specified in the parameters ***/

		int k =  ( operator == "min") ? 1 : -1;
 		
 		//height and width of sub matrixes
		int[] dimensions = this.calculateDimensions(size);
				
		int H = dimensions [0];
		int W = dimensions [1];
 		
		int horizontal_shifts = (int) Math.floor((this.image.width - W)/stride)+1;
		int vertical_shifts = (int)Math.floor((this.image.height - H)/stride)+1;
		
 		int n = horizontal_shifts * vertical_shifts;
 		double [][] metrics = new double [n][3];    //  tlx, tly, value

		int i = 0;
		
		// convolution
		for (int tly = 0; tly < (this.image.height - H);  tly = tly+stride){
			for (int tlx = 0; tlx < (this.image.width - W); tlx = tlx+stride){

				Tile tile = this.select_tile_by_coordinates(tlx, tly,  W, H);
				metrics[i][0] = (double) tlx;
				metrics[i][1] = (double) tly;
				metrics[i][2] = tile.getStats().get(measure);
				i++;

			}//end x
		}//end y
		
		//sorting
		Arrays.sort(metrics, Comparator.comparingDouble(row -> row[2]*k));
		int optimal_tlx = (int) metrics[0][0];
		int optimal_tly = (int) metrics[0][1];

		Tile output = this.select_tile_by_coordinates(optimal_tlx, optimal_tly, W, H);
		return output.getBufferedImage();
		
		
	}


	public Tile composeImage (Boolean drawTiles, Boolean RGBrescale) {

		/*** Returns a Buffered Image stitching all tiles together in one single image Tile
		 * (reverse operation of createTiles) ***/

		int rows = tiles.length;
		int columns = tiles[0].length;
		int subh= tiles[0][0].getHeight();
		int subw = tiles[0][0].getWidth();

		//final image
		Tile imageout =  new Tile();
		imageout.setHeight(subh*rows);
		imageout.setWidth(subw*columns);
		int i=0;

		// stitching all tiles together
		for (int r=0; r<rows; r++){
			for (int c=0; c<columns; c++){

				Tile render_tile =  new Tile();
				render_tile.clone(tiles[r][c]);

				if  (drawTiles) render_tile.mark( Color.RED,String.valueOf(i), 24);

				imageout.place(render_tile, tiles[r][c].tlx, tiles[r][c].tly);
				i++;

			}//columns
		}//rows

		if(RGBrescale)  imageout.rescaleRGB(0,255);

		return imageout;

	}

	//SORT TILES  ==============================================================================
	public int[] sortTiles(String measure, Boolean ascending) throws IOException{
		
		int k =  ( ascending == true) ? 1 : -1;
		
		// storing tile index, measure in array
	    double[][] measures = new double [sortedTiles.length][2]; 
	    int avgs[]= null;
	   
		//create a list of indexes and measures
		for (int t = 0; t<sortedTiles.length; t++){			
			measures[t][0] = t;	
			measures[t][1] = this.getTile(t).getStats().get(measure);
		}
		
		// Sorting based on the second column (index 1)
		Arrays.sort(measures, Comparator.comparingDouble(row -> row[1]*k));
		for (int i=0; i<this.sortedTiles.length; i++)  this.sortedTiles[i] =  (int) measures[i][0];
		//Utils.logger(this.sortedTiles);
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
				tiles[r][c].mean();
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


		
	
	public BufferedImage merge(BufferedImage img, String operation) {
		
		Tile t1 = this.composeImage(false, false);
		Tile t2 = new Tile();
		t2.setBufferedImage(img);
		
		t1.merge_with(t2, operation );
		
		return t1.getBufferedImage();
	    
	}//end merge
	
	
	//EXPORT  ====================================================================================
	
	
	public BufferedImage showTiles() {
		
		return this.composeImage(true, false).getBufferedImage();
		
	}//end ShowTiles
	
	public BufferedImage showImage() {
		
		return this.composeImage(false, true).getBufferedImage();
		
	}//end ShowTiles
	
	public Tile getTile (int tileIndex) {
		
		
		int r = this.getTileCoordinates(tileIndex)[0];
		int c =  this.getTileCoordinates(tileIndex)[1];
		
		return tiles[r][c];
		
	}

	public Tile crop(double percent) {

		Tile current = this.composeImage(false, true);

		int newH = (int) Math.round(current.height*percent);
		int newW = (int) Math.round(current.width*percent);

		int tlx = Math.round((current.height-newH)/2);
		int tly = Math.round((current.width-newW)/2);

		return this.select_tile_by_coordinates(tlx,tly, newW, newH);

	}


	public void  saveImage (String filepath, String format) throws IOException {
		
		this.composeImage(false, true).savetoFile(filepath, format);
		
		//json File
		String json_path = filepath.replace(".jpeg", ".json").replace(".jpg", ".json").replace(".png", ".json");
		
		this.saveJson(json_path, 0);
		
	}
	
	public void  saveTiles (String filepath) throws IOException {
		
		this.saveTiles(filepath, this.sortedTiles);
					
		}
	
	
	public void  saveTiles (String filepath,  int[] listTiles ) throws IOException {
		
		File file = new File(filepath);
		String path = file.getParent();
		String name = Utils.getFilename(filepath);
		
		
		//cycle through tiles
		for (int i : listTiles){	
			
			int r = (int) this.getTileCoordinates(i)[0];
			int c = (int) this.getTileCoordinates(i)[1];
		
			tiles[r][c].savetoFile(path+File.separator+name+"-"+r+"-"+c+".png", "PNG");
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
		String name = Utils.getFilename(filepath);
	
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


		Utils.writeFile(path+File.separator+name+"-"+r+"-"+c+".json", content);
		
		
	}

	
	//TOOLS ===================================================================================



    public int[] getTileCoordinates(int index) {
				
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

	private int[] calculateDimensions(String pixelWindow) throws NumberFormatException {
		
		int[] dimensions = new int[2];
		String[] s = pixelWindow.split("x");
	  
		  
		dimensions[0] = Integer.parseInt(s[0]);
        dimensions[1] = Integer.parseInt(s[1]);

        return dimensions;
        
	}
    

}//end class
