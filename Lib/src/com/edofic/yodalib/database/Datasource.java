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
import android.database.sqlite.SQLiteDatabase;

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
     * @param context
     * @param c       must be equal to T
     */
    public Datasource(Context context, Class c) {
        this.c = c;
        metaData = MetaDataFactory.get(c);
        helper = new DatabaseOpenHelper(context, c);
    }

    private void open() {
        db = helper.getWritableDatabase();
    }

    private void close() {
        db.close();
    }

    /**
     * inserts new entry to the db or updates existing one
     * update is performed if field(s) with autoincrement are set
     *
     * @param t
     */
    public void insert(T t) {
        open();

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

        close();
    }

    public T get(String whereClause) {
        //todo
        return null;
    }

    public List<T> getAll() {
        //todo
        return null;
    }

    public void delete(String whereClause) {
        //todo
    }
}
