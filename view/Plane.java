package view;

import java.awt.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Stack;
import java.awt.event.ComponentEvent;

public class Plane extends JPanel
{
    final int LY = Constants.LY;
    final int LX = Constants.LX;
    int GRID_SCALE = Constants.GRID_SCALE;
    private ArrayList<Point> points;
    private Stack<Pixel> pixeles, pixelesOrigen;
    private ArrayList<Pixel> pixelesGrilla;
    private GraphicsShape graphic;
    private int index;
    
    public Plane () {
        setLayout(null);
        index = 0;
        pixeles = new Stack<Pixel>();
        pixelesOrigen = new Stack<Pixel>();
        pixelesGrilla = new ArrayList<Pixel>();
        crearGrilla();
        setPreferredSize(new Dimension(GRID_SCALE*LX+1, GRID_SCALE*LY+1));
        setBackground(new Color(250, 250, 250));
    }
    
    public void zoomMas () {
        GRID_SCALE *= 2;
    }
    
    public void zoomMenos () {
        GRID_SCALE /= 2;
    }
    
    public void clearPoints () {
        points = new ArrayList<Point>();
        pixeles = new Stack<Pixel>();
        index = 0;
        repaint();
    }
    
    public void setGraphic (GraphicsShape g) {
        this.graphic = g;
    }
    
    public GraphicsShape getGraphicShape() {
        return graphic;
    }
    
    public void setPoints (ArrayList<Point> points) {
        index = 0;
        this.points = points;
        pixeles.clear();
        repaint();
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        paintGrilla(g);
        int centroX = getWidth() / 2;
        int centroY = getHeight() / 2;
        
        g.setColor(Color.BLACK);
        g.drawLine(0, centroY, getWidth(), centroY);
        g.drawLine(centroX, 0, centroX, getHeight());
        pixeles.forEach((pixel) -> pixel.paintFill(g));
        pixelesOrigen.forEach((pixel) -> pixel.paintFill(g));
        if (graphic != null) graphic.paint((Graphics2D)g);
    }
    
    public void run () {
        Thread h = new Thread() {
            public void run () {
                while (index < points.size()) {
                    pushPixel();
                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {}
                }
            }  
        };
        h.start();
    }
    
    public void pushPixel () {
        if (index == points.size()) return; 
        Point p = points.get(index);
        int mX = LX / 2;
        int mY = LY / 2;
        Pixel pixel = new Pixel((int)p.getX()+mX, -(int)p.getY()+mY, GRID_SCALE);
        pixeles.push(pixel);
        index++;
        repaint();
    }
    
    public void removePixel () {
        if (index == 0) return;
        pixeles.pop();
        index--;
        repaint();
    }
    
    public void crearGrilla () {
        for (int i = 0; i < LX; i++) {
            for (int j = 0; j < LY; j++) {
                Pixel p = new Pixel(i, j, GRID_SCALE);
                pixelesGrilla.add(p);
            }
        }
    }
    
    public void paintGrilla(Graphics g) {
        g.setColor(new Color(117, 117, 117));
        for (int i = 0; i < LX; i++) {
            for (int j = 0; j < LY; j++) {
                g.drawRect(i*GRID_SCALE, j*GRID_SCALE,GRID_SCALE, GRID_SCALE);
            }
        }
    }
    private void paintNumbersPlane (Graphics g) {
        g.setColor(Color.RED);
        g.setFont(new Font("arial", Font.BOLD, 8));
        for (int i = -LX/2; i <= LX/2-1; i++) {
            if (i >= 0 && i <10)
                g.drawString(Integer.toString(i), i*GRID_SCALE + (LX/2-1)*GRID_SCALE+GRID_SCALE/2, LY/2*GRID_SCALE);
            else g.drawString(Integer.toString(i), i*GRID_SCALE + (LX/2-1)*GRID_SCALE, LY/2*GRID_SCALE);
        }
        for (int j = -LY/2; j <= LY/2-1; j++) {
            if (j != 0) {
                g.drawString(Integer.toString(-j), (LX/2-1)*GRID_SCALE, j*GRID_SCALE + (LY/2-1)*GRID_SCALE + GRID_SCALE);
            }
        }
    }
    
    public void paintPixel (int x, int y) {
        Graphics g = getGraphics();
        int mX = LX / 2;
        int mY = LY / 2;
        g.setColor(Color.GREEN);
        g.drawRect(x+mX-1, -y+mY-1, GRID_SCALE, GRID_SCALE);
    }
    
    public void pushPixelOrigen (int x, int y) {
        int mX = LX / 2;
        int mY = LY / 2;
        pixelesOrigen.push(new Pixel(x+mX, -y+mY, GRID_SCALE, Color.RED));
        repaint();
    }
    
    public void popPixelOrigen () {
        pixelesOrigen.pop();
        repaint();
    }
    
    public Pixel peekPixelOrigen () {
        return pixelesOrigen.peek();
    }
    
    public void clearPixelesOrigen () {
        pixelesOrigen = new Stack<Pixel>();
    }
    
    public int sizePixelesOrigen () {
        return pixelesOrigen.size();
    }
    
    public void showPoints() {
        points.forEach((p) -> {
            System.out.println("("+(int)p.getX()+", "+(int)p.getY()+") ");
        });
    }
}
