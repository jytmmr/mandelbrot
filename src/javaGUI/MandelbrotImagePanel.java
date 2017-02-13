

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Jay on 2/9/2017.
 */
public class MandelbrotImagePanel extends JLayeredPane implements MouseMotionListener,MouseListener{

    MandelbrotImageGenerator mandelbrotImageGenerator;

    private final String imageName = "image.png";
    private final int X_PIXELS = 3500;
    private final int Y_PIXELS = 2000;

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
    public MandelbrotImagePanel(MandelbrotImageViewer frame, MandelbrotImageGenerator mandelbrotImageGenerator){
        //this.add(new JLabel(new ImageIcon(imageName)));
        System.out.println(imageName);
        this.frame = frame;
        this.mandelbrotImageGenerator = mandelbrotImageGenerator;
        this.mandelbrotImageGenerator.generateStandardImage();

        this.addMouseListener(this);
        this.addMouseMotionListener(this);


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
            BufferedImage background = ImageIO.read(new File(imageName));
            g.drawImage(background, 0,0, this.getWidth(), this.getHeight(), null);
        }
        catch(IOException e){
            e.printStackTrace();
        }
        g2.setStroke(new BasicStroke(3));
        g2.setColor(Color.green);

        double height = 1.0 * Y_PIXELS / X_PIXELS * (maxX - minX);
        g2.drawRoundRect( minX, minY, maxX - minX, (int)height,8,8);

    }

    public void clear(){
        minX = -1;
        minY = -1;
        maxX = -1;
        maxY = -1;
        super.paint(getGraphics());
        rectangleDrawn = false;
        frame.disableButtons();
    }

    public void generateNewZoomed(){
        double width = this.getWidth();
        double height = this.getHeight();
        mandelbrotImageGenerator.generateZoomedImage(-2,.5,-0.5,0.5);

        System.out.println("Generating new image");




    }

    public void regenerateImage(){
        super.paint(getGraphics());
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
        System.out.println("Mouse pressed at " + selectionPoint.getX() + ", " + selectionPoint.getY());
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




}
