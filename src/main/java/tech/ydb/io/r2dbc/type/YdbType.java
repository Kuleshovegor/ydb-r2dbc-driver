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

package tech.ydb.io.r2dbc.type;

import io.r2dbc.spi.R2dbcType;
import io.r2dbc.spi.Type;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.function.Function;

import tech.ydb.table.values.DecimalType;
import tech.ydb.table.values.DecimalValue;
import tech.ydb.table.values.PrimitiveType;
import tech.ydb.table.values.PrimitiveValue;
import tech.ydb.table.values.Value;

/**
 * @author Kirill Kurdyukov
 */
public enum YdbType implements Type {

    /**
     * Boolean value
     */
    BOOL(Boolean.class, PrimitiveType.Bool,
            obj -> PrimitiveValue.newBool((Boolean) obj),
            value -> value.asData().getBool()),

    /**
     * A signed integer. Acceptable values: from -2^7 to 2^7–1.
     */
    INT8(Byte.class, PrimitiveType.Int8,
            obj -> PrimitiveValue.newInt8((Byte) obj),
            value -> value.asData().getInt8()),

    /**
     * A signed integer. Acceptable values: from –2^15 to 2^15–1.
     */
    INT16(Short.class, PrimitiveType.Int16,
            obj -> PrimitiveValue.newInt16((Short) obj),
            value -> value.asData().getInt16()),

    /**
     * A signed integer. Acceptable values: from –2^31 to 2^31–1.
     */
    INT32(Integer.class, PrimitiveType.Int32,
            obj -> PrimitiveValue.newInt32((Integer) obj),
            value -> value.asData().getInt32()),

    /**
     * A signed integer. Acceptable values: from –2^63 to 2^63–1.
     */
    INT64(Long.class, PrimitiveType.Int64,
            obj -> PrimitiveValue.newInt64((Long) obj),
            value -> value.asData().getInt64()),

    /**
     * A real number with variable precision, 4 bytes in size.
     * Can't be used in the primary key
     */
    FLOAT(Float.class, PrimitiveType.Float,
            obj -> PrimitiveValue.newFloat((Float) obj),
            value -> value.asData().getFloat()),

    /**
     * A real number with variable precision, 8 bytes in size.
     * Can't be used in the primary key
     */
    DOUBLE(Double.class, PrimitiveType.Double,
            obj -> PrimitiveValue.newDouble((Double) obj),
            value -> value.asData().getDouble()),

    /**
     * A binary data, synonym for YDB type String
     */
    BYTES(byte[].class, PrimitiveType.Bytes,
            obj -> PrimitiveValue.newBytes((byte[]) obj),
            value -> value.asData().getBytes()),

    /**
     * Text encoded in UTF-8, synonym for YDB type Utf8
     */
    TEXT(String.class, PrimitiveType.Text,
            obj -> PrimitiveValue.newText((String) obj),
            value -> value.asData().getText()),

    /**
     * YSON in a textual or binary representation.
     * Doesn't support matching, can't be used in the primary key
     */
    YSON(byte[].class, PrimitiveType.Yson,
            obj -> PrimitiveValue.newYson((byte[]) obj),
            value -> value.asData().getYson()),

    /**
     * JSON represented as text. Doesn't support matching, can't be used in the primary key
     */
    JSON(String.class, PrimitiveType.Json,
            obj -> PrimitiveValue.newJson((String) obj),
            value -> value.asData().getJson()),

    /**
     * Universally unique identifier UUID. Not supported for table columns
     */
    UUID(UUID.class, PrimitiveType.Uuid,
            obj -> PrimitiveValue.newUuid((UUID) obj),
            value -> value.asData().getUuidJdk()),

    /**
     * Date, precision to the day
     */
    DATE(LocalDate.class, PrimitiveType.Date,
            obj -> PrimitiveValue.newDate((LocalDate) obj),
            value -> value.asData().getDate()),

    /**
     * Date/time, precision to the second
     */
    DATETIME(LocalDateTime.class, PrimitiveType.Datetime,
            obj -> PrimitiveValue.newDatetime((LocalDateTime) obj),
            value -> value.asData().getDatetime()),

    /**
     * Date/time, precision to the microsecond
     */
    TIMESTAMP(Instant.class, PrimitiveType.Timestamp,
            obj -> PrimitiveValue.newTimestamp((Instant) obj),
            value -> value.asData().getTimestamp()),

    /**
     * Time interval (signed), precision to microseconds
     */
    INTERVAL(Duration.class, PrimitiveType.Interval,
            obj -> PrimitiveValue.newInterval((Duration) obj),
            value -> value.asData().getInterval()),

    /**
     * Date with time zone label, precision to the day
     */
    TZ_DATE(ZonedDateTime.class, PrimitiveType.TzDate,
            obj -> PrimitiveValue.newTzDate((ZonedDateTime) obj),
            value -> value.asData().getTzDate()),

    /**
     * Date/time with time zone label, precision to the second
     */
    TZ_DATETIME(ZonedDateTime.class, PrimitiveType.TzDatetime,
            obj -> PrimitiveValue.newTzDatetime((ZonedDateTime) obj),
            value -> value.asData().getTzDatetime()),

    /**
     * Date/time with time zone label, precision to the microsecond
     */
    TZ_TIMESTAMP(ZonedDateTime.class, PrimitiveType.TzTimestamp,
            obj -> PrimitiveValue.newTzTimestamp((ZonedDateTime) obj),
            value -> value.asData().getTzTimestamp()),

    /**
     * JSON in an indexed binary representation.
     * Doesn't support matching, can't be used in the primary key
     */
    JSON_DOCUMENT(String.class, PrimitiveType.JsonDocument,
            obj -> PrimitiveValue.newJsonDocument((String) obj),
            value -> value.asData().getJsonDocument()),

    /**
     * A real number with the specified precision, up to 35 decimal digits.
     * <p>
     * When used in table columns, precision is fixed: Decimal (22,9).
     * Can't be used in the primary key
     */
    DECIMAL(BigDecimal.class, DecimalType.getDefault(),
            obj -> DecimalType.getDefault().newValue((BigDecimal) obj),
            value -> ((DecimalValue) value).toBigDecimal());

    private final Class<?> javaType;
    private final tech.ydb.table.values.Type ydbType;
    private final Function<Object, Value<?>> valueConstructor;
    private final Function<Value<?>, Object> valueGetter;

    YdbType(Class<?> javaType,
            tech.ydb.table.values.Type ydbType,
            Function<Object, Value<?>> valueConstructor,
            Function<Value<?>, Object> valueGetter) {
        this.javaType = javaType;
        this.ydbType = ydbType;
        this.valueConstructor = valueConstructor;
        this.valueGetter = valueGetter;
    }

    @Override
    public Class<?> getJavaType() {
        return javaType;
    }

    @Override
    public String getName() {
        return name();
    }

    public tech.ydb.table.values.Type getYdbType() {
        return ydbType;
    }

    public Value<?> createValue(Object obj) {
        return valueConstructor.apply(obj);
    }

    public Object getObject(Value<?> value) {
        return valueGetter.apply(value);
    }

    public static YdbType valueOf(R2dbcType r2dbcType) {
        return switch (r2dbcType) {
            case BOOLEAN -> BOOL;
            case TINYINT -> INT8;
            case SMALLINT -> INT16;
            case INTEGER -> INT32;
            case BIGINT -> INT64;
            case CHAR, VARCHAR, NCHAR, NVARCHAR, CLOB, NCLOB -> TEXT;
            case REAL, FLOAT -> FLOAT;
            case DOUBLE -> DOUBLE;
            case BINARY, VARBINARY, BLOB -> BYTES;
            case DATE -> DATE;
            case TIME -> DATETIME;
            case TIMESTAMP -> TIMESTAMP;
            case TIMESTAMP_WITH_TIME_ZONE -> TZ_TIMESTAMP;
            case TIME_WITH_TIME_ZONE -> TZ_DATETIME;
            case NUMERIC, DECIMAL -> DECIMAL;
            case COLLECTION -> throw new UnsupportedOperationException("R2dbc type COLLECTION not supported");
        };
    }
}
