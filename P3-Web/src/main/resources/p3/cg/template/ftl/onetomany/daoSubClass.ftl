package ${daoPackage};

import java.util.List;
import org.jeecgframework.p3.core.utils.common.PageQuery;
import org.jeecgframework.p3.core.utils.persistence.GenericDao;
import ${domainPackage}.${className}Entity;

/**
 * 描述：${codeName}
 * @author：${author}
 * @since：${nowDate}
 * @version:1.0
 */
public interface ${className}Dao extends GenericDao<${className}Entity>{

    public Integer count(PageQuery<${className}Entity> pageQuery);
	
	public List<${className}Entity> queryPageList(PageQuery<${className}Entity> pageQuery,Integer itemCount);
	
	public List<${className}Entity> getBy${foreignKeyUpper}(String ${foreignKey});

	public void delBy${foreignKeyUpper}(String ${foreignKey});

	public int getCountBy${foreignKeyUpper}(String ${foreignKey});

	public void deleteBy${foreignKeyUpper}(String ${foreignKey});
}

