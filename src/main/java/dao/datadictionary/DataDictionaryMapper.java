package dao.datadictionary;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import pojo.DataDictionary;

public interface DataDictionaryMapper {

		public List<DataDictionary> getDataDictionaryList(@Param("typeCode")String typeCode);
}
