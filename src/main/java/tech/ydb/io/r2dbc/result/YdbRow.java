/*
 * Copyright 2022 YANDEX LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.ydb.io.r2dbc.result;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import tech.ydb.table.result.ResultSetReader;

/**
 * @author Kirill Kurdyukov
 */
public final class YdbRow implements Row {
    private final ResultSetReader resultSetReader;

    public YdbRow(ResultSetReader resultSetReader) {
        this.resultSetReader = resultSetReader;
    }

    @Override
    public RowMetadata getMetadata() {
        return new YdbRowMetadata(resultSetReader);
    }

    @Override
    public <T> T get(int index, Class<T> type) {
        resultSetReader.getColumn(index).getType();
        
        return null;
    }

    @Override
    public <T> T get(String name, Class<T> type) {
        return null;
    }
}
