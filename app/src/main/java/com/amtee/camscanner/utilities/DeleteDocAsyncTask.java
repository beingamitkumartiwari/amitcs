package com.amtee.camscanner.utilities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.amtee.camscanner.R;
import com.amtee.camscanner.listners.AdapterListner;
import com.amtee.camscanner.custom_adapters.MyDocsAdapter;
import com.amtee.camscanner.utilities.extra_utils.CamScannerDatabase;
import com.amtee.camscanner.utilities.extra_utils.CustomProgressDialog;

import java.io.File;

/**
 * Created by DEVEN SINGH on 2/19/2015.
 */
public class DeleteDocAsyncTask extends AsyncTask<Void, Void, Void> {

    CamScannerDatabase dbHelper;
    Context context;
    CustomProgressDialog customProgressDialog;
    MyDocsAdapter myDocsAdapter;
    String[] docNames;
    AdapterListner adapterListner;

    public DeleteDocAsyncTask(Context context, CamScannerDatabase dbHelper, MyDocsAdapter myDocsAdapter,String[] docNames, AdapterListner adapterListner) {
        this.context = context;
        this.dbHelper = dbHelper;
        this.myDocsAdapter = myDocsAdapter;
        this.docNames=docNames;
        this.adapterListner=adapterListner;
        customProgressDialog = new CustomProgressDialog(context, R.mipmap.progress_img);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        customProgressDialog.show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        for(String docName: docNames) {
            String[] docsPath = dbHelper.getDocsForDeletionAndPdfCreation(docName);
            dbHelper.deleteDoc(docName);
            String allDocsPath = dbHelper.getAllPathsFromAllDocs();
            for (String path : docsPath) {
                if (!allDocsPath.contains(path)) {
                    File file = new File(path.toString());
                    file.delete();
                }
            }
            File pdfFile = new File(Environment.getExternalStorageDirectory() + "/Amtee_CamScanner/Pdf_Doc/"+docName+".pdf");
            if(pdfFile.exists()){
                pdfFile.delete();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        customProgressDialog.dismiss();
        adapterListner.listenerAdapter();
    }
}
