import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImageGenerator
{
    private final int rows;
    private final int cols;

    private final int rowHeight;
    private final int colWidth;

    private final BufferedImage img;

    public ImageGenerator(String code, int width, int height, int numRows, int numCols) {
        this.img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        this.rows = numRows;
        this.cols = numCols;

        // Determine the space available in each space
        this.rowHeight = img.getHeight() / rows;
        this.colWidth = img.getWidth() / cols;

        drawText(code);
    }

    public void blank(int pos, Color color) {
        Rectangle area = getArea(pos);

        Graphics g = img.getGraphics();
        g.setColor(color);
        g.fillRect(area.x, area.y, area.width, area.height);

        g.dispose();
    }

    public void hideInRed(int pos, BufferedImage pic) {
        Rectangle area = getArea(pos);

        pic = getImageToFit(pic);

        for (int row = 0; row < rowHeight; row++) {
            for (int col = 0; col < colWidth; col++) {
                Color picColor = new Color(pic.getRGB(col, row));

                Color hideColor = new Color(img.getRGB(col + area.x, row + area.y));
                int red = picColor.getRed();
                if (((hideColor.getRed() + hideColor.getGreen()  + hideColor.getBlue()) / 3) > 127) {
                    red = red | 0b00000001;
                }
                else {
                    red = red & 0b11111110;
                }
                picColor = new Color(red, picColor.getGreen(), picColor.getBlue());

                img.setRGB(col + area.x, row + area.y, picColor.getRGB());
            }
        }
    }

    public void hideInGreen(int pos, BufferedImage pic) {
        Rectangle area = getArea(pos);

        pic = getImageToFit(pic);

        for (int row = 0; row < rowHeight; row++) {
            for (int col = 0; col < colWidth; col++) {
                Color picColor = new Color(pic.getRGB(col, row));

                Color hideColor = new Color(img.getRGB(col + area.x, row + area.y));
                int green = picColor.getGreen();
                if (((hideColor.getRed() + hideColor.getGreen()  + hideColor.getBlue()) / 3) > 127) {
                    green = green | 0b00000001;
                }
                else {
                    green = green & 0b11111110;
                }
                picColor = new Color(picColor.getRed(), green, picColor.getBlue());

                img.setRGB(col + area.x, row + area.y, picColor.getRGB());
            }
        }
    }

    public void hideInBlue(int pos, BufferedImage pic) {
        Rectangle area = getArea(pos);

        pic = getImageToFit(pic);

        for (int row = 0; row < rowHeight; row++) {
            for (int col = 0; col < colWidth; col++) {
                Color picColor = new Color(pic.getRGB(col, row));

                Color hideColor = new Color(img.getRGB(col + area.x, row + area.y));
                int blue = picColor.getBlue();
                if (((hideColor.getRed() + hideColor.getGreen()  + hideColor.getBlue()) / 3) > 127) {
                    blue = blue | 0b00000001;
                }
                else {
                    blue = blue & 0b11111110;
                }
                picColor = new Color(picColor.getRed(), picColor.getGreen(), blue);

                img.setRGB(col + area.x, row + area.y, picColor.getRGB());
            }
        }
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

    private int getRow(int pos) {
        return (pos % rows);
    }

    private int getCol(int pos) {
        return (pos / rows);
    }

    private Rectangle getArea(int pos) {
        int row = getRow(pos);
        int col = getCol(pos);

        return new Rectangle(col * this.colWidth, row * this.rowHeight, colWidth, rowHeight);
    }

    private BufferedImage getImageToFit(BufferedImage img) {
        BufferedImage scaled = new BufferedImage(colWidth, rowHeight, BufferedImage.TYPE_INT_ARGB);

        Graphics g = scaled.getGraphics();

        double areaRatio = ((double)colWidth)/rowHeight;
        double imageRatio = ((double)img.getWidth())/img.getHeight();

        int sliceWidth;
        int sliceHeight;
        if (areaRatio > imageRatio) {
            sliceWidth = img.getWidth();
            sliceHeight = (int)(sliceWidth / areaRatio);
        }
        else {
            sliceHeight = img.getHeight();
            sliceWidth = (int)(sliceHeight * areaRatio);
        }

        int sliceTop = (img.getHeight() - sliceHeight) / 2;
        int sliceLeft = (img.getWidth() - sliceWidth) / 2;
        g.drawImage(img, 0, 0, colWidth, rowHeight,
                sliceLeft, sliceTop, sliceLeft + sliceWidth, sliceTop + sliceHeight, null);

        g.dispose();

        return scaled;
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

        int numRows = 3;
        int numCols = 2;

        ImageGenerator gen = new ImageGenerator(code, 400, 400, numRows, numCols);

        // create an array of the possible algorithms
        int[] algorithms = new int[numRows * numCols];
        for (int i = 0; i < algorithms.length; i++) {
            algorithms[i] = i;
        }

        // randomly swap the possible algorithm positions
        for (int i = 0; i < algorithms.length; i++) {
            int pos = (int)(Math.random() * algorithms.length);
            int tmp = algorithms[i];
            algorithms[i] = algorithms[pos];
            algorithms[pos] = tmp;
        }


        try {
            for (int i = 0; i < algorithms.length; i++) {
                switch (algorithms[i]) {
                    case 0:
                        gen.hideInRed(i, randomImage());
                        break;
                    case 1:
                        gen.hideInGreen(i, randomImage());
                        break;
                    case 2:
                        gen.hideInBlue(i, randomImage());
                        break;
                    case 3:
                    case 4:
                    case 5:
                        break;
                    default:
                        gen.blank(i, Color.WHITE);
                }
            }
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        String filename = lock + ".png";
        try {
            gen.saveImage(filename);
            System.out.println("Saved " + filename);
        } catch (IOException e) {
            System.err.println("Could not save image: " + e.getMessage());
        }
    }

    public static BufferedImage randomImage() throws IOException {
        Path thisPath = Paths.get("");

        String originalDir = thisPath.toAbsolutePath().toString();
        if (!originalDir.endsWith(File.separator)) {
            originalDir += File.separator;
        }
        String imagePath = originalDir + "images" + File.separator;
        try {
            imagePath = URLDecoder.decode(imagePath, "UTF-8");
        } catch (UnsupportedEncodingException ignored) {        }

        File imagesDir = new File(imagePath);

        if (!imagesDir.exists()) {
            throw new FileNotFoundException("image folder not found");
        }

        File[] images = imagesDir.listFiles((file, name)-> {
            return name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".jpg");
        });

        int num = (int)(Math.random() * images.length);

        return ImageIO.read(images[num]);
    }

    private static class Rectangle {
        int x;
        int y;
        int width;
        int height;

        int bottom;
        int right;

        public Rectangle(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.bottom = y + height;
            this.right = x + width;
        }
    }
}
