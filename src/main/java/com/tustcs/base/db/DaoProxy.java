package com.tustcs.base.db;

import com.tustcs.base.utils.JSObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DaoProxy implements IDao {
	private DatabaseConnection dbc = null;
	private IDao dao = null;

	public DaoProxy() throws SQLException{
		this.dbc = new DatabaseConnection();
		this.dao = new DaoImpl(this.dbc.getConnection());
	}


	public List<JSObject> findModeResult(String con, Object... params){
		List<JSObject> list = new ArrayList<JSObject>();
		try {
			list = this.dao.findModeResult(con, params);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				this.dbc.releaseConn();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return list;
	}

	public JSObject findSimpleResult(String con, Object... params){
		JSObject map = new JSObject();
		try {
			map = this.dao.findSimpleResult(con, params);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				this.dbc.releaseConn();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return map;
	}


	public boolean updateByPreparedStatement(String con, Object... params){
		boolean flag = false;
		try {
			flag = this.dao.updateByPreparedStatement(con, params);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				this.dbc.releaseConn();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return flag;
	}
}
