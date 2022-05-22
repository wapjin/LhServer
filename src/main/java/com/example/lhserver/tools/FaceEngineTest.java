package com.example.lhserver.tools;

import com.arcsoft.face.*;
import com.arcsoft.face.enums.*;
import com.arcsoft.face.toolkit.ImageInfo;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.arcsoft.face.toolkit.ImageInfoEx;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import static com.arcsoft.face.toolkit.ImageFactory.getGrayData;
import static com.arcsoft.face.toolkit.ImageFactory.getRGBData;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.UUID;


public class FaceEngineTest {


    public static String GenerateImage(String imgStr) {
        String imgClassPath = System.getProperty("user.dir")+"/3.png";
        if (imgStr == null) {
            // 图像数据为空
            return "1";
        }
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            // Base64解码
            byte[] b = decoder.decodeBuffer(imgStr);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {// 调整异常数据
                    b[i] += 256;
                }
            }
            // 生成jpeg图片

            OutputStream out = new FileOutputStream(imgClassPath);
            out.write(b);
            out.flush();
            out.close();
            return imgClassPath;
        } catch (Exception e) {
            return "1";
        }
    }
    public static String GetImageStr() {
        String imgFile = System.getProperty("user.dir")+"/3.png";// 待处理的图片
        InputStream in = null;
        byte[] data = null;
        // 读取图片字节数组
        try {
            in = new FileInputStream(imgFile);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        System.out.println(encoder.encode(data));
        return encoder.encode(data);// 返回Base64编码过的字节数组字符串
    }

//    public static void main(String[] args) {
//        String str = "";
//        str = GetImageStr();
//        System.out.println(str);
//
//        GenerateImage(str);
//    }


    public static String generateImage(String file) {
        // 解密
        try {
            // 项目绝对路径
            // 图片分类路径+图片名+图片后缀
            String imgClassPath = System.getProperty("user.dir")+"/3.png";
            // 解密
            Base64.Decoder decoder = Base64.getDecoder();
            System.out.println("---------------------------------------------------6666__________________________________________");
            System.out.println(file);
            System.out.println("---------------------------------------------------6666__________________________________________");

            // 去掉base64前缀 data:image/jpeg;base64,
//            file = file.substring(file.indexOf(",", 1) + 1, file.length());
//            System.out.println(file);
            byte[] b = decoder.decode(file);
            // 处理数据
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    b[i] += 256;
                }
            }
            // 保存图片
            OutputStream out = new FileOutputStream(imgClassPath);
            out.write(b);
            out.flush();
            out.close();
            // 返回图片的相对路径 = 图片分类路径+图片名+图片后缀
            return imgClassPath;
        } catch (IOException e) {
            return null;
        }
    }



    public static String Face(String str){
        GenerateImage(str);
        String gh  ="1";
        String str1;
        String str2;
        String appId = "";
        String sdkKey = "";
        String appid_fileName = System.getProperty("user.dir")+"/lib/appid.txt";
        try {
            BufferedReader in = new BufferedReader(new FileReader(appid_fileName));

            while ((str1 = in.readLine()) != null) {
                appId =str1;
            }
            System.out.println(str1);
        } catch (IOException e) {
        }
        String sdkKey_fileName = System.getProperty("user.dir")+"/lib/sdkKey.txt";
        try {
            BufferedReader in = new BufferedReader(new FileReader(sdkKey_fileName));

            while ((str2 = in.readLine()) != null) {
                sdkKey = str2;
            }

        } catch (IOException e) {
        }


        FaceEngine faceEngine = new FaceEngine(System.getProperty("user.dir")+"/lib/WIN64");
        try {

            ArrayList p  = getFileName();
    //        String ps = GenerateImage(bs64);
            //从官网获取

            //激活引擎
            int errorCode = faceEngine.activeOnline(appId, sdkKey);

            if (errorCode != ErrorInfo.MOK.getValue() && errorCode != ErrorInfo.MERR_ASF_ALREADY_ACTIVATED.getValue()) {
                System.out.println("引擎激活失败");
            }


            ActiveFileInfo activeFileInfo=new ActiveFileInfo();
            errorCode = faceEngine.getActiveFileInfo(activeFileInfo);
            if (errorCode != ErrorInfo.MOK.getValue() && errorCode != ErrorInfo.MERR_ASF_ALREADY_ACTIVATED.getValue()) {
                System.out.println("获取激活文件信息失败");
            }

            //引擎配置
            EngineConfiguration engineConfiguration = new EngineConfiguration();
            engineConfiguration.setDetectMode(DetectMode.ASF_DETECT_MODE_IMAGE);
            engineConfiguration.setDetectFaceOrientPriority(DetectOrient.ASF_OP_ALL_OUT);
            engineConfiguration.setDetectFaceMaxNum(1);
            engineConfiguration.setDetectFaceScaleVal(16);
            //功能配置
            FunctionConfiguration functionConfiguration = new FunctionConfiguration();
            functionConfiguration.setSupportAge(true);
            functionConfiguration.setSupportFace3dAngle(true);
            functionConfiguration.setSupportFaceDetect(true);
            functionConfiguration.setSupportFaceRecognition(true);
            functionConfiguration.setSupportGender(true);
            functionConfiguration.setSupportLiveness(true);
            functionConfiguration.setSupportIRLiveness(true);
            engineConfiguration.setFunctionConfiguration(functionConfiguration);


            //初始化引擎
            errorCode = faceEngine.init(engineConfiguration);

            if (errorCode != ErrorInfo.MOK.getValue()) {
                System.out.println("初始化引擎失败");
            }


            //人脸检测
            ImageInfo imageInfo = getRGBData(new File(System.getProperty("user.dir")+"/3.png"));
            List<FaceInfo> faceInfoList = new ArrayList<FaceInfo>();
            errorCode = faceEngine.detectFaces(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList);
            System.out.println(faceInfoList);

            //特征提取
            FaceFeature faceFeature = new FaceFeature();
            errorCode = faceEngine.extractFaceFeature(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList.get(0), faceFeature);
            System.out.println("特征值大小：" + faceFeature.getFeatureData().length);


            for(int i = 0; i < p.size(); i++){
                try {
                    ImageInfo imageInfo2 = getRGBData(new File(System.getProperty("user.dir")+"/image/"+p.get(i)));
                    List<FaceInfo> faceInfoList2 = new ArrayList<FaceInfo>();
                    errorCode = faceEngine.detectFaces(imageInfo2.getImageData(), imageInfo2.getWidth(), imageInfo2.getHeight(),imageInfo.getImageFormat(), faceInfoList2);
                    System.out.println(faceInfoList);

                    //特征提取2
                    FaceFeature faceFeature2 = new FaceFeature();
                    errorCode = faceEngine.extractFaceFeature(imageInfo2.getImageData(), imageInfo2.getWidth(), imageInfo2.getHeight(), imageInfo.getImageFormat(), faceInfoList2.get(0), faceFeature2);
                    System.out.println("特征值大小：" + faceFeature.getFeatureData().length);

                    //特征比对
                    FaceFeature targetFaceFeature = new FaceFeature();
                    targetFaceFeature.setFeatureData(faceFeature.getFeatureData());
                    FaceFeature sourceFaceFeature = new FaceFeature();

                    sourceFaceFeature.setFeatureData(faceFeature2.getFeatureData());
                    FaceSimilar faceSimilar = new FaceSimilar();

                    errorCode = faceEngine.compareFaceFeature(targetFaceFeature, sourceFaceFeature, faceSimilar);

                    System.out.println("相似度：" + faceSimilar.getScore());
                    if(faceSimilar.getScore()>0.75){

                        System.out.println("找到你了：" + p.get(i));
                        gh = p.get(i).toString();
                        break;

                    }

                } catch (Exception e) {
                    System.out.println(p.get(i));
                    System.out.println("异常");
                }

                System.out.println(p.get(i));



        }
        //人脸检测2
//
//
//        //设置活体测试
//        errorCode = faceEngine.setLivenessParam(0.5f, 0.7f);
//        //人脸属性检测
//        FunctionConfiguration configuration = new FunctionConfiguration();
//        configuration.setSupportAge(true);
//        configuration.setSupportFace3dAngle(true);
//        configuration.setSupportGender(true);
//        configuration.setSupportLiveness(true);
//        errorCode = faceEngine.process(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList, configuration);
//
//
//        //性别检测
//        List<GenderInfo> genderInfoList = new ArrayList<GenderInfo>();
//        errorCode = faceEngine.getGender(genderInfoList);
//        System.out.println("性别：" + genderInfoList.get(0).getGender());
//
//        //年龄检测
//        List<AgeInfo> ageInfoList = new ArrayList<AgeInfo>();
//        errorCode = faceEngine.getAge(ageInfoList);
//        System.out.println("年龄：" + ageInfoList.get(0).getAge());
//
//        //3D信息检测
//        List<Face3DAngle> face3DAngleList = new ArrayList<Face3DAngle>();
//        errorCode = faceEngine.getFace3DAngle(face3DAngleList);
//        System.out.println("3D角度：" + face3DAngleList.get(0).getPitch() + "," + face3DAngleList.get(0).getRoll() + "," + face3DAngleList.get(0).getYaw());
//
//        //活体检测
//        List<LivenessInfo> livenessInfoList = new ArrayList<LivenessInfo>();
//        errorCode = faceEngine.getLiveness(livenessInfoList);
//        System.out.println("活体：" + livenessInfoList.get(0).getLiveness());



        errorCode = faceEngine.unInit();
        } catch (Exception e) {
           faceEngine.unInit();
            return gh;
        }
        return gh;

    }


   static ArrayList getFileName() {
        ArrayList list  = new ArrayList();
        String path = System.getProperty("user.dir")+"/image"; // 路径
        File f = new File(path);//获取路径  F:\测试目录
        if (!f.exists()) {
            System.out.println(path + " not exists");//不存在就输出
            return list;
        }

        File fa[] = f.listFiles();//用数组接收  F:\笔记总结\C#, F:\笔记总结\if语句.txt
        System.out.println(fa);
        for (int i = 0; i < fa.length; i++) {//循环遍历
            File fs = fa[i];//获取数组中的第i个
            if (fs.isDirectory()) {
//                System.out.println(fs.getName() + " [目录]");//如果是目录就输出
            } else {
//                System.out.println(fs.getName());//否则直接输出
                list.add(fs.getName());

            }
        }
        return list;
    }

//    public static void main(String[] args) {


//        //IR属性处理
//        ImageInfo imageInfoGray = getGrayData(new File("d:\\IR_480p.jpg"));
//        List<FaceInfo> faceInfoListGray = new ArrayList<FaceInfo>();
//        errorCode = faceEngine.detectFaces(imageInfoGray.getImageData(), imageInfoGray.getWidth(), imageInfoGray.getHeight(), imageInfoGray.getImageFormat(), faceInfoListGray);
//
//        FunctionConfiguration configuration2 = new FunctionConfiguration();
//        configuration2.setSupportIRLiveness(true);
//        errorCode = faceEngine.processIr(imageInfoGray.getImageData(), imageInfoGray.getWidth(), imageInfoGray.getHeight(), imageInfoGray.getImageFormat(), faceInfoListGray, configuration2);
//        //IR活体检测
//        List<IrLivenessInfo> irLivenessInfo = new ArrayList<>();
//        errorCode = faceEngine.getLivenessIr(irLivenessInfo);
//        System.out.println("IR活体：" + irLivenessInfo.get(0).getLiveness());
//
//        ImageInfoEx imageInfoEx = new ImageInfoEx();
//        imageInfoEx.setHeight(imageInfo.getHeight());
//        imageInfoEx.setWidth(imageInfo.getWidth());
//        imageInfoEx.setImageFormat(imageInfo.getImageFormat());
//        imageInfoEx.setImageDataPlanes(new byte[][]{imageInfo.getImageData()});
//        imageInfoEx.setImageStrides(new int[]{imageInfo.getWidth() * 3});
//        List<FaceInfo> faceInfoList1 = new ArrayList<>();
//        errorCode = faceEngine.detectFaces(imageInfoEx, DetectModel.ASF_DETECT_MODEL_RGB, faceInfoList1);
//
//        FunctionConfiguration fun = new FunctionConfiguration();
//        fun.setSupportAge(true);
//        errorCode = faceEngine.process(imageInfoEx, faceInfoList1, functionConfiguration);
//        List<AgeInfo> ageInfoList1 = new ArrayList<>();
//        int age = faceEngine.getAge(ageInfoList1);
//        System.out.println("年龄：" + ageInfoList1.get(0).getAge());
//
//        FaceFeature feature = new FaceFeature();
//        errorCode = faceEngine.extractFaceFeature(imageInfoEx, faceInfoList1.get(0), feature);


        //引擎卸载


//    }
}