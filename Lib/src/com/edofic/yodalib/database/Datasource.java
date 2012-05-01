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

/**
 * User: andraz
 * Date: 4/28/12
 * Time: 5:13 PM
 */
public class Datasource<T> {
    private SQLiteDatabase db;
    private final DatabaseOpenHelper helper;
    private Class c;

    /**
     * creates new datasource (one table/db)
     *
     * @param context
     * @param c       must be equal to T
     */
    public Datasource(Context context, Class c) {
        this.c = c;
        helper = new DatabaseOpenHelper(context, c);
    }

    private void open() {
        db = helper.getWritableDatabase();
    }

    public void close() {
        db.close();
    }

    //insert
    //get
    //get all
    //delete

    public void insert(T t) {

    }
}
