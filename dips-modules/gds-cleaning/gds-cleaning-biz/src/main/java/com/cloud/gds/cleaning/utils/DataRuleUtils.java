package com.cloud.gds.cleaning.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cloud.gds.cleaning.api.entity.DataRule;
import com.cloud.gds.cleaning.api.vo.DataRuleVo;
import com.cloud.gds.cleaning.api.vo.DataSetVo;
import com.cloud.gds.cleaning.api.vo.LabelVo;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 数据规则utils
 *
 * @Author : yaonuan
 * @Email : 806039077@qq.com
 * @Date : 2018-12-11
 */
public class DataRuleUtils {


	/**
	 * 规则 vo 2 po
	 * @param dataRuleVo
	 * @return
	 */
	public static DataRule vo2po(DataRuleVo dataRuleVo) {
		DataRule entity = new DataRule();
		BeanUtils.copyProperties(dataRuleVo, entity);
		if (dataRuleVo.getDetail() != null){
			entity.setParams(DataRuleUtils.dataSet2String(dataRuleVo.getDetail()));
		}
		return entity;
	}

	/**
	 * 规则 po 2 vo
	 * @param dataRule
	 * @return
	 */
	public static DataRuleVo po2Vo(DataRule dataRule) {
		DataRuleVo vo = new DataRuleVo();
		BeanUtils.copyProperties(dataRule, vo);
		vo.setDetail(JSON.parseArray(dataRule.getParams(), DataSetVo.class));
		return vo;
	}

	/**
	 * list vo 2 po
	 * @param list
	 * @return
	 */
	public static List<DataRule> listVo2Po(List<DataRuleVo> list) {
		List<DataRule> entitys = new ArrayList<>();
		for (DataRuleVo vo : list){
			DataRule entity = DataRuleUtils.vo2po(vo);
			entitys.add(entity);
		}
		return entitys;
	}

	/**
	 * list po 2 v0
	 * @param list
	 * @return
	 */
	public static List<DataRuleVo> listPo2Vo(List<DataRule> list) {
		List<DataRuleVo> vos = new ArrayList<>();
		for (DataRule entity : list){
			DataRuleVo vo = DataRuleUtils.po2Vo(entity);
			vos.add(vo);
		}
		return vos;
	}

	/**
	 * 前端vo显示的时候转json
	 * @param list
	 * @return
	 */
	public static String dataSet2String(List<DataSetVo> list){
		List<String> a = new ArrayList<>();
		for (DataSetVo vo : list){
			a.add(JSONObject.toJSONString(vo));
		}
		return a.toString();
	}

	public static ArrayList<LabelVo> convet(DataRuleVo dataRuleVo){

		ArrayList<LabelVo> list = new ArrayList<>();
		List<DataSetVo> dataSetVos = dataRuleVo.getDetail();
		if (dataSetVos.size() > 0){
			for (DataSetVo vo : dataSetVos){
				LabelVo labelVo = new LabelVo();
				labelVo.setLabel(vo.getLabel());
				labelVo.setProp(vo.getProp());
				list.add(labelVo);
			}
			return list;
		}
		return null;
	}

	/**
	 * dataSetVos 转换成 SortedMap
	 * @param dataSetVos
	 * @return
	 */
	public static SortedMap<String,String> changeSortedMap(List<DataSetVo> dataSetVos){
		SortedMap<String,String> sortedMap = new TreeMap<>();
		for (DataSetVo vo : dataSetVos){
			if (vo.getLabel() != "" || vo.getLabel().trim()!=""){
				sortedMap.put(vo.getLabel(), vo.getLabel());
			}
		}
		return sortedMap;
	}


}