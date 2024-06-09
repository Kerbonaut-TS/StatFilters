package com.babelcoding.StatFilters;

public class MatrixOps {

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

        int mean= MatrixOps.mean(matrix);
        return MatrixOps.std_dev(matrix, mean);

    }//end standard deviation

    static public double std_dev(int [][] matrix, double mean){

        int avg= (int) mean ;

        double sum=0;

        for(int j=0; j<matrix.length;j++){
            for(int i=0;i<matrix[0].length;i++){

                sum=sum + Math.pow(matrix[j][i]-avg, 2);

            }//i
        }//j

        double n = matrix[0].length*matrix.length;
        return Math.sqrt(sum/n);

    }//end standard deviation

    static public void standardise(int[][] matrix){

        //calculate z variable
        double mean = MatrixOps.mean(matrix);
        double sigma = MatrixOps.std_dev(matrix);
        MatrixOps.standardise(matrix, mean, sigma);


    }//end standardize

    static public void standardise(int[][] matrix, double mean, double sigma){

        for(int h=0; h<matrix.length; h++){
            for(int w=0; w<matrix[0].length; w++){
                matrix[h][w]= (int) Math.round((matrix[h][w]-mean)/sigma);

            }//i
        }//j

    }//end standardize


    public static int getMaxValue(int[][] matrix) {
        int maxValue = matrix[0][0];
        for (int j = 0; j < matrix.length; j++) {
            for (int i = 0; i < matrix[j].length; i++) {
                if (matrix[j][i] > maxValue) {
                    maxValue = matrix[j][i];
                }
            }
        }
        return maxValue;
    }

    public static int getMinValue(int[][] matrix) {
        int minValue = matrix[0][0];

        for (int[] number : matrix) {
            for (int j : number) {
                if (j < minValue) {
                    minValue = j;
                }
            }
        }
        return minValue ;

    }

}
