package com.amtee.camscanner.utilities.pdf_utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by DEVEN SINGH on 2/19/2015.
 */

public class PdfCreator {

    Context context;
    FileOutputStream fOut;

    public PdfCreator(Context context) {
        this.context = context;
    }

    public String createPdf(String[] imgPath, String docName) {
        Document document = new Document(PageSize.A4, 38, 38, 38, 38);
        File file = null;
        try {
            File dir = new File(Environment.getExternalStorageDirectory(),
                    "/Amtee_CamScanner");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File pdfDir = new File(dir.getAbsolutePath() + "/Pdf_Doc");
            if (!pdfDir.exists()) {
                pdfDir.mkdir();
            }

            file = new File(pdfDir, docName);
            fOut = new FileOutputStream(file);

            PdfWriter pdfWriter = PdfWriter.getInstance(document, fOut);
            pdfWriter.setPageEvent(new MyHeaderAndFooter());
            document.open();


            for (int i = 0; i < imgPath.length; i++) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Bitmap bitmap = BitmapFactory.decodeFile(imgPath[i]);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                Image myImg = Image.getInstance(stream.toByteArray());
                myImg.scaleToFit(595, 815);
                myImg.setAbsolutePosition((PageSize.A4.getWidth() - myImg.getScaledWidth()) / 2, (PageSize.A4.getHeight() - myImg.getScaledHeight()) / 2);
                pdfWriter.getDirectContent().addImage(myImg);

                document.newPage();
            }


            try {
                fOut.flush();


            } catch (IOException ioe) {
            }

        } catch (DocumentException de) {
            Log.e("PDFCreator", "DocumentException:" + de);
            return null;
        } catch (IOException e) {
            Log.e("PDFCreator", "ioException:" + e);
            return null;
        } finally {
            document.close();
        }

        try {

            fOut.close();

        } catch (IOException ioe) {
        }

        return file.getAbsolutePath();
    }
}
