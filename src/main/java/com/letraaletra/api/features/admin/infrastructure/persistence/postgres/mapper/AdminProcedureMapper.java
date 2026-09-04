package com.letraaletra.api.features.admin.infrastructure.persistence.postgres.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.letraaletra.api.features.admin.domain.Admin;
import com.letraaletra.api.features.admin.domain.permission.Permission;
import com.letraaletra.api.features.admin.domain.permission.Permissions;
import com.letraaletra.api.shared.domain.security.PermissionAction;
import com.letraaletra.api.shared.domain.security.PermissionKey;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public final class AdminProcedureMapper {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private AdminProcedureMapper() {}

    public static Admin toDomain(ResultSet rs) throws SQLException {
        UUID id = (UUID) rs.getObject("admin_id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        String hash = rs.getString("password_hash");
        UUID tokenVersion = (UUID) rs.getObject("token_version");
        boolean isSuper = rs.getBoolean("is_super");
        java.sql.Timestamp ts = rs.getTimestamp("created_at");
        LocalDateTime createdAt = ts != null ? ts.toLocalDateTime() : LocalDateTime.now();
        String permJson = rs.getString("permissions");

        Permissions perms = new Permissions();
        if (permJson != null && !permJson.isBlank() && !permJson.equals("[]") && !permJson.equals("null")) {
            try {
                JsonNode array = MAPPER.readTree(permJson);
                Map<PermissionKey, Set<PermissionAction>> grouped = new HashMap<>();
                for (JsonNode node : array) {
                    String keyStr = node.get("permission_key").asText();
                    String actionStr = node.get("action").asText();
                    PermissionKey key = PermissionKey.valueOf(keyStr);
                    PermissionAction action = PermissionAction.valueOf(actionStr);
                    grouped.computeIfAbsent(key, k -> new HashSet<>()).add(action);
                }
                grouped.forEach((k, v) -> perms.set(new Permission(k, v)));
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse permissions JSON: " + permJson, e);
            }
        }

        return Admin.restore(id, name, email, hash, tokenVersion, isSuper, perms, createdAt);
    }

    public static String permissionsToJson(Admin admin) {
        try {
            List<Map<String, String>> out = new ArrayList<>();
            for (Permission p : admin.getPermissions().getAll()) {
                for (PermissionAction a : p.actions()) {
                    Map<String, String> m = new HashMap<>();
                    m.put("permission_key", p.key().name());
                    m.put("action", a.name());
                    out.add(m);
                }
            }
            return MAPPER.writeValueAsString(out);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize permissions", e);
        }
    }
}
