package com.example.lhserver.tools;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.ECGenParameterSpec;
import java.util.Enumeration;

//import sun.security.pkcs.ContentInfo;
import sun.security.pkcs.ContentInfo;
import sun.security.pkcs.PKCS7;
import sun.security.pkcs.SignerInfo;
import sun.security.x509.AlgorithmId;
import sun.security.x509.X500Name;

import static com.example.lhserver.tools.Conver.encodeBase64;


public class Sm2Key {
    //    签名算法
    private final static String signingAlgorithm = "SHA256withRSA";
    //    摘要算法
    private final static String digestAlgorithm = "SHA256";

    private static boolean matchUsage(boolean[] keyUsage, int usage) {
        if (0 == usage || null == keyUsage) {
            return true;
        }
        for (int i = 0; i < Math.min(keyUsage.length, 32); i++) {
            if ((usage & (1 << i)) != 0 && !keyUsage[i]) {
                return false;
            }
        }
        return true;
    }


    public static Object[] initSigner(String keyStorePath,
                                      String keyStorePassword, String keyPassword) throws Exception {
        KeyStore keyStore = null;
        if (keyStorePath.toLowerCase().endsWith(".p12")) {
            keyStore = keyStore.getInstance("PKCS12");
        } else {
            keyStore = keyStore.getInstance("JKS");
        }
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(keyStorePath);
            keyStore.load(fis, keyStorePassword.toCharArray());
        } finally {
            if (null != fis) {
                fis.close();
            }
        }
        Enumeration<String> aliases = keyStore.aliases();
        String keyAlias = null;
        if (null != aliases) {
            while (aliases.hasMoreElements()) {
                keyAlias = (String) aliases.nextElement();
                java.security.cert.Certificate[] certs = keyStore.getCertificateChain(keyAlias);
                if (null == certs || certs.length == 0) {
                    continue;
                }
                X509Certificate cert = (X509Certificate) certs[0];

                if (matchUsage(cert.getKeyUsage(), 1)) {
                    try {
                        cert.checkValidity();
                    } catch (CertificateException ex) {
                        continue;
                    }
                    break;
                }

            }
        }

        Object[] objects  = new Object[2];

        X509Certificate[] certificates = null;

        if (keyStore.isKeyEntry(keyAlias)) {
            java.security.cert.Certificate[] certs = keyStore.getCertificateChain(keyAlias);
            for (int i = 0; i < certs.length; i++) {
                if (!(certs[i] instanceof X509Certificate)) {
                    throw new IllegalAccessError("证书链接所指的证书不符合x509格式！");
                }
            }

            certificates = new X509Certificate[certs.length];

            for (int i = 0; i < certs.length; i++) {
                certificates[i] = (X509Certificate) certs[i];
            }

        } else if (keyStore.isCertificateEntry(keyAlias)) {
            Certificate cert = keyStore.getCertificate(keyAlias);
            if (cert instanceof X509Certificate) {
                certificates = new X509Certificate[] { (X509Certificate) cert };
            }
        } else {

            throw new IllegalAccessError("keystore存储的证书不合法！");
        }

        objects[0] = (PrivateKey) keyStore
                .getKey(keyAlias, keyPassword.toCharArray());

        objects[1] = certificates;

        return objects;
    }






    /**
     * 签名
     *
     * @param privateKey
     *            经base64编码后的原数据
     * @return signature 签名结果
     * @throws Exception
     */
    public static String p7DetacheSignMsg(String plainText, PrivateKey privateKey, X509Certificate[] certificates) throws Exception {

        byte[] data = Conver.decodeBase64(plainText);

        Signature signature = Signature.getInstance(signingAlgorithm);

        signature.initSign(privateKey);

        signature.update(data, 0, data.length);

        byte[] signedAttributes = signature.sign();

        // 根证书
        X509Certificate publicKey = certificates[0];

        SignerInfo signerInfo = new SignerInfo(new X500Name(publicKey
                .getIssuerX500Principal().getName()),
                publicKey.getSerialNumber(), AlgorithmId.get(digestAlgorithm),
                AlgorithmId.get(privateKey.getAlgorithm()), signedAttributes);

        PKCS7 pkcs7 = new PKCS7(
                new AlgorithmId[] { AlgorithmId.get(digestAlgorithm) },
                new ContentInfo(ContentInfo.DATA_OID, null),
                new X509Certificate[] { publicKey },
                new SignerInfo[] { signerInfo });

        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        pkcs7.encodeSignedData(bout);

        String p7Siger = encodeBase64(bout.toByteArray());

        return p7Siger;

    }

    public static void main(String[] args) throws Exception {
//        final ECGenParameterSpec sm2Spec = new ECGenParameterSpec("sm2p256v1");
//        final KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC", new BouncyCastleProvider());
//        kpg.initialize(sm2Spec);
//        KeyPair keyPair = kpg.generateKeyPair();
//        PublicKey publicKey = keyPair.getPublic();
//        PrivateKey privateKey = keyPair.getPrivate();

//        String base64Str = encodeSignedDataSignedDataBase64("{credno:\"1234567\",credtype:\"01\", custName:\"asdc\" }".getBytes("utf-8"));
        String text="<request>" +
                "<head><requestTime></requestTime></head>" +
                "<body>" +
                "<orderNo>DX20211216<orderNo>" +
                "<curCode>001</curCode>" +
                "<orderAmount>0.01</orderAmount>" +
                "<orderTime>202112161909</orderTime>" +
                "<orderNote>test</orderNote>" +
                "<orderUrl>http://47.104.193.241/getorder.php</orderUrl>" +
                "<subMerchantName>000</subMerchantName>" +
                "<subMerchantCode>000</subMerchantCode>" +
                "<subMerchantClass>0000</subMerchantClass>" +
                "<subMerchantZone>00000000</subMerchantZone>" +
                "</body>" +
                "</request>";
        String base64Str = encodeBase64(text.getBytes("UTF-8"));
        System.out.println(base64Str);
        Object[] objs = initSigner(System.getProperty("user.dir")+"/lh.pfx","fw402216","fw402216");
        System.out.println(objs);

        PrivateKey privateKey = (PrivateKey)objs[0];
        System.out.println(privateKey);
        X509Certificate[] certificates = (X509Certificate[])objs[1];

        String sign = p7DetacheSignMsg(base64Str, privateKey, certificates);

        System.out.println("签名:"+sign);


    }


}
