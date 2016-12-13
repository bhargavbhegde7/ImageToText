package com.ocr;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by goodbytes on 12/13/2016.
 */
@RestController
public class UploadCtrlr {

    private String getTextFromImage(MultipartFile imageFile){
        return "dummy text";
    }

    @RequestMapping(method = POST)
    public String postImage(@RequestParam("inputImage") MultipartFile imageFile){
        return getTextFromImage(imageFile)+ " --- " +imageFile.getOriginalFilename();
    }

    @RequestMapping(method = GET)
    public String getDocumentation(){
        return "post the image file to this url with name attribute as inputImage. " +
                "Also set enctype attribute of the form tag as 'multipart/form-data' ";
    }
}
