package db;

import utils.JSObject;

import java.util.List;


public interface IDao {
	public List<JSObject> findModeResult(String con, Object... params) throws Exception;
	public JSObject findSimpleResult(String con, Object... params) throws Exception;
	public boolean updateByPreparedStatement(String con, Object... params) throws Exception;
}
