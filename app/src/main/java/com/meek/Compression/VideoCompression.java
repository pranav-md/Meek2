
package com.meek.Compression;

        import android.app.ProgressDialog;
        import android.content.Context;
        import android.os.AsyncTask;
        import android.util.Log;

        import com.iceteck.silicompressorr.SiliCompressor;

        import java.io.File;
        import java.net.URISyntaxException;

class VideoimageCompression {



    class VideoCompressAsyncTask extends AsyncTask<String, String, String> {

        Context mContext;
        ProgressDialog p_dialog;

        //public void compressImage()


        public VideoCompressAsyncTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p_dialog = new ProgressDialog(mContext);
            p_dialog.setMessage("Its loading....");
            p_dialog.show();
            p_dialog.setCancelable(false);

        }

        @Override
        protected String doInBackground(String... paths) {
            String filePath = null;
            try {

                filePath = SiliCompressor.with(mContext).compressVideo(paths[0], paths[1]);

            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return filePath;

        }


        @Override
        protected void onPostExecute(String compressedFilePath) {
            super.onPostExecute(compressedFilePath);
            File imageFile = new File(compressedFilePath);
            float length = imageFile.length() / 1024f; // Size in KB
            String value;
            if (length >= 1024)
                value = length / 1024f + " MB";
            else
                value = length + " KB";
            p_dialog.dismiss();
            Log.i("Silicompressor", "Path: " + compressedFilePath);
        }
    }

}
