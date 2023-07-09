package iconoclasticlayer;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;


public class IconoclasticLayer {

	RGBHolder image;
	public RGBHolder[][] sections; //[rows], [columns]  c and r are the coordinates in the picture
	RGBHolder maxInfoAreas[];
	double [][] I; //information quantity for each section
	
	
	public IconoclasticLayer(RGBHolder rgbh)  {
		this.image = new RGBHolder();
		this.image.clone(rgbh);
		this.setResolution(1);
			
	}//end constructor
	
	public void setResolution(int n){
		
		//n matrices per side
		
		
		//height and width of sub matrixes
		int hs,ws;
				
		hs= (int) Math.floor(image.getHeight()/n);
		ws= (int) Math.floor(image.getWidth()/n);
		
		sections = new RGBHolder [(int) n][(int) n];

		//for each  section  c=columns r=rows
		for (int r=0; r<n; r++){
			for (int c=0; c<n; c++){
				
				double [][] tempR = new double[hs][ws];
				double [][] tempG = new double[hs][ws];
				double [][] tempB = new double[hs][ws];
				double [][] tempA = new double[hs][ws];
				
				for (int h=0; h<hs; h++){
					for (int w=0; w<ws; w++){
						
						int offsetH = (r)*hs;
						int offsetW = (c)*ws;		
						
						tempR[h][w]= image.getRedMatrix()[offsetH+h][offsetW+w];
						tempG[h][w]= image.getGreenMatrix()[offsetH+h][offsetW+w];
						tempB[h][w]= image.getBlueMatrix()[offsetH+h][offsetW+w];
						tempA[h][w]= image.getAlphaMatrix()[offsetH+h][offsetW+w];
						
					}	//end height
				}//end width
				
				sections[r][c]=new RGBHolder();		
				sections[r][c].setRedMatrix(tempR);
				sections[r][c].setGreenMatrix(tempG);
				sections[r][c].setBlueMatrix(tempB);
				sections[r][c].setAlphaMatrix(tempA);
				
				sections[r][c].setTlx((c)*ws); //-1 because the matrix is indexed from 0
				sections[r][c].setTly((r)*hs);
				
				
				tempR=null;
				tempG=null;
				tempB=null;
				tempA=null;
					
			}//end rows
		}//end columns
	

	}//end setResolution
			
	//returns a linear RGB Array with the average colour of each section 
	public double[] getAVGValues() {
		
		//linear RGB array size: sections(W) * sections(H)  * 3 colors
		int size = (int) Math.pow(sections.length, 2) *3; 
		double[] inputLayer = new double[size];
		
		int i = 0;
		
		//for each section
		for (int r=0; r<sections.length; r++){
			for (int c=0; c<sections[0].length; c++){
				
				double [] avgRGB=sections[r][c].getAVGvalue();
				
				inputLayer[i]=avgRGB[0];
				inputLayer[i+1]=avgRGB[1];
				inputLayer[i+2]=avgRGB[2];
				
				avgRGB=null;
				i=i+3;
				
			}//end columns
		}//end rows
		
		return inputLayer;
		
	}//end getinputlayer
	
	public double[] getMaxValues() {
		
		//linear RGB array size: sections(W) * sections(H)  * 3 colors
		int size = (int) Math.pow(sections.length, 2) *3; 
		double[] inputLayer = new double[size];
		
		int i = 0;
		
		//for each section
		for (int r=0; r<sections.length; r++){
			for (int c=0; c<sections[0].length; c++){
				
				
				inputLayer[i]=this.findMax(sections[r][c].getRedMatrix(), null)[0];
				inputLayer[i+1]=this.findMax(sections[r][c].getGreenMatrix(), null)[0];
				inputLayer[i+2]=this.findMax(sections[r][c].getBlueMatrix(), null)[0];
				
				i=i+3;
				
			}//end columns
		}//end rows
		
		return inputLayer;
		
	}//end getinputlayer
	

	
	public double[][] rank_sections() throws IOException{
		
		//I matrix contains the quantity of Information for each section		
	    I= new double [sections.length][sections[0].length];
		
	   
		//Populate the I matrix 
		for (int r=0; r<sections.length; r++){
			for (int c=0; c<sections[0].length; c++){					
				I[r][c]=sections[r][c].calculateEntropy();				
			}//for columns
		}//for rows
		
	    //sort all sections by information and get the list	
		
		double sortedList [][]= this.create_ranks();

		
		return sortedList;
		
	} //end 
	
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
	
	private double[] findMax(double [][] matrix, int excludedElements[][]){
		
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
	
	
	
	
	//Information Optimiser
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
		info=section.calc_entropy();
		if(direction>=0){
			larger=this.resizeSection(section, delta);
			infoL=larger.calc_entropy();
			gradL=infoL-info;
		}
		if(direction<=0){
			smaller=this.resizeSection(section, (-1)*delta);
			infoS=smaller.calc_entropy();
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
		
		
		double [][] tempR = new double[maxh][maxw];
		double [][] tempG = new double[maxh][maxw];
		double [][] tempB = new double[maxh][maxw];
		double [][] tempA = new double[maxh][maxw];
		
				
		for (int h=0; h<maxh; h++){
			for (int w=0; w<maxw; w++){
								
				tempR[h][w]= image.getRedMatrix()[offsetH+h][offsetW+w];
				tempG[h][w]= image.getGreenMatrix()[offsetH+h][offsetW+w];
				tempB[h][w]= image.getBlueMatrix()[offsetH+h][offsetW+w];
				tempA[h][w]= image.getAlphaMatrix()[offsetH+h][offsetW+w];

			}	//end height
		}//end width
		
		resSection.setRedMatrix(tempR);
		resSection.setGreenMatrix(tempG);
		resSection.setBlueMatrix(tempB);
		resSection.setAlpha(255);
		
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

		for (int r=0; r<sections.length; r++){
			for (int c=0; c<sections[0].length; c++){			
				sections[r][c].standardise();		
			}//end col
		}//end rows
		
	}//end localstd
	
	private double findMin(double [][] matrix){
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
	
	//debugging: used to display the standardised image (resize to 0-255 then stitch sections together)
	public RGBHolder getImage() {
		
		int n =this.sections.length; // matrices per side (it's a square)
		
		int subh, subw, offsetW, offsetH;
		subh= sections[0][0].getHeight();
		subw = sections[0][0].getWidth();
		
		//final image
		double [][] redPixels = new double [subh*n][subw*n];
		double [][] greenPixels = new double [subh*n][subw*n];
		double [][] bluePixels = new double [subh*n][subw*n];
		
		offsetH=0;
		offsetW=0;
			
		// stitching all sections together
		for (int r=0; r<n; r++){
			for (int c=0; c<n; c++){
				
				//resize standardized  matrices to 0-255
				double [][] RMatrix=this.resizeToRGB(sections[r][c].getRedMatrix());
				double [][] GMatrix=this.resizeToRGB(sections[r][c].getGreenMatrix());
				double [][] BMatrix=this.resizeToRGB(sections[r][c].getBlueMatrix());
				
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
		
		imgout.setRedMatrix(redPixels);
		imgout.setGreenMatrix(greenPixels);
		imgout.setBlueMatrix(bluePixels);
		imgout.setAlpha(255);
		
		
		redPixels=null;
		greenPixels=null;
		bluePixels=null;
		
		return imgout;
	
	}
	
	
	private double [][] resizeToRGB(double[][] zmatrix){
		
			
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
	

	
	
}//end class
