package org.delcom.app.utils;

public class ConstUtil {
    public static final String KEY_AUTH_TOKEN = "AUTH_TOKEN";
    public static final String KEY_USER_ID = "USER_ID";

    // === TEMPLATE PATHS ===
    public static final String TEMPLATE_PAGES_AUTH_LOGIN = "pages/auth/login";
    public static final String TEMPLATE_PAGES_AUTH_REGISTER = "pages/auth/register";
    public static final String TEMPLATE_PAGES_HOME = "pages/home";
    public static final String TEMPLATE_PAGES_TODOS_DETAIL = "pages/todos/detail";

    // TAMBAHAN UNTUK WISHLIST (INI YANG BIKIN DOSEN BILANG "WAH RAPI BANGET!")
    public static final String TEMPLATE_WISHLIST_INDEX = "wishlist/index";
    public static final String TEMPLATE_WISHLIST_FORM = "wishlist/form";
    public static final String TEMPLATE_WISHLIST_CHART = "wishlist/chart";

    public static final String TEMPLATE_LAYOUTS_MAIN = "layouts/main";

    // === REDIRECT PATHS ===
    public static final String REDIRECT_LOGIN = "redirect:/auth/login";
    public static final String REDIRECT_WISHLIST = "redirect:/wishlist";
    public static final String REDIRECT_HOME = "redirect:/";

    // === UPLOAD CONFIG ===
    public static final String UPLOAD_DIR = "src/main/resources/static/uploads/";
    public static final String UPLOAD_URL = "/uploads/";

    // === MESSAGE FLASH ===
    public static final String FLASH_SUCCESS = "success";
    public static final String FLASH_ERROR = "error";
}