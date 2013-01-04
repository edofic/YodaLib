/*
 * Copyright 2012 Andraz Bajt
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.edofic.yodalib.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * User: andraz
 * Date: 4/28/12
 * Time: 5:13 PM
 * The core unit.
 * Represents one table in database.
 * Connection is made when object is created and closed with close()
 */
public class Datasource<T> {

    public static <T2> Datasource<T2> create(Context context, Class<T2> c) {
        return new Datasource<T2>(context, c);
    }

    public static <T2> Datasource<T2> create(Proxy proxy, Class c) {
        return new Datasource<T2>(proxy, c);
    }


    private SQLiteDatabase db;
    private final Proxy proxy;
    private final Class c;
    private final TableMetaData metaData;

    /**
     * creates new datasource (one table/db)
     * and opens it
     *
     * @param context context
     * @param c       must be equal to T, limitation because of type erasure
     */
    private Datasource(Context context, Class c) {
        this(new SingleTableProxy(context, c), c);
    }

    /**
     * creates new datasource for use in a database (multiple tables)
     * usually this constructor is called from proxy constructor,
     * so opening cannot be performed, because proxy hasn't fully initialised - catch 22
     * this is solved by incjection. if you happen to use subclasses of this, just don't call
     * super(Proxy, Class)
     *
     * @param proxy usually the parent database. for 1 table/db usage use context,class constructor
     * @param c     must be equal to T, limitation because of type erasure
     */
    private Datasource(Proxy proxy, Class c) {
        this.c = c;
        this.metaData = MetaDataFactory.get(c);
        this.proxy = proxy;
        open();
    }

    public TableMetaData getMetaData() {
        return metaData;
    }

    public Class getType() {
        return c;
    }

    /**
     * gets writable database
     */
    public void open() {
        db = proxy.getWritableDatabase();
    }

    /**
     * closes database connection, renders the datasource useless
     */
    public void close() {
        db.close();
        db = null;
    }

    /**
     * inserts new entry to the db or updates existing one
     * update is performed if field(s) with autoincrement are set
     * *YOU HAVE TO MANUALLY OPEN AND CLOSE THE DB*
     *
     * @param t object to insert
     * @return new id if insertion or numbers of row affected if update
     */
    public long insert(T t) {
        ContentValues values = new ContentValues();
        for (ColumnMetaData column : metaData.getColumnsNoIncrement()) {
            column.get(values, t);
        }

        boolean update = false;
        StringBuilder whereClause = new StringBuilder();
        for (ColumnMetaData column : metaData.getColumnsAutoincrement()) {
            if (column.isSet(t)) {
                column.get(values, t);
                final String name = column.getName();
                if (update) {
                    whereClause.append(", ");
                }
                whereClause.append(name);
                whereClause.append(" = ");
                whereClause.append(values.get(name).toString());
                update = true;
            }
        }

        long id;
        if (!update) {
            id = db.insert(metaData.getTableName(), null, values);
        } else {
            id = db.update(metaData.getTableName(), values,
                    whereClause.toString(), null);
        }
        return id;
    }

    /**
     * selectively load table
     *
     * @param whereClause where clause formatted for SQLite without the "WHERE"
     * @return list of elements that satisfy the predicate
     */
    @SuppressWarnings("unchecked") //explained in place
    public List<T> get(String whereClause) {
        ArrayList<T> items = new ArrayList<T>();
        Cursor cursor =
                db.query(metaData.getTableName(), metaData.getColumnNames(), whereClause,
                        null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            //noinspection unchecked
            //cursor to object generates object from constructor
            //of T, so we are in fact type safe
            items.add((T) metaData.cursorToObject(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return items;
    }

    /**
     * load whole table
     *
     * @return list of all elements
     */
    public List<T> getAll() {
        return get(null);
    }

    /**
     * Wrapper for sqlite database query
     *
     * @param distinct  adds the Distintc keyword to query
     * @param columns   array of column names
     * @param selection where clause
     * @param groupBy   group by clause
     * @param having    having clause
     * @param orderBy   order by clause
     * @param limit     limit clause
     * @return
     */
    public Cursor query(boolean distinct, String[] columns, String selection, String groupBy,
                        String having, String orderBy, String limit) {
        return db.query(distinct, metaData.getTableName(), columns, selection, null, groupBy, having, orderBy, limit);
    }

    /**
     * selectively remove elements
     *
     * @param whereClause where clause formatted for SQLite without the "WHERE"
     */
    public void delete(String whereClause) {
        db.delete(metaData.getTableName(), whereClause, null);
    }

    /**
     * clear the whole table
     */
    public void clear() {
        delete(null);
    }

    private static class SingleTableProxy implements Proxy {
        private Context mContext;
        private DatabaseOpenHelper helper;

        private SingleTableProxy(Context context, Class c) {
            this.mContext = context;
            TableMetaData meta = MetaDataFactory.get(c);
            helper = new DatabaseOpenHelper(context, new TableMetaData[]{meta},
                    meta.getTableName() + ".db", meta.getVersion());
        }

        @Override
        public Context getContext() {
            return mContext;
        }

        @Override
        public SQLiteDatabase getWritableDatabase() {
            return helper.getWritableDatabase();
        }
    }
}
