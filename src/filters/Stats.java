package filters;

import java.awt.print.Printable;

public class Stats {

    public static int apply(String operator, int pixelA, int pixelB) {
        /* applies an operator to two pixel values*/

        return switch (operator) {
            case "diff" -> Stats.pixel_diff(pixelA, pixelB);
            case "add" -> Stats.pixel_add(pixelA, pixelB);
            case "avg" -> Stats.pixel_avg(pixelA, pixelB);
            case "max" -> Stats.pixel_max(pixelA, pixelB);
            case "min" -> Stats.pixel_min(pixelA, pixelB);


            default -> {
                System.out.println("Invalid operation: not recognized");
                yield 0;
            }
        };

    }

    public static void transform(String function, int[][] matrix) {
        /* transform int[][] -> int[][] using the specified function*/

        switch (function) {

            case "sqrt" -> Stats.sqrt(matrix);

            default -> {
                System.out.println("Invalid operation: not recognized");
            }

        }
        ;
    }

    public static int calculate(String function, int[][] matrix) {
        /* aggregation function: calculates int[][] -> Y  */

        return switch (function) {

            case "mean" -> Stats.mean(matrix);

            default -> {
                System.out.println("Invalid operation: not recognized");
                yield 0;
            }

        };
    }


    static private int pixel_diff(int pixelA, int pixelB) {

        return (int) Math.max(pixelA - pixelB, 0);
    }

    static private int pixel_add(int pixelA, int pixelB) {

        return (int) Math.min(pixelA + pixelB, 255);
    }

    static private int pixel_avg(int pixelA, int pixelB) {

        return (int) Math.floor((pixelA + pixelB) * 0.5);
    }

    static private int pixel_max(int pixelA, int pixelB) {

        return (int) Math.max(pixelA, pixelB);
    }

    static private int pixel_min(int pixelA, int pixelB) {

        return (int) Math.min(pixelA, pixelB);
    }


    // POOLING FILTERS  ========================================================================

    public static void sqrt(int[][] matrix) {

        for (int h = 0; h < matrix.length; h++) {
            for (int w = 0; w < matrix[0].length; w++) {

                matrix[h][w] = (int) Math.sqrt(matrix[h][w]);

            }//end width
        }//end height

    }

    public static int mean(int[][] matrix) {

        double sum = 0;

        double n = matrix.length * matrix[0].length;

        //sum
        for (int h = 0; h < matrix.length; h++) {
            for (int w = 0; w < matrix[0].length; w++) {

                sum = sum + matrix[h][w];

            }
        }
        return (int) Math.round((sum) / n);

    }




}
