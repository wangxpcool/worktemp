package com.kmerit.reponsitory;

import javax.sql.DataSource;
        import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
        import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplateDB1;
    private final JdbcTemplate jdbcTemplateDB2;

    @Autowired
    public UserRepository(@Qualifier("dataSourceDB1") DataSource dataSourceDB1,
                          @Qualifier("dataSourceDB2") DataSource dataSourceDB2) {
        this.jdbcTemplateDB1 = new JdbcTemplate(dataSourceDB1);
        this.jdbcTemplateDB2 = new JdbcTemplate(dataSourceDB2);
    }

    public void query() {
        jdbcTemplateDB1.execute("select * from tem");
    }

//    public void saveUserToDB2(User user) {
//        String sql = "INSERT INTO users (name, email) VALUES (?, ?)";
//        jdbcTemplateDB2.update(sql, user.getName(), user.getEmail());
//    }
}
