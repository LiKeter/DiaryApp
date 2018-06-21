package com.example.asus.diaryexa;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by asus on 2018/6/19.
 */

public class MyDataBase {
    Context context;
    MyOpenHelper myHelper;
    SQLiteDatabase myDatabase;

    public static final String PATH = "path";

    public MyDataBase(Context con){
        this.context=con;
        myHelper=new MyOpenHelper(context);
    }

    public ArrayList<Cuns> getArray(){
        ArrayList<Cuns> array = new ArrayList<Cuns>();
        ArrayList<Cuns> array1 = new ArrayList<Cuns>();
        myDatabase = myHelper.getWritableDatabase();
        Cursor cursor=myDatabase.rawQuery("select ids,title,times from mybook" , null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            int id = cursor.getInt(cursor.getColumnIndex("ids"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String times = cursor.getString(cursor.getColumnIndex("times"));
            Cuns cun = new Cuns(id, title, times);
            array.add(cun);
            cursor.moveToNext();
        }
        myDatabase.close();
        for (int i = array.size(); i >0; i--) {
            array1.add(array.get(i-1));
        }
        return array1;
    }

    /*
     * 返回可能要修改的数据
     */
    public Cuns getTiandCon(int id){
        myDatabase = myHelper.getWritableDatabase();
        Cursor cursor=myDatabase.rawQuery("select title,content from mybook where ids='"+id+"'" , null);
        cursor.moveToFirst();
        String title=cursor.getString(cursor.getColumnIndex("title"));
        String content=cursor.getString(cursor.getColumnIndex("content"));
        Cuns cun=new Cuns(title,content);
        myDatabase.close();
        return cun;
    }


    public void toUpdate(Cuns cun){
        myDatabase = myHelper.getWritableDatabase();
        myDatabase.execSQL(
                "update mybook set title='"+ cun.getTitle()+
                        "',times='"+cun.getTimes()+
                        "',content='"+cun.getContent() +
                        "' where ids='"+ cun.getIds()+"'");
        myDatabase.close();
    }


    public void toInsert(Cuns cun){
        myDatabase = myHelper.getWritableDatabase();
        myDatabase.execSQL("insert into mybook(title,content,times)values('"
                + cun.getTitle()+"','"
                +cun.getContent()+"','"
                +cun.getTimes()
                +"')");
        myDatabase.close();
    }


    public void toDelete(int ids){
        myDatabase  = myHelper.getWritableDatabase();
        myDatabase.execSQL("delete from mybook where ids="+ids+"");
        myDatabase.close();
    }
}
