package com.cloud.gds.preprocessing.mapper;

import com.cloud.gds.preprocessing.entity.BasePolicy;
import com.cloud.gds.preprocessing.entity.ScrapyGovPolicyGeneral;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface InvalidInformationMapper {

	/**
	 * 获取国策采集长度问题数据
	 *
	 * @param titleLength
	 * @param textLength
	 * @return
	 */
	List<Long> gainIssueId(@Param("titleLength") Integer titleLength, @Param("textLength") Integer textLength);

	/**
	 * 根据ids更新爬取表中的数据状态
	 *
	 * @param ids
	 * @return
	 */
	boolean updateScrapyIsDeleted(@Param("ids") List<Long> ids);

	/**
	 * 查询采集库中重复数据
	 *
	 * @return
	 */
	List<BasePolicy> gainIdenticalPolicy();

	/**
	 * 获取资讯数据
	 *
	 * @return
	 */
	List<BasePolicy> scrapyInformationBase();

	/**
	 * 获取采集库数据1w
	 *
	 * @return
	 */
	List<ScrapyGovPolicyGeneral> invalidInformationPolicy();

	/**
	 * 根据ids更新爬虫表的状态值
	 *
	 * @param state 状态值
	 * @param ids   需要更新的ids
	 * @return
	 */
	boolean updateByIdsAndIsDeleted(@Param("state") Long state, @Param("ids") List<Long> ids);
}