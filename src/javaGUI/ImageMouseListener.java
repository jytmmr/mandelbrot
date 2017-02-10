import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * Created by Jay on 2/9/2017.
 */
public class ImageMouseListener implements MouseListener,MouseMotionListener{
    JComponent component;
    int startX;
    int startY;
    int endX;
    int endY;



    public ImageMouseListener(JPanel comp){
        this.component = comp;

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

    @Override
    public void mousePressed(MouseEvent e){

        startX = e.getX();
        startY = e.getY();

        System.out.println("Mouse pressed at " + startX + ", " + startY);
    }
    @Override
    public void mouseReleased(MouseEvent e){

    }
    @Override
    public void mouseDragged(MouseEvent e){
        endX = e.getX();
        endY = e.getY();
        System.out.println("Mouse pressed at " + startX + ", " + startY);

    }

    @Override
    public void mouseMoved(MouseEvent e){

    }


}
