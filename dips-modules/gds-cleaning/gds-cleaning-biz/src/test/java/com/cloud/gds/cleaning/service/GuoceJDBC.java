package com.cloud.gds.cleaning.service;

import com.alibaba.fastjson.JSON;
import com.cloud.gds.cleaning.GdsCleaningApplication;
import com.cloud.gds.cleaning.api.entity.DataFieldValue;
import com.cloud.gds.cleaning.config.MyDataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Data;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @Author : lolilijve
 * @Email : lolilijve@gmail.com
 * @Date : 2019-02-01
 */
@SpringBootTest(classes = GdsCleaningApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class GuoceJDBC {

	@Autowired
	private DataFieldValueService dataFieldValueService;
	@Autowired
	private ExecutorService analysisThreadPool;
	@Autowired
	private DoAnalysisService doAnalysisService;
	@Autowired
	private MyDataSource myDataSource;

	/**
	 * 100%相似度的set方式清洗
	 */
	@Test
	public void guoceClean() {
		List<Long> ids = doAnalysisService.getNoExactlySameDataIds(98L);
		List<List<Long>> idLists = cutIds(ids, 500);

		AtomicInteger allNum = new AtomicInteger(idLists.size());
		for (List<Long> idList : idLists) {
			analysisThreadPool.execute(() -> {
				this.move(idList);
				allNum.getAndDecrement();
			});
		}

		while (allNum.get() > 0) {
			try {
				Thread.sleep(1000L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	public void gouceInsert() {
		Connection conn = null;
		Statement stmt = null;
		try {

			// 打开链接
			conn = myDataSource.getConnection();

			// 执行查询
			stmt = conn.createStatement();
			String sql;
			sql = "SELECT id,title,is_deleted FROM scrapy_gov_policy_general WHERE is_deleted != 1 ORDER BY is_deleted"
				+ " DESC ";
			ResultSet rs = stmt.executeQuery(sql);

			List<DataFieldValue> list = new ArrayList<>();
			// 展开结果集数据库
			while (rs.next()) {
				// 通过字段检索
				int id = rs.getInt("id");
				String name = rs.getString("title");
				int is_deleted = rs.getInt("is_deleted");

				DataFieldValue fieldValue = new DataFieldValue();
				fieldValue.setFieldId(97L);

				GouceEntity gouceEntity = new GouceEntity();
				gouceEntity.setId(id);
				gouceEntity.setTitle(name);
				gouceEntity.setIs_deleted(is_deleted);
				fieldValue.setFieldValue(JSON.toJSONString(gouceEntity));
				fieldValue.setCreateUser(0);
				list.add(fieldValue);

			}
			dataFieldValueService.batchSave(list, 1000);
			// 完成后关闭
			// rs.close();
			// stmt.close();
			myDataSource.releaseConnection(conn);
		} catch (Exception e) {
			// 处理 Class.forName 错误
			e.printStackTrace();
		}
		System.out.println("OVER!");
	}

	/**
	 * 表间移动数据
	 *
	 * @param list
	 */
	public void move(List<Long> list) {
		Connection conn = null;
		Statement stmt = null;
		String idStr = list.toString();
		String ids = idStr.substring(1, idStr.length() - 1);
		try {
			// 打开链接
			System.out.println("连接数据库...");
			conn = myDataSource.getConnection();

			// 执行查询
			System.out.println(" 实例化Statement对象...");
			stmt = conn.createStatement();
			String sql;
			sql = "INSERT INTO gov_policy_general (title,reference,issue,style,`level`,write_time,publish_time,"
				+ "effect_time,text,url,creator_id,scrapy_id,examine_status,examine_user_id,processor_id,"
				+ "examine_date)"
				+ "SELECT title,reference,issue,(CASE style WHEN \"通知\" THEN 1 WHEN \"公告\" THEN 2 WHEN \"报告\" THEN"
				+ " 3 WHEN \"意见\" THEN 4 WHEN \"办法\" THEN 5 WHEN \"通报\" THEN 6 WHEN \"其他\" THEN 7 ELSE 0 END)AS "
				+ "style,(CASE level WHEN \"国家级\" THEN 1 WHEN \"省级\" THEN 2 WHEN \"市级\" THEN 3 WHEN \"区级（县级）\" "
				+ "THEN 4 ELSE 0 END)AS `level`,write_time,publish_time,effect_time,text,url,creator_id,id AS "
				+ "scrapy_id,3 as examine_status,2112 AS examine_user_id,2112 AS processor_id,CURRENT_TIME() AS "
				+ "examine_date FROM scrapy_gov_policy_general where id in ";

			String text;
			text = "SELECT title,reference,issue,(CASE style WHEN \"通知\" THEN 1 WHEN \"公告\" THEN 2 WHEN \"报告\" THEN 3 "
				+ "WHEN \"意见\" THEN 4 WHEN \"办法\" THEN 5 WHEN \"通报\" THEN 6 WHEN \"其他\" THEN 7 ELSE 0 END)AS style,"
				+ "(CASE level WHEN \"国家级\" THEN 1 WHEN \"省级\" THEN 2 WHEN \"市级\" THEN 3 WHEN \"区级（县级）\" THEN 4 ELSE 0"
				+ " END)AS `level`,write_time,publish_time,effect_time,text,url,creator_id,id AS scrapy_id,3 as "
				+ "examine_status,2112 AS examine_user_id,2112 AS processor_id,CURRENT_TIME() AS examine_date FROM "
				+ "scrapy_gov_policy_general where id in ";

			String sqlcount;
			sqlcount = sql + "(" + ids + ")";
			System.out.println(sqlcount);

			stmt.execute(sqlcount);
			// stmt.close();
			myDataSource.releaseConnection(conn);
		} catch (SQLException se) {
			// 处理 JDBC 错误
			se.printStackTrace();
		} catch (Exception e) {
			// 处理 Class.forName 错误
			e.printStackTrace();
		} finally {
			// 关闭资源
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException se2) {
				// 什么都不做
			}
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
		System.out.println("OVER!");
	}

	/**
	 * 多线程打标签
	 *
	 * @throws Exception
	 */
	public void MultiThreadLabel() throws Exception {

		//查询未打标签的ids
		List<Long> ids = selectNoTagId();
		List<List<Long>> idLists = cutIds(ids, 100);

		for (List<Long> idList : idLists) {
			analysisThreadPool.execute(() -> {
				//根据ids分块查询数据
				Connection conn = null;
				Statement stmt;
				ResultSet rs = null;
				try {
					conn = myDataSource.getConnection();
					stmt = conn.createStatement();
					//todo 请输入正确的sql
					String sql = "SELECT * FROM x";
					rs = stmt.executeQuery(sql);
					while (rs.next()) {
						//todo 请自建内部类并填充数据

					}
				} catch (SQLException e) {
					e.printStackTrace();
				}

				//todo 打标签
				TagRelation tagRelation = new TagRelation();
				// 固定
				tagRelation.setNode("gov_general_policy");
				tagRelation.setType_id(4L);

				//todo 对标签进行存储

				myDataSource.releaseConnection(conn);
			});
		}
	}

	/**
	 * 切分ids
	 *
	 * @param ids
	 * @return
	 */
	public List<List<Long>> cutIds(List<Long> ids, int oneSize) {
		int currNum = 0;
		List<List<Long>> lll = new ArrayList<>();
		boolean flag = true;

		while (flag) {
			if (ids.size() > oneSize * (currNum + 1)) {
				//非最后一块
				lll.add(ids.subList(currNum * oneSize, oneSize * (currNum + 1)));
			} else {
				//最后一块
				lll.add(ids.subList(currNum * oneSize, ids.size()));
				flag = false;
			}
			currNum++;
		}
		return lll;
	}

	public List<Long> selectNoTagId() {
		List<Long> ids = new ArrayList<>();
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = myDataSource.getConnection();
			stmt = conn.createStatement();
			String sql =
				"SELECT a.id FROM gov_policy_general a LEFT JOIN gov_tag_relation b ON a.id = b.relation_id WHERE b"
					+ ".relation_id IS NULL AND a.examine_user_id = 2112 ";
			// 执行查询
			ResultSet rs = stmt.executeQuery(sql);
			// 展开结果集数据库
			while (rs.next()) {
				// 通过字段检索
				Long id = rs.getLong("id");
				ids.add(id);
			}
			// 完成后归还连接
			myDataSource.releaseConnection(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("selectNoTagId OVER!");
		return ids;
	}

	@Data
	private static class GouceEntity {

		private Integer id;
		private String title;
		private Integer is_deleted;

	}

	@Data
	private class TagRelation {

		private String node;
		private Long tag_id;
		private Long relation_id;
		private Long type_id;

	}

}
