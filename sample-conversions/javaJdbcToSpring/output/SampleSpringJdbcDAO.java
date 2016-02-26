package com.company.product.dao.jdbc;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
*
*/

public class SampleSpringJdbcDAO {

    private SimpleJdbcTemplate simpleJbdcTemplate;

    public SampleSpringJdbcDAO(DataSource aDataSource) {
        simpleJbdcTemplate = new SimpleJdbcTemplate(aDataSource);
    }

    import java.sql.ResultSet;
    import java.sql.SQLException;

    private static final String GET_PROPERTIES_SQL =
    "SELECT FIELD1 AS F1, FIELD2 AS F2, FIELD3 AS F3" +
    " FIELD4 AS F4 FROM TABLE  WHERE FIELD1 = ? AND FIELD2 = ?";

    public Collection<SomeDTO> getSomeDTOs(int field1, String field2)
    throws SomeDMException {

        List<SomeDTO> dtoList = new ArrayList<SomeDTO>();
        Map<String, Object> namedParams = new HashMap<String, Object>();

        namedParams.put("F1", field1);
        namedParams.put("F2", field2);

        ResultSet res = executePreparedQuery(preparedStatement);

        ResultSetExtractor<Collection<SomeDTO>> resultSetExtractor =
        new ResultSetExtractor<Collection<SomeDTO>>() {
            public Collection<SomeDTO> extractData(ResultSet res)
            throws SQLException, DataAccessException {
                List<SomeDTO> dtoList = new ArrayList<SomeDTO>();
                while (res.next()) {
                    int field1 = res.getString("F1");
                    String field2 = res.getString("F2");
                    dtoList.add(new SomeDTO(field1, field2));
                }
                return dtoList;
            }
        };

        if (log.isDebugEnabled()) {
            log.debug("getSomeDTOs returned [" + SomeDTO + "]");
        }
        return dtoList;
    }

}
