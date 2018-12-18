package com.cloud.gds.cleaning.api.vo;

import lombok.Data;

/**
 * 用户数据vo
 *
 * @Author : yaonuan
 * @Email : 806039077@qq.com
 * @Date : 2018-12-10
 */
@Data
public class DataFieldVo {

	/**
	 * id
	 */
	private Long id;
	/**
	 * 数据名称
	 */
	private String name;
	/**
	 * 来源部门名称
	 */
	private String deptName;
	/**
	 * 来源方式
	 */
	private String source;

	private Long ruleId;

	/**
	 * 分析状态（0、未分析 1、正在分析 2、已分析 3、分析出错）
	 */
	private Integer analyseState;

}