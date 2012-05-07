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
 */
public class Datasource<T> {
    private SQLiteDatabase db;
    private final DatabaseOpenHelper helper;
    private final Class c;
    private final TableMetaData metaData;

    /**
     * creates new datasource (one table/db)
     *
     * @param context context
     * @param c       must be equal to T
     */
    public Datasource(Context context, Class c) {
        this.c = c;
        metaData = MetaDataFactory.get(c);
        helper = new DatabaseOpenHelper(context, c);
    }

    /**
     * gets writable database
     */
    public void open() {
        db = helper.getWritableDatabase();
    }

    /**
     * closes database connection
     */
    public void close() {
        db.close();
    }

    /**
     * make sure that database is open and transaction is in progress
     * does not nest transactions!
     */
    public void beginTransaction() {
        if (db == null || !db.isOpen()) {
            open();
        }
        if (!db.inTransaction()) {
            db.beginTransaction();
        }
    }

    /**
     * is db is open and transaction is in progress, set it to successful
     */
    public void transactionSuccessful() {
        if (db.isOpen() && db.inTransaction()) {
            db.setTransactionSuccessful();
        }
    }

    /**
     * is db is open and transaction is in progress, end it
     */
    public void endTransaction() {
        if (db.isOpen() && db.inTransaction()) {
            db.endTransaction();
        }
    }

    /**
     * inserts new entry to the db or updates existing one
     * update is performed if field(s) with autoincrement are set
     * *YOU HAVE TO MANUALLY OPEN AND CLOSE THE DB*
     *
     * @param t object to insert
     */
    public void insert(T t) {
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

        if (!update) {
            db.insert(metaData.getTableName(), null, values);
        } else {
            db.update(metaData.getTableName(), values,
                    whereClause.toString(), null);
        }
    }

    /**
     * convenience method for inserting single element
     * see docs for insert
     *
     * @param t item to insert
     */
    public void insertSingle(T t) {
        open();
        insert(t);
        close();
    }

    /**
     * selectively load table
     *
     * @param whereClause where clause formatted for SQLite without the "WHERE"
     * @return list of elements that satisfy the predicate
     */
    @SuppressWarnings("unchecked") //explained in place
    public List<T> get(String whereClause) {
        open();
        ArrayList<T> items = new ArrayList<T>();
        Cursor cursor =
                db.query(metaData.getTableName(), metaData.getColumnNames(), whereClause,
                        null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            //noinspection unchecked
            //cursor to object generates object from constructor
            items.add((T) metaData.cursorToObject(cursor));     //of T, so we are in fact type safe
            cursor.moveToNext();
        }
        cursor.close();
        close();
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
     * selectively remove elements
     *
     * @param whereClause where clause formatted for SQLite without the "WHERE"
     */
    public void delete(String whereClause) {
        open();
        db.delete(metaData.getTableName(), whereClause, null);
        close();
    }

    /**
     * clear the whole table
     */
    public void clear() {
        delete(null);
    }
}
