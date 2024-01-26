package filters;

public class Utils
{
    public static int [][] copyMatrix(int[][] matrix){

        int[][] output=new int[matrix.length][matrix[0].length];

        for(int i=0; i<matrix.length;i++){
            for(int j=0; j<matrix[0].length; j++) output[i][j]=matrix[i][j];
        }

        return output;
    }//end copyMatrix


    public static int getMaxValue(int[][] numbers) {
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

    public static int getMinValue(int[][] numbers) {
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


    public static void logger(int[] list) {

        System.out.print("{");

        for (int i=0; i<list.length; i++)  System.out.print(list[i]+",");

        System.out.println("}");

    }

    public static void logger(double[][] list) {

        System.out.print("{");

        for (int i=0; i<list.length; i++) {
            for (int y=0; y<list[0].length; y++) {

                System.out.print(list[i][y]+",");

            }

            System.out.println("|");

        }

    }

}
