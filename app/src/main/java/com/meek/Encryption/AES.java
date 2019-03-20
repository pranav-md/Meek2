package com.meek.Encryption;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.util.Base64;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Scanner;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class AES {

    private static SecretKeySpec secretKey;
    private static byte[] key;

    public static void setKey(String myKey)
    {
        MessageDigest sha = null;
        try {
            key = myKey.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public String encrypt(String strToEncrypt, String secret)
    {
        try
        {
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return android.util.Base64.encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")), android.util.Base64.DEFAULT);
        }
        catch (Exception e)
        {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    public  void encryptActivityImage(String secret,Context context)
    {

        setKey(secret);
        Cipher cipher = null;
        String storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();

        try {
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        // file to be encrypted
        FileInputStream inFile = null;
        FileOutputStream outFile = null;
        try {
            inFile = new FileInputStream(storageDir+"/activity.png");
            outFile = new FileOutputStream(storageDir+"/activity.crypt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // encrypted file
        byte[] input = new byte[64];
        int bytesRead;
        try
        {
            while ((bytesRead = inFile.read(input)) != -1)
            {
                byte[] output = cipher.update(input, 0, bytesRead);
                if (output != null)
                    outFile.write(output);
            }

            byte[] output = cipher.doFinal();
            if (output != null)
                outFile.write(output);

            inFile.close();
            outFile.flush();
            outFile.close();
        }
        catch (Exception e)
        {
            System.out.println("Error while encrypting: " + e.toString());
        }

    }
    public void encryptActivityVideo(String secret,Context context)
    {
        setKey(secret);
        Cipher cipher = null;
        String storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();

        try {
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        // file to be encrypted
        FileInputStream inFile = null;
        FileOutputStream outFile = null;
        try {
            inFile = new FileInputStream(storageDir+"/activity.mp4");
            outFile = new FileOutputStream(storageDir+"/activity.crypt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // encrypted file
        byte[] input = new byte[64];
        int bytesRead;
        try
        {
            while ((bytesRead = inFile.read(input)) != -1)
            {
                byte[] output = cipher.update(input, 0, bytesRead);
                if (output != null)
                    outFile.write(output);
            }

            byte[] output = cipher.doFinal();
            if (output != null)
                outFile.write(output);

            inFile.close();
            outFile.flush();
            outFile.close();
        }
        catch (Exception e)
        {
            System.out.println("Error while encrypting: " + e.toString());
        }

    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    public String decrypt(String strToDecrypt, String secret)
    {
        try
        {
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(android.util.Base64.decode(strToDecrypt, Base64.DEFAULT)));
        }
        catch (Exception e)
        {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }

    public void decryptActivityImage(String secret,String filepath,String filename)
    {
        setKey(secret);
        Cipher cipher = null;
        FileInputStream inFile = null;
        FileOutputStream outFile= null;
        try {
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            // file to be encrypted
            inFile = new FileInputStream(filepath+"/"+filename+".crypt");
            // encrypted file

            outFile = new FileOutputStream(filepath+"/"+filename+".png");

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        byte[] input = new byte[64];
        int bytesRead;

        try
        {
            while ((bytesRead = inFile.read(input)) != -1)
            {
                byte[] output = cipher.update(input, 0, bytesRead);
                if (output != null)
                    outFile.write(output);
            }

            byte[] output = cipher.doFinal();
            if (output != null)
                outFile.write(output);

            inFile.close();
            outFile.flush();
            outFile.close();
        }
        catch (Exception e)
        {
            System.out.println("Error while encrypting: " + e.toString());
        }
    }

    public void decryptActivityVideo(String secret,String filepath,String filename)  {
        setKey(secret);
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        // file to be decrypted
        FileInputStream inFile = null;
        FileOutputStream outFile = null;
        try {
            inFile = new FileInputStream(filepath+"/"+filename+".crypt");
            outFile = outFile = new FileOutputStream(filepath+"/"+filename+".mp4");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // encrypted file

        byte[] input = new byte[64];
        int bytesRead;

        try
        {
            while ((bytesRead = inFile.read(input)) != -1)
            {
                byte[] output = cipher.update(input, 0, bytesRead);
                if (output != null)
                    outFile.write(output);
            }

            byte[] output = cipher.doFinal();
            if (output != null)
                outFile.write(output);

            inFile.close();
            outFile.flush();
            outFile.close();
        }
        catch (Exception e)
        {
            System.out.println("Error while encrypting: " + e.toString());
        }
    }
    /*@RequiresApi(api = Build.VERSION_CODES.O)
    public static void main(String[] args)
    {
        final String secretKey = "we23rasfs";
        String originalString = "howtodoinjava.com";
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter your name:");
        String name = sc.nextLine();
        String encryptedString = AES.encrypt(name, secretKey) ;
        String decryptedString = AES.decrypt(encryptedString, secretKey) ;

        System.out.println("Your name is:"+name);
        System.out.println("Your encrypted name is:"+encryptedString);
        System.out.println("Your encrypted name is:"+decryptedString);
    }
*/
}