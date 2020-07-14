package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;
import ru.javawebinar.topjava.util.ValidationUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Transactional(readOnly = true)
public class JdbcUserRepository implements UserRepository {

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertUser;

    @Autowired
    public JdbcUserRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.insertUser = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    @Transactional
    public User save(User user) {
        ValidationUtil.checkBean(user);
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);

        if (user.isNew()) {
            Number newKey = insertUser.executeAndReturnKey(parameterSource);
            user.setId(newKey.intValue());
        } else if (namedParameterJdbcTemplate.update(
                "UPDATE users SET name=:name, email=:email, password=:password, " +
                        "registered=:registered, enabled=:enabled, calories_per_day=:caloriesPerDay " +
                        "WHERE id=:id", parameterSource) == 0) {
            return null;
        } else {
            deleteRoles(user);
        }
        addRoles(user);
        return user;
    }

    @Override
    @Transactional
    public boolean delete(int id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
    }

    @Override
    public User get(int id) {
        return DataAccessUtils.singleResult(jdbcTemplate.query("SELECT * FROM users " +
                        "LEFT JOIN user_roles ON users.id = user_roles.user_id WHERE id=?",
                new UserExtractor(),
                id));
    }

    @Override
    public User getByEmail(String email) {
        return DataAccessUtils.singleResult(jdbcTemplate.query("SELECT * FROM users " +
                        "LEFT JOIN user_roles ON users.id = user_roles.user_id WHERE email=?",
                new UserExtractor(),
                email));
    }

    @Override
    public List<User> getAll() {
        return Objects.requireNonNull(jdbcTemplate.query("SELECT * FROM users " +
                        " LEFT JOIN user_roles ON users.id = user_roles.user_id",
                new UserExtractor()))
                .stream()
                .sorted(Comparator.comparing(User::getName).thenComparing(User::getEmail))
                .collect(Collectors.toList());
    }

    private void deleteRoles(User user) {
        jdbcTemplate.update("DELETE FROM user_roles WHERE user_id=?", user.getId());
    }

    private void addRoles(User user) {
        Set<Role> roles = user.getRoles();
        if (!CollectionUtils.isEmpty(roles)) {
            jdbcTemplate.batchUpdate("INSERT INTO user_roles(user_id, role) VALUES (?,?)",
                    roles,
                    roles.size(),
                    (preparedStatement, role) -> {
                        preparedStatement.setInt(1, user.getId());
                        preparedStatement.setString(2, role.name());
                    });
        }
    }

    private static class UserExtractor implements ResultSetExtractor<List<User>> {
        @Override
        public List<User> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
            Map<Integer, User> users = new HashMap<>();
            while (resultSet.next()) {
                Integer id = resultSet.getInt("id");
                User user = users.computeIfAbsent(id, key -> {
                    User currentUser = new User();
                    try {
                        currentUser.setId(id);
                        currentUser.setName(resultSet.getString("name"));
                        currentUser.setEmail(resultSet.getString("email"));
                        currentUser.setCaloriesPerDay(resultSet.getInt("calories_per_day"));
                        currentUser.setEnabled(resultSet.getBoolean("enabled"));
                        currentUser.setRegistered(resultSet.getTimestamp("registered"));
                        currentUser.setPassword(resultSet.getString("password"));
                    } catch (SQLException exception) {
                        throw new RuntimeException(exception);
                    }
                    return currentUser;
                });
                String roleString = resultSet.getString("role");
                if (!StringUtils.isEmpty(roleString)) {
                    if (user.getRoles() == null) {
                        user.setRoles(new HashSet<>());
                    }
                    user.getRoles().add(Role.valueOf(roleString));
                }
            }
            return new ArrayList<>(users.values());
        }
    }
}
