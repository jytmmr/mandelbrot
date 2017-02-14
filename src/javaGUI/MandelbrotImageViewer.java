import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Jay on 2/9/2017.
 */
public class MandelbrotImageViewer  extends JFrame{
    MandelbrotImagePanel mandelbrotImagePanel;

    JButton generateButton;
    JButton clearButton;
    JButton originalButton;
    JLabel menuLabel;
    public MandelbrotImageViewer(){


        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setSize(500,500);
        this.pack();
        Dimension dimension = new Dimension( 1750,1040);
        this.setMinimumSize(dimension);
        this.setResizable(false);

        mandelbrotImagePanel = new MandelbrotImagePanel(this);
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

    public void enableOriginalButton(){
        this.originalButton.setEnabled(true);
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
                mandelbrotImagePanel.clearAndRegenerate();
                disableButtons();
            }
        });


        originalButton = new JButton("Original");
        originalButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                mandelbrotImagePanel.generateStandardImage();
                originalButton.setEnabled(false);
            }
        });
        originalButton.setEnabled(false);

        menuBar.add(originalButton);

        menuLabel = new JLabel();
        menuBar.add(menuLabel);

        disableButtons();

        this.setJMenuBar(menuBar);
    }
}
