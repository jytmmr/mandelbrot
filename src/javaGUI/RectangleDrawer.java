import javax.swing.*;
import java.awt.*;

/**
 * Created by Jay on 2/9/2017.
 */
public class RectangleDrawer {
    int startx;
    int starty;
    int endx;
    int endy;
    public RectangleDrawer(JPanel panel){

    }

    public void paint(Graphics g){
        g.drawRect(startx,starty,endx,endy);
    }

}
