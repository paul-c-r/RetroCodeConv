
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class SampleSpringJdbcDAO implements ISomeDAO {

    private SimpleJdbcTemplate simpleJbdcTemplate;

    public SomeDAO(DataSource aDataSource) {
        simpleJbdcTemplate = new SimpleJdbcTemplate(aDataSource);
    }

    public void insertUpdateDTOs(Collection<SomeDTO> someDTOs) throws Exception {

        for (SomeDTO dto : someDTOs) {
            Map<String, Object> namedParams = new HashMap<String, Object>();
            namedParams.put("Field1", dto.getField1());
            namedParams.put("Field2", dto.getField2());
            namedParams.put("Field3", dto.getField3());
            namedParams.put("Field4", dto.getField4());

            try {
                simpleJbdcTemplate.getNamedParameterJdbcOperations().update(SomeDAOSQL.INSERT_SQL, namedParams);
            } catch (DataAccessException e) {
                throw new SomeException("Exception Message", e);
            }
        }
    }

    public Collection<SomeDTO> getRowMapperDTOs(int id, String field1Value) {

        Map<String, Object> namedParams = new HashMap<String, Object>();
        namedParams.put("ID", id);
        namedParams.put("Field1", field1Value);

        RowMapper<SomeDTO> rowMapper = new RowMapper<SomeDTO>() {
            public SomeDTO mapRow(ResultSet res, int i) throws SQLException {
                BigDecimal field1 = res.getBigDecimal("Field1");
                String field2 = res.getString("Field2");
                int field3 = res.getInt("Field3");
                return new SomeDTO(field1, field2, field3);
            }
        };
        try {
            return simpleJbdcTemplate.query(SomeDAOSQL.GET_DTO_SQL,
                                           rowMapper,
                                           namedParams);
        } catch (DataAccessException e) {
            throw new SomeException("Exception Message", e);
        }
    }


    private Collection<SomeDTO> getExtractorDTOs(int id, String field1Value) {

        Map<String, Object> namedParams = new HashMap<String, Object>();
        namedParams.put("ID", id);
        namedParams.put("Field1", field1Value);

        ResultSetExtractor<Collection<SomeDTO>> resultSetExtractor = new ResultSetExtractor<Collection<SomeDTO>>() {
            public Collection<SomeDTO> extractData(ResultSet res) throws SQLException, DataAccessException {

                List<SomeDTO> dtoList = new ArrayList<SomeDTO>();
                while (res.next()) {
                    BigDecimal field1 = res.getBigDecimal("Field1");
                    String field2 = res.getString("Field2");
                    int field3 = res.getInt("Field3");
                    dtoList.add(new SomeDTO(field1, field2, field3));
                }
                return dtoList;
            }
        };
        try {
            return simpleJbdcTemplate.getNamedParameterJdbcOperations().query(SomeDAOSQL.GET_DTO_SQL,
                                                                             namedParams, resultSetExtractor);
        } catch (DataAccessException e) {
            throw new SomeException("Exception Message", e);
        }
    }

}
