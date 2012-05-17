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
import android.database.Cursor;

import java.lang.reflect.Field;

/**
 * User: andraz
 * Date: 4/28/12
 * Time: 1:00 PM
 * Meta data about a single column
 * Contains name, index in the cursor, type, field and method to fill it
 */
class ColumnMetaData {
    private int index;
    private String name;
    private boolean primary = false;
    private boolean autoincrement = false;
    private Type type;
    private Field field;

    public ColumnMetaData(int index, Field field) {
        @SuppressWarnings(value = "unchecked")
        Column column = field.getAnnotation(Column.class);
        if (column == null) {
            return;
        }

        this.index = index;
        name = column.name();
        if(name.equals("")) {
            name = field.getName();
        }
        primary = column.primaryKey();
        autoincrement = column.autoIncrement();

        Class fieldType = field.getType();
        if (fieldType == long.class) {
            type = Type.INTEGER;
        } else if (fieldType == double.class) {
            type = Type.FLOAT;
        } else if (fieldType == String.class) {
            type = Type.STRING;
        } else {
            type = Type.BLOB;
        }

        this.field = field;
        this.field.setAccessible(true);
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public boolean isPrimary() {
        return primary;
    }

    public boolean isAutoincrement() {
        return autoincrement;
    }

    public void set(Cursor c, Object o) {
        try {
            switch (type) {
                case INTEGER:
                    field.setLong(o, c.getLong(index));
                    break;
                case FLOAT:
                    field.setDouble(o, c.getDouble(index));
                    break;
                case STRING:
                    field.set(o, c.getString(index));
                    break;
                case BLOB:
                    throw new UnsupportedOperationException("blobs are not implemented yet");
            }
        } catch (IllegalAccessException e) {
            throw new UnsupportedOperationException("illegal acces should not be happening");
        }
    }

    public void get(ContentValues values, Object o) {
        try {
            switch (type) {
                case INTEGER:
                    values.put(name, (Long) field.get(o));
                    break;
                case FLOAT:
                    values.put(name, (Double) field.get(o));
                    break;
                case STRING:
                    values.put(name, (String) field.get(o));
                    break;
                case BLOB:
                    throw new UnsupportedOperationException("blobs are not implemented yet");
            }
        } catch (IllegalAccessException e) {
            throw new UnsupportedOperationException("illegal acces should not be happening");
        }
    }

    public boolean isSet(Object o) {
        try {
            switch (type) {
                case INTEGER:
                    return (Long) field.get(o) != 0;
                case FLOAT:
                    return (Double) field.get(o) != 0;
                case STRING:
                    String s = (String) field.get(o);
                    return !(s == null || s.equals(""));
                case BLOB:
                    throw new UnsupportedOperationException("blobs are not implemented yet");
            }
        } catch (IllegalAccessException e) {
            throw new UnsupportedOperationException("illegal acces should not be happening");
        }
        return false;
    }
}
