package imageapi;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.mongodb.client.model.Filters.eq;

/**
 * This class handles all the image related tasks.
 */
public class ImageAPI {
    /**
     * h            - The final height of the image.
     * w            - The final width of the image.
     * defaultImage - The default image to use if the requested image is not found.
     */
    public static int h = 250, w = 150;
    public static byte[] defaultImage;

    static {
        String path = "E:/images/empty.jpg";
        File f = new File(path);
        try (FileInputStream fis = new FileInputStream(f)) {
            byte[] bytes = new byte[(int) f.length()];
            fis.read(bytes);
            defaultImage = transform(bytes);
        } catch (Exception ex) {
            System.out.println("Failed to load empty image");
        }
    }

    /**
     * gfb - The GridFS bucket into which images are retrieved from or inserted to.
     */
    private GridFSBucket gfb;

    public ImageAPI(String dbname) {
        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.SEVERE);
        MongoClient mc = new MongoClient();
        MongoDatabase mdb = mc.getDatabase(dbname);
        gfb = GridFSBuckets.create(mdb, "images");
    }

    /**
     * This function tries to transforms the input image into an image of width "w" and height "h",
     * while maintaining the aspect ratio of the input image.
     *
     * @param ip - The image to transform
     * @return - The transformed image. The returned image will have width "w" and height "h".
     */
    private static byte[] transform(byte[] ip) {
        try {
            BufferedImage bim = ImageIO.read(new ByteArrayInputStream(ip));
            int curH = bim.getHeight(), curW = bim.getWidth();
            int newH = (int) (w * (double) curH / curW);
            Image scaled = bim.getScaledInstance(w, newH, Image.SCALE_SMOOTH);
            BufferedImage newImage = toBufferedImage(scaled);

            newImage = newImage.getSubimage(0, 0, w, h);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(newImage, "png", bos);
            bos.close();
            return bos.toByteArray();
        } catch (Exception ex) {
            System.out.println("Error transforming: " + ex.toString());
            return null;
        }
    }

    private static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D bGr = bimage.createGraphics();

        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        return bimage;
    }

    /**
     * Delete all files in the image collection of GridFS.
     */
    public void deleteAll() {
        gfb.find().forEach((Block<GridFSFile>) file -> gfb.delete(file.getObjectId()));
    }

    /**
     * Adds the image represented by the path to images collection. It also transforms the image.
     *
     * @param path - The input image.
     * @return - Returns true if the insertion succeeded, false otherwise.
     */
    public boolean addImage(String path) {
        File f = new File(path);
        gfb.find(eq("filename", f.getName())).forEach((Block<GridFSFile>) file -> gfb.delete(file.getObjectId()));
        try (FileInputStream fis = new FileInputStream(f)) {
            byte[] bytes = new byte[(int) f.length()];
            int size = fis.read(bytes);
            System.out.println("Trying to add a file ( " + f.getName() + " ) of size " + size + " bytes.");
            byte[] transformed = transform(bytes);
            if (transformed == null)
                return false;
            gfb.uploadFromStream(f.getName(), new ByteArrayInputStream(transformed));
        } catch (Exception ex) {
            System.out.println(ex.toString());
            return false;
        }
        return true;
    }

    public byte[] getImage(String rollNo) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            gfb.downloadToStream(rollNo + ".jpg", bos);
            return bos.toByteArray();
        } catch (Exception ex) {
            System.out.println("Unable to retrieve image for " + rollNo);
            System.out.println(ex.toString());
            return defaultImage;
        }
    }
}
