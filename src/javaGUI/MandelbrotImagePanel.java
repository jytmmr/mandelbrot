

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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

    /**
     * Constructor
     * @param frame
     */
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

    /**
     * paints the JLayeredPane with image and rectangles
     * invoked by super's paint() method
     * @param g
     */
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

    /**
     * Clears rectangle and repaints image
     */
    public void clearAndRegenerate(){
        minX = -1;
        minY = -1;
        maxX = -1;
        maxY = -1;
        super.paint(getGraphics());
        rectangleDrawn = false;
    }

    /**
     * Generates new bounds for the new image(up to 15 decimal places of precision)
     * Then called the
     */
    public void initZoomedBoundsAndGenerate(){

        double width = this.getWidth();
        double height = this.getHeight();


        double cartesianXWidth = Math.abs(cartesianXMax - cartesianXMin);
        double cartesianYHeight = Math.abs(cartesianYMax - cartesianYMin);

        double viewerWidthIncrement = cartesianXWidth / width;
        double viewerHeightIncrement = cartesianYHeight / height;


        DecimalFormat df = new DecimalFormat("###.###############");
        double tempCartXMin = cartesianXMin;
        cartesianXMin = Double.valueOf(df.format(viewerWidthIncrement * minX + cartesianXMin));
        cartesianXMax = Double.valueOf(df.format(viewerWidthIncrement * maxX + tempCartXMin));

        cartesianYMin = Double.valueOf(df.format(viewerHeightIncrement * minY + cartesianYMin));
        cartesianYMax = Double.valueOf(df.format(cartesianYMin + (1.0 * Y_PIXELS/X_PIXELS * (Math.abs(cartesianXMax - cartesianXMin)))));



        generateZoomedImage(cartesianXMin,cartesianXMax,cartesianYMin,cartesianYMax);
        minX = -1;
        minY = -1;
        maxX = -1;
        maxY = -1;
        rectangleDrawn = false;
    }


    /**
     *  MousePressed, MouseReleased, and mouseDragged methods for drawing rectangles on image
     * @param e
     */
    @Override
    public void mousePressed(MouseEvent e){

        selectionPoint = e.getPoint();
        mousePressed = true;
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

        }
    }


    /**
     * Generates the Original image with standard bounds
     */
    public boolean generateStandardImage(){
        try {
            System.out.println("Running mandelbrotcalculator.c with arguments");

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

    /**
     * Generates a Zoomed image and then paints the new image onto the Panel
     * @param xMin
     * @param xMax
     * @param yMin
     * @param yMax
     * @return
     */

    public boolean generateZoomedImage(double xMin, double xMax, double yMin, double yMax) {
        try {
            System.out.println("Running mandelbrotcalculator.c with ZOOMED arguments");


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


    /**
     * Unused methods
     *
     */


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


}
