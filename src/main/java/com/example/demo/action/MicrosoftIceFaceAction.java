package com.example.demo.action;

import com.example.demo.service.IAnalyseImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by weihuaxiao
 * on 2017/9/22.
 */
@Controller
@RequestMapping("/whx")
public class MicrosoftIceFaceAction {

    private static final Logger logger = LoggerFactory.getLogger(MicrosoftIceFaceAction.class);

    @Autowired
    private IAnalyseImageService analyseImageService;

    @RequestMapping(value = "/getFaceScore", method = RequestMethod.POST)
    @ResponseBody
    public String getScoreByMicrosoftIce(@RequestParam("file") MultipartFile file,HttpServletRequest request) {
        int score = analyseImageService.getScoreByImageResult(file);
        return String.valueOf(score);
    }

    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    public String forwardUploadage() {
        return "/upload";
    }
}
