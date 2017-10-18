package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by weihuaxiao
 * on 2017/9/22.
 */
public interface IAnalyseImageService {

    int getScoreByImageResult(MultipartFile multipartFile);




}
