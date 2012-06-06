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

/**
 * User: andraz
 * Date: 4/28/12
 * Time: 3:01 PM
 * Supported types for persisting on a sqlite db
 */
enum Type {
    NULL(0, "null"),
    INTEGER(1, "integer"),
    FLOAT(2, "real"),
    STRING(3, "text"),
    BLOB(4, "blob");

    private final int value;
    private final String text;

    Type(int value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * return value
     *
     * @return value as displayed in android javadoc
     */
    public int getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
