package com.arirus.fragmenttest.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.arirus.fragmenttest.database.CrimeDbSchema.CrimeTable;

/**
 * Created by whd910421 on 16/9/20.
 */
public class CrimeBaseHelper extends SQLiteOpenHelper {

    private static final String DATABASENAME = "crimeBase.db";
    private static final int VERSION = 1;

    public CrimeBaseHelper(Context context)
    {
        super(context,DATABASENAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + CrimeTable.NAME + "(" +
            " _id integer primary key autoincrement, " +
                CrimeTable.Cols.UUID + ", " +
                CrimeTable.Cols.TITLE + ", " +
                CrimeTable.Cols.DATE + ", " +
                CrimeTable.Cols.SOLVED  + ", " +
                CrimeTable.Cols.SUSPECT + ", " +
                CrimeTable.Cols.CONTACT_ID +
            ")"
        );
        //创建的时候会打印出数据库语句
        System.out.println("create table " + CrimeTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                    CrimeTable.Cols.UUID + ", " +
                    CrimeTable.Cols.TITLE + ", " +
                    CrimeTable.Cols.DATE + ", " +
                    CrimeTable.Cols.SOLVED  + ", " +
                    CrimeTable.Cols.SUSPECT +", " +
                    CrimeTable.Cols.CONTACT_ID +
                        ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
