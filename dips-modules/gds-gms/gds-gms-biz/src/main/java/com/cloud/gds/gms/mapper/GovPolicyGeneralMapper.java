package com.cloud.gds.gms.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.cloud.dips.common.core.util.Query;
import com.cloud.gds.gms.entity.GovPolicyGeneral;
import com.cloud.gds.gms.vo.GeneralVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface GovPolicyGeneralMapper extends BaseMapper<GovPolicyGeneral> {


	/**
	 * 根据条件查询
	 *
	 * @param query
	 * @param map
	 * @return
	 */
	List<GeneralVO> selectAll(Query query, Map<String, Object> map);
//	List<GeneralVO> selectAll(Map<String,Object> map);
}