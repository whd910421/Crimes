package com.arirus.fragmenttest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.arirus.fragmenttest.database.CrimeBaseHelper;
import com.arirus.fragmenttest.database.CrimeCursorWrapper;
import com.arirus.fragmenttest.database.CrimeDbSchema.CrimeTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by whd910421 on 16/7/25.
 */
public class CrimeLab {
    private static CrimeLab sCrimeLab;
    private Context mContext;
    private SQLiteDatabase mDatabase; //原mCrimes不需要了,每次都是从dataBase中读取

    public static CrimeLab get(Context context)
    {
        if (sCrimeLab == null)
            sCrimeLab = new CrimeLab(context);
        return sCrimeLab;
    }

    private CrimeLab(Context context)
    {
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();
    }


    //获取所有的记录
    public List<Crime> getCrimes() {
        List<Crime> crimes = new ArrayList<>();
        CrimeCursorWrapper cursor = queryCrimes(null,null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast())
            {
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        }finally {
            cursor.close();
        }

        return crimes;
    }
    //获取某一条的记录
    public Crime getCrime(UUID id) {
        CrimeCursorWrapper cursor = queryCrimes(CrimeTable.Cols.UUID + " = ?",
                                                new String[]{ id.toString() });

        try {
            if (cursor.getCount() == 0)
                return null;
            /**
             * Move the cursor to the first row.
             *
             * <p>This method will return false if the cursor is empty.
             *
             * @return whether the move succeeded.
             */
            if (cursor.moveToFirst())
                return cursor.getCrime();
            else
                return null;
        }finally {
            cursor.close();
        }
    }

    private static ContentValues getContentValues(Crime crime)      //k-y 储存类 只用于sqlite数据,用于写入数据,只是相当于一个中间数据
    {
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID, crime.getId().toString());
        values.put(CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, crime.isSolved()?1:0);

        return values;
    }


    //数据库写入数据
    public void addCrime(Crime c)
    {
        ContentValues values = getContentValues(c);

        mDatabase.insert(CrimeTable.NAME, null, values);
    }

    //查询数据
    //    * @param whereClause A filter declaring which rows to return, formatted as an
    //    *            SQL WHERE clause (excluding the WHERE itself). Passing null
    //    *            will return all rows for the given table.
    //    * @param whereArgs You may include ?s in whereClause, which will be
    //    *         replaced by the values from selectionArgs, in order that they
    //    *         appear in the selection. The values will be bound as Strings.
    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs)
    {
        Cursor cursor = mDatabase.query(CrimeTable.NAME,
                                        null,
                                        whereClause,
                                        whereArgs,
                                        null,
                                        null,
                                        null);
        return new CrimeCursorWrapper(cursor);
    }


    //数据库更新数据
    public void updateCrime(Crime c)
    {
        String uuidString = c.getId().toString();
        ContentValues values = getContentValues(c);

        int num = mDatabase.update(CrimeTable.NAME, values, CrimeTable.Cols.UUID + " = ?", new String[] {uuidString});
        //如果第三个参数不写会是啥样的??所有行都会改变!!
        System.out.println("啦啦啦啦" + String.valueOf(num));
    }

    public void removeCrime(Crime c)
    {
        // ContentValues values = getContentValues(c); 这么是更新或者添加数据用的
        String uuidString = c.getId().toString();
        mDatabase.delete(CrimeTable.NAME,CrimeTable.Cols.UUID + " = ?", new String[] {uuidString});
    }
}
