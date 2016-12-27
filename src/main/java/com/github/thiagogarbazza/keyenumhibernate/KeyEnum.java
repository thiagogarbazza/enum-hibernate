package com.github.thiagogarbazza.keyenumhibernate;

import java.io.Serializable;

public interface KeyEnum<T extends Serializable> {
    T getKey();
}
