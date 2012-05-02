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

package com.edofic.yodalib.examples.benchmark;

import com.edofic.yodalib.database.Column;
import com.edofic.yodalib.database.Table;

@Table(name = "myTable")
public class TestClass {
    @Column(name = "ID", primaryKey = true, autoIncrement = true)
    private long id;
    @Column(name = "len")
    private double a;
    @Column(name = "nome")
    private String b;

    public TestClass() {
    }

    public TestClass(long id, double a, String b) {
        this.id = id;
        this.a = a;
        this.b = b;
    }
}