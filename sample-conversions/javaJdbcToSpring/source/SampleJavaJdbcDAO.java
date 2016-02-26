
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class SampleJavaJdbcDAO implements ISampleJavaJdbcDAO {

    private static final String GET_PROPERTIES_SQL =
    "SELECT FIELD1 AS F1, FIELD2 AS F2, FIELD3 AS F3" +
    " FIELD4 AS F4 FROM TABLE  WHERE FIELD1 = ? AND FIELD2 = ?";

    public Collection<SomeDTO> getSomeDTOs(int field1, String field2)
    throws SomeDMException {

        List<SomeDTO> dtoList = new ArrayList<SomeDTO>();
        PreparedStatement preparedStatement;
        try {
            preparedStatement = getPreparedStatement(GET_PROPERTIES_SQL);
            preparedStatement.setInt(1, field1);
            preparedStatement.setString(2, field2);

            ResultSet res = executePreparedQuery(preparedStatement);

            while (res.next()) {
                int field1 = res.getString("F1");
                String field2 = res.getString("F2");
                dtoList.add(new SomeDTO(field1, field2));
            }
        } catch (SQLException e) {
            throw new SomeDMException("SQL Exception: Cannot find row with " +
                                      field1 + " and " + field2, e);
        } finally {
            closeAllResources();
        }

        if (log.isDebugEnabled()) {
            log.debug("getSomeDTOs returned [" + SomeDTO + "]");
        }
        return dtoList;
    }
}
