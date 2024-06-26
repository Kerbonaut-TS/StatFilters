package com.babelcoding.StatFilters;

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

    //image for export
    BufferedImage img;

    int height, width;
    int tlx;
    int tly;

    int[][] redPixels;
    int[][] greenPixels;
    int[][] bluePixels;

    //channel averages
    double  meanR,meanG,meanB;
    Boolean monochrome;
    String[] channels = {"red", "green", "blue"};
    public String[] metrics = new String[] {"tlx", "tly", "height","width","mean","std.dev","entropy","red","blue", "green", "hue", "saturation", "brightness"};

    public Tile() {

        //an imageHolder can be empty
        this.height = 0;
        this.width = 0;

        //default reference system
        tlx = 0;
        tly = 0;

    }//end constructor

    public void setChannels(String[] channels) {
        this.channels = channels;
        this.monochrome = channels.length>1?  false : true;
    }

    //=== IMPORT METHODS =====================================================================

    //import image from another RGBHolder Object
    public void clone(Tile ih) {

        this.setHeight(ih.getHeight());
        this.setWidth(ih.getWidth());
        this.setChannels(ih.channels);

        for (String c : this.channels) {

            this.setMatrix(c, ih.getMatrix(c));

        }
    }//end setImage

    //import a BufferedImage
    public void setBufferedImage(BufferedImage image, Boolean monochrome) {

        if (image == null) {
            this.height = 0;
            this.width = 0;
        } else {

            this.setHeight(image.getHeight());
            this.setWidth(image.getWidth());

            //get Image matrix
            for (int h = 0; h < height; h++) {
                for (int w = 0; w < width; w++) {

                    Color colour =  this.getPixelColour(image, w, h);

                    if (monochrome){
                        redPixels[h][w] = (colour.getRed() +colour.getGreen() + colour.getBlue())/3;
                    } else{
                        redPixels[h][w] = colour.getRed();
                        greenPixels[h][w] = colour.getGreen();
                        bluePixels[h][w] = colour.getBlue();
                    }

                }//end height
            }// end width

            if(monochrome) this.setChannels( new String[] {"red"});


        }//end else

    }//end

    //import Image from file
    public void setImageFromFile(String imgpath, Boolean monochrome) throws IOException {

        File myImg = new File(imgpath);
        BufferedImage image = ImageIO.read(myImg);

        this.setBufferedImage(image, monochrome);

    } //end

    //import image from a linear RGB array
    public void setImgFromVector(int[] array) {

        //linear format is R1,G1,B1, R2,G2,B2, R3.....

        int count = 0;

        for (int h = 0; h < this.getHeight(); h++) {
            for (int w = 0; w < this.getWidth(); w++) {
                redPixels[h][w] = array[count];
                greenPixels[h][w] = array[count + 1];
                bluePixels[h][w] = array[count + 2];
                count = count + 3;
            }//end width
        }//end height
    }

    //==== TILES OPERATIONS ========================================================================

    public void merge_with(Tile t2, String operation) {
        /*overlays the tile T2 using its reference system [(tyx),(try)]*/
        //use dimensions of the smaller image
        int max_height = Math.min(height, t2.getHeight());
        int max_width = Math.min(width, t2.getWidth());

        //get Image matrix
        for (int h = 0; h < max_height; h++) {
            for (int w = 0; w < max_width; w++) {
                for (String c : this.channels) {

                    int pixelA = this.getMatrix(c)[h][w];
                    int pixelB = t2.getMatrix(c)[h][w];
                    int value = Stats.apply(operation, pixelA, pixelB);
                    this.setPixel(c, h, w, value);

                }//end channels
            }//end height
        }// end width

    }

    public void place(Tile t2, int X, int Y) {

        this.setChannels(t2.channels);

		/*place the tile T2 in at coordinates X, Y.
		If this falls outside this tile it will stop the loop*/
        int max_height = Math.min(height - Y - 1, t2.getHeight());
        int max_width = Math.min(width - X - 1, t2.getWidth());

        for (int h = 0; h < max_height; h++) {
            for (int w = 0; w < max_width; w++) {
                for (String c : this.channels) {

                    this.setPixel(c, h + Y, w + X, t2.getMatrix(c)[h][w]);

                }//end channels
            }//end height
        }// end width
    }

    public void mark(Color mycolor, String text, int fontsize) {

        //output Img
        BufferedImage imgWithText = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = imgWithText.createGraphics();

        // draw Text
        Font font = new Font("Arial", Font.BOLD, fontsize);
        g2d.setFont(font);
        g2d.setColor(mycolor);

        Rectangle2D bounds;
        bounds = font.getStringBounds(text, g2d.getFontRenderContext());
        int stringWidth = (int) bounds.getWidth();


        //Text Location
        int x = this.get_center_x(false);
        int y = this.get_center_y(false);

        if (x + stringWidth >= width) x = x - ((x + stringWidth) - width);
        if (x < 0) x = 0;

        //draw Rectangles
        this.drawSquare();

        g2d.drawImage(this.getBufferedImage(), 0, 0, null);
        g2d.drawString(text, x, y);
        g2d.dispose();

        this.setBufferedImage(imgWithText, this.monochrome);


    }//end mark

    public void drawSquare() {

        for (int h = 0; h < this.height; h++) {

            redPixels[h][0] = 255;
            redPixels[h][this.width - 1] = 255;

            greenPixels[h][0] = 0;
            greenPixels[h][this.width - 1] = 0;

            bluePixels[h][0] = 0;
            bluePixels[h][this.width - 1] = 0;
        }


        for (int w = 0; w < this.width; w++) {

            redPixels[0][w] = 255;
            redPixels[this.height - 1][w] = 255;

            greenPixels[0][w] = 0;
            greenPixels[this.height - 1][w] = 0;

            bluePixels[0][w] = 0;
            bluePixels[this.height - 1][w] = 0;
        }


    }
    public Tile resize(int newHeight, int newWidth) throws IOException {

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
        resizedIMG.setBufferedImage(bimage, this.monochrome);

        return resizedIMG;

    }


    public void rescaleRGB(int newmin, int newmax) {

        for (String c : this.channels) {

            //resize z variable to 0-255
            int max = Utils.getMaxValue(this.getMatrix(c));
            int min = Utils.getMinValue(this.getMatrix(c));

            double range = max - min == 0 ? 1 : max - min;

            for (int h = 0; h < this.getHeight(); h++) {
                for (int w = 0; w < this.getWidth(); w++) {

                    int value = (int) (newmin + ((this.getMatrix(c)[h][w] - min) / range) * (newmax - newmin));
                    this.setPixel(c, h, w, value);

                }//i
            }//j
        }


    }// end resizeToRGB

    //==== PIXEL TRANSFORMS ==================================================================

    public void standardise() {
        for (String c : this.channels) Stats.standardise(this.getMatrix(c));
    }//end standardize

    public void log() {
        for (String c : this.channels) Stats.transform("log", this.getMatrix(c));
    }

    public void invert() {
        for (String c : this.channels) Stats.transform("invert", this.getMatrix(c));
    }

    public void sqrt() {
        for (String c : this.channels) Stats.transform("sqrt", this.getMatrix(c));
    }

    public void mean() {

        this.setMatrix("red", this.meanR);
        this.setMatrix("green", this.meanG);
        this.setMatrix("blue", this.meanB);

    }


    public void multiply(double multiplier) {

        //get Image matrix
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                redPixels[h][w] = (int) Math.floor(redPixels[h][w] * multiplier);
                greenPixels[h][w] = (int) Math.floor(greenPixels[h][w] * multiplier);
                bluePixels[h][w] = (int) Math.floor(bluePixels[h][w] * multiplier);
            }//end height
        }// end width

    }

    public void sobel() {


        int sx[][] = new int[][]
                {
                        {-1, 0, 1},
                        {-2, 0, 2},
                        {-1, 0, 1}
                };

        int sy[][] = new int[][]
                {
                        {-1, -2, -1},
                        {0, 0, 0},
                        {1, 2, 1}
                };

        int gx, gy, G;

        gx = 0;
        gy = 0;
        G = 0;

        //iterate in the 3x3 tile
        for (int h = 0; h < this.height; h++) {
            for (int w = 0; w < this.width; w++) {
                int pixel = (int) Math.floor((redPixels[h][w] + greenPixels[h][w] + bluePixels[h][w]) / 3);

                gx = gx + pixel * sx[h][w];
                gy = gy + pixel * sy[h][w];

            }//end width
        }//end height

        G = Math.min((int) Math.sqrt(Math.pow((double) gx, 2) + Math.pow((double) gy, 2)), 255);


        //set pixels equal to gradient
        for (int h = 0; h < this.height; h++) {
            for (int w = 0; w < this.width; w++) {

                redPixels[h][w] = G;
                greenPixels[h][w] = G;
                bluePixels[h][w] = G;

            }//end width
        }//end height


    }


    // TILE METRICS/STATS  ========================================================================

    private Color getPixelColour(BufferedImage image, int x, int y) {

        return new Color(image.getRGB(x, y));

    }//end getpixelcolour

    public int getTlx() {
        return this.tlx;
    }

    public int getTly() {
        return this.tly;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int get_center_x(Boolean absolute) {

        int offset = (absolute) ? this.tlx : 0;

        return offset + (int) ((float) width * 0.5);

    }

    public int get_center_y(Boolean absolute) {

        int offset = (absolute) ? this.tly : 0;
        return offset + (int) ((float) height * 0.5);


    }
    private void calculate_avgs(){

        this.meanR = Stats.mean(this.getMatrix("red"));
        this.meanG = Stats.mean(this.getMatrix("green"));
        this.meanB = Stats.mean(this.getMatrix("blue"));

    }


    private double avg() {

        return (this.meanR + this.meanG + this.meanB) / 3;
    }


    private double hue() {
        double rp, gp, bp, max, min, hue;

        rp = this.meanR/ 255f;
        gp = this.meanG / 255f;
        bp = this.meanB / 255f;

        max = Math.max(Math.max(rp, gp), bp);
        min = Math.min(Math.min(rp, gp), bp);

        // Check if max and min are equal (pixel is grey)
        if (max == min) {
            // Return -1 to represent grey
            return -1;
        }

        // Calculate hue colour wheel
        hue = (float) Math.toDegrees(Math.atan2(Math.sqrt(3) * (gp - bp), 2 * rp - gp - bp));

        // Normalize hue to be within the range of 0 to 360 degrees
        if (hue < 0) {
            hue += 360;
        }

        return Math.floor(hue);
    }

    private double saturation() {

        double rp, gp, bp, max, min, saturation;

        rp = this.meanR/ 255f;
        gp = this.meanG / 255f;
        bp = this.meanB / 255f;

        max = Math.max(Math.max(rp, gp), bp);
        min = Math.min(Math.min(rp, gp), bp);

        saturation = (max == 0) ? 0 : ((max - min) / max);

        saturation = saturation * 100;

        return Math.floor(saturation);

    }

    private double brightness() {

        double max, brightness;

        double rp = this.meanR/ 255f;
        double gp = this.meanG / 255f;
        double bp = this.meanB / 255f;


        max = Math.max(Math.max(rp, gp), bp);
        brightness = max * 100;


        return Math.floor(brightness);

    }

    private double std_dev() {

        double sigmar, sigmag, sigmab;
        sigmar = Stats.std_dev(this.getMatrix("red"), this.meanR);
        sigmag = Stats.std_dev(this.getMatrix("green"), this.meanG);
        sigmab = Stats.std_dev(this.getMatrix("blue"), this.meanB);

        return sigmar + sigmag + sigmab;

    }//end getinfo

    private double entropy() {
        // Create a map to count the frequency of each color

        Map<Color, Integer> colorCounts = new HashMap<>();
        int[][] red = this.getMatrix("red");
        int[][] blue = this.getMatrix("blue");
        int[][] green = this.getMatrix("green");


        // Iterate over each pixel and count the occurrences of each color
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Color pixel = new Color((int) red[i][j], (int) green[i][j], (int) blue[i][j]);
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


    public Dictionary<String, Double> getStats() {

        //if unspecified export all
        return this.getStats(this.metrics);

    }

    public double getStats(String metric) {

        //if unspecified export all
        Dictionary<String, Double> json_result = this.getStats(new String[] {metric});
        return json_result.get(metric);

    }


    public Dictionary<String, Double> getStats(String[] metrics) {

        Dictionary<String, Double> stats = new Hashtable();
        this.calculate_avgs();

        for (String m : metrics) {
            switch (m) {

                case "tlx":
                    stats.put("tlx", (double) this.tlx);
                    break;
                case "height":
                    stats.put("height", (double) this.height);
                    break;
                case "width":
                    stats.put("width", (double) this.width);
                    break;
                case "mean":
                    stats.put("mean", (double) this.avg());
                    break;
                case "std.dev":
                    stats.put("std.dev", this.std_dev());
                    break;
                case "entropy":
                    stats.put("entropy", this.entropy());
                    break;
                case "red":
                    stats.put("red", this.meanR);
                    break;
                case "green":
                    stats.put("green", this.meanG);
                    break;
                case "blue":
                    stats.put("blue", this.meanB);
                    break;
                case "hue":
                    stats.put("hue", this.hue());
                    break;
                case "saturation":
                    stats.put("saturation", this.saturation());
                    break;
                case "brightness":
                    stats.put("brightness", this.brightness());
                    break;


            }//end switch
        }

        return stats;

    }


    // === IMG OPERATIONS ======================================================================

    public int[][] getMatrix(String channel) {

        switch (channel) {
            case "red":
                return redPixels;

            case "green":
                return greenPixels;

            case "blue":
                return bluePixels;


            default:
                System.out.println("Invalid: set red, green, blue. ");
                System.exit(0);
                return null;

        }

    }

    public BufferedImage getBufferedImage() {

        //prepare the buffered output image
        BufferedImage imgBuf = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

        int rgb;
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                if(this.monochrome) {
                    rgb = (((int) 255 << 24) | ((int) redPixels[h][w]) << 16 | ((int) redPixels[h][w]) << 8 | ((int) redPixels[h][w]));
                } else {
                    rgb = (((int) 255 << 24) | ((int) redPixels[h][w]) << 16 | ((int) greenPixels[h][w]) << 8 | ((int) bluePixels[h][w]));
                }
                imgBuf.setRGB(w, h, rgb);
            }//end h
        }//end w

        return imgBuf;

    }//end write image

    public String getJson() {

     return getJson(this.metrics);
    }

    public String getJson(String[] metrics) {

        // statistics to String
        Dictionary stats = this.getStats(metrics);
        String stats_string = stats.toString().replace("=", "\":").replace(", ", ", \"").replace("{", "\"").replace("}", "");

        //tile stats
        String json_string = "{ " + stats_string + " }";

        return json_string;
    }



    public void savetoFile(String filepath, String format) throws IOException {


        File file = new File(filepath);
        file.getParentFile().mkdirs();
        ImageIO.write(this.getBufferedImage(), format, file);

    }//end write image


    //=== GET/SET  ========================================================================


    public void setMatrix(String channel, double constant) {


        switch (channel) {
            case "red":
                redPixels = new int[height][width];
                for (int i = 0; i < redPixels.length; i++) {
                    for (int j = 0; j < redPixels[0].length; j++) redPixels[i][j] = (int) constant;
                }
                break;

            case "green":
                greenPixels = new int[height][width];
                for (int i = 0; i < greenPixels.length; i++) {
                    for (int j = 0; j < greenPixels[0].length; j++) greenPixels[i][j] = (int) constant;
                }
                break;

            case "blue":
                bluePixels = new int[height][width];
                for (int i = 0; i < bluePixels.length; i++) {
                    for (int j = 0; j < bluePixels[0].length; j++) bluePixels[i][j] = (int) constant;
                }
                break;


            default:
                System.out.println("Invalid: set red, green, blue");

        }


    }

    public void setMatrix(String channel, int[][] RGBmatrix) {

        //this.setHeight(RGBmatrix.length);
        //this.setWidth(RGBmatrix[0].length);

        switch (channel) {
            case "red":
                redPixels = Utils.copyMatrix(RGBmatrix);
                break;

            case "green":
                greenPixels = Utils.copyMatrix(RGBmatrix);
                break;

            case "blue":
                bluePixels = Utils.copyMatrix(RGBmatrix);
                break;


            default:
                System.out.println("Invalid: set red, green, blue");

        }

    }

    public void setPixel(String channel, int h, int w, int value) {


        if ((value >= 0) && (value <= 255)) {

            switch (channel) {
                case "red":
                    redPixels[h][w] = value;
                    break;

                case "green":
                    greenPixels[h][w] = value;
                    break;

                case "blue":
                    bluePixels[h][w] = value;
                    break;

                default:
                    System.out.println("ERROR. Invalid channel: " + channel);
                    System.exit(0);

            }//end switch
        } else {

            System.out.print("ERROR. Invalid pixel value:" + value);
            System.exit(0);
        }

    }//end setPixel


    public void setHeight(int h) {
        this.height = h;

        bluePixels = new int[height][width];
        greenPixels = new int[height][width];
        redPixels = new int[height][width];


    }

    public void setWidth(int w) {
        this.width = w;
        bluePixels = new int[height][width];
        greenPixels = new int[height][width];
        redPixels = new int[height][width];


    }

    public void setTlx(int x) {
        this.tlx = x;
    }

    public void setTly(int y) {
        this.tly = y;
    }


    public String vectorize() {
        //** exports 0-255 RGB array stating from top left corner R1,G1,B1, R2,G2,B2....

        StringBuffer sb = null;
        String pixels = "";


        for (int h = 0; h < this.height; h++) {
            for (int w = 0; w < this.width; w++) {

                pixels = pixels + "[" + redPixels[h][w] + "," + greenPixels[h][w] + "," + bluePixels[h][w] + "],";

            }

            sb = new StringBuffer(pixels);
            sb.delete(pixels.length() - 1, pixels.length());

        }


        return sb.toString();

    }//end getPixels


}//end class



	


