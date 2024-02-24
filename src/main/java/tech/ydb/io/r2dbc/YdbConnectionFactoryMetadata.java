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

package tech.ydb.io.r2dbc;

import io.r2dbc.spi.ConnectionFactoryMetadata;

/**
 * @author Kirill Kurdyukov
 */
public final class YdbConnectionFactoryMetadata implements ConnectionFactoryMetadata {

    /**
     * The name of the YDB product.
     */
    public static final String NAME = "YDB";

    static final ConnectionFactoryMetadata INSTANCE = new YdbConnectionFactoryMetadata();

    private YdbConnectionFactoryMetadata() {
    }

    @Override
    public String getName() {
        return NAME;
    }
}
