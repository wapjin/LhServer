package com.example.lhserver.tools;

import com.bocnet.common.security.PKCS7Tool;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.lhserver.tools.Conver.encodeBase64;

public class XmlJq {

   public ArrayList  getXmlJq(String ply){
       ArrayList arr = new ArrayList(); //初始化数组,下面各种方法省略初始化
        try{//加签
            //拼接数据  注意xml报文节点
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            System.out.println(formatter.format(date));
            String dtime =formatter.format(date);
            String order="LH"+dtime;
            String plainData="<?xml version=\"1.0\" encoding=\"utf-8\"?><request><head><requestTime>"+dtime+"</requestTime></head><body><orderNo>"+order+"</orderNo><curCode>001</curCode><orderAmount>"+ply+"</orderAmount><orderTime>20190417112952</orderTime><orderNote>充值"+ply+"元</orderNote><orderUrl>http://47.104.193.241/test_dome/cer.php</orderUrl><subMerchantName>武汉</subMerchantName><subMerchantCode>104340182200002</subMerchantCode><subMerchantClass>0001</subMerchantClass><subMerchantZone>HB340000</subMerchantZone></body></request>";
            byte[] data = plainData.getBytes("UTF-8");
            String base64Str = encodeBase64(data);
//            System.out.println(base64Str);
            //     私钥证书路径、密码

//            System.out.println();
            PKCS7Tool tool = PKCS7Tool.getSigner(System.getProperty("user.dir")+"/lh.pfx","fw402216","fw402216");
            String signResult = tool.sign(data);//加签
//            System.out.println("/n");
//            System.out.println(signResult.toString());

            arr.add(base64Str);
            arr.add(signResult);
            arr.add(order);
            return arr;
////*************************分割线******************************
//            //本地验签，此处方法也适用于商户验银行的通知数据
//            String plainData2=plainData;//拼接数据
//            byte[] data2 = plainData2.getBytes("UTF-8");
//            PKCS7Tool tool2 = PKCS7Tool.getVerifier("C:\\bocnet\\simmerchant\\aaa.cer"); //公钥路径
//            tool2.verify(signResult, data2, null);//验签
        }catch(Exception e){
            System.out.println(e.getMessage());
            return arr;
        }

    }
//    public static void main(String[] args) {
//        XmlJq p = new XmlJq();
////        String a = ;
//        ArrayList arr = p.getXmlJq("0.01");
//        System.out.println(arr.get(0));
//        System.out.print(arr.get(1));
//
//
//    }
//        try{//加签
//            //拼接数据  注意xml报文节点
//            String plainData="<?xml version=\"1.0\" encoding=\"utf-8\"?><request><head><requestTime>20190417112956</requestTime></head><body><orderNo>EC1118355873182257153</orderNo><curCode>001</curCode><orderAmount>0.01</orderAmount><orderTime>20190417112952</orderTime><orderNote>充值0.01元</orderNote><orderUrl>http://47.104.193.241/test_dome/cer.php</orderUrl><subMerchantName>武汉</subMerchantName><subMerchantCode>104340182200002</subMerchantCode><subMerchantClass>0001</subMerchantClass><subMerchantZone>HB340000</subMerchantZone></body></request>";
//            byte[] data = plainData.getBytes("UTF-8");
//            String base64Str = encodeBase64(data);
//            System.out.println(base64Str);
//            //     私钥证书路径、密码
//            PKCS7Tool tool = PKCS7Tool.getSigner("D:\\zhluhua\\武汉鲁花-中行.pfx","fw402216","fw402216");
//            String signResult = tool.sign(data);//加签
//            System.out.println("/n");
//            System.out.println(signResult.toString());
//////*************************分割线******************************
////            //本地验签，此处方法也适用于商户验银行的通知数据
////            String plainData2=plainData;//拼接数据
////            byte[] data2 = plainData2.getBytes("UTF-8");
////            PKCS7Tool tool2 = PKCS7Tool.getVerifier("C:\\bocnet\\simmerchant\\aaa.cer"); //公钥路径
////            tool2.verify(signResult, data2, null);//验签
//        }catch(Exception e){
//            System.out.println(e.getMessage());
//        }}



}
