package com.example.demo.service.impl;

import com.example.demo.service.IAnalyseImageService;
import net.sf.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by weihuaxiao
 * on 2017/9/22.
 */
@Service
public class AnalyseImageServiceImpl implements IAnalyseImageService{

    private static final String upload_picBase64_api = "http://kan.msxiaobing.com/APi/Image/UploadBase64";
    private static final String ice_api = "http://kan.msxiaobing.com/Api/ImageAnalyze/Process?service=yanzhi&tid=8b9a88049f9c4f26b4da60afc9d70ef4";
    private static final String ice_page = "http://kan.msxiaobing.com/ImageGame/Portal?task=yanzhi";

    @Override
    public int getScoreByImageResult(MultipartFile multipartFile) {
        //读取文件图片为base64编码格式
        String imgDataBase64 = getPictureBase64(multipartFile);
        //base64上传到微软服务器
        String jsonResultPic = getUploadPicResult(imgDataBase64);

        String analyzeResult = analyzeImage(jsonResultPic);
        int faceAuthScore = findScoreFromString(analyzeResult);
        return faceAuthScore;
    }
    private String getPictureBase64(MultipartFile multipartFile) {
        byte[] data = null;
        try {
            BufferedInputStream in = new BufferedInputStream(multipartFile.getInputStream());
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Base64.encodeBase64String(data);
    }

    private String getUploadPicResult(String imgdataBase64) {
        StringBuffer sb=new StringBuffer();
        try {
            URL realUrl = new URL(upload_picBase64_api);
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");

            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接

            DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());
            dataOutputStream.writeBytes(imgdataBase64);
            dataOutputStream.flush();
            dataOutputStream.close();

            String readLine=new String();
            BufferedReader responseReader=new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
            while((readLine=responseReader.readLine())!=null){
                sb.append(readLine).append("\n");
            }
            responseReader.close();
            System.out.println(sb.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            return sb.toString();
        }
    }

    public String analyzeImage(String jsonResultPic) {
        DefaultHttpClient httpClient=new DefaultHttpClient();
        DefaultHttpClient httpClient2=new DefaultHttpClient();
        HttpPost httpPost1=new HttpPost(ice_api);
        HttpPost httpPost2=new HttpPost(ice_page);

        JSONObject jasonObject = JSONObject.fromObject(jsonResultPic);
        Map<String,String> contentImgUrl = (Map)jasonObject;
        String imgUrl = contentImgUrl.get("Host") + contentImgUrl.get("Url");
        System.out.print(jsonResultPic);
        List<NameValuePair> form=new ArrayList<NameValuePair>();

        form.add(new BasicNameValuePair("MsgId",String.valueOf(System.currentTimeMillis())+"063"));
        form.add(new BasicNameValuePair("CreateTime",String.valueOf(Calendar.getInstance().getTimeInMillis())));
        form.add(new BasicNameValuePair("Content[imageUrl]",imgUrl));

        try {

            httpPost1.addHeader("Referer","http://kan.msxiaobing.com/V3/Portal?task=yanzhi&ftid=91ac082228fb48739f12c66ee3a3fee0");
            httpPost1.setEntity(new UrlEncodedFormEntity(form, "UTF-8"));

            HttpResponse pageResponse=httpClient.execute(httpPost2);
            CookieStore cookieStore=httpClient.getCookieStore();
            httpClient2.setCookieStore(cookieStore);
            HttpResponse response=httpClient2.execute( httpPost1);
            return EntityUtils.toString(response.getEntity());
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }
    public int findScoreFromString(String jsonResultPic) {
        Pattern pattern = Pattern.compile("\\d+[.]\\d+");
        Matcher m = pattern.matcher(jsonResultPic);
        if (m.find()) {
            System.out.println("analyzeResult=" + m.group());
            double temp = Double.valueOf(m.group());
            int score = (int)(temp*10);
            return score;
        }else{
            return 0;
        }
    }









}
