package com.example.asus.diaryexa;



import android.content.ContentResolver;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;



public class EditDiary extends AppCompatActivity {

    EditText ed1,ed2;
    ImageButton imageButton;
    ImageButton picButton;
    ImageButton camButton;
    ImageButton recButton;
    MyDataBase myDatabase;
    Cuns cun;


    private static final int Gallary = 1;
    private static final int TakePhoto = 2;


    int ids;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_diary);

        ed1=(EditText) findViewById(R.id.editText1);
        ed2=(EditText) findViewById(R.id.editText2);
        imageButton=(ImageButton) findViewById(R.id.saveButton);
        picButton=(ImageButton)findViewById(R.id.insetpicButton);
        camButton=(ImageButton)findViewById(R.id.cameraButton);
        recButton=(ImageButton)findViewById(R.id.recordButton);
        myDatabase=new MyDataBase(this);



        Intent intent=this.getIntent();
        ids=intent.getIntExtra("ids", 0);

        if(ids!=0){
            cun=myDatabase.getTiandCon(ids);
            ed1.setText(cun.getTitle());
            ed2.setText(cun.getContent());
        }

        camButton.setOnClickListener(new OnClickListener() {              //拍照插入图片
            @Override
            public void onClick(View v) {
                Intent getImageByCamera= new Intent("android.media.action.IMAGE_CAPTURE");
                startActivityForResult(getImageByCamera, TakePhoto);
            }
        });

        picButton.setOnClickListener(new OnClickListener() {             //相册插入图片
            @Override
            public void onClick(View v) {
                Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);
                getImage.addCategory(Intent.CATEGORY_OPENABLE);
                getImage.setType("image/*");
                startActivityForResult(getImage, Gallary);

            }
        });

        recButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        imageButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isSave();
            }
        });
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        ContentResolver resolver = getContentResolver();
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Gallary:
                    Uri originalUri = intent.getData();
                    Bitmap bitmap = null;
                    try {
                        Bitmap originalBitmap = BitmapFactory.decodeStream(resolver.openInputStream(originalUri));
                        bitmap = resizeImage(originalBitmap, 600, 600);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (bitmap != null) {
                        ImageSpan imageSpan = new ImageSpan(EditDiary.this, bitmap);
                        SpannableString spannableString = new SpannableString("[local]" + 1 + "[/local]");
                        spannableString.setSpan(imageSpan, 0, "[local]1[local]".length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        int index = ed2.getSelectionStart(); //获取光标所在位置
                        Editable edit_text = ed2.getEditableText();
                        if (index < 0 || index >= edit_text.length()) {
                            edit_text.append(spannableString);
                        } else {
                            edit_text.insert(index, spannableString);
                        }
                    } else {
                        Toast.makeText(EditDiary.this, "获取图片失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case TakePhoto:
                    Bundle extras = intent.getExtras();
                    Bitmap originalBitmap1 = (Bitmap) extras.get("data");
                    if (originalBitmap1 != null) {
                        bitmap = resizeImage(originalBitmap1, 600, 600);
                        ImageSpan imageSpan = new ImageSpan(EditDiary.this, bitmap);
                        SpannableString spannableString = new SpannableString("[local]" + 1 + "[/local]");
                        spannableString.setSpan(imageSpan, 0, "[local]1[local]".length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        int index = ed2.getSelectionStart();
                        Editable edit_text = ed2.getEditableText();
                        if (index < 0 || index >= edit_text.length()) {
                            edit_text.append(spannableString);
                        } else {
                            edit_text.insert(index, spannableString);
                        }
                    } else {
                        Toast.makeText(EditDiary.this, "获取图片失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }
    }


    @Override
    public void onBackPressed() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd  HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        String times = formatter.format(curDate);
        String title=ed1.getText().toString();
        String content=ed2.getText().toString();
        if(ids!=0){
            cun=new Cuns(title,ids, content, times);
            myDatabase.toUpdate(cun);
            Intent intent=new Intent(EditDiary.this,MainActivity.class);
            startActivity(intent);
            EditDiary.this.finish();
        }
        else{
            if(title.equals("")&&content.equals("")){
                Intent intent=new Intent(EditDiary.this,MainActivity.class);
                startActivity(intent);
                EditDiary.this.finish();
            }
            else{
                cun=new Cuns(title,content,times);
                myDatabase.toInsert(cun);
                Intent intent=new Intent(EditDiary.this,MainActivity.class);
                startActivity(intent);
                EditDiary.this.finish();
            }
        }
    }

    private void isSave(){

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd  HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        String times = formatter.format(curDate);
        String title=ed1.getText().toString();
        String content=ed2.getText().toString();
        if(ids!=0){
            cun=new Cuns(title,ids, content, times);
            myDatabase.toUpdate(cun);
            Intent intent=new Intent(EditDiary.this,MainActivity.class);
            startActivity(intent);
            EditDiary.this.finish();
        }
        else{
            cun=new Cuns(title,content,times);
            myDatabase.toInsert(cun);
            Intent intent=new Intent(EditDiary.this,MainActivity.class);
            startActivity(intent);
            EditDiary.this.finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.second, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,
                        "标题："+ed1.getText().toString()+"    " +
                                "内容："+ed2.getText().toString());
                startActivity(intent);
                break;

            default:
                break;
        }
        return false;
    }

    private Bitmap resizeImage(Bitmap originalBitmap, int newWidth, int newHeight){
        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();
        float scanleWidth = (float)newWidth/width;
        float scanleHeight = (float)newHeight/height;
        Matrix matrix = new Matrix();
        matrix.postScale(scanleWidth,scanleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(originalBitmap,0,0,width,height,matrix,true);
        return resizedBitmap;
    }

}


