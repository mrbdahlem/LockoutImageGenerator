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

/**
 * A simple steganographic image generator to hide simple codes using various algorithms.
 */
public class ImageGenerator
{
    private final int rows;
    private final int cols;

    private final int rowHeight;
    private final int colWidth;

    private final BufferedImage img;
    private final String code;

    public final static int NUM_ALGORITHMS = 6;

    /**
     * A single slot Image generator to hide a specific code. Use one of the included algorithms to encode the image.
     *
     * @param code the code to hide
     * @param width the width of the image to hide the code in
     * @param height the height of the image to hide the code in
     */
    public ImageGenerator(String code, int width, int height) {
        this(code, width, height, 1, 1);
    }

    /**
     * An Image generator to hide a specific code in several ways. The generated image will be divided into slots each
     * of which can be used to demonstrate an algorithm to hide the given code. Each slot will have an index
     * numbered [0..(numRows * numCols)) with index 0 in the top left and index (numRows * numCols) - 1 in the bottom
     * right.
     *
     * @param code the code to hide
     * @param width the width of the image to hide the code in
     * @param height the height of the image to hide the code in
     * @param numCols the number of columns of slots to divide the image into
     * @param numRows the number of rows of slots to divide the image into
     */
    public ImageGenerator(String code, int width, int height, int numRows, int numCols) {
        this.img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        this.rows = numRows;
        this.cols = numCols;

        // Determine the space available in each space
        this.rowHeight = img.getHeight() / rows;
        this.colWidth = img.getWidth() / cols;

        this.code = code;

        drawText(code);
    }

    /**
     * Fill the entire image with color
     *
     * @param color the color to fill the image with
     */
    public void blank(Color color) {
        blank(0, color);
    }

    /**
     * Fill one slot with the given color
     *
     * @param pos the image slot's index
     * @param color the color to fill the slot with
     */
    public void blank(int pos, Color color) {
        Rectangle area = getArea(pos);

        Graphics g = img.getGraphics();
        g.setColor(color);
        g.fillRect(area.x, area.y, area.width, area.height);

        g.dispose();
    }

    /**
     * Hide this ImageGenerator's code in the red channel of the provided image. The generated image will look
     * like the one provided in pic.
     *
     *  @param pic the picture to hide the code in
     */
    public void hideInRed(BufferedImage pic) {
        this.hideInRed(0, pic);
    }

    /**
     * Hide this ImageGenerator's code in the red channel of the provided image. The generated image will look
     * like the one provided in pic.
     *
     * @param pos the slot index within which this algorithm will be used
     * @param pic the picture to hide the code in
     */
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

    /**
     * Hide this ImageGenerator's code in the green channel of the provided image. The generated image will look
     * like the one provided in pic.
     *
     * @param pic the picture to hide the code in
     */

    public void hideInGreen(BufferedImage pic) {
        this.hideInGreen(0, pic );
    }

    /**
     * Hide this ImageGenerator's code in the green channel of the provided image. The generated image will look
     * like the one provided in pic.
     *
     * @param pos the slot index within which this algorithm will be used
     * @param pic the picture to hide the code in
     */
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

    /**
     * Hide this ImageGenerator's code in the blue channel of the provided image. The generated image will look
     * like the one provided in pic.
     *
     * @param pic the picture to hide the code in
     */
    public void hideInBlue(BufferedImage pic) {
        this.hideInBlue(0, pic);
    }

    /**
     * Hide this ImageGenerator's code in the blue channel of the provided image. The generated image will look
     * like the one provided in pic.
     *
     * @param index the slot index within which this algorithm will be used
     * @param pic the picture to hide the code in
     */
    public void hideInBlue(int index, BufferedImage pic) {
        Rectangle area = getArea(index);

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

    /**
     * Hide the code in static
     */
    public void hideInStatic() {
        this.hideInStatic(0);
    }

    /**
     * Mask the code in static in the given slot.
     *
     * @param index the index of the slot to fill with static.
     */
    public void hideInStatic(int index) {
        Rectangle area = getArea(index);

        for (int row = 0; row < rowHeight; row++) {
            for (int col = 0; col < colWidth; col++) {
                Color hideColor = new Color(img.getRGB(col + area.x, row + area.y));

                int[] levels = random(3, 255);
                if (((hideColor.getRed() + hideColor.getGreen() + hideColor.getBlue()) / 3) > 127) {
                    levels[1] = levels[0];
                }
                shuffle(levels);
                Color picColor = new Color(levels[0], levels[1], levels[2]);

                img.setRGB(col + area.x, row + area.y, picColor.getRGB());
            }
        }
    }

    /**
     * Hide the code in a gradient of color that has been randomly shuffled
     */
    public void hideGradientColor() {
        this.hideGradientColor(0);
    }

    /**
     * Hide the code in a gradient of color that has been randomly shuffled
     *
     * @param index the slot to use this algorithm in
     */

    public void hideGradientColor(int index) {
        Rectangle area = getArea(index);

        Color[][] gradient = new Color[area.width][area.height];

        for (int row = 0; row < rowHeight; row++) {
            for (int col = 0; col < colWidth; col++) {
                Color hideColor = new Color(img.getRGB(col + area.x, row + area.y));

                if (((hideColor.getRed() + hideColor.getGreen()  + hideColor.getBlue()) / 3) < 127) {
                    gradient[col][row] = new Color(0, 0, col);
                }
                else {
                    gradient[col][row] = Color.BLACK;
                }
            }
        }

        shuffle(gradient);

        for (int row = 0; row < rowHeight; row++) {
            for (int col = 0; col < colWidth; col++) {
                img.setRGB(col + area.x, row + area.y, gradient[col][row].getRGB());
            }
        }
    }

    /**
     * Hide the code as binary data in the red channel of the given picture.
     *
     * @param pic the picture that the code will be hidden in.
     */
    public void hideBinaryData(BufferedImage pic) {
        this.hideBinaryData(0, pic);
    }

    /**
     * Hide the code as binary data in the red channel of the given picture.
     *
     * @param index the slot to hide the code within
     * @param pic the picture that the code will be hidden in.
     */
    public void hideBinaryData(int index, BufferedImage pic) {
        Rectangle area = getArea(index);

        pic = getImageToFit(pic);

        String data = "The code for your lock is " + code + ".";

        char[] chars = data.toCharArray();

        int charIndex = 0;
        int bitIndex = 0;

        for (int row = 0; row < rowHeight; row++) {
            for (int col = 0; col < colWidth; col++) {
                Color picColor = new Color(pic.getRGB(col, row));

                int bit = 0;
                if (charIndex < chars.length) {
                    bit = (chars[charIndex] >> (7 - bitIndex)) & 0b00000001;
                }

                int red = (picColor.getRed() & 0b11111110) | bit;

                bitIndex++;
                if (bitIndex == 8) {
                    bitIndex = 0;
                    charIndex++;
                }

                picColor = new Color(red, picColor.getGreen(), picColor.getBlue());

                img.setRGB(col + area.x, row + area.y, picColor.getRGB());
            }
        }
    }

    /**
     * Draw the given text over each slot in the generated image
     * @param text the text to draw in each slot
     */
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

    /**
     * Save the generated image as filename. Image will be saved in png format.
     * @param filename the file to save as
     * @throws IOException if the file could not be saved
     */
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

    public void doAlgorithm(int num) {
        try {
            switch (num) {
                case 0:
                    this.hideInRed(randomImage());
                    break;
                case 1:
                    this.hideInGreen(randomImage());
                    break;
                case 2:
                    this.hideInBlue(randomImage());
                    break;
                case 3:
                    this.hideInStatic();
                    break;
                case 4:
                    this.hideGradientColor();
                    break;
                case 5:
                    this.hideBinaryData(randomImage());
                    break;
                default:
                    throw new RuntimeException("Invalid algorithm number " + num + " must be 0.." + NUM_ALGORITHMS);
            }
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

    }
    /**
     * Get a random image from the images directory
     *
     * @return the random image from the images directory
     * @throws IOException if the image cannot be loaded
     */
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
            name = name.toLowerCase();
            return name.endsWith(".png") || name.endsWith(".jpg");
        });

        assert images != null;
        int num = (int)(Math.random() * images.length);

        return ImageIO.read(images[num]);
    }

    private static void shuffle(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            int pos = (int)(Math.random() * arr.length);
            int tmp = arr[i];
            arr[i] = arr[pos];
            arr[pos] = tmp;
        }
    }
    private static void shuffle(Color[][] arr) {
        for (int i = 0; i < arr.length; i++) {
            int pos = (int)(Math.random() * arr.length);
            Color[] tmp = arr[i];
            arr[i] = arr[pos];
            arr[pos] = tmp;
        }
    }

    private static int[] random(int size, int max) {
        int[] arr = new int[size];

        for (int i = 0; i < arr.length; i++) {
            boolean same = true;
            while (same) {
                arr[i] = (int) (Math.random() * max);

                same = false;
                for (int j = i - 1; j >= 0; j--) {
                    if (arr[j] == arr[i]) {
                        same = true;
                        break;
                    }
                }
            }
        }

        return arr;
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
