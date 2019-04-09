package com.meek.Encryption;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import static com.meek.ActivityViewSetter.copy;

public class RSAKeyExchange {

    String uid;
    KeyPair keyPair;
    PublicKey pubKey,id_pubkey;
    boolean flg;
    PrivateKey privateKey;
    Context context;


    public void generateKeyPair() throws Exception {
        // generate public and private keys
        KeyPair keyPair = buildKeyPair();
        pubKey = keyPair.getPublic();
        privateKey = keyPair.getPrivate();

        // sign the message
        byte [] encrypted = encrypt(pubKey, "This is a secret message");
        System.out.println("Encrypted message is :"+(new String(encrypted)));  // <<signed message>>

        // verify the message
        byte [] decrypted = decrypt(privateKey, encrypted);
        System.out.println("Decrypted message is :"+(new String(decrypted)));     // This is a secret message
        System.out.println("Public key="+pubKey);
        System.out.println("Private key="+privateKey);
    }


    public KeyPair buildKeyPair() throws NoSuchAlgorithmException
    {
        final int keySize = 2048;
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keySize);
        return keyPairGenerator.genKeyPair();
    }

    public static byte [] encrypt(PublicKey publicKey, String message) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        return cipher.doFinal(message.getBytes());

    }

    public static byte [] decrypt(PrivateKey privateKey, byte [] encrypted) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(encrypted);
    }

    void writeMyKeys() throws IOException
    {
        FileOutputStream out=null;
        out=context.openFileOutput("MyKey/private.key", Context.MODE_PRIVATE);
        out.write(privateKey.getEncoded());

        out = context.openFileOutput("MyKey/public.pub", Context.MODE_PRIVATE);
        out.write(pubKey.getEncoded());
        out.close();
    }

    void uploadPublicKey() throws IOException
    {
        FileInputStream fis = context.openFileInput("MyKey/public.pub");
        final byte[] bytes = new byte[(int) fis.getChannel().size()];
        try
        {
            BufferedInputStream buf = new BufferedInputStream(fis);
            buf.read(bytes, 0, bytes.length);
            buf.close();
        }
        catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child("Public Key/"+uid+".pub").putBytes(bytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>(){
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                Log.e("PUBLIC KEY","Key has uploaded");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });
    }

    public PublicKey getPublicKey(String id) throws IOException {
        final FirebaseStorage storage = FirebaseStorage.getInstance();
        flg=false;
        StorageReference storageRef = storage.getReference();
        String file_name=id+".pub";
        final File localFile = File.createTempFile(file_name, "pub");
        storageRef.child("Public Keys/"+file_name).getFile(localFile).addOnSuccessListener(
                new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot)
            {
                int size = (int) localFile.length();
                byte[] bytes = new byte[size];
                try {
                    BufferedInputStream buf = new BufferedInputStream(new FileInputStream(localFile));
                    buf.read(bytes, 0, bytes.length);
                    buf.close();
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                X509EncodedKeySpec ks = new X509EncodedKeySpec(bytes);
                KeyFactory kf = null;
                try {
                    kf = KeyFactory.getInstance("RSA");
                    id_pubkey = kf.generatePublic(ks);
                    flg=true;
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (InvalidKeySpecException e) {
                    e.printStackTrace();
                }


            }
        });
        while(!flg);
            return id_pubkey;
    }

    PrivateKey myPrivateKey()
    {
        PrivateKey pvt=null;
        try
        {
            FileInputStream out = context.openFileInput("MyKey/public.pub");
            final byte[] bytes = new byte[(int) out.getChannel().size()];

            BufferedInputStream buf = new BufferedInputStream(out);
            buf.read(bytes, 0, bytes.length);
            buf.close();

            PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(bytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            pvt = kf.generatePrivate(ks);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return  pvt;

    }
}