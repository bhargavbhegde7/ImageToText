package com.ocr;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.lept;
import org.bytedeco.javacpp.tesseract;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

import static org.bytedeco.javacpp.lept.pixDestroy;
import static org.bytedeco.javacpp.lept.pixRead;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by goodbytes on 12/13/2016.
 */
@RestController
public class UploadCtrlr {

    private String getTextFromImage1(MultipartFile imageFile){
        return "dummy text";
    }

    private void convert(){
        BufferedImage image;
        int width;
        int height;
        try {
            File input = new File("C:\\Users\\goodbytes\\Desktop\\Capture.jpg");
            image = ImageIO.read(input);
            width = image.getWidth();
            height = image.getHeight();

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

            File ouptut = new File("grayscale.jpg");
            ImageIO.write(image, "jpg", ouptut);

        } catch (Exception e) {}
    }

    public String getTextFromImage(String fileName){
        BytePointer outText;

        tesseract.TessBaseAPI api = new tesseract.TessBaseAPI();
        if (api.Init(".", "ENG") != 0) {
            return "Couldn't initialize tesseract";
        }

        lept.PIX image = pixRead("test.png");

        api.SetImage(image);
        outText = api.GetUTF8Text();
        String string = outText.getString();

        api.End();
        outText.deallocate();
        pixDestroy(image);

        return string;
    }

    @RequestMapping(method = POST)
    public String postImage(@RequestParam("inputImage") MultipartFile imageFile){
        //return getTextFromImage1(imageFile)+ " --- " +imageFile.getOriginalFilename();
        convert();
        return getTextFromImage("")+ " --- " +imageFile.getOriginalFilename();
    }

    @RequestMapping(method = GET)
    public String getDocumentation(){
        return "post the image file to this url with name attribute as inputImage. " +
                "Also set enctype attribute of the form tag as 'multipart/form-data' ";
    }
}
