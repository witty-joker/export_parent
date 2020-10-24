package com.itheima.test;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;

public class QiniuDemo {
    public static void main(String[] args) {
        //指定region2  华南地区
        Configuration cfg = new Configuration(Region.region2());
        UploadManager uploadManager = new UploadManager(cfg);

        //填写自己的信息
        String accessKey = "BWm6BK_jjvPDUHwOOPa_BDc39fTIJTGbiWBvx5KE";
        String secretKey = "0xARqchcl5eDjLdqw8MDGN2XOndZQgKrwFzV-kiU";
        String bucket = "saas1-export";

        //准备一张图片
        String localFilePath = "C:\\Users\\Data\\Pictures\\Camera Roll\\你懂的.gif";

        String key = null;
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        try {
            Response response = uploadManager.put(localFilePath, key, upToken);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            System.out.println(putRet.key);
            System.out.println(putRet.hash);
        } catch (QiniuException ex) {
            Response r = ex.response;
            System.err.println(r.toString());
            try {
                System.err.println(r.bodyString());
            } catch (QiniuException ex2) {
                ex2.printStackTrace();
            }
        }
    }
}
