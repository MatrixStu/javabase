package com.tustcs.base.db;

import com.tustcs.base.services.DB;
import com.tustcs.base.utils.JSObject;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DaoProxy implements IDao {
	private Connection conn = null;
	private DataSource dsc = DB.dsc;
	private IDao dao = null;

	public DaoProxy() throws SQLException{
		this.conn = this.dsc.getConnection();
		this.dao = new DaoImpl(conn);
	}


	public List<JSObject> findModeResult(String con, Object... params){
		List<JSObject> list = new ArrayList<JSObject>();
		try {
			list = this.dao.findModeResult(con, params);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(this.conn != null ){
				try{
					this.conn.close();
				}catch(Exception e){
					e.printStackTrace();
				}
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
			if(this.conn != null ){
				try{
					this.conn.close();
				}catch(Exception e){
					e.printStackTrace();
				}
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
			if(this.conn != null ){
				try{
					this.conn.close();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		return flag;
	}
}
