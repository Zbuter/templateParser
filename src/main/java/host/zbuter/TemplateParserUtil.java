package host.zbuter;

import cn.hutool.core.bean.BeanPath;
import cn.hutool.core.util.ReflectUtil;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* templateAnalysisUtil
* <br/>
*
* @author Zhang Jiashun
* @since 2022-01-21 18:05
*/
public class TemplateParserUtil {
    private static final Pattern VAR_PATTERN = Pattern.compile("\\{\\{\\s*(?<key>[\\S\\s]+?)\\s*(?:\\|\\s*(?<filter>[\\s\\S]+?)\\s*)?\\}\\}");
    private static final String FILTER_SPLIT_REG = "\\|";
    private static final Pattern METHOD_PATTERN = Pattern.compile("(?<methodName>\\w+)(?:[(（](?<parameters>[\\s\\S]+?)[)）])?");
    private static final String METHOD_PARAM_SPLIT_REG = "[,，]";


    public static String processTemplate(String template, Map<String, Object> params) {
        return processTemplate(template, params, p -> p);
    }

    /**
     * 替换模板内容
     *
     * @param template 模板
     * @param params  参数
     * @param func    当过滤器为空时 调用的方法，可根据类型返回一个默认过滤器。
     * @return 替换后的模板
     */
    public static String processTemplate(String template, Map<String, Object> params, Function<Object, Object> func) {
        Matcher m = VAR_PATTERN.matcher(template);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String key = matchByGroupName(m, "key");
            String filter = matchByGroupName(m, "filter");

            Object value = params.get(key);

            if(value == null){
                // 没有对应key 可能是一个表达式。 表达式没取到默认赋值为空字符串。
                Object pathValue = BeanPath.create(key).get(params);
                value = pathValue==null?"":pathValue;
            }
//
//            if (StrUtil.isBlankIfStr(filter)) {
//                // 没有过滤器， 调用用户自定义的回调方法返回一个过滤器字符串。
//                value = func.apply(value);
//            }

            Object val =  func.apply(parserFilter(filter, value));
            // 由于$是正则的特殊替换字符。 所以转一下。
            m.appendReplacement(sb,val.toString().replaceAll("\\$","\\\\\\$"));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * 解析过滤器
     *
     * @param filterStr 过滤器
     * @param value    原始值。
     * @return 过滤器处理后的结果。
     */
    private static Object parserFilter(String filterStr, Object value) {
        if (filterStr == null) {
            return value;
        }
        // 分解所有过滤器名。
        String[] split = filterStr.split(FILTER_SPLIT_REG);
        for (String method : split) {
            // 调用过滤方法。
            value = parserMethod(method, value);
        }
        return value == null? "":value;
    }

    private static Object parserMethod(String filterMethod, Object value) {
        Matcher m = METHOD_PATTERN.matcher(filterMethod);
        if (m.find()) {
            String methodName = matchByGroupName(m, "methodName");
            Method methodByName = ReflectUtil.getMethodByName(TemplateFilterMethods.class, "distribute");
            String parameters = m.group("parameters");
            if (parameters == null) {
                parameters = "";
            }
            String[] split = parameters.split(METHOD_PARAM_SPLIT_REG);

            try {
                Object otherVal = split;
                if (split[0].equals(parameters)) {
                    otherVal = "".equals(parameters) ? null : split;
                }
                value = ReflectUtil.invokeStatic(methodByName, methodName, value, otherVal);
            } catch (RuntimeException e) {
                // pass
            }

            return value;
        }


        return value;
    }

    private static String matchByGroupName(Matcher m, String name) {
        String group = m.group(name);
        if (group != null) {
            return group.trim();
        }
        return "";
    }





}