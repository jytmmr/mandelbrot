

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

/**
 * Created by Jay on 2/9/2017.
 */
public class MandelbrotImagePanel extends JLayeredPane implements MouseMotionListener,MouseListener{



    private final String imageName = "image.png";
    private final int X_PIXELS = 1750;
    private final int Y_PIXELS = 1000;

    double cartesianXMin;
    double cartesianXMax;
    double cartesianYMin;
    double cartesianYMax;

    boolean rectangleDrawn = false;
    int minX;
    int minY;
    int maxX;
    int maxY;

    Point selectionPoint;

    boolean mousePressed;

    MandelbrotImageViewer frame;
    public MandelbrotImagePanel(MandelbrotImageViewer frame){
        //this.add(new JLabel(new ImageIcon(imageName)));
        System.out.println(imageName);
        this.frame = frame;

        this.addMouseListener(this);
        this.addMouseMotionListener(this);

        generateStandardImage();

        cartesianXMin = -2.5;
        cartesianXMax = 1;
        cartesianYMin = -1;
        cartesianYMax = 1;

        mousePressed = true;
        minX = -1;
        minY = -1;
        maxX = -1;
        maxY = -1;
    }


    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        super.paintComponent(g);
        try{
            //System.out.println("Setting background");
            BufferedImage background = ImageIO.read(new File(imageName));
            g.drawImage(background, 0,0, this.getWidth(), this.getHeight(), null);
        }
        catch(IOException e){
            e.printStackTrace();
        }
        g2.setStroke(new BasicStroke(1));
        g2.setColor(Color.blue);

        double height = 1.0 * Y_PIXELS / X_PIXELS * (maxX - minX);
        g2.drawRoundRect( minX, minY, maxX - minX, (int)height,4,4);

    }

    public void clearAndRegenerate(){
        minX = -1;
        minY = -1;
        maxX = -1;
        maxY = -1;
        super.paint(getGraphics());
        rectangleDrawn = false;
        //frame.disableButtons();
    }

    public void generateNewZoomed(){
        double width = this.getWidth();
        double height = this.getHeight();
        System.out.println("The width is " + width);
        System.out.println("The height is " + height);




        double cartesianXWidth = Math.abs(cartesianXMax - cartesianXMin);
        double cartesianYHeight = Math.abs(cartesianYMax - cartesianYMin);

        double viewerWidthIncrement = cartesianXWidth / width;
        double viewerHeightIncrement = cartesianYHeight / height;
        System.out.println("Viewer width " +  viewerWidthIncrement);
        System.out.println("Viewer height " +  viewerHeightIncrement);


        DecimalFormat df = new DecimalFormat("###.##########");
        double tempCartXMin = cartesianXMin;
        cartesianXMin = Double.valueOf(df.format(viewerWidthIncrement * minX + cartesianXMin));
        cartesianXMax = Double.valueOf(df.format(viewerWidthIncrement * maxX + tempCartXMin));

        cartesianYMin = Double.valueOf(df.format(viewerHeightIncrement * minY + cartesianYMin));
        cartesianYMax = Double.valueOf(df.format(cartesianYMin + (1.0 * Y_PIXELS/X_PIXELS * (Math.abs(cartesianXMax - cartesianXMin)))));

        System.out.println("C X min " +  cartesianXMin);
        System.out.println("C X max " +  cartesianXMax);
        System.out.println("C Y min " +  cartesianYMin);
        System.out.println("C Y max " +  cartesianYMax);

        System.out.println("X min" +  getMinX());
        System.out.println("X max" +  getMaxX());
        System.out.println("Y min" +  getMinY());
        System.out.println("Y max" +  getMaxY());


        generateZoomedImage(cartesianXMin,cartesianXMax,cartesianYMin,cartesianYMax);
        minX = -1;
        minY = -1;
        maxX = -1;
        maxY = -1;
        rectangleDrawn = false;
    }




    public int getMinX() {
        return minX;
    }

    public int getMinY() {
        return minY;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMaxY() {
        return maxY;
    }


    @Override
    public void mousePressed(MouseEvent e){

        selectionPoint = e.getPoint();
        mousePressed = true;
        //System.out.println("Mouse pressed at " + selectionPoint.getX() + ", " + selectionPoint.getY());
    }
    @Override
    public void mouseReleased(MouseEvent e){
        if(rectangleDrawn){
            frame.enableButtons();
        }
    }
    @Override
    public void mouseDragged(MouseEvent e){



        minX = Math.min(e.getX(), (int)selectionPoint.getX());
        minY = Math.min(e.getY(), (int)selectionPoint.getY());
        maxX = Math.max(e.getX(), (int)selectionPoint.getX());
        maxY = Math.max(e.getY(), (int)selectionPoint.getY());


        if(mousePressed == true) {
            super.paint(getGraphics());
            rectangleDrawn = true;

            //System.out.println("Mouse dragged to " + endX + ", " + endY);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e){

    }

    @Override
    public void mouseEntered(MouseEvent e){

    }

    @Override
    public void mouseClicked(MouseEvent e){

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    public boolean generateStandardImage(){
        try {
            //ProcessBuilder pb = new ProcessBuilder("echo", "");
            System.out.println("Running mandelbrotcalculator.c with arguments");
            Process compile =
                    new ProcessBuilder(new String[] {"bash", "-c", "gcc -o CompiledMandelbrot mandelbrotcalculator.c -pthread -lm "})
                            .redirectErrorStream(true)
                            .start();
            compile.waitFor();

            Process run =
                    new ProcessBuilder(new String[] {"bash", "-c", "./CompiledMandelbrot '-2.5' '1' '-1' '1'"})
                            .redirectErrorStream(true)
                            .start();
            run.waitFor();

            System.out.println("Generated Standard Image.  Painting");

            clearAndRegenerate();
            cartesianXMin = -2.5;
            cartesianXMax = 1;
            cartesianYMin = -1;
            cartesianYMax = 1;

        }
        catch (Exception e){
            e.printStackTrace();
            frame.displayStatus("Something went wrong.  Please try to generate the image again.");
            return false;
        }

        return true;
    }

    public boolean generateZoomedImage(double xMin, double xMax, double yMin, double yMax) {
        try {
            System.out.println("Running mandelbrotcalculator.c with ZOOMED arguments");


            Process compile =
                    new ProcessBuilder(new String[] {"bash", "-c", "gcc -o CompiledMandelbrot mandelbrotcalculator.c -pthread -lm "})
                            .redirectErrorStream(true)
                            .start();
            compile.waitFor();
            System.out.println("./CompiledMandelbrot '" + xMin + "' '" + xMax + "' '" + yMin + "' '" + yMax + "'");
            Process run =
                    new ProcessBuilder(new String[] {"bash", "-c", "./CompiledMandelbrot '" + xMin + "' '" + xMax + "' '" + yMin + "' '" + yMax + "'"})
                            .redirectErrorStream(true)
                            .start();
            run.waitFor();


            System.out.println("Generated Zoomed Image.  Painting.");

            frame.enableOriginalButton();


            clearAndRegenerate();


        } catch (Exception e) {
            e.printStackTrace();
            frame.displayStatus("Something went wrong.  Please try to generate the image again.");
            return false;
        }

        return true;
    }


}
