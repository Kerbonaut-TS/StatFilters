package com.babelcoding.StatFilters;

import java.awt.*;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class ImageStats {

    Hashtable<String, Double> stats;
    public String[] metrics = new String[] {"mean","std.dev","red","blue", "green", "hue", "saturation", "brightness","entropy"};

    public ImageStats (){
         this.stats = new Hashtable();
    }

    public Dictionary<String, Double> getStat(Tile tile, String[] metrics) {

        Hashtable<String, Double> stats = new Hashtable();

        for (String m : metrics) {
            if (this.stats.containsKey(m)){

                //already calculated, do nothing

            }else{

                switch (m) {

                    case "mean":
                        this.stats.put("mean", (double) this.mean(tile));
                        break;
                    case "std.dev":
                        this.stats.put("std.dev", this.std_dev(tile));
                        break;
                    case "red":
                        this.mean(tile);
                        break;
                    case "green":
                        this.mean(tile);
                        break;
                    case "blue":
                        this.mean(tile);
                        break;
                    case "hue":
                        this.stats.put("hue", this.hue(tile));
                        break;
                    case "saturation":
                        this.stats.put("saturation", this.saturation(tile));
                        break;
                    case "brightness":
                        this.stats.put("brightness", this.brightness(tile));
                        break;

                    case "entropy":
                        this.stats.put("entropy", this.entropy(tile));
                        break;


                }//end switch
            }//end else
        }//end for metrics

        return stats;

    }


    public Dictionary<String, Double> getStat(Tile tile) {
        /* option to return all metrics*/
        Dictionary<String, Double> stats = new Hashtable();
        return this.getStat(tile, this.metrics);

    }

    private double mean(Tile tile) {

        double meanR = MatrixOps.mean(tile.getMatrix("red"));
        double meanG = MatrixOps.mean(tile.getMatrix("green"));
        double meanB = MatrixOps.mean(tile.getMatrix("blue"));

        this.stats.put("red", meanR);
        this.stats.put("green", meanG);
        this.stats.put("blue", meanB);


        return (meanR + meanG + meanB) / 3;
    }



    // AGGREGATION FUNCTIONS  int[][] -> Y ========================================================================
    private double hue(Tile tile) {
        double rp, gp, bp, max, min, hue;

        if(!(this.stats.containsKey("red"))&(this.stats.containsKey("green"))&(this.stats.containsKey("blue"))) this.mean(tile);

        rp = this.stats.get("red")/ 255f;
        gp = this.stats.get("green") / 255f;
        bp = this.stats.get("blue") / 255f;

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

    private double saturation(Tile tile) {

        double rp, gp, bp, max, min, saturation;

        if(!(this.stats.containsKey("red"))&(this.stats.containsKey("green"))&(this.stats.containsKey("blue"))) this.mean(tile);

        rp = this.stats.get("red")/ 255f;
        gp = this.stats.get("green") / 255f;
        bp = this.stats.get("blue") / 255f;

        max = Math.max(Math.max(rp, gp), bp);
        min = Math.min(Math.min(rp, gp), bp);

        saturation = (max == 0) ? 0 : ((max - min) / max);

        saturation = saturation * 100;

        return Math.floor(saturation);

    }

    private double brightness(Tile tile) {

        double rp, gp, bp, max, brightness;

        if(!(this.stats.containsKey("red"))&(this.stats.containsKey("green"))&(this.stats.containsKey("blue"))) this.mean(tile);

        rp = this.stats.get("red")/ 255f;
        gp = this.stats.get("green") / 255f;
        bp = this.stats.get("blue") / 255f;



        max = Math.max(Math.max(rp, gp), bp);
        brightness = max * 100;


        return Math.floor(brightness);

    }

    private double std_dev(Tile tile) {

        double sum_sigma =0;

        for (String c : tile.channels){

            if(this.stats.containsKey(c)){
                sum_sigma = sum_sigma + MatrixOps.std_dev(tile.getMatrix(c), this.stats.get(c));
            }else{
                sum_sigma = sum_sigma + MatrixOps.std_dev(tile.getMatrix(c));
            }
        }//end for

        return sum_sigma;


    }//end std dev

    private double entropy(Tile tile) {
        //TODO: use hue bins

        // Create a map to count the frequency of each color
        Map<Color, Integer> colorCounts = new HashMap<>();
        int[][] red = tile.getMatrix("red");
        int[][] blue = tile.getMatrix("blue");
        int[][] green = tile.getMatrix("green");


        // Iterate over each pixel and count the occurrences of each color
        for (int i = 0; i < tile.getHeight(); i++) {
            for (int j = 0; j < tile.getWidth(); j++) {
                Color pixel = new Color((int) red[i][j], (int) green[i][j], (int) blue[i][j]);
                colorCounts.put(pixel, colorCounts.getOrDefault(pixel, 0) + 1);
            }
        }

        // Calculate the entropy
        double totalPixels = tile.getWidth() * tile.getHeight();
        double entropy = 0.0;

        for (int count : colorCounts.values()) {
            double probability = count / totalPixels;
            entropy -= probability * Math.log(probability) / Math.log(2);
        }

        return entropy;
    }



    public static int apply(String operator, int pixelA, int pixelB) {
        /* applies an operator to two pixel values   int-> int */

        return switch (operator) {
            case "diff" -> (int) Math.max(pixelA - pixelB, 0);
            case "add" ->  (int) Math.min(pixelA + pixelB, 255);
            case "avg" ->  (int) Math.floor((pixelA + pixelB) * 0.5);
            case "max" ->  (int) Math.max(pixelA, pixelB);
            case "min" ->  (int) Math.min(pixelA, pixelB);


            default -> {
                System.out.println("Invalid operation: not recognized");
                yield 0;
            }
        };

    }

    public static void transform(String function, int[][] matrix) {
        /* transform int[][] -> int[][] using the specified function*/
        for (int h = 0; h < matrix.length; h++) {
            for (int w = 0; w < matrix[0].length; w++) {
                switch (function) {

                    case "sqrt" -> matrix[h][w] = (int) Math.sqrt(matrix[h][w]);
                    case "log" -> matrix[h][w] = (matrix[h][w] == 0) ? (int) Math.log(matrix[h][w] + 1) : (int) Math.log(matrix[h][w]);
                    case "invert" -> matrix[h][w] = 255 - matrix[h][w];
                    default -> {
                        System.out.println("Invalid operation: not recognized");
                    }//end default
                }//end switch
            }//end w
        }//endh
    }



}
