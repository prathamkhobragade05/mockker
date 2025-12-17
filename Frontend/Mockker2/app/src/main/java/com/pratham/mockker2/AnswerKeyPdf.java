package com.pratham.mockker2;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.pratham.mockker2.MainActivityResult.answerKey;
import static com.pratham.mockker2.MainActivityResult.progressBar;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AnswerKeyPdf {
    Context context;

    public Uri generatePdf(HashMap<String, String> selectedResponse, List<ModelQuestion> questionList, Context context) {
        this.context = context;
        PdfDocument pdfDocument = new PdfDocument();

        Paint paint = new Paint();
        Paint greenPaint = new Paint();
        Paint redPaint = new Paint();
        Paint title = new Paint();
        title.setTextSize(20);
        title.setFakeBoldText(true);

        greenPaint.setColor(ContextCompat.getColor(context, R.color.green));
        redPaint.setColor(ContextCompat.getColor(context, R.color.red));

        int pageW = 595;
        int pageH = 842;
        int y = 50;

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageW, pageH, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        waterMark(canvas, pageW, pageH);

        String Title = "Answer Key";
        float titleWidth = paint.measureText(Title);
        float titleX = (pageW - titleWidth) / 2;
        canvas.drawText(Title, 220, y, title);
        y += 50;


        for (int i = 0; i < questionList.size(); i++) {
            int xStartAxis = 30;
            String queNo = String.valueOf(i + 1);
            canvas.drawText(queNo + ". ", xStartAxis, y, paint);             //------------question no
            ModelQuestion question = questionList.get(i);
            String p = queNo.length() + "0";

            int xAxis = xStartAxis + Integer.parseInt(p);

            String questionText = question.getQuestion();                     //------------question text
            List<String> questionLine = getWrappedLines(questionText, paint, pageW - (2 * xStartAxis));

            for (String line : questionLine) {
                canvas.drawText(" " + line, xAxis, y, paint);
                y += 13;
                if (y > 780) {
                    pdfDocument.finishPage(page);
                    pageInfo = new PdfDocument.PageInfo.Builder(pageW, pageH, i + 2).create();
                    page = pdfDocument.startPage(pageInfo);
                    canvas = page.getCanvas();
                    y = 50;
                    waterMark(canvas, pageW, pageH);
                }
            }
            y += 10;
            String selectedAnswer = selectedResponse.get(String.valueOf(question.getId()));
            String correctAnswer = question.getAnswer();

            for (String opt : question.getOptions()) {                           //------------options
                String bullet;
                if (selectedAnswer.equals(opt)) {
                    bullet = "● ";
                } else {
                    bullet = "○ ";
                }

                if (opt.equals(correctAnswer)) {
                    canvas.drawText(bullet + opt, xAxis, y, greenPaint);
                } else {
                    if (opt.equals(selectedAnswer)) {
                        canvas.drawText(bullet + opt, xAxis, y, redPaint);
                    } else {
                        canvas.drawText(bullet + opt, xAxis, y, paint);
                    }
                }
                y += 15;
                if (y > 780) {
                    pdfDocument.finishPage(page);
                    pageInfo = new PdfDocument.PageInfo.Builder(pageW, pageH, i + 2).create();
                    page = pdfDocument.startPage(pageInfo);
                    canvas = page.getCanvas();
                    y = 50;
                    waterMark(canvas, pageW, pageH);
                }
            }
            y += 15;
            if (y > 780) {
                pdfDocument.finishPage(page);
                pageInfo = new PdfDocument.PageInfo.Builder(pageW, pageH, i + 2).create();
                page = pdfDocument.startPage(pageInfo);
                canvas = page.getCanvas();
                y = 50;
                waterMark(canvas, pageW, pageH);
            }
            if (i == questionList.size() - 1) {
                y += 50;
                String text = "* * * Thank You * * *\n";
                float textWidth = paint.measureText(text);
                float x = (pageW - textWidth) / 2;

                canvas.drawText(text, x, y, paint);
            }
        }

        pdfDocument.finishPage(page);
        // -------------------- MediaStore Save ---------------------
        String fileName = "AnswerKey_" + System.currentTimeMillis() + ".pdf";
        ContentValues values = new ContentValues();
        values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
        values.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
        values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

        Uri pdfUri = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            pdfUri = context.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
        }

        try {
            OutputStream outputStream = context.getContentResolver().openOutputStream(pdfUri);
            pdfDocument.writeTo(outputStream);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        pdfDocument.close();
        return pdfUri;
    }

    private List<String> getWrappedLines(String text, Paint paint, int maxWidth) {
        List<String> lines = new ArrayList<>();
        StringBuilder line = new StringBuilder();
        String[] words = text.split("\\s+");
        for (String word : words) {
            String testLine = line.length() == 0 ? word : line + " " + word;
            if (paint.measureText(testLine) <= maxWidth) {
                line.setLength(0);
                line.append(testLine);
            } else {
                if (line.length() > 0) {
                    lines.add(line.toString());
                }
                line.setLength(0);
                line.append(word);
            }
        }
        if (line.length() > 0) {
            lines.add(line.toString());
        }
        return lines;
    }

    private void waterMark(Canvas canvas, int pageW, int pageH) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_watermark);
        Bitmap waterMarkLogo = Bitmap.createScaledBitmap(bitmap, 150, 150, true);
        Paint imgPaint = new Paint();
        imgPaint.setAlpha(60);
        canvas.save();
        int centerX = (pageW - waterMarkLogo.getWidth()) / 2;
        int centerY = (pageH - waterMarkLogo.getHeight()) / 2;
        canvas.drawBitmap(waterMarkLogo, centerX, centerY, imgPaint);
        canvas.restore();
    }
    String channelId="mockker.notification";
    String description="mocker download";
    int notificationId=1234;

}
