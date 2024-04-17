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

package tech.ydb.io.r2dbc.state;

import reactor.core.publisher.Mono;
import tech.ydb.io.r2dbc.YdbIsolationLevel;
import tech.ydb.io.r2dbc.YdbTxSettings;
import tech.ydb.table.Session;
import tech.ydb.table.transaction.TxControl;

/**
 * @author Egor Kuleshov
 */
public interface YdbConnectionState {
    Mono<Session> getSession();

    TxControl<?> txControl();

    boolean isInTransaction();

    YdbTxSettings getYdbTxSettings();

    YdbConnectionState withDataQuery(String txId, Session session);

    YdbConnectionState withBeginTransaction(String id, Session session, YdbTxSettings ydbTxSettings);

    YdbConnectionState withCommitTransaction();

    YdbConnectionState withRollbackTransaction();

    YdbConnectionState withAutoCommit(boolean autoCommit);

    YdbConnectionState withIsolationLevel(YdbIsolationLevel isolationLevel);

    YdbConnectionState withReadOnly(boolean readOnly);

    void withError(Session session);

    YdbConnectionState close();

    boolean isClosed();
}
