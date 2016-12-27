package com.github.thiagogarbazza.keyenumhibernate;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.usertype.DynamicParameterizedType;
import org.hibernate.usertype.UserType;

import static com.github.thiagogarbazza.keyenumhibernate.KeyEnumUtils.findByKey;

public class KeyEnumType implements DynamicParameterizedType, UserType {

    private static final String METHOD_NAME = "getKey";

    private Class<? extends KeyEnum> keyEnumClass;
    private int sqlType;

    @Override
    public void setParameterValues(Properties parameters) {
        final ParameterType reader = (ParameterType) parameters.get(PARAMETER_TYPE);
        if (reader == null) {
            String enumClassName = (String) parameters.get(RETURNED_CLASS);
            try {
                keyEnumClass = ReflectHelper.classForName(enumClassName, this.getClass()).asSubclass(KeyEnum.class);
            } catch (ClassNotFoundException exception) {
                throw new HibernateException("Key Enum class not found", exception);
            }
        } else {
            keyEnumClass = reader.getReturnedClass().asSubclass(KeyEnum.class);
        }

        try {
            Method keyMethod = keyEnumClass.getMethod(METHOD_NAME);
            Class returnType = keyMethod.getReturnType();
            sqlType = sqlTypeFactory(returnType);
        } catch (NoSuchMethodException exception) {
            throw new HibernateException("Key Enum method not found", exception);
        }
    }

    private int sqlTypeFactory(Class type) {
        int sqlType;
        switch (type.getSimpleName()) {
            case "Integer":
                sqlType = Types.INTEGER;
                break;

            case "Long":
                sqlType = Types.BIGINT;
                break;

            case "String":
            default:
                sqlType = Types.VARCHAR;
                break;
        }
        return sqlType;
    }

    @Override
    public int[] sqlTypes() {
        return new int[] {sqlType};
    }

    @Override
    public Object nullSafeGet(final ResultSet resultSet, final String[] strings,
            final SharedSessionContractImplementor sharedSessionContractImplementor, final Object owner)
            throws HibernateException, SQLException {
        if (resultSet.wasNull()) {
            return null;
        }

        Object dbValue = resultSet.getObject(strings[0]);
        return findByKey((Class<? extends Enum<?>>) returnedClass(), dbValue);
    }

    @Override
    public void nullSafeSet(final PreparedStatement preparedStatement, final Object value, final int index,
            final SharedSessionContractImplementor sharedSessionContractImplementor)
            throws HibernateException, SQLException {
        if (value == null) {
            preparedStatement.setNull(index, sqlType);
        } else {
            Object key = ((KeyEnum) value).getKey();
            preparedStatement.setObject(index, key, sqlType);
        }
    }

    @Override
    public Class returnedClass() {
        return keyEnumClass;
    }

    @Override
    public boolean equals(final Object x, final Object y) throws HibernateException {
        if (x == y)
            return true;
        if (x == null)
            return false;

        final KeyEnum keX = (KeyEnum) x;
        final KeyEnum keY = (KeyEnum) y;
        return keX.getKey().equals(keY.getKey());
    }

    @Override
    public int hashCode(final Object x) throws HibernateException {
        final int prime = 31;
        final int result = 1;
        final KeyEnum keX = (KeyEnum) x;
        return prime * result + (x == null ? 0 : keX.getKey().hashCode());
    }

    @Override
    public Object deepCopy(final Object value) throws HibernateException {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(final Object value) throws HibernateException {
        return (Serializable) value;
    }

    @Override
    public Object assemble(final Serializable cached, final Object owner) throws HibernateException {
        return cached;
    }

    @Override
    public Object replace(final Object original, final Object target, final Object owner) throws HibernateException {
        return original;
    }
}
