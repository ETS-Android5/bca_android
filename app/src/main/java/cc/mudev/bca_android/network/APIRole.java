package cc.mudev.bca_android.network;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class APIRole {
    public class APIRoleException extends Exception {
    }

    public class NotThisRoleClassException extends APIRoleException {
    }

    public abstract class APIRoleBase {
        public String type;
        public int id;

        public Map<String, String> parseRoleString(String roleString) throws UnsupportedEncodingException {
            final Map<String, String> query_pairs = new LinkedHashMap<>();
            final String[] pairs = roleString.split("&");
            for (String pair : pairs) {
                final int idx = pair.indexOf("=");
                final String key = idx > 0 ? pair.substring(0, idx) : pair;
                final String value = idx > 0 && pair.length() > idx + 1 ? pair.substring(idx + 1) : null;
                query_pairs.put(key, value);
            }
            return query_pairs;
        }
    }

    public class AdminRole extends APIRoleBase {
        public AdminRole(String roleString) throws APIRoleException {
            if (roleString != null && roleString.contains("admin")) {
                this.type = "admin";
                this.id = -1;
            } else {
                throw new NotThisRoleClassException();
            }
        }
    }

    public class ProfileRole extends APIRoleBase {
        public ProfileRole(String roleString) throws APIRoleException {
            try {
//                Map<String, String> parsedRole = parseRoleString(roleString);
                JSONObject parsedRole = new JSONObject(roleString);
                if (!parsedRole.has("type") || !parsedRole.getString("type").equals("profile")) {
                    throw new NotThisRoleClassException();
                }

                this.type = "profile";
                this.id = parsedRole.getInt("id");
                if (this.id <= 0) throw new APIRoleException();
            } catch (NotThisRoleClassException e) {
                throw e;
            } catch (Exception e) {
                throw new NotThisRoleClassException();
            }
        }
    }

    List<Class<? extends APIRoleBase>> possibleAPIRoles;
    public List<APIRoleBase> role;

    public APIRole(List<String> roleStringList) {
        possibleAPIRoles = new ArrayList<>();
        possibleAPIRoles.add(AdminRole.class);
        possibleAPIRoles.add(ProfileRole.class);

        role = new ArrayList<>();
        for (String roleString : roleStringList) {
            try {
                role.add(new ProfileRole(roleString));
                continue;
            } catch (Exception e) {
            }
            try {
                role.add(new AdminRole(roleString));
                continue;
            } catch (Exception e) {
            }
        }
    }
}
