package com.example.lhserver.tools;

import com.arcsoft.face.*;
import com.arcsoft.face.enums.DetectMode;
import com.arcsoft.face.enums.DetectOrient;
import com.arcsoft.face.enums.ErrorInfo;
import com.arcsoft.face.toolkit.ImageInfo;
import org.json.JSONException;
import org.json.JSONObject;
import sun.misc.BASE64Decoder;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import static com.arcsoft.face.toolkit.ImageFactory.getRGBData;

public class ZcRl {

    public static boolean GenerateImage(String imgStr) {
        String imgClassPath = System.getProperty("user.dir")+"/4.png";
        if (imgStr == null) {
            // 图像数据为空
            return false;
        }
        BASE64Decoder decoder = new BASE64Decoder();
//        System.out.println(imgStr);
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
            System.out.println("imgStr");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String Face_get(String gh) {

//        查询人脸

        try{
//连接SQLite的JDBC
            Class.forName("org.sqlite.JDBC");
//建立一个数据库名zieckey.db的连接，如果不存在就在当前目录下创建之
            Connection conn = DriverManager.getConnection("jdbc:sqlite:"+System.getProperty("user.dir")+"/lib/rlsj.db");
            Statement stat = conn.createStatement();
//                stat.executeUpdate("create table user(gh varchar(20), tz TEXT(0) ,img TEXT(0));");//创建一个表，两列

//                String base64Str = DatatypeConverter.printBase64Binary(faceFeature.getFeatureData());
//                stat.executeUpdate("insert into user values('"+gh+"','"+ base64Str +"','"+str+"');");//插入数据
            String where=" ";
            if(gh!=null){

                where=" where gh='"+gh+"'";
            }

            ResultSet rs = stat.executeQuery("select * from user "+where);//查询数据
            ArrayList arr = new ArrayList(); //初始化数组,下面各种方法省略初始化

            //string



            while(rs.next()) {//将查询到的数据打印出来
                JSONObject object = new JSONObject();
//                System.out.print("name = " + rs.getString("gh") + " ");//列属性一
//                System.out.println("salary = " + rs.getString("tz"));//列属性二
                try {
                    object.put("gh",rs.getString("gh"));
                    object.put("id",rs.getString("id"));
                    object.put("tz",rs.getString("tz"));
                    object.put("img",rs.getString("img"));
                    arr.add(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return "1";
                }

            }
//
//                rs.close();
            conn.close();//结束数据库的连接
            return arr.toString();
//            return gh;

        }
        catch(Exception e ){
            e.printStackTrace();

            return "1";
        }



    }
    public static String Face_del(String idz) {

//        删除人脸

        try {
//连接SQLite的JDBC
            Class.forName("org.sqlite.JDBC");
//建立一个数据库名zieckey.db的连接，如果不存在就在当前目录下创建之
            Connection conn = DriverManager.getConnection("jdbc:sqlite:" + System.getProperty("user.dir") + "/lib/rlsj.db");
            Statement stat = conn.createStatement();
//                stat.executeUpdate("create table user(gh varchar(20), tz TEXT(0) ,img TEXT(0));");//创建一个表，两列

//                String base64Str = DatatypeConverter.printBase64Binary(faceFeature.getFeatureData());
//                stat.executeUpdate("insert into user values('"+gh+"','"+ base64Str +"','"+str+"');");//插入数据

            if (idz == null) {

                return "id不能为空";
            }

            int rs = stat.executeUpdate("DELETE FROM user WHERE id = '" + idz+"'");//查询数据
            conn.close();//结束数据库的连接
            System.out.println(rs);
            if(rs>0){

                return "删除成功";

            }else{
                return "删除失败";

            }
            //string


        } catch(Exception e ){
            e.printStackTrace();

            return "1";

            }
//
//                rs.close();

    }
    public static boolean Face_set(String str,String gh){

//        设置添加人脸
        if (GenerateImage(str)==false) {

            return false;
        }
//        String gh ="1";
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
            ImageInfo imageInfo = getRGBData(new File(System.getProperty("user.dir")+"/4.png"));
            List<FaceInfo> faceInfoList = new ArrayList<FaceInfo>();
            errorCode = faceEngine.detectFaces(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList);
            System.out.println(faceInfoList);

            //特征提取
            FaceFeature faceFeature = new FaceFeature();
            errorCode = faceEngine.extractFaceFeature(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList.get(0), faceFeature);
//            System.out.println("特征值大小：" + faceFeature.getFeatureData().length);
            System.out.println(faceFeature.getFeatureData());

//            FaceFeature targetFaceFeature = new FaceFeature();
//            targetFaceFeature.setFeatureData(faceFeature.getFeatureData());

            try{
//连接SQLite的JDBC
                Class.forName("org.sqlite.JDBC");
//建立一个数据库名zieckey.db的连接，如果不存在就在当前目录下创建之
                Connection conn = DriverManager.getConnection("jdbc:sqlite:"+System.getProperty("user.dir")+"/lib/rlsj.db");
                Statement stat = conn.createStatement();
//                stat.executeUpdate("create table user(gh varchar(20), tz TEXT(0) ,img TEXT(0));");//创建一个表，两列

                String base64Str = DatatypeConverter.printBase64Binary(faceFeature.getFeatureData());
                stat.executeUpdate("insert into user(gh,tz,img) values('"+gh+"','"+ base64Str +"','"+str+"');");//插入数据

//                ResultSet rs = stat.executeQuery("select * from user;");//查询数据
//                while(rs.next()){//将查询到的数据打印出来
//                    System.out.print("name = "+ rs.getString("gh")+" ");//列属性一
//                    System.out.println("salary = "+ rs.getString("tz"));//列属性二
////
//                    FaceFeature sourceFaceFeature = new FaceFeature();
////                    byte b[] = rs.getBytes("tz");
//
//                    byte [] byteArray = DatatypeConverter.parseBase64Binary(rs.getString("tz"));
//                    System.out.println("特征值大小：" + byteArray.length);
//                    sourceFaceFeature.setFeatureData(byteArray);
//                    FaceSimilar faceSimilar = new FaceSimilar();
//
////
//                    errorCode = faceEngine.compareFaceFeature(targetFaceFeature, sourceFaceFeature, faceSimilar);
////
////                    System.out.println("相似度：" + faceSimilar.getScore());
//                    if(faceSimilar.getScore()>0.75){
//
//                        System.out.print(rs.getString("gh"));
//                    }

//                }
//                rs.close();
                conn.close();//结束数据库的连接
                faceEngine.unInit();
                return true;

            }
            catch(Exception e ){
                e.printStackTrace();
                faceEngine.unInit();
                return false;
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




        } catch (Exception e) {
            faceEngine.unInit();
            return true;
        }



    }

//    public static void main(String[] args) {
//
//
//
//
//    }

}
