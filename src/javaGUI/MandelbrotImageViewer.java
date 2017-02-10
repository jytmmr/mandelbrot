import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * Created by Jay on 2/9/2017.
 */
public class MandelbrotImageViewer  extends JFrame{
    MandelbrotImagePanel mandelbrotImagePanel;

    public MandelbrotImageViewer(){


        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setSize(500,500);
        this.pack();
        Dimension minDim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setMinimumSize(minDim);

        mandelbrotImagePanel = new MandelbrotImagePanel("C:/Users/Jay/CLionProjects/340Mandelbrot/mandelbrot/src/javaGUI/image.png");
        addMenu();




        this.add(mandelbrotImagePanel);


        this.setVisible(true);
    }

    private void addMenu(){
        JMenuBar menuBar = new JMenuBar();
        JButton generateButton = new JButton("Generate");
        //generateButton.setVisible(false);
        menuBar.add(generateButton);
        generateButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                System.out.println("Clicked");
                System.out.println("" + mandelbrotImagePanel.getMinX());
                System.out.println("" + mandelbrotImagePanel.getMaxX());
                System.out.println("" + mandelbrotImagePanel.getMinY());
                System.out.println("" + mandelbrotImagePanel.getMaxY());


            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                super.mousePressed(mouseEvent);
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                super.mouseReleased(mouseEvent);
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                super.mouseEntered(mouseEvent);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                super.mouseExited(mouseEvent);
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
                super.mouseWheelMoved(mouseWheelEvent);
            }

            @Override
            public void mouseDragged(MouseEvent mouseEvent) {
                super.mouseDragged(mouseEvent);
            }

            @Override
            public void mouseMoved(MouseEvent mouseEvent) {
                super.mouseMoved(mouseEvent);
            }

            @Override
            public int hashCode() {
                return super.hashCode();
            }

            @Override
            public boolean equals(Object o) {
                return super.equals(o);
            }

            @Override
            protected Object clone() throws CloneNotSupportedException {
                return super.clone();
            }

            @Override
            public String toString() {
                return super.toString();
            }

            @Override
            protected void finalize() throws Throwable {
                super.finalize();
            }
        });
        this.setJMenuBar(menuBar);
    }
}
