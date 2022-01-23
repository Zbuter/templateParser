package host.zbuter;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.ReflectUtil;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * TemplateFilterMethods
 * <br/>
 *
 * @author Zhang Jiashun
 * @since 2022-01-21 18:24
 */
class TemplateFilterMethods {
    private TemplateFilterMethods() {
    }

    public static Object index(Iterable<?> list, int index) {
        if (list == null) {
            return null;
        }
        int i = 0;
        for (Object o : list) {
            if (i == index) {
                return o;
            }
            i++;
        }
        return null;
    }

    public static Object field(Object obj, String fieldName) {
        return ReflectUtil.getFieldValue(obj, fieldName);
    }

    public static String re(String content, String re, int group) {
        return ReUtil.get(re, content, group);
    }

    public static String replaceAll(String content, String re, String replacementTemplate) {
        return ReUtil.replaceAll(content, re, replacementTemplate);
    }

    public static String remove(String content, String re) {
        return ReUtil.replaceAll(content, re, "");
    }

    public static String timeFormat(LocalDateTime date, String pattern) {
        if (pattern == null) {
            pattern = "yyyy-MM-dd HH:mm:ss";
        }
        return DateUtil.format(date, pattern);
    }

    public static String append(String str, String... strings) {
        StringBuilder sb = new StringBuilder(str);
        for (String string : strings) {
            sb.append(string);
        }
        return sb.toString();
    }

    public static String onlyTime(long date) {
        return DateUtil.format(new Date(date), "HH:mm:ss");
    }

    public static String onlyTime(Date date) {
        return DateUtil.format(date, "HH:mm:ss");
    }

    public static String onlyDate(Date date) {
        return DateUtil.format(date, "YYYY-MM-DD");
    }

    public static String onlyDate(long date) {
        return DateUtil.format(new Date(date), "YYYY-MM-DD");
    }


    public static Object distribute(String methodName, Object source, Object[] args) {
        // 不区分大小写获取方法名
        Method method = ReflectUtil.getMethodByNameIgnoreCase(TemplateFilterMethods.class, methodName);
        // 方法参数 类型
        final Class<?>[] parameterTypes = method.getParameterTypes();
        final Object[] actualArgs = new Object[parameterTypes.length];

        if (actualArgs.length >= 1) {
            actualArgs[0] = source;
        }

        if (args != null && actualArgs.length - 1 >= 0) {
            System.arraycopy(args, 0, actualArgs, 1, actualArgs.length - 1);
        }


        return ReflectUtil.invokeStatic(method, actualArgs);
    }
}