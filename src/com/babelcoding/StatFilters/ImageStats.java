package com.babelcoding.StatFilters;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ImageStats {

    HashMap<String, Double> stats;
    public String[] metrics = new String[]{"red", "blue", "green", "mean", "std.dev", "hue", "saturation", "brightness", "entropy", "baricenterRx", "baricenterRy", "baricenterGx", "baricenterGy", "baricenterBx", "baricenterBy"};


    public ImageStats() {

        this.stats = new HashMap<String, Double>();

    }

    public HashMap<String, Double> getStats(Tile tile, String[] metrics) {
        int[] baricenter_R;

        if (!(this.stats.containsKey("red")) & (this.stats.containsKey("green")) & (this.stats.containsKey("blue"))) {
            this.refresh(tile);
        }

        for (String m : metrics) {
            if (this.stats.containsKey(m)) {

                //already calculated, do nothing
            } else {

                switch (m) {
                    case "red":
                        break;
                    case "green":
                        break;
                    case "blue":
                        break;
                    case "mean":
                        this.stats.put("mean", (double) this.mean(tile));
                        break;
                    case "std.dev":
                        this.stats.put("std.dev", this.std_dev(tile));
                        break;
                    case "hue":
                        this.stats.put("hue", this.hue(this.stats.get("red"), this.stats.get("green"), this.stats.get("blue")));
                        break;
                    case "saturation":
                        this.stats.put("saturation", this.saturation(this.stats.get("red"), this.stats.get("green"), this.stats.get("blue")));
                        break;
                    case "brightness":
                        this.stats.put("brightness", this.brightness(this.stats.get("red"), this.stats.get("green"), this.stats.get("blue")));
                        break;
                    case "baricenterRx":
                        this.stats.put("baricenterRx", (double) this.baricenter(tile, "red", "X"));
                        break;
                    case "baricenterRy":
                        this.stats.put("baricenterRy", (double) this.baricenter(tile, "red", "Y"));
                        break;
                    case "baricenterGx":
                        this.stats.put("baricenterGx", (double) this.baricenter(tile, "green", "X"));
                        break;
                    case "baricenterGy":
                        this.stats.put("baricenterGy", (double) this.baricenter(tile, "green", "Y"));
                        break;
                    case "baricenterBx":
                        this.stats.put("baricenterBx", (double) this.baricenter(tile, "blue", "X"));
                        break;
                    case "baricenterBy":
                        this.stats.put("baricenterBy", (double) this.baricenter(tile, "blue", "Y"));
                        break;
                    case "entropy":
                        this.stats.put("entropy", this.entropy(tile));
                        break;


                }//end switch
            }//end else
        }//end for metrics

        return stats;

    }


    public HashMap<String, Double> getStats(Tile tile) {
        /* option to return all metrics*/
        return this.getStats(tile, this.metrics);

    }

    public double getStat(Tile tile, String metric) {
        /* option to return a single metric metrics*/
        HashMap<String, Double> dict = this.getStats(tile, new String[]{metric});
        return dict.get(metric);

    }

    public void setStats(String key, double value) {

        this.stats.put(key, value);
    }

    public void refresh(Tile tile) {

        this.stats = new HashMap<String, Double>();

        for (String c : tile.channels) {
            this.stats.put(c, (double) MatrixOps.mean(tile.getMatrix(c)));

        }
    }


    private double mean(Tile tile) {

        double sum = 0;
        int count = 0;

        for (String c : tile.channels) {
            if (this.stats.containsKey(c)) {
                sum = sum + this.stats.get(c);
            } else {
                this.stats.put(c, (double) MatrixOps.mean(tile.getMatrix(c)));
                sum = sum + this.stats.get(c);
            }
            count++;

        }
        return sum / count;

    }

    private double std_dev(Tile tile) {

        double sum_sigma = 0;

        for (String c : tile.channels) {
            if (this.stats.containsKey(c)) {
                //pass to std.dev function the precalculated mean
                sum_sigma = sum_sigma + MatrixOps.std_dev(tile.getMatrix(c), this.stats.get(c));
            } else {
                sum_sigma = sum_sigma + MatrixOps.std_dev(tile.getMatrix(c));
            }

        }//end for

        return sum_sigma;


    }//end std dev

    private double hue(double R, double G, double B) {

        double rp, gp, bp, max, min, hue;

        rp = R / 255f;
        gp = G / 255f;
        bp = B / 255f;

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

    private double saturation(double R, double G, double B) {

        double rp, gp, bp, max, min, saturation;

        rp = (float) R / 255f;
        gp = (float) G / 255f;
        bp = (float) B / 255f;

        max = Math.max(Math.max(rp, gp), bp);
        min = Math.min(Math.min(rp, gp), bp);

        saturation = (max == 0) ? 0 : ((max - min) / max);

        return saturation;

    }

    private double brightness(double R, double G, double B) {

        double rp, gp, bp, max, brightness;

        rp = R / 255f;
        gp = G / 255f;
        bp = B / 255f;

        brightness = Math.max(Math.max(rp, gp), bp);

        return brightness;

    }


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

        //TODO 0-255 rescale


        return entropy;
    }

    private int baricenter(Tile tile, String channel, String axis) {
        /* returns the baricenter for a specific channel in x,y coordinates*/
        double baricenterX = 0, baricenterY = 0, weights_sum = 0;
        int c_angle = 0;

        switch (channel) {
            case "red" -> c_angle = 0 ;
            case "green" -> c_angle = 120;
            case "blue" -> c_angle = 240;
        }
        if (Objects.equals(axis, "X")) {
            //horizontal scan
            for (int x = 0; x < tile.getWidth(); x++) {
                double distance_tot = 0;
                for (int y = 0; y < tile.getHeight(); y++) {

                    int r = tile.getMatrix("red")[y][x];
                    int g = tile.getMatrix("green")[y][x];
                    int b = tile.getMatrix("blue")[y][x];

                    double hue = this.hue(r, g, b);
                    double angle_distance = Math.abs(hue - c_angle);
                    angle_distance =  ((hue >= 0) & (angle_distance<=10))? 1: 0;
                    distance_tot = distance_tot + angle_distance;

                }

                //update baricenter with (coordinate * weight)
                double weight = distance_tot;
                weights_sum = weights_sum + weight;
                baricenterX = baricenterX + x * weight;

            }
            //calculate X coordinate

            return (int) Math.round(baricenterX / weights_sum);
        } else {

            //vertical scan
            for (int y = 0; y < tile.getHeight(); y++) {
                double distance_tot = 0;

                for (int x = 0; x < tile.getWidth(); x++) {
                    int r = tile.getMatrix("red")[y][x];
                    int g = tile.getMatrix("green")[y][x];
                    int b = tile.getMatrix("blue")[y][x];

                    double hue = this.hue(r, g, b);
                    double angle_distance = Math.abs(hue - c_angle);
                    angle_distance =  ((hue >= 0) & (angle_distance<=10))? 1: 0;
                    distance_tot = distance_tot + angle_distance;
                }
                //update baricenter with (coordinate * weight)
                double weight = distance_tot;
                weights_sum = weights_sum + weight;
                baricenterY = baricenterY + y * weight;
            }
            //calculate Y Coordinate
            return (int) Math.round(baricenterY / weights_sum);

        }
    }


    public static int apply(String operator, int pixelA, int pixelB) {
        /* applies an operator to two pixel values   int-> int */

        return switch (operator) {
            case "diff" -> (int) Math.max(pixelA - pixelB, 0);
            case "add" -> (int) Math.min(pixelA + pixelB, 255);
            case "avg" -> (int) Math.floor((pixelA + pixelB) * 0.5);
            case "max" -> (int) Math.max(pixelA, pixelB);
            case "min" -> (int) Math.min(pixelA, pixelB);


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

                    case "sqrt" -> matrix[h][w] = (int) Math.round(Math.sqrt(matrix[h][w]) / Math.sqrt(255) * 255);
                    case "log" ->
                            matrix[h][w] = (matrix[h][w] == 0) ? 0 : (int) Math.round(Math.log(matrix[h][w]) / Math.log(255) * 255);
                    case "invert" -> matrix[h][w] = 255 - matrix[h][w];

                    default -> {
                        System.out.println("Invalid operation: not recognized");
                    }//end default
                }//end switch
            }//end w
        }//endh
    }


}