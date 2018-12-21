package com.cloud.gds.cleaning.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.cloud.gds.cleaning.api.entity.DataRule;
import com.cloud.gds.cleaning.api.vo.DataRulePageVo;
import com.cloud.gds.cleaning.api.vo.DataRuleVo;

import java.util.List;
import java.util.Set;

/**
 * 待清洗数据
 *
 * @author lolilijve
 * @date 2018-11-22 10:59:36
 */
public interface DataRuleService extends IService<DataRule> {

	/**
	 * 查询规则
	 * @return
	 */
	List<DataRulePageVo> selectAll();
	/**
	 * 单独删除数据
	 * @param id
	 * @return
	 */
	Boolean deleteById(Long id);
	/**
	 * 批量删除数据
	 * @param ids
	 */
	Boolean deleteByIds(Set<Long> ids);

	/**
	 * 保存
	 * @param dataRuleVo
	 * @return
	 */
	Boolean save(DataRuleVo dataRuleVo);
}

