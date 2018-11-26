package com.yumu.hexie.integration.wuye.util;


import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

import org.slf4j.LoggerFactory;



public abstract class JHRSACoder extends JHCoder {
	
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(JHRSACoder.class);
	
	public static final String KEY_ALGORITHM = "RSA";
	public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

	private static final String PUBLIC_KEY = "RSAPublicKey";
	private static final String PRIVATE_KEY = "RSAPrivatekey";

	/**
	 * 用私钥对信息生成数字签名
	 * 
	 * @param data
	 *            加密数据
	 * @param privateKey
	 *            私钥
	 * @return
	 * @throws Exception
	 */
	public static String sign(byte[] data, String privateKey) throws Exception {
		// 解密由base64编码的私钥
		byte[] keyBytes = decryptBASE64(privateKey);

		PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);

		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

		// 取私钥对象
		PrivateKey pKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);

		// 用私钥生成数字签名
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initSign(pKey);
		signature.update(data);

		return encryptBASE64(signature.sign());
	}

	/**
	 * 校验数字签名
	 * 
	 * @param data
	 *            加密数据
	 * @param publicKey
	 *            公钥
	 * @param sign
	 *            数字签名
	 * @return
	 * @throws Exception
	 */
	public static boolean verify(byte[] data, String publicKey, String sign) throws Exception {

		// 解密有base64编码的公钥
		byte[] keyBytes = decryptBASE64(publicKey);

		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);

		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

		// 取公钥对象
		PublicKey pKey = keyFactory.generatePublic(keySpec);

		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initVerify(pKey);
		signature.update(data);
		// 验证签名是否正常
		return signature.verify(decryptBASE64(sign));
	}

	/**
	 * 解密 用私钥解密
	 * 
	 * @param data
	 *            加密数据
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptPrivateKey(byte[] data, String key) throws Exception {
		byte[] keyBytes = decryptBASE64(key);

		// 取得私钥
		PKCS8EncodedKeySpec encodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory factory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key pKey = factory.generatePrivate(encodedKeySpec);

		// 对数据解密
		Cipher cipher = Cipher.getInstance(factory.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, pKey);

		return cipher.doFinal(data);
	}

	/**
	 * 用公钥解密
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptByPublicKey(byte[] data, String key) throws Exception {

		// 解密
		byte[] keyBytes = decryptBASE64(key);

		// 取得公钥
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key pKey = keyFactory.generatePublic(keySpec);

		// 对数据解密
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, pKey);

		return cipher.doFinal(data);
	}

	/**
	 * 用公钥加密
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptByPublicKey(byte[] data, String key) throws Exception {

		byte[] keyBytes = decryptBASE64(key);

		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key pKey = keyFactory.generatePublic(keySpec);

		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, pKey);

		return cipher.doFinal(data);
	}

	/**
	 * 用私钥加密
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptByPrivateKey(byte[] data, String key) throws Exception {

		byte[] keyBytes = decryptBASE64(key);

		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key privateKey = keyFactory.generatePrivate(keySpec);

		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, privateKey);

		return cipher.doFinal(data);
	}

	/**
	 * 取得私钥
	 * 
	 * @param keyMap
	 * @return
	 * @throws Exception
	 */
	public static String getPrivateKey(Map<String, Object> keyMap) throws Exception {

		Key key = (Key) keyMap.get(PRIVATE_KEY);

		return encryptBASE64(key.getEncoded());
	}

	/**
	 * 取得公钥
	 * 
	 * @param keyMap
	 * @return
	 * @throws Exception
	 */
	public static String getPublicKey(Map<String, Object> keyMap) throws Exception {

		Key key = (Key) keyMap.get(PUBLIC_KEY);

		return encryptBASE64(key.getEncoded());
	}

	/**
	 * 初始化密钥
	 * 
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> initKey() throws Exception {

		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
		keyPairGenerator.initialize(1024);

		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		// 公钥
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

		// 私钥
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

		Map<String, Object> keyMap = new HashMap<String, Object>(2);
		keyMap.put(PRIVATE_KEY, privateKey);
		keyMap.put(PUBLIC_KEY, publicKey);
		return keyMap;
	}
	
	/**
	 * 初始化密钥
	 * 
	 * @return
	 * @throws Exception
	 */
	public static Map<String, String> initKeyStr() throws Exception {

		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
		keyPairGenerator.initialize(1024);

		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		// 公钥
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

		// 私钥
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

		Map<String, String> keyMap = new HashMap<String, String>(2);
		keyMap.put(PRIVATE_KEY, encryptBASE64(privateKey.getEncoded()));
		keyMap.put(PUBLIC_KEY, encryptBASE64(publicKey.getEncoded()));
		return keyMap;
	}
	
	public static void main(String[] args) throws Exception{
		/*Map<String, String> keyMap = initKeyStr();
		String publicKey = keyMap.get(PUBLIC_KEY);
		String privateKey = keyMap.get(PRIVATE_KEY);
		
		logger.info("publicKey     " + publicKey);
		logger.info("privateKey    " + privateKey);
		
		String plain = "1B31CEAE1935583A078D46203677735129FC01A591022906";
		logger.info("plain         " + plain);
		
		byte[] bs = encryptByPublicKey(ConvertUtils.hexStringToByte(plain), publicKey);
		String enHex = ConvertUtils.bytesToHexString(bs);
		logger.info("enHex         " + enHex);
		
		byte[] bss = decryptPrivateKey(ConvertUtils.hexStringToByte(enHex), privateKey);
		String dePlain = ConvertUtils.bytesToHexString(bss);
		
		logger.info("dePlain       " + dePlain);*/
		//Map<String, String> keyMap = initKeyStr();
		/*String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCkrnjaV2BS0ohkol8OOLdy6f6kCqpPZ/Y9fdZvW350Ir0WSSyTIuem7mkklE2qwwhpFnccsfHMOzTpwVZ8ctQ0DubUecbQkxOJ3nZ85ntjjVAKZS6XfKvSVUG5rz/t/rpFqkWdCMTP9FnQyB2n1cCkwpSVOExoJHivzj8VIrtavQIDAQAB";
		String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAKSueNpXYFLSiGSiXw44t3Lp/qQKqk9n9j191m9bfnQivRZJLJMi56buaSSUTarDCGkWdxyx8cw7NOnBVnxy1DQO5tR5xtCTE4nednzme2ONUAplLpd8q9JVQbmvP+3+ukWqRZ0IxM/0WdDIHafVwKTClJU4TGgkeK/OPxUiu1q9AgMBAAECgYAsvtdJhDpA5sF2joPDane1+oxc6CT0ZGwMszdjlHmcdp/oAUPiSrakrdzVupIL1uhfHWKJ4nOvqVqsNQMZ111+w8HmGkbKyPc4C0kq+SXJ+yBrekfpncx8Geb4SwpevYcHrB9VIr+lrcIue1NLusUayklZfDxLlaT1XtKqVAjxoQJBANvyEvxSjFfjmEgmB7GUoLrJCcVi17gGiyLsue0cHIWt43M5wx4LAqqtIXjEFkdbvhnaYB0m+a5VosmvfpAU28UCQQC/rUOfVz1gjSjxfHXOUKfPdZzbQ+wx/aaty5ZAAoaWeugDgcbhpJ6PCuPDFTVJqcF55sq2Zlfn46pMZirtShqZAkEAqPXgYddkGowrSpl0ZZWgl3yitfBFeQgF0JF8Dsr+mi7HwJeJ7UVWChkv0l8IIk82zRN1rE9plOQSRs868aIkZQJAS+tuy2AcmW+P9gZcCvn7XbqyBE8k8LANIu1ud7AIavYHi8wNYDZ57yJbEFwIHUM1tPjq9dAXpWuRem1FN3OQkQJBAMZlmeZGNECvw52cUBwikRbOrryd2wZN+BvjkOjF23fV93H3UB7+rxuqq412AoMX3tgfN/VcTaS6LVz5ndYlDJ4=";
		
		logger.info("publicKey     " + publicKey);
		logger.info("privateKey    " + privateKey);
		
		String plain = "张勇";
		logger.info("plain         " + plain);
		String plainHex = HexUtils.toHex(plain.getBytes("UTF-8"));
		logger.info("plainHex         " + plainHex);
		byte[] bs = encryptByPrivateKey(ConvertUtils.hexStringToByte(plainHex), privateKey);
		String enHex = ConvertUtils.bytesToHexString(bs);
		logger.info("enHex         " + enHex);
		
		byte[] bss = decryptByPublicKey(ConvertUtils.hexStringToByte(enHex), publicKey);
		String dePlainHex = ConvertUtils.bytesToHexString(bss);
		
		logger.info("dePlainHex       " + dePlainHex);
		byte[] dePlain = HexUtils.fromHex(dePlainHex);
		System.out.println(new String(dePlain));*/
		
		
		Map<String, String> keyMap = initKeyStr();
		String publicKey = keyMap.get(PUBLIC_KEY);
		String privateKey = keyMap.get(PRIVATE_KEY);
		
		logger.info("publicKey     " + publicKey);
		logger.info("privateKey    " + privateKey);
		
		/*String pubKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCTEYXMnIhKevaFVh+YiVIW5DWKlm+a+qNVf0FJTPaAPYbnr45ELvXIHdwpBQaJdraMz6b4+iC+EorQ9uQPmSdP0yV5/GFSR+QGHiDPx95U7Ue9YHChffbVNCQtsI3KMXiSeOr+5Syo2fREDRLyNna2iEoEfwhqI3Yg5FxTeqoHzwIDAQAB";
		byte[] keyBytes = decryptBASE64(pubKey);	
		logger.info("keyBytes=       " + keyBytes);
		logger.info("pubKeyBase64=   " + ConvertUtils.bytesToHexString(keyBytes));*/
	}
}
