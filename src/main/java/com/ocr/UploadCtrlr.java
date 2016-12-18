package com.ocr;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.annotation.Cast;
import org.bytedeco.javacpp.lept;
import org.bytedeco.javacpp.tesseract;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.bytedeco.javacpp.lept.pixDestroy;
import static org.bytedeco.javacpp.lept.pixRead;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by goodbytes on 12/13/2016.
 */
@RestController
public class UploadCtrlr {

    private void convertToGreyScale(InputStream inputFile){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        try {
            BufferedImage image = ImageIO.read(inputFile);
            int width = image.getWidth();
            int height = image.getHeight();

            for(int i=0; i<height; i++){

                for(int j=0; j<width; j++){

                    Color c = new Color(image.getRGB(j, i));
                    int red = (int)(c.getRed() * 0.299);
                    int green = (int)(c.getGreen() * 0.587);
                    int blue = (int)(c.getBlue() *0.114);
                    Color newColor = new Color(red+green+blue,

                            red+green+blue,red+green+blue);

                    image.setRGB(j,i,newColor.getRGB());
                }
            }

            //File ouptut = new File("grayscale.jpg");
            //ImageIO.write(image, "jpg", ouptut);
            asdf(image);

        } catch (Exception e) {
            System.out.println("Error");
        }
    }

    private Mat bufferedImageToMat(BufferedImage bi) {
        Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
        byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
        mat.put(0, 0, data);
        return mat;
    }

    public Mat matify(BufferedImage im) {
        // Convert INT to BYTE
        //im = new BufferedImage(im.getWidth(), im.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
        // Convert bufferedimage to byte array
        byte[] pixels = ((DataBufferByte) im.getRaster().getDataBuffer())
                .getData();

        // Create a Matrix the same size of image
        Mat image = new Mat(im.getHeight(), im.getWidth(), CvType.CV_8UC3);
        // Fill Matrix with image values
        image.put(0, 0, pixels);

        return image;

    }

    private void asdf(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write( image, "jpg", baos );
        baos.flush();
        byte[] imageInByte = baos.toByteArray();
        baos.close();

        int width = image.getWidth();
        int height = image.getHeight();

        //Mat m = bufferedImageToMat(image);
        Mat m = matify(image);

        Long bytesPerPixel = m.elemSize();
        Long bytesPerLine = m.rows()*m.elemSize();

        String finalOutput = getTextFromImage(imageInByte, width, height, bytesPerPixel.intValue(), bytesPerLine.intValue());

        System.out.println(finalOutput);
    }

    public String getTextFromImage(byte[] imageData, int width, int height, int bytes_per_pixel, int bytes_per_line){
        BytePointer outText;

        tesseract.TessBaseAPI api = new tesseract.TessBaseAPI();
        if (api.Init(".", "ENG") != 0) {
            return "Couldn't initialize tesseract";
        }

        //lept.PIX image = pixRead("test.png");


        /**TODO use the below method. get width, height and bytes perpixes from opencv
         * public native void SetImage(@Cast("const unsigned char*") byte[] imagedata, int width, int height,
         int bytes_per_pixel, int bytes_per_line);
         */

        //api.SetImage(image);
        api.SetImage(imageData, width, height, bytes_per_pixel, bytes_per_line);

        outText = api.GetUTF8Text();
        String string = outText.getString();

        api.End();
        outText.deallocate();
        //pixDestroy(image);

        return string;
    }

    @RequestMapping(method = POST)
    public String postImage(@RequestParam MultipartFile multipartImageFile){
        try{
            convertToGreyScale(multipartImageFile.getInputStream());
        }catch(Exception e){
            System.out.println("Error");
        }
        return "POOP";
    }

    @RequestMapping(method = GET)
    public String getDocumentation(){
        return "post the image file to this url with name attribute as inputImage. " +
                "Also set enctype attribute of the form tag as 'multipart/form-data' ";
    }
}
