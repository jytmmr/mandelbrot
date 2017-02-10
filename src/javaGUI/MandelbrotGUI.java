import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;

/**
 * Created by Jay on 2/9/2017.
 */
public class MandelbrotGUI {
    public static void main(String[] args) {
//        JFrame frame = new JFrame("Mandelbrot Image Viewer");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//
//        frame.getContentPane().setSize(500,500);
//        frame.pack();
//        Dimension minDim = Toolkit.getDefaultToolkit().getScreenSize();
//        frame.setMinimumSize(minDim);
//
//        JPanel panel = new JPanel();
//
//        panel.addMouseListener();
//        panel.add(new JLabel(new ImageIcon("image.png")));
//
//
//        frame.add(panel);
//
//        frame.setVisible(true);
        try {
            //ProcessBuilder pb = new ProcessBuilder("echo", "This is ProcessBuilder Example from JCG");
            //Process compile = Runtime.getRuntime().exec("gcc -o mandelbrot mandelbrotcalculator.c -pthread -lm ");
            //ompile.waitFor();
            //Process run = Runtime.getRuntime().exec("./mandelbrot");
            //run.waitFor();
            //System.out.println(run.getInputStream();
            System.out.println("done");


        }
        catch (Exception e){
            e.printStackTrace();
        }

        MandelbrotImageViewer mandelbrotImageViewer = new MandelbrotImageViewer();



    }
}
