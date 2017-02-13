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
    JButton generateButton;
    JButton clearButton;
    JLabel menuLabel;
    public MandelbrotImageViewer(){


        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setSize(500,500);
        this.pack();
        MandelbrotImageGenerator mandelbrotImageGenerator = new MandelbrotImageGenerator(this);
        Dimension minDim = new Dimension( (int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 0.75),(int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.75));
        this.setMinimumSize(minDim);

        mandelbrotImagePanel = new MandelbrotImagePanel(this, mandelbrotImageGenerator);
        addMenu();
        this.add(mandelbrotImagePanel);
        this.setVisible(true);
    }

    public void enableButtons(){
        generateButton.setEnabled(true);
        clearButton.setEnabled(true);
    }

    public void disableButtons(){
        generateButton.setEnabled(false);
        clearButton.setEnabled(false);
    }

    public void displayStatus(String status){
        this.menuLabel.setText(status);
    }


    public void regenerateImage(){
        mandelbrotImagePanel.regenerateImage();
    }


    private void addMenu(){
        JMenuBar menuBar = new JMenuBar();
        generateButton = new JButton("Generate");
        menuBar.add(generateButton);
        generateButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                System.out.println("Generate Clicked");
                System.out.println("" + mandelbrotImagePanel.getMinX());
                System.out.println("" + mandelbrotImagePanel.getMaxX());
                System.out.println("" + mandelbrotImagePanel.getMinY());
                System.out.println("" + mandelbrotImagePanel.getMaxY());
                mandelbrotImagePanel.generateNewZoomed();
                disableButtons();
            }
        });
        clearButton = new JButton("Clear");
        menuBar.add(clearButton);
        clearButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                mandelbrotImagePanel.clear();
                disableButtons();
            }
        });
        disableButtons();

        menuLabel = new JLabel();
        menuBar.add(menuLabel);

        this.setJMenuBar(menuBar);
    }
}
