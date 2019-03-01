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


}


