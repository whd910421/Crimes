package com.arirus.fragmenttest.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.arirus.fragmenttest.Crime;
import com.arirus.fragmenttest.database.CrimeDbSchema.CrimeTable;

import java.util.Date;
import java.util.UUID;

/**
 * Created by whd910421 on 16/9/20.
 */
public class CrimeCursorWrapper extends CursorWrapper {
    public CrimeCursorWrapper(Cursor cursor)
    {
        super(cursor);
    }

    public Crime getCrime()
    {
        String uuidString = getString(getColumnIndex(CrimeTable.Cols.UUID));
        String title = getString(getColumnIndex(CrimeTable.Cols.TITLE));
        long data = getLong(getColumnIndex(CrimeTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(CrimeTable.Cols.SOLVED));
        String suspect = getString(getColumnIndex(CrimeTable.Cols.SUSPECT));
        long contactID = getLong(getColumnIndex(CrimeTable.Cols.CONTACT_ID));


        Crime crime = new Crime(UUID.fromString(uuidString));
        crime.setTitle(title);
        crime.setDate(new Date(data));
        crime.setSolved(isSolved != 0);
        crime.setSuspect(suspect);
        crime.setContactID(contactID);

        return crime;
    }
}
