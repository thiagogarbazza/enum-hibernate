package com.github.thiagogarbazza.keyenumhibernate;

import java.io.Serializable;

public interface KeyValueEnum<T extends Serializable> extends KeyEnum<T> {
    String getValue();
}