package com.yumu.hexie.common.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.yumu.hexie.service.exception.BizValidateException;

/**
 * RSA签名验签类
 * huym
 */
public class RSASignUtil {


	/**
	 * 签名算法
	 */
	public static final String SIGN_ALGORITHMS = "MD5withRSA";
	public static final String KEY_ALGORITHMS = "RSA";
	public static final String PRI_KEY_PATH = "E:/narbo/fufeitong/prikey.txt";
	public static final String PUB_KEY_PATH = "E:/narbo/fufeitong/pubkey.txt";
	

	/**
	 * RSA签名
	 * RSA/ECB/PKCS1Padding
	 * @param content	内容
	 * String keyPath	密钥路径
	 * @return
	 */
	public static String signByKeyPath(String content, String keyPath){
		
		String key = getKeyStrByPath(keyPath);
		return sign(content, key);
		
	}
	
	/**
	 * RSA签名
	 * RSA/ECB/PKCS1Padding
	 * @param content	内容
	 * String keyPath	密钥路径
	 * @return
	 */
	public static String signByKeyPath(String content, String keyPath, String charset){
		
		String key = getKeyStrByPath(keyPath);
		return sign(content, key, charset);
		
	}
	
	/**
	 * RSA私钥签名
	 * RSA/ECB/PKCS1Padding
	 * @param content	
	 * @param privateKey	私钥
	 * @return
	 */
	public static String sign(String content, String privateKey) {
		
		return sign(content, privateKey, "");
	}
	
	/**
	 * RSA私钥签名
	 * RSA/ECB/PKCS1Padding
	 * @param content	
	 * @param privateKey	私钥字串
	 * @param charset	字符集
	 * @return
	 */
	public static String sign(String content, String privateKey, String charset) {
		
		try {
			PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey.getBytes()));
			KeyFactory key = KeyFactory.getInstance(KEY_ALGORITHMS);
			PrivateKey priKey = key.generatePrivate(priPKCS8);
			Signature signature = Signature.getInstance(SIGN_ALGORITHMS);
			signature.initSign(priKey);
			if (ObjectUtil.isEmpty(charset)) {
				signature.update(content.getBytes());
			}else {
				signature.update(content.getBytes(charset));
			}
			byte[]signed = signature.sign();
			return new String(Base64.encodeBase64Chunked(signed));
			
		} catch (Exception e) {

			throw new BizValidateException(e.getMessage());
		}
	
	}
	
	/**
     * 根据公钥路径校验签名
	 * 返回串为json字符串缀上签名值得{...}&SIGN=
	 * 1.第一步先获取签名值SIGN
	 * 2.第二步对签名进行BASE64解码
	 * 3.第三步使用对方的RSA公钥对签名进行解码，得到哈希值A
	 * 4.第四步对接收到的报文进行SHA1计算，获得哈希值B
	 * 5.比较A和B，如果值相同，表示签名验证通过。
	 * @param responseStr
     * @param publicKeyPath
     * @param content
     * @param sign
     * @return
     */
    public static boolean doCheckByKeyPath(String content, String sign, String publicKeyPath){
    	
    	String key = getKeyStrByPath(publicKeyPath);
    	return doCheck(content, sign, key);
    
    }
    
    public static boolean doCheckByKeyPath(String content, String sign, String publicKeyPath, String charset){
    	
    	String key = getKeyStrByPath(publicKeyPath);
    	return doCheck(content, sign, key, charset);
    
    }
	
	/**
	 * 验证签名
	 * @param content
	 * @param sign
	 * @param publicKey
	 * @return
	 */
	public static boolean doCheck(String content, String sign, String publicKey) {
		
		return doCheck(content, sign, publicKey, "");
	}
	
	/**
	 * 验证签名
	 * @param content	内容
	 * @param sign	需要验证的签名
	 * @param publicKey	公钥字串
	 * @param charset 字符集
	 * @return
	 */
	public static boolean doCheck(String content, String sign, String publicKey, String charset) {
		
		boolean bverify = false;
		try {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			byte[] encodedKey = Base64.decodeBase64(publicKey.getBytes());
			PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));

			Signature signature = Signature.getInstance(SIGN_ALGORITHMS);
			signature.initVerify(pubKey);
			if (!ObjectUtil.isEmpty(charset)) {
				signature.update(content.getBytes(charset));
			}else {
				signature.update(content.getBytes());
			}
			
			bverify = signature.verify(Base64.decodeBase64(sign.getBytes()));

		} catch (Exception e) {

			throw new BizValidateException(e.getMessage());
		} 

		return bverify;
	}
	
	/**
	 * 根据路径获取公钥
	 * @param publicKeyPath	密钥路径
	 * @param keyAlgorithm	密钥算法
	 * @return
	 */
    public static String getKeyStrByPath(String publicKeyPath){  
    	
        InputStream inputStream = null;
        StringBuffer sb = new StringBuffer();  
        try {  
            inputStream = new FileInputStream(publicKeyPath);  
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));  
            String readLine = null;  
            while ((readLine = br.readLine()) != null) {  
                if (readLine.charAt(0) == '-') {  
                    continue;  
                } else {  
                    sb.append(readLine);  
                    sb.append('\r');  
                }  
            }  
            
        } catch (Exception e) {
        	
        	throw new BizValidateException("load public key failed !");  
        
        } finally {  
            if (inputStream != null){  
                try {  
                    inputStream.close();  
                }catch (Exception e){  
                    throw new BizValidateException(e.getMessage());
                }  
            }  
        }  
        return sb.toString();  
    
    }
    
    /** The Constant KEY_ALGORITHM. */ 
    public static final String KEY_ALGORITHM = "DESede";  
    /** The Constant CIPHER_ALGORITHM. */  public static final String CIPHER_ALGORITHM = "DESede/ECB/NoPadding"; 
     
    /** The Constant CIPHER_ALGORITHM. */ 
    public static final String SINGLE_CIPHER_ALGORITHM = "DES";   
     

    public static final String SINGLE_CIPHER_ALGORITHM_FOR_UP = "DES/ECB/NoPadding";   
    public static final String ALGORITHM_TYPE = "BC"; 
    static { 
	    BouncyCastleProvider bcProvider = new BouncyCastleProvider(); 
	    java.security.Security.addProvider(bcProvider);
    }
    
    
    /** 
    *	密钥字节转Key. 
      *  
    *	@param key 
    *	the key 
    *	@return the key 
    *	@throws Exception 
    *	the exception 
      */ 
     private static Key toKey(byte[] key) throws Exception {   
    	 SecretKey deskey = new SecretKeySpec(key, KEY_ALGORITHM);  
    	 return deskey; 
     } 

    /** 
    *	3des加密. 
      *  
    *	@param data 
    *	the data 
    *	@param key 
    *	the key 
    *	@return the byte[] 
    *	@throws Exception 
    *	the exception 
      */ 
     public static byte[] threeEncrypt(byte[] data, byte[] key) throws Exception { 
	      try { 
	       Key k = toKey(key); 
	       Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, ALGORITHM_TYPE);
	       cipher.init(Cipher.ENCRYPT_MODE, k);
	       return cipher.doFinal(data);   
	       } catch (Exception e) {
	    	   e.printStackTrace();
	    	   throw new Exception(e.getMessage()); 
	       } 
     } 
    
 	public static String signByKeyDes(String content, String keyPath, String charset){
 		
 		String key = getKeyStrByPath(keyPath);
 		byte[] by = null;
		try {
			by = threeEncrypt(content.getBytes(),key.getBytes());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 		return new String(Base64.encodeBase64Chunked(by));
 		
 	}

}
