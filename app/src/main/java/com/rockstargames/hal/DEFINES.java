package com.rockstargames.hal;

public class DEFINES {
    public static final boolean DUMP_IMAGES_ON_SCREEN_CHANGE = false;
    public static final boolean ENABLE_TOASTS = false;
    public static final boolean FILE_SPAM = false;
    public static final boolean FONT_SPAM = false;
    public static final boolean HIERARCHY_SPAM = false;
    public static final boolean HTTP_SPAM = false;
    public static final boolean IMAGE_SPAM = false;
    public static final boolean MEM_SPAM = false;
    public static final long OBB_PATCH_SIZE = 0;
    public static final boolean STARTUP_DIAG_SPAM = false;
    public static final boolean USE_OBB = false;
    public static final boolean VIEW_SPAM = false;
    public static final boolean VISIBLE_CONTAINERS = false;
    public static final boolean VISIBLE_NULL_IMAGES = false;
    public static ObbInfo[] OBBS = {new ObbInfo(32, 263950903), new ObbInfo(33, 263950903), new ObbInfo(34, 263950903), new ObbInfo(35, 263950903), new ObbInfo(36, 236816515), new ObbInfo(37, 236816515), new ObbInfo(38, 236994952), new ObbInfo(39, 237003015), new ObbInfo(40, 236638366), new ObbInfo(41, 236638366), new ObbInfo(42, 236638366), new ObbInfo(43, 237463165), new ObbInfo(44, 237463165), new ObbInfo(45, 237463165), new ObbInfo(46, 237463165), new ObbInfo(47, 237426449), new ObbInfo(48, 237426449), new ObbInfo(49, 237426449), new ObbInfo(50, 237480828), new ObbInfo(51, 237480828), new ObbInfo(52, 237480828), new ObbInfo(53, 237480828), new ObbInfo(54, 237480828), new ObbInfo(55, 237480828), new ObbInfo(56, 237480828), new ObbInfo(57, 237480828), new ObbInfo(58, 237480828), new ObbInfo(59, 237480828), new ObbInfo(60, 237480828), new ObbInfo(61, 237480828), new ObbInfo(62, 237480828), new ObbInfo(63, 237480828), new ObbInfo(64, 237480828), new ObbInfo(65, 237487900), new ObbInfo(68, 237487900)};
    public static final int OBB_MAIN_VERSION = OBBS[OBBS.length - 1].version;
    public static final long OBB_MAIN_SIZE = OBBS[OBBS.length - 1].size;
    public static final int OBB_PATCH_VERSION = OBBS[OBBS.length - 1].version;

    public static class ObbInfo {
        public long size;
        public int version;

        public ObbInfo(int v, long s) {
            this.version = v;
            this.size = s;
        }
    }
}