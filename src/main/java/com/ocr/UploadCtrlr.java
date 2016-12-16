package com.ocr;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.lept;
import org.bytedeco.javacpp.tesseract;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

    public String getTextFromImage(String fileName){
        BytePointer outText;

        tesseract.TessBaseAPI api = new tesseract.TessBaseAPI();
        if (api.Init(".", "ENG") != 0) {
            return "Couldn't initialize tesseract";
        }

        lept.PIX image = pixRead(fileName);

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
        return getTextFromImage1(imageFile)+ " --- " +imageFile.getOriginalFilename();
    }

    @RequestMapping(method = GET)
    public String getDocumentation(){
        return "post the image file to this url with name attribute as inputImage. " +
                "Also set enctype attribute of the form tag as 'multipart/form-data' ";
    }
}
