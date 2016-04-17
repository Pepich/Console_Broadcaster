

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class Ciphers
{
	public static Cipher RSA_DECODE;
	
	public Cipher AES_DECODE;
	public Cipher AES_ENCODE;
	
	public static SecretKey AES_SECRET_KEY;
	public static PrivateKey RSA_PRIVATE_KEY;
	
	public Ciphers(SecretKey key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, ClassNotFoundException, IllegalBlockSizeException, BadPaddingException, IOException
	{
		AES_SECRET_KEY = key;
		
		AES_DECODE = Cipher.getInstance("AES/CTR/NoPadding");
		byte[] AES_DECODE_IV = new byte[AES_DECODE.getBlockSize()];
		IvParameterSpec AES_DECODE_PS = new IvParameterSpec(AES_DECODE_IV);
		AES_DECODE.init(Cipher.DECRYPT_MODE, AES_SECRET_KEY, AES_DECODE_PS);
		
		AES_ENCODE = Cipher.getInstance("AES/CTR/NoPadding");
		byte[] AES_ENCODE_IV = new byte[AES_DECODE.getBlockSize()];
		IvParameterSpec AES_ENCODE_PS = new IvParameterSpec(AES_ENCODE_IV);
		AES_ENCODE.init(Cipher.ENCRYPT_MODE, AES_SECRET_KEY, AES_ENCODE_PS);
		
		System.out.println(new SealedObject("AES key test succesfull!", AES_DECODE).getObject(AES_ENCODE));
	}
	
	public static void init() throws NoSuchAlgorithmException, NoSuchPaddingException, IOException, InvalidKeySpecException, InvalidKeyException, ClassNotFoundException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException
	{
		File privateKeyFile = new File("private_key");
		FileInputStream fileInputStream = new FileInputStream(privateKeyFile);
		
		byte[] encodedPrivateKey = new byte[(int) privateKeyFile.length()];
		fileInputStream.read(encodedPrivateKey);
		fileInputStream.close();
		
		KeyFactory keyFac = KeyFactory.getInstance("RSA");
		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
		RSA_PRIVATE_KEY = keyFac.generatePrivate(privateKeySpec);
		
		RSA_DECODE = Cipher.getInstance("RSA");
		RSA_DECODE.init(Cipher.DECRYPT_MODE, RSA_PRIVATE_KEY);
	}
}
