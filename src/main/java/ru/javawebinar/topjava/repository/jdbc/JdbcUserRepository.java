package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;
import ru.javawebinar.topjava.util.ValidationUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@Transactional(readOnly = true)
public class JdbcUserRepository implements UserRepository {

    private static final BeanPropertyRowMapper<User> ROW_MAPPER = BeanPropertyRowMapper.newInstance(User.class);

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
        }
        deleteRoles(user);
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
        return jdbcTemplate.query("SELECT * FROM users " +
                        " LEFT JOIN user_roles ON users.id = user_roles.user_id ORDER BY name, email",
                new UserExtractor());
    }

    private void deleteRoles(User user) {
        jdbcTemplate.update("DELETE FROM user_roles WHERE user_id=?", user.getId());
    }

    private void addRoles(User user) {
        Set<Role> roles = user.getRoles();
        if (roles != null && roles.size() > 0) {
            jdbcTemplate.batchUpdate("INSERT INTO user_roles(user_id, role) VALUES (?,?)",
                    roles,
                    roles.size(),
                    (preparedStatement, role) -> {
                        preparedStatement.setInt(1, user.getId());
                        preparedStatement.setString(2, role.name());
                    });
        }
    }

    public static class UserExtractor implements ResultSetExtractor<List<User>> {
        @Override
        public List<User> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
            Map<Integer, User> users = new HashMap<>();
            while (resultSet.next()) {
                Integer id = resultSet.getInt("id");
                User user = null;
                if (users.containsKey(id)) {
                    user = users.get(id);
                } else {
                    user = new User();
                    user.setId(id);
                    user.setName(resultSet.getString("name"));
                    user.setEmail(resultSet.getString("email"));
                    user.setCaloriesPerDay(resultSet.getInt("calories_per_day"));
                    user.setEnabled(resultSet.getBoolean("enabled"));
                    user.setRegistered(resultSet.getTimestamp("registered"));
                    user.setPassword(resultSet.getString("password"));
                    users.put(id, user);
                }
                user.getRoles().add(Role.valueOf(resultSet.getString("role")));
            }
            return new ArrayList<>(users.values());
        }
    }
}
