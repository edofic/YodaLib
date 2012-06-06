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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: andraz
 * Date: 5/17/12
 * Time: 1:24 PM
 * <p/>
 * For using multiple tables you should extend this class
 * Inside you should put several datasources annotated with TableDatasource
 * that's mostly it. see examples
 */
public abstract class Database implements Proxy {
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    protected @interface TableDatasource {
        public Class injectForType();
    }

    private Context mContext;
    private DatabaseOpenHelper helper;
    private String mName;
    private int mVersion;

    public Database(Context context, String name, int version) {
        this.mContext = context;
        this.mName = name;
        this.mVersion = version;

        init();
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        return helper.getWritableDatabase();
    }

    private void init() {
        List<TableMetaData> metaDataList = new ArrayList<TableMetaData>();
        Map<Field, Class> fields = new HashMap<Field, Class>();
        for (Field field : this.getClass().getDeclaredFields()) {
            TableDatasource t = field.getAnnotation(TableDatasource.class);
            if (t == null) {
                continue;
            }

            try {
                Class c = t.injectForType();
                metaDataList.add(MetaDataFactory.get(c));
                fields.put(field, c);
            } catch (ClassCastException e) {
                throw new IllegalArgumentException("annotated class should extend datasource");
            }
        }

        TableMetaData[] meta = metaDataList.toArray(new TableMetaData[metaDataList.size()]);
        helper = new DatabaseOpenHelper(mContext, meta, mName, mVersion);

        //injection magic
        for (Field field : fields.keySet()) {
            Class c = fields.get(field);
            field.setAccessible(true);
            try {
                field.set(this, new Datasource(this, c));
            } catch (IllegalAccessException e) {
                throw new AssertionError("Illegal access - field is set accessible?!?");
            }
        }
    }
}
