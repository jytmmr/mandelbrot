import com.sun.corba.se.impl.orbutil.graph.Graph;

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


    int minX;
    int minY;
    int maxX;
    int maxY;

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
    Point selectionPoint;
    String imageName;
    boolean mousePressed;

    public MandelbrotImagePanel(String imageName){
        //this.add(new JLabel(new ImageIcon(imageName)));
        System.out.println(imageName);
        this.imageName = imageName;
        this.addMouseListener(this);
        this.addMouseMotionListener(this);

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
            g.drawImage(background, 0,0, null);
        }
        catch(IOException e){
            e.printStackTrace();
        }
        g2.setStroke(new BasicStroke(5));
        g2.setColor(Color.green);
        g2.drawRoundRect( minX, minY, maxX - minX, maxY - minY,15,15);

    }



    @Override
    public void mousePressed(MouseEvent e){

        selectionPoint = e.getPoint();
        mousePressed = true;
        System.out.println("Mouse pressed at " + selectionPoint.getX() + ", " + selectionPoint.getY());
    }
    @Override
    public void mouseReleased(MouseEvent e){

    }
    @Override
    public void mouseDragged(MouseEvent e){

        minX = Math.min(e.getX(), (int)selectionPoint.getX());
        minY = Math.min(e.getY(), (int)selectionPoint.getY());
        maxX = Math.max(e.getX(), (int)selectionPoint.getX());
        maxY = Math.max(e.getY(), (int)selectionPoint.getY());


        if(mousePressed == true) {
            super.paint(getGraphics());
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
