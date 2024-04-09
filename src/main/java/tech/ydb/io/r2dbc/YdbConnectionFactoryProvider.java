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

import java.time.Duration;

import com.google.common.base.Preconditions;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.ConnectionFactoryProvider;
import io.r2dbc.spi.Option;
import tech.ydb.core.grpc.BalancingSettings;
import tech.ydb.core.grpc.GrpcCompression;
import tech.ydb.core.grpc.GrpcTransport;
import tech.ydb.core.grpc.GrpcTransportBuilder;
import tech.ydb.table.TableClient;

/**
 * @author Kirill Kurdyukov
 */
public final class YdbConnectionFactoryProvider implements ConnectionFactoryProvider {

    private static final String YDB_DRIVER = "ydb";

    private static final int SESSION_POOL_DEFAULT_MIN_SIZE = 10;
    private static final int SESSION_POOL_DEFAULT_MAX_SIZE = 100;
    private static final Duration SESSION_KEEP_ALIVE_DEFAULT_TIME = Duration.ofMinutes(5);

    private static final Option<Integer> SESSION_POOL_MIN_SIZE = Option.valueOf("sessionPoolMinSize");
    private static final Option<Integer> SESSION_POOL_MAX_SIZE = Option.valueOf("sessionPoolMaxSize");
    private static final Option<Duration> SESSION_KEEP_ALIVE_TIME = Option.valueOf("sessionKeepAliveTime");
    private static final Option<Duration> SESSION_MAX_IDLE_TIME = Option.valueOf("sessionMaxIdleTime");
    private static final Option<Boolean> KEEP_QUERY_TEXT = Option.valueOf("keepQueryText");
    private static final Option<GrpcCompression> GRPC_COMPRESSION = Option.valueOf("grpcCompression");
    private static final Option<BalancingSettings.Policy> BALANCING_POLICY = Option.valueOf("balancingPolicy");
    private static final Option<byte[]> SECURE_CONNECTION_CERTIFICATE = Option.valueOf("secureConnectionCertificate");
    private static final Option<Boolean> SECURE_CONNECTION = Option.valueOf("secureConnection");
    private static final Option<String> SA_FILE = Option.valueOf("saFile"); // TODO

    @Override
    public YdbConnectionFactory create(ConnectionFactoryOptions connectionFactoryOptions) {
        OptionExtractor optionExtractor = new OptionExtractor(connectionFactoryOptions);

        String schema = optionExtractor.extract(ConnectionFactoryOptions.PROTOCOL);

        GrpcTransportBuilder grpcTransportBuilder = GrpcTransport.forHost(
                (schema == null ? "" : schema + "://") + optionExtractor.extractRequired(ConnectionFactoryOptions.HOST),
                optionExtractor.extractRequired(ConnectionFactoryOptions.PORT),
                optionExtractor.extractRequired(ConnectionFactoryOptions.DATABASE)
        );

        optionExtractor.extractThenConsume(BALANCING_POLICY, policy -> grpcTransportBuilder
                .withBalancingSettings(BalancingSettings.fromPolicy(policy)));
        optionExtractor.extractThenConsume(GRPC_COMPRESSION, grpcTransportBuilder::withGrpcCompression);
        optionExtractor.extractThenConsume(SECURE_CONNECTION_CERTIFICATE, grpcTransportBuilder::withSecureConnection);
        optionExtractor.extractThenConsume(SECURE_CONNECTION, isSecure -> {
            if (isSecure) {
                grpcTransportBuilder.withSecureConnection();
            }
        });

        TableClient.Builder tableClientBuilder = TableClient.newClient(grpcTransportBuilder.build())
                .sessionPoolSize(
                        optionExtractor.extractOrDefault(SESSION_POOL_MIN_SIZE, SESSION_POOL_DEFAULT_MIN_SIZE),
                        optionExtractor.extractOrDefault(SESSION_POOL_MAX_SIZE, SESSION_POOL_DEFAULT_MAX_SIZE)
                ).sessionKeepAliveTime(
                        optionExtractor.extractOrDefault(SESSION_KEEP_ALIVE_TIME, SESSION_KEEP_ALIVE_DEFAULT_TIME)
                );

        optionExtractor.extractThenConsume(SESSION_MAX_IDLE_TIME, tableClientBuilder::sessionMaxIdleTime);
        optionExtractor.extractThenConsume(KEEP_QUERY_TEXT, tableClientBuilder::keepQueryText);

        return new YdbConnectionFactory(new YdbContext(tableClientBuilder.build()));
    }

    @Override
    public boolean supports(ConnectionFactoryOptions connectionFactoryOptions) {
        Preconditions.checkNotNull(connectionFactoryOptions, "connectionFactoryOptions must not be null");

        Object driver = connectionFactoryOptions.getValue(ConnectionFactoryOptions.DRIVER);

        return driver != null && driver.equals(YDB_DRIVER);
    }

    @Override
    public String getDriver() {
        return YDB_DRIVER;
    }
}
