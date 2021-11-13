package com.training.vueblog.data;

import com.training.vueblog.objects.User;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

public class CustomMapper implements ResultSetExtractor<User> {

  @Override
  public User extractData(ResultSet resultSet) throws SQLException, DataAccessException {
    return null;
  }
}
