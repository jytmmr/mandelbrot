import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by Jay on 2/12/2017.
 */
public class MandelbrotImageGenerator{

    final String compileString = "gcc -o mandelbrot mandelbrotcalculator.c -pthread -lm ";
    MandelbrotImageViewer mandelbrotImageViewer;
    MandelbrotImagePanel mandelbrotImagePanel;

    public MandelbrotImageGenerator(MandelbrotImageViewer mandelbrotImageViewer){
        this.mandelbrotImageViewer = mandelbrotImageViewer;
        this.mandelbrotImagePanel = mandelbrotImagePanel;

    }

    public boolean generateStandardImage(){
        try {
            //ProcessBuilder pb = new ProcessBuilder("echo", "");
            Process compile = Runtime.getRuntime().exec("gcc -o CompiledMandelbrot mandelbrotcalculator.c -pthread -lm ");
            compile.waitFor();
            Process run = Runtime.getRuntime().exec("./CompiledMandelbrot '-2.5' '1' '-1' '1'");
            run.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(run.getInputStream()));
            while(reader.readLine() != null){
                //mandelbrotImageViewer.displayStatus(reader.readLine());
                System.out.println(reader.readLine());
            }

            System.out.println("done");

            displayNewImage();


        }
        catch (Exception e){
            e.printStackTrace();
            mandelbrotImageViewer.displayStatus("Something went wrong.  Please try to generate the image again.");
            return false;
        }

        return true;
    }

    public boolean generateZoomedImage(double xMin, double xMax, double yMin, double yMax){
        try {
            //ProcessBuilder pb = new ProcessBuilder("echo", "");
            Process compile = Runtime.getRuntime().exec("gcc -o CompiledMandelbrot mandelbrotcalculator.c -pthread -lm ");
            compile.waitFor();
            Process run = Runtime.getRuntime().exec("./CompiledMandelbrot'" + xMin + "' '" + xMax + "' '" + yMin + "' '" + yMax + "'");
            run.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(run.getInputStream()));
            while(reader.readLine() != null){
                //mandelbrotImageViewer.displayStatus(reader.readLine());
                System.out.println(reader.readLine());
            }

            System.out.println("Image Generated");

            displayNewImage();


        }
        catch (Exception e){
            e.printStackTrace();
            mandelbrotImageViewer.displayStatus("Something went wrong.  Please try to generate the image again.");
            return false;
        }

        return true;
    }

    public void displayNewImage(){
        mandelbrotImageViewer.regenerateImage();
    }

}

