package ${servicePackage};

import org.jeecgframework.p3.core.utils.common.PageList;
import org.jeecgframework.p3.core.utils.common.PageQuery;
import ${domainPackage}.${className}Entity;

/**
 * 描述：${codeName}
 * @author: ${author}
 * @since：${nowDate}
 * @version:1.0
 */
public interface ${className}Service {
	public ${className}Entity get(String id);

	public void update(${className}Entity ${lowerName});

	public void insert(${className}Entity ${lowerName});

	public PageList<${className}Entity> queryPageList(PageQuery<${className}Entity> pageQuery);

	public void delete(${className}Entity ${lowerName});
	
}
