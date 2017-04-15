package db;


import utils.JSObject;

import java.sql.SQLException;
import java.util.List;

public class Sql {
	public static IDao getIPeinterDaoInstance() throws SQLException{
		return new DaoProxy();
	}
	public static List<JSObject> findList(String sql, Object...params) throws Exception{
//		return new ArrayList<JSObject>();
		return getIPeinterDaoInstance().findModeResult(sql,params);
	}

	public static boolean update(String sql,Object...params) throws Exception{
//		return false;
		return getIPeinterDaoInstance().updateByPreparedStatement(sql,params);
	}

	public static JSObject findOne(String sql, Object...params) throws Exception{
//		return new JSObject();
		return getIPeinterDaoInstance().findSimpleResult(sql,params);
	}
}
