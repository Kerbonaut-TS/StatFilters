package filters;

public class Stats {

    public static int apply(String operation, int pixelA, int pixelB){

        return switch (operation) {
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

    static private int pixel_diff(int pixelA, int pixelB) {

        return (int) Math.max(pixelA - pixelB, 0);
    }

    static private int pixel_add(int pixelA, int pixelB) {

        return (int)  Math.min(pixelA + pixelB, 255);
    }

    static private int pixel_avg(int pixelA, int pixelB) {

        return (int)  Math.floor((pixelA + pixelB)*0.5);
    }

    static private int pixel_max(int pixelA, int pixelB) {

        return (int)  Math.max(pixelA, pixelB);
    }

    static private int pixel_min(int pixelA, int pixelB) {

        return (int)  Math.min(pixelA, pixelB);
    }





}
