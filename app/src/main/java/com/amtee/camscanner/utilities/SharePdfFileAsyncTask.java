package com.amtee.camscanner.utilities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;


import com.amtee.camscanner.R;
import com.amtee.camscanner.custom_adapters.MyDocsAdapter;
import com.amtee.camscanner.utilities.extra_utils.CamScannerDatabase;
import com.amtee.camscanner.utilities.extra_utils.CustomProgressDialog;
import com.amtee.camscanner.utilities.pdf_utils.PdfCreator;
import com.amtee.camscanner.model_classes.MyDocItems;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by DEVEN SINGH on 2/21/2015.
 */
public class SharePdfFileAsyncTask extends AsyncTask<Void, Void, String[]> {

    Context context;
    MyDocsAdapter myDocsAdapter;
    CamScannerDatabase dbHelper;
    CustomProgressDialog customProgressDialog;

    public SharePdfFileAsyncTask(Context context, MyDocsAdapter myDocsAdapter, CamScannerDatabase dbHelper) {
        this.context = context;
        this.myDocsAdapter = myDocsAdapter;
        this.dbHelper = dbHelper;
        customProgressDialog = new CustomProgressDialog(context, R.mipmap.progress_img);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        customProgressDialog.show();
    }

    @Override
    protected String[] doInBackground(Void... params) {
        String docsName = null;
        String[] pdfFileName = new String[myDocsAdapter.getSelected().size()];
        File pdfFileDir = new File(Environment.getExternalStorageDirectory() + "/Amtee_CamScanner/Pdf_Doc");
        if (!pdfFileDir.exists()) {
            pdfFileDir.mkdirs();
        }
        int i = 0;
        Iterator iterator = myDocsAdapter.getSelected().iterator();
        while (iterator.hasNext()) {
            docsName = ((MyDocItems) iterator.next()).getMyDocName().toString();
            File file = new File(pdfFileDir.getAbsolutePath() + File.separator + docsName + ".pdf");
            pdfFileName[i] = file.getAbsolutePath();
            if (!file.exists()) {
                new PdfCreator(context).createPdf(dbHelper.getDocsForDeletionAndPdfCreation(docsName), docsName + ".pdf");
            }
            i++;
        }
        return pdfFileName;
    }

    @Override
    protected void onPostExecute(String[] pdfFilesToSend) {
        super.onPostExecute(pdfFilesToSend);
        customProgressDialog.dismiss();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Share PDF Doc");
        intent.setType("application/pdf");

        ArrayList<Uri> files = new ArrayList<Uri>();

        for (String path : pdfFilesToSend) {
            File file = new File(path);
            Uri uri = Uri.fromFile(file);
            files.add(uri);
        }

        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
        context.startActivity(intent);
    }
}
