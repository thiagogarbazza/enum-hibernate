package com.github.thiagogarbazza.keyenumhibernate;

public final class KeyEnumUtils {

    public static <T extends Object, C extends Enum> C findByKey(final Class<C> keyEnumClass, final T codigo) {
        Enum response = null;
        for (Enum e : keyEnumClass.getEnumConstants()) {
            KeyEnum castEnum = (KeyEnum) e;
            if (castEnum.getKey().equals(codigo)) {
                response = e;
                break;
            }
        }

        return (C) response;
    }
}
