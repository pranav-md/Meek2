package com.meek.Encryption;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Base64;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import static android.content.Context.MODE_PRIVATE;
import static com.meek.ActivityViewSetter.copy;

public class RSAKeyExchange {

    String uid;
    KeyPair keyPair;
    PublicKey pubKey,id_pubkey;
    boolean flg;
    PrivateKey privateKey;
    Context context;

    public RSAKeyExchange(Context context,String uid) {
        this.context = context;
        this.uid=uid;
    }

    public void generateKeyPair() throws Exception {
        // generate public and private keys
        KeyPair keyPair = buildKeyPair();
        pubKey = keyPair.getPublic();
        privateKey = keyPair.getPrivate();

        // sign the message
  //      byte [] encrypted = encrypt(pubKey, "This is a secret message");
  //      System.out.println("Encrypted message is :"+(new String(encrypted)));  // <<signed message>>

        // verify the message
  //      byte [] decrypted = decrypt(privateKey, encrypted);
  //      System.out.println("Decrypted message is :"+(new String(decrypted)));     // This is a secret message
  //      System.out.println("Public key="+pubKey);
  //      System.out.println("Private key="+privateKey);
    }


    public KeyPair buildKeyPair() throws NoSuchAlgorithmException
    {
        final int keySize = 2048;
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keySize);
        return keyPairGenerator.genKeyPair();
    }

    public String encrypt(PublicKey publicKey, String message) {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return android.util.Base64.encodeToString(cipher.doFinal(message.getBytes("UTF-8")), android.util.Base64.DEFAULT);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return "null";

    }

    public String decrypt(PrivateKey privateKey, String encrypted)  {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(cipher.doFinal(android.util.Base64.decode(encrypted, android.util.Base64.DEFAULT)));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return "null";
    }

    public void writeMyKeys() throws IOException
    {

        //String storageDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString();
        //////////////
        SharedPreferences getPref=context.getSharedPreferences("USERKEY",MODE_PRIVATE);
        SharedPreferences.Editor editKey=getPref.edit();
        byte[] encodedKey = pubKey.getEncoded();
        String bpubkey=Base64.encodeToString(encodedKey,Base64.NO_WRAP);
        editKey.putString("PublicKey",bpubkey);
        Log.e("WRITE KEYS","PublicKey="+bpubkey);
        encodedKey = privateKey.getEncoded();
        bpubkey=Base64.encodeToString(encodedKey,Base64.NO_WRAP);
        Log.e("WRITE KEYS","PrivateKey="+bpubkey);

        editKey.putString("PrivateKey",bpubkey);
        editKey.commit();
        /*
        FileOutputStream out=null;
        out= new FileOutputStream(storageDir+"/KEYS/private.key");
        out.write(privateKey.getEncoded());

        out= new FileOutputStream(storageDir+"/KEYS/public.pub");
        out.write(pubKey.getEncoded());
        out.close();
        */


    }
    public void uploadPublicKey() throws IOException
    {
        SharedPreferences getPref=context.getSharedPreferences("USERKEY",MODE_PRIVATE);
        String pubKey=getPref.getString("PublicKey","");
        DatabaseReference ppl_ref = FirebaseDatabase.getInstance().getReference();
        ppl_ref.child("PublicKey").child(uid).setValue(pubKey);
    }

/*
    public void uploadPublicKey() throws IOException
    {
        String storageDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString();
        Log.e("PUBLIC KEY UPLOADER","THe key is being uploaded");

        FileInputStream fis = new FileInputStream(storageDir+"/KEYS/public.pub");
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
*/

   /* public PublicKey getPublicKey(String id) {
        final FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        String file_name=id+".pub";
        final File localFile;
        try {
            localFile = File.createTempFile(file_name, "pub");
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
        } catch (IOException e) {
            flg=true;
            e.printStackTrace();
        }

        while(!flg);
            return id_pubkey;
    }*/
/*
    public PrivateKey myPrivateKey()
    {
        PrivateKey pvt=null;
        String storageDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString();
        try
        {
            FileInputStream out =new FileInputStream(storageDir+"/KEYS/public.pub");
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

    }*/

    public PrivateKey myPrivateKey()
    {
        SharedPreferences getPref=context.getSharedPreferences("USERKEY",MODE_PRIVATE);
        String pvtKey=getPref.getString("PrivateKey","");
        PrivateKey pvt=null;

        // Base64 decode the result

        byte [] pkcs8EncodedBytes = Base64.decode(pvtKey, Base64.NO_WRAP);

        // extract the private key

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pkcs8EncodedBytes);
        KeyFactory kf = null;
        try {
            kf = KeyFactory.getInstance("RSA");
            pvt = kf.generatePrivate(keySpec);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return pvt;
    }
}