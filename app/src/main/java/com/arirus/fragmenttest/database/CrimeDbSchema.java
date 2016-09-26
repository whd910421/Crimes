package com.arirus.fragmenttest.database;

/**
 * Created by whd910421 on 16/9/19.
 */
public class CrimeDbSchema {
    public static final class CrimeTable{
        public static final String NAME = "crimes";

        public static final class Cols{
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
            public static final String SUSPECT = "suspect";
            public static final String CONTACT_ID = "contact_id";
        }
    }
}


//定义数据库的表结构,全都是静态常量