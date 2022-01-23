package host.zbuter;

import cn.hutool.core.lang.Dict;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Main
 * <br />
 *
 * @author zbuter
 * @date 2022-01-22 17:53
 */
public class Main {
    public static void main(String[] args) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            User user = new User();
            user.setEmail("email" + i);
            user.setName("name" + i);
            User.Type type = new User.Type();
            type.setType("type" + i);
            user.setType(type);
            users.add(user);
        }

        String str = TemplateParserUtil.processTemplate("{{ user }} 感谢您的参加 {{时间}}",
                Dict.create().set("参与人", "name")
                        .set("时间", LocalDateTime.now())
                        .set("user", users)
                , (value) -> {
                    if (value instanceof LocalDateTime) {
                        return TemplateFilterMethods.timeFormat((LocalDateTime)value,null);
                    }
                    if (value instanceof List) {
                        List<?> obj = (List<?>) value;
                        Object o = obj.get(0);
                        if (o instanceof User) {
                            ((User) o).setEmail("testemail");
                            return ((User) o).getEmail();
                        }
                    }
                    return value;
                });
        System.out.println(str);
    }

    static public class User {
        String email;
        String name;
        Type type;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }

        static public class Type {
            String type;

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }
        }
    }
}
