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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * User: andraz
 * Date: 4/28/12
 * Time: 4:26 PM
 */
class DatabaseOpenHelper extends SQLiteOpenHelper {
    private final TableMetaData[] metaData;
    private String[] create;

    public DatabaseOpenHelper(Context context, TableMetaData[] meta, String name, int version) {
        super(context, name, null, version);
        metaData = meta;
        if (meta == null) {
            throw new IllegalArgumentException("You must provide meta data in order to create a db");
        }
        buildScripts();
    }

    private void buildScripts() {
        create = new String[metaData.length];
        for (int index = 0; index < metaData.length; index++) {
            TableMetaData tableMetaData = metaData[index];

            StringBuilder sb = new StringBuilder();
            sb.append("create table ");
            sb.append(tableMetaData.getTableName());
            sb.append(" ( ");
            final ArrayList<ColumnMetaData> columns = tableMetaData.getColumns();
            for (int i = 0; i < columns.size(); i++) {
                final ColumnMetaData column = columns.get(i);
                sb.append(column.getName());
                sb.append(" ");
                sb.append(column.getType().getText());
                if (column.isPrimary()) {
                    sb.append(" primary key");
                }
                if (column.isAutoincrement()) {
                    sb.append(" autoincrement");
                }
                if (i < columns.size() - 1) {
                    sb.append(",");
                }
                sb.append(" ");
            }
            sb.append(");");
            create[index] = sb.toString();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (String s : create) {
            db.execSQL(s);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DatabaseOpenHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        for (TableMetaData tableMetaData : metaData) {
            db.execSQL("DROP TABLE IF EXISTS " + tableMetaData.getTableName());
        }
        onCreate(db);
    }
}
