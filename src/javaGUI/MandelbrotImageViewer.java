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

    /**
     * Constructor for MandelbrotImageViewer
     * inits new ImagePanel, adds menu buttons...
     */
    public MandelbrotImageViewer(){


        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setSize(500,500);
        this.pack();
        this.setSize(new Dimension( (int)(0.75 * java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth()),
                (int)(0.75 *java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight())));


        mandelbrotImagePanel = new MandelbrotImagePanel(this);
        addMenu();
        this.add(mandelbrotImagePanel);
        this.setVisible(true);
    }

    /**
     * Enables generate and clear buttons
     */
    public void enableButtons(){
        generateButton.setEnabled(true);
        clearButton.setEnabled(true);
    }
    /**
     * Disables generate and clear buttons
     */
    public void disableButtons(){
        generateButton.setEnabled(false);
        clearButton.setEnabled(false);
    }

    /**
     * displays new text of JLabel
     * @param status
     */
    public void displayStatus(String status){
        this.menuLabel.setText(status);
    }

    public void enableOriginalButton(){
        this.originalButton.setEnabled(true);
    }


    /**
     * Called from Constructor
     * Adds menu buttons, labels
     */
    private void addMenu(){
        JMenuBar menuBar = new JMenuBar();
        generateButton = new JButton("Generate");
        menuBar.add(generateButton);
        generateButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                mandelbrotImagePanel.initZoomedBoundsAndGenerate();
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

        menuLabel = new JLabel("    Click and drag to generate a scaled rectangle to zoom in.  Then, click generate.  Click Original to return to original bound (-2.5, 1, -1,1).");
        menuBar.add(menuLabel);

        disableButtons();

        this.setJMenuBar(menuBar);
    }
}
