package ${serviceImplPackage};

import javax.annotation.Resource;
import java.util.List;
import org.jeecgframework.p3.core.utils.common.PageList;
import org.jeecgframework.p3.core.utils.common.PageQuery;
import org.jeecgframework.p3.core.utils.common.Pagenation;
import org.springframework.stereotype.Service;

import ${daoPackage}.${className}Dao;
import ${domainPackage}.${className}Entity;
import ${servicePackage}.${className}Service;

/**
 * 描述：${codeName}
 * @author: ${author}
 * @since：${nowDate}
 * @version:1.0
 */

@Service("${lowerName}Service")
public class ${className}ServiceImpl implements ${className}Service {
	@Resource
	private ${className}Dao ${lowerName}Dao;

	@Override
	public ${className}Entity get(String id) {
		return ${lowerName}Dao.get(id);
	}

	@Override
	public void update(${className}Entity ${lowerName}) {
		${lowerName}Dao.update(${lowerName});
	}

	@Override
	public void insert(${className}Entity ${lowerName}) {
		${lowerName}Dao.add(${lowerName});
		
	}

	@Override
	public PageList<${className}Entity> queryPageList(
		PageQuery<${className}Entity> pageQuery) {
		PageList<${className}Entity> result = new PageList<${className}Entity>();
		Integer itemCount = ${lowerName}Dao.count(pageQuery);
		List<${className}Entity> list = ${lowerName}Dao.queryPageList(pageQuery,itemCount);
		Pagenation pagenation = new Pagenation(pageQuery.getPageNo(), itemCount, pageQuery.getPageSize());
		result.setPagenation(pagenation);
		result.setValues(list);
		return result;
	}

	@Override
	public void delete(${className}Entity ${lowerName}) {
		${lowerName}Dao.delete(${lowerName});
		
	}
}
