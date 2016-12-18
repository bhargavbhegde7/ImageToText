package com.ocr;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.annotation.Cast;
import org.bytedeco.javacpp.lept;
import org.bytedeco.javacpp.tesseract;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.springframework.ui.Model;
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

    private BufferedImage convertToGreyScale(InputStream inputFile){
        BufferedImage image = null;
        try {
            image = ImageIO.read(inputFile);
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
            //System.out.println(getTextFromImage(image));
        } catch (Exception e) {
            System.out.println("Error in the opencv method");
        }
        return image;
    }

    public String getTextFromImage(BufferedImage image){
        BytePointer outText;

        tesseract.TessBaseAPI api = new tesseract.TessBaseAPI();
        if (api.Init(".", "ENG") != 0) {
            return "Couldn't initialize tesseract";
        }

        File incomingFile = new File("test.png");
        try {
            ImageIO.write(image, "png", incomingFile);
        } catch (IOException e) {
            System.out.println("Error writing file test.png");
        }
        lept.PIX PIXimage = pixRead("test.png");

        api.SetImage(PIXimage);

        outText = api.GetUTF8Text();
        String string = new String(outText.getString());

        api.End();
        outText.deallocate();
        pixDestroy(PIXimage);

        try{

            File tempSavedImage = new File("test.png");

            if(tempSavedImage.delete()){
                System.out.println(tempSavedImage.getName() + " is deleted!");
            }else{
                System.out.println("Delete operation is failed.");
            }

        }catch(Exception e){

            System.out.println("Error in deletion");

        }

        return string;
    }

    @RequestMapping(value="/upload", method = POST)
    public String postImage(@RequestParam MultipartFile multipartImageFile){
        String output = "POOP";
        try{
            BufferedImage bufImage = convertToGreyScale(multipartImageFile.getInputStream());
            output = getTextFromImage(bufImage);
        }catch(Exception e){
            System.out.println("Error in the controller");
        }
        return output;
    }

    /*@RequestMapping(method = GET)
    public String getDocumentation(){
        return "post the image file to this url with name attribute as inputImage. " +
                "Also set enctype attribute of the form tag as 'multipart/form-data' ";
    }*/

    /*@RequestMapping("/greeting")
    public String greeting(@RequestParam(value="name", required=false, defaultValue="World") String name, Model model) {
        //model.addAttribute("name", name);
        return "index";
    }*/
}
