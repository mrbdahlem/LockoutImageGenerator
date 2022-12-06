import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageGenerator
{
    private final int rows;
    private final int cols;

    private final int rowHeight;
    private final int colWidth;

    private final BufferedImage img;

    public ImageGenerator(String code, int width, int height, int numRows, int numCols) {
        this.img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        this.rows = numRows;
        this.cols = numCols;

        // Determine the space available in each space
        this.rowHeight = img.getHeight() / rows;
        this.colWidth = img.getWidth() / cols;

        drawText(code);
    }


    public void drawText(String text) {
        Graphics g = img.getGraphics();

        // Shrink the text until it fits in the space available
        float div = 1.0F;
        Rectangle2D rect;
        do {
            Font font = g.getFont().deriveFont(rowHeight / div);
            g.setFont(font);

            FontMetrics metrics = g.getFontMetrics();
            rect = metrics.getStringBounds(text, g);
            div++;
        } while (rect.getHeight() > rowHeight || rect.getWidth() > colWidth);

        // Calculate the offsets to center the text in each space
        int offsetX = (colWidth / 2) - (int)(rect.getWidth() / 2);
        int offsetY =  (rowHeight / 2) + (int)(rect.getHeight() / 2);

        // Draw the text in each space
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int x = (col * colWidth) + offsetX;
                int y = (row * rowHeight) + offsetY;
                g.drawString(text, x, y);
            }
        }

        g.dispose();
    }

    public void saveImage(String filename) throws IOException {
        ImageIO.write(img, "png", new File(filename));
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java ImageGenerator A1 CODE");
            System.out.println("   where LOCK is the lock number and CODE is the code to hide in the image");
            System.out.println("   ie. java ImageGenerator A1 356");
            System.out.println("   Will generate a file named A1.png with the code A1-356 hidden in it");
            args = new String[]{"A1", "356"};
        }

        String lock = args[0];
        String code = lock+"-"+args[1];

        ImageGenerator gen = new ImageGenerator(code, 400, 400, 3, 2);

        String filename = lock + ".png";
        try {
            gen.saveImage(filename);
            System.out.println("Saved " + filename);
        } catch (IOException e) {
            System.err.println("Could not save image: " + e.getMessage());
        }
    }
}
