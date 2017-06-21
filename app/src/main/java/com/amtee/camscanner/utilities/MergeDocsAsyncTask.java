package com.amtee.camscanner.utilities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;


import com.amtee.camscanner.R;
import com.amtee.camscanner.custom_adapters.MyDocsAdapter;
import com.amtee.camscanner.utilities.extra_utils.CamScannerDatabase;
import com.amtee.camscanner.utilities.extra_utils.CustomProgressDialog;
import com.amtee.camscanner.activities.PdfDocumentCreationActivity;
import com.amtee.camscanner.model_classes.MyDocItems;

import java.util.Iterator;

/**
 * Created by DEVEN SINGH on 2/19/2015.
 */
public class MergeDocsAsyncTask extends AsyncTask<Void, Void, String> {

    Context context;
    MyDocsAdapter myDocsAdapter;
    CamScannerDatabase dbHelper;
    String newDocName;
    CustomProgressDialog customProgressDialog;

    public MergeDocsAsyncTask(Context context, CamScannerDatabase dbHelper, MyDocsAdapter myDocsAdapter, String newDocName) {
        this.context = context;
        this.dbHelper = dbHelper;
        this.myDocsAdapter = myDocsAdapter;
        this.newDocName = newDocName;
        customProgressDialog = new CustomProgressDialog(context, R.mipmap.progress_img);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        customProgressDialog.show();
    }

    @Override
    protected String doInBackground(Void... params) {
        int i = 0;
        String paths = "";
        String docsName = null;
        Iterator iterator = myDocsAdapter.getSelected().iterator();
        while (iterator.hasNext()) {
            if (i > 0) {
                paths = paths + ",";
            }
            docsName = ((MyDocItems) iterator.next()).getMyDocName().toString();
            paths = paths + dbHelper.getDocPathsForMerge(docsName);
            i++;
        }
        return paths;
    }

    @Override
    protected void onPostExecute(String paths) {
        super.onPostExecute(paths);
        customProgressDialog.dismiss();
        if (dbHelper.insertDoc(newDocName, paths,new DateAndTime().DateNTime(),"")) {
            Intent intentDocCreationActivity = new Intent(context, PdfDocumentCreationActivity.class);
            intentDocCreationActivity.putExtra("docPath", newDocName);
            context.startActivity(intentDocCreationActivity);
        } else {
            Toast.makeText(context, "error in making new doc", Toast.LENGTH_SHORT).show();
        }
    }
}
