package filters;

import java.awt.print.Printable;

 final class Stats {

     final static String[]  channels = {"red", "green", "blue"};

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

     static public void standardise(int[][] matrix){

         //calculate z variable
         double avg = Stats.mean(matrix);
         double stddev = Stats.std_dev(matrix);

         for(int h=0; h<matrix.length; h++){
             for(int w=0; w<matrix[0].length; w++){
                 matrix[h][w]= (int) Math.round((matrix[h][w]-avg)/stddev);

             }//i
         }//j

     }//end standardize

     // AGGREGATION FUNCTIONS  int[][] -> Y ========================================================================

     static public int mean(int[][] matrix) {

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

     static public double std_dev(int [][] matrix){

         int avg= Stats.mean(matrix);
         double sum=0;

         for(int j=0; j<matrix.length;j++){
             for(int i=0;i<matrix[0].length;i++){

                 sum=sum + Math.pow(matrix[j][i]-avg, 2);

             }//i
         }//j

         double n = matrix[0].length*matrix.length;
         return Math.sqrt(sum/n);

     }//end standard deviation


}
