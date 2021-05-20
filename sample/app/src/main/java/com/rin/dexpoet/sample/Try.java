package com.rin.dexpoet.sample;

class Try {
    public static void trying() {

    }

    public static void handler(Throwable throwable) {

    }

    public static void completion() {

    }

    public static void common() {
        try {
            trying();
        } catch (Exception e) {
            handler(e);
        } finally {
            completion();
        }
    }

    public static int withReturn() {
        try {
            trying();
            return 1;
        } catch (Exception e) {
            handler(e);
            return 0;
        }
    }
}
