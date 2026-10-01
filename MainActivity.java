package com.mekarsari.barzanji;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivity extends Activity {
    private PdfRenderer renderer;
    private ParcelFileDescriptor pfd;
    private ImageView pageImage;
    private TextView pageLabel;
    private int currentPage = 0;
    private float downX;
    private float scaleFactor = 1f;
    private final Matrix imageMatrix = new Matrix();
    private ScaleGestureDetector scaleDetector;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showHome();
    }

    private int dp(int v) { return (int)(v * getResources().getDisplayMetrics().density + .5f); }

    private TextView title(String text, int size) {
        TextView t = new TextView(this);
        t.setText(text); t.setTextSize(size); t.setTextColor(Color.WHITE);
        t.setGravity(Gravity.CENTER); t.setPadding(dp(12), dp(10), dp(12), dp(10));
        return t;
    }

    private Button button(String text) {
        Button b = new Button(this); b.setText(text); b.setTextSize(16); return b;
    }

    private LinearLayout base() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL); root.setBackgroundColor(Color.rgb(255,253,245));
        return root;
    }

    private TextView toolbar(String text) {
        TextView bar = title(text, 20); bar.setBackgroundColor(Color.rgb(20,83,45));
        bar.setTypeface(null, android.graphics.Typeface.BOLD); return bar;
    }

    private String getPdfPageCountText() {
        try {
            File f = new File(getCacheDir(), "barzanji.pdf");
            if (!f.exists()) {
                InputStream in = getAssets().open("MAULID BARZANJI.pdf");
                FileOutputStream out = new FileOutputStream(f);
                byte[] buf = new byte[8192]; int n; while ((n=in.read(buf))>0) out.write(buf,0,n);
                in.close(); out.close();
            }
            ParcelFileDescriptor temp = ParcelFileDescriptor.open(f, ParcelFileDescriptor.MODE_READ_ONLY);
            PdfRenderer r = new PdfRenderer(temp);
            int count = r.getPageCount();
            r.close(); temp.close();
            return String.valueOf(count);
        } catch (Exception e) {
            return "";
        }
    }

    private void showHome() {
        LinearLayout root = base();
        root.addView(toolbar("KITAB MAULID AL-BARZANJI"), new LinearLayout.LayoutParams(-1, dp(60)));
        LinearLayout center = new LinearLayout(this); center.setOrientation(LinearLayout.VERTICAL); center.setGravity(Gravity.CENTER); center.setPadding(dp(28),dp(28),dp(28),dp(28));
        TextView title = new TextView(this); title.setText("مَوْلِدُ الْبَرْزَنْجِي"); title.setTextSize(32); title.setTextColor(Color.rgb(20,83,45)); title.setGravity(Gravity.CENTER); title.setPadding(0,dp(20),0,dp(10));
        TextView sub = new TextView(this); sub.setText("Kitab digital dari PDF sumber\nBaca offline • " + getPdfPageCountText() + " halaman"); sub.setTextSize(16); sub.setGravity(Gravity.CENTER); sub.setTextColor(Color.DKGRAY); sub.setPadding(0,0,0,dp(30));
        center.addView(title); center.addView(sub);
        Button read = button("▶  MULAI MEMBACA");
        Button pages = button("☷  DAFTAR HALAMAN");
        center.addView(read, new LinearLayout.LayoutParams(-1, dp(56)));
        center.addView(pages, new LinearLayout.LayoutParams(-1, dp(56)));
        read.setOnClickListener(v -> { openPdf(); showReader(); });
        pages.setOnClickListener(v -> { openPdf(); showPages(); });
        root.addView(center, new LinearLayout.LayoutParams(-1,0,1));
        setContentView(root);
    }

    private void openPdf() {
        if (renderer != null) return;
        try {
            File f = new File(getCacheDir(), "barzanji.pdf");
            if (!f.exists()) {
                InputStream in = getAssets().open("MAULID BARZANJI.pdf");
                FileOutputStream out = new FileOutputStream(f);
                byte[] buf = new byte[8192]; int n; while ((n=in.read(buf))>0) out.write(buf,0,n);
                in.close(); out.close();
            }
            pfd = ParcelFileDescriptor.open(f, ParcelFileDescriptor.MODE_READ_ONLY);
            renderer = new PdfRenderer(pfd);
        } catch (Exception e) { Toast.makeText(this, "PDF tidak dapat dibuka: "+e.getMessage(), Toast.LENGTH_LONG).show(); }
    }

    private void showPages() {
        LinearLayout root = base();
        TextView bar = toolbar("DAFTAR HALAMAN"); bar.setOnClickListener(v -> showHome());
        root.addView(bar, new LinearLayout.LayoutParams(-1, dp(60)));
        GridLayout grid = new GridLayout(this); grid.setColumnCount(3); grid.setPadding(dp(16),dp(16),dp(16),dp(16));
        for (int i=0;i<renderer.getPageCount();i++) {
            final int p=i; Button b=button("Halaman " + (i+1));
            GridLayout.LayoutParams lp=new GridLayout.LayoutParams(); lp.width=0; lp.height=dp(62); lp.columnSpec=GridLayout.spec(i%3,1f); lp.setMargins(dp(5),dp(5),dp(5),dp(5));
            grid.addView(b,lp); b.setOnClickListener(v->{currentPage=p; showReader();});
        }
        root.addView(grid,new LinearLayout.LayoutParams(-1,0,1));
        setContentView(root);
    }

    private void showReader() {
        LinearLayout root=base();
        LinearLayout top=new LinearLayout(this); top.setGravity(Gravity.CENTER_VERTICAL); top.setBackgroundColor(Color.rgb(20,83,45));
        Button back=button("‹"); back.setTextColor(Color.WHITE); back.setTextSize(28); back.setBackgroundColor(Color.TRANSPARENT); top.addView(back,new LinearLayout.LayoutParams(dp(55),dp(58)));
        pageLabel=title("",16); top.addView(pageLabel,new LinearLayout.LayoutParams(0,dp(58),1));
        Button list=button("☷"); list.setTextColor(Color.WHITE); list.setTextSize(22); list.setBackgroundColor(Color.TRANSPARENT); top.addView(list,new LinearLayout.LayoutParams(dp(55),dp(58)));
        back.setOnClickListener(v->showHome()); list.setOnClickListener(v->showPages()); root.addView(top);

        pageImage=new ImageView(this); pageImage.setBackgroundColor(Color.rgb(245,242,232)); pageImage.setScaleType(ImageView.ScaleType.MATRIX); pageImage.setAdjustViewBounds(true);
        pageImage.setOnTouchListener((v,event)->handleTouch(event));
        scaleDetector=new ScaleGestureDetector(this,new ScaleGestureDetector.SimpleOnScaleGestureListener(){ public boolean onScale(ScaleGestureDetector d){ scaleFactor*=d.getScaleFactor(); scaleFactor=Math.max(1f,Math.min(4f,scaleFactor)); imageMatrix.setScale(scaleFactor,scaleFactor,pageImage.getWidth()/2f,pageImage.getHeight()/2f); pageImage.setImageMatrix(imageMatrix); return true; }});
        root.addView(pageImage,new LinearLayout.LayoutParams(-1,0,1));
        LinearLayout nav=new LinearLayout(this); nav.setPadding(dp(8),dp(5),dp(8),dp(5)); nav.setGravity(Gravity.CENTER);
        Button prev=button("← Sebelumnya"), next=button("Berikutnya →"); nav.addView(prev,new LinearLayout.LayoutParams(0,dp(52),1)); nav.addView(next,new LinearLayout.LayoutParams(0,dp(52),1)); root.addView(nav);
        prev.setOnClickListener(v->{if(currentPage>0){currentPage--; renderPage();}}); next.setOnClickListener(v->{if(currentPage<renderer.getPageCount()-1){currentPage++;renderPage();}});
        setContentView(root); pageImage.post(this::renderPage);
    }

    private boolean handleTouch(MotionEvent e){
        scaleDetector.onTouchEvent(e);
        if(e.getAction()==MotionEvent.ACTION_DOWN){downX=e.getX(); return true;}
        if(e.getAction()==MotionEvent.ACTION_UP && scaleFactor<=1.05f){float dx=e.getX()-downX; if(Math.abs(dx)>dp(70)){ if(dx<0 && currentPage<renderer.getPageCount()-1) currentPage++; else if(dx>0 && currentPage>0) currentPage--; renderPage(); } return true;}
        return true;
    }

    private void renderPage(){
        if(renderer==null || pageImage==null) return;
        try{
            PdfRenderer.Page page=renderer.openPage(currentPage);
            int width=Math.max(pageImage.getWidth(),600); int height=(int)((float)page.getHeight()/page.getWidth()*width);
            Bitmap bmp=Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
            page.render(bmp,null,null,PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY); page.close();
            pageImage.setImageBitmap(bmp); scaleFactor=1f; imageMatrix.reset(); pageImage.setImageMatrix(imageMatrix);
            pageLabel.setText("Maulid Al-Barzanji   •   Halaman "+(currentPage+1)+" / "+renderer.getPageCount());
        }catch(Exception e){Toast.makeText(this,"Gagal menampilkan halaman",Toast.LENGTH_SHORT).show();}
    }

    @Override protected void onDestroy(){super.onDestroy(); try{if(renderer!=null)renderer.close(); if(pfd!=null)pfd.close();}catch(Exception ignored){}}
}
