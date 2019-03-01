package ${daoImplPackage};

import java.util.List;

import org.jeecgframework.p3.core.utils.common.PageQuery;
import org.jeecgframework.p3.core.utils.common.PageQueryWrapper;
import org.jeecgframework.p3.core.utils.persistence.mybatis.GenericDaoDefault;
import org.springframework.stereotype.Repository;
import ${daoPackage}.${className}Dao;
import ${domainPackage}.${className}Entity;

/**
 * 描述：${codeName}
 * @author：${author}
 * @since：${nowDate}
 * @version:1.0
 */
@Repository("${lowerName}Dao")
public class ${className}DaoImpl extends GenericDaoDefault<${className}Entity> implements ${className}Dao{

	@Override
	public Integer count(PageQuery<${className}Entity> pageQuery) {
		return (Integer) super.queryOne("count",pageQuery);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<${className}Entity> queryPageList(
			PageQuery<${className}Entity> pageQuery,Integer itemCount) {
		PageQueryWrapper<${className}Entity> wrapper = new PageQueryWrapper<${className}Entity>(pageQuery.getPageNo(), pageQuery.getPageSize(),itemCount, pageQuery.getQuery());
		return (List<${className}Entity>)super.query("queryPageList",wrapper);
	}
	
	public List<${className}Entity> getBy${foreignKeyUpper}(String ${foreignKey}){
		return (List<${className}Entity>)super.query("getBy${foreignKeyUpper}",${foreignKey});
	}

	public void delBy${foreignKeyUpper}(String ${foreignKey}){
		super.update("delBy${foreignKeyUpper}", ${foreignKey});
	}

	public int getCountBy${foreignKeyUpper}(String ${foreignKey}){
		return (Integer) super.queryOne("getCountBy${foreignKeyUpper}",${foreignKey});
	}

	public void deleteBy${foreignKeyUpper}(String ${foreignKey}){
		super.getSqlSession().delete(getStatementId("deleteBy${foreignKeyUpper}"), ${foreignKey});
	}


}


