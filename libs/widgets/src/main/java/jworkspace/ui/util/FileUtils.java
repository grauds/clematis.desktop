package jworkspace.ui.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;

import com.hyperrealm.kiwi.io.StreamUtils;

public class FileUtils {

    private static final int BUFFER = 1024;
    /**
     * Directory for source files in copying or moving
     * whole directories
     */
    private static String dir = null;
    /**
     * Directory for destination files in copying or moving
     * whole directories
     */
    private static String dest = null;

    private FileUtils() {}

    private static void doCopyDir(File dir, FileSystemView fileSystem) throws IOException {

        File[] list = fileSystem.getFiles(dir, false);
        Vector<File> dirList = new Vector<>();

        for (File file : list) {

            if (file.isDirectory()) {
                dirList.addElement(file);
                // calculate name of new directory
                File destSubDir = new File(dest + file.getAbsolutePath().substring(FileUtils.dir.length()));
                org.apache.commons.io.FileUtils.forceMkdir(destSubDir);
            } else if (file.isFile()) {
                File destFile = new File(dest + file.getAbsolutePath().
                    substring(FileUtils.dir.length()));

                try (FileInputStream input = new FileInputStream(file);
                     FileOutputStream output = new FileOutputStream(destFile)) {

                    StreamUtils.readStreamToStream(input, output);
                } catch (IOException e) {
                    throw new IOException(e);
                }
            }
        }

        for (int i = 0; i < dirList.size(); i++) {
            doCopyDir(dirList.elementAt(i), fileSystem);
        }
    }

    private static void doMoveDir(File dir, FileSystemView fileSystem) throws IOException {

        File[] list = fileSystem.getFiles(dir, false);
        Vector<File> dirList = new Vector<>();

        for (File file : list) {

            if (file.isDirectory()) {
                dirList.addElement(file);
                // calculate name of new directory
                File destSubDir = new File(dest + file.getAbsolutePath().
                    substring(FileUtils.dir.length()));
                org.apache.commons.io.FileUtils.forceMkdir(destSubDir);
            } else if (file.isFile()) {
                File destFile = new File(dest + file.getAbsolutePath().
                    substring(FileUtils.dir.length()));
                if (!file.renameTo(destFile)) {
                    throw new IOException("Couldn't rename the file: " + file.getName() + " to " + destFile.getName());
                }
            }
        }
        for (int i = 0; i < dirList.size(); i++) {
            doMoveDir(dirList.elementAt(i), fileSystem);
        }
    }

    /**
     * Copies directory along with all files within.
     */
    public static void copyDir(String dir, String dest) throws IOException {

        FileSystemView fileSystem = FileSystemView.getFileSystemView();
        File directory = new File(dir);

        FileUtils.dir = dir;
        FileUtils.dest = dest;

        doCopyDir(directory, fileSystem);
    }

    /**
     * Copy file to another directory
     */
    public static String copyFileToDir(String file, String dest) throws IOException {

        File f = new File(file);

        File destFile = new File(dest + f.getAbsolutePath().
            substring(f.getAbsolutePath().lastIndexOf(File.separator)));

        try (FileInputStream input = new FileInputStream(f);
             FileOutputStream output = new FileOutputStream(destFile)) {

            StreamUtils.readStreamToStream(input, output);
        }

        return destFile.getAbsolutePath();
    }

    /**
     * Copy file to another file with the same extension
     */
    public static String copyFile(String file, String dest, String destFile) throws IOException {

        File f = new File(file);

        File d = new File(dest + destFile
            + f.getAbsolutePath().substring(f.getAbsolutePath().lastIndexOf('.')));

        try (FileInputStream input = new FileInputStream(f);
             FileOutputStream output = new FileOutputStream(d)) {

            StreamUtils.readStreamToStream(input, output);
        }
        return d.getAbsolutePath();
    }

    /**
     * Utility method to copy a file from one directory to another
     */
    public static void copyFile(File from, File to) throws IOException {

        if (!from.canRead()) {
            throw new IOException("Cannot read file: " + from);
        }
        if (to.exists() && (!to.canWrite())) {
            throw new IOException("Cannot write to file: " + to);
        }

        try (FileInputStream fis = new FileInputStream(from);
            FileOutputStream fos = new FileOutputStream(to)) {

            byte[] buf = new byte[BUFFER];
            int bytesLeft;
            while ((bytesLeft = fis.available()) > 0) {
                if (bytesLeft >= buf.length) {
                    if (fis.read(buf) != -1) {
                        fos.write(buf);
                    }
                } else {
                    byte[] smallBuf = new byte[bytesLeft];
                    if (fis.read(smallBuf) != -1) {
                        fos.write(smallBuf);
                    }
                }
            }
        }
    }

    /**
     * Copy file to another file with the same extension
     */
    public static String copyFile(String file, String destFile) throws IOException {

        File f = new File(file);
        File d = new File(destFile);

        try (FileInputStream input = new FileInputStream(f);
            FileOutputStream output = new FileOutputStream(d)) {
            StreamUtils.readStreamToStream(input, output);
        }

        return d.getAbsolutePath();
    }

    /**
     * Moves directory to new location along with all files
     * within.
     */
    public static void moveDir(String dir, String dest) throws IOException {

        FileSystemView fileSystem = FileSystemView.getFileSystemView();

        File directory = new File(dir);

        FileUtils.dir = dir;
        FileUtils.dest = dest;

        doMoveDir(directory, fileSystem);
    }

    public static Icon cloneIcon(Icon icon) {
        if (icon instanceof ImageIcon imgIcon) {
            Image img = imgIcon.getImage();
            // ensure copy of the underlying image
            BufferedImage copy = new BufferedImage(
                img.getWidth(null),
                img.getHeight(null),
                BufferedImage.TYPE_INT_ARGB
            );
            Graphics2D g2d = copy.createGraphics();
            g2d.drawImage(img, 0, 0, null);
            g2d.dispose();
            return new ImageIcon(copy);
        }
        // fallback: just return the original
        return icon;
    }
}
