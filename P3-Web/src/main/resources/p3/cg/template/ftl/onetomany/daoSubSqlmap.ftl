<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//ibatis.apache.org//DTD iBatis Mapper 3.0 //EN" 
	"http://ibatis.apache.org/dtd/ibatis-3-mapper.dtd">

<mapper namespace="${domainPackage}.${className}Entity">

	<!-- Result Map-->
	<resultMap id="${className}Entity" type="${domainPackage}.${className}Entity" >
	<#list columnDatas as item>
		<result column="${item.columnName}" property="${item.domainPropertyName}" jdbcType="${item.jdbcType?upper_case}"/>
	</#list>
	</resultMap>
	
	<!-- 查询条件 -->
	<sql id="wherecontation">
		<trim  suffixOverrides="," >
		   <#list columnDatas as item>
				<#if item.columnKey != 'PRI'>
				 <if test="query.${item.domainPropertyName} != null and query.${item.domainPropertyName} != ''" >
	  		 		/* ${item.columnComment} */
			    	AND ${tablesAsName}.${item.columnName} =  ${"#"}{query.${item.domainPropertyName},jdbcType=${item.jdbcType?upper_case}}
				 </if>
				</#if>
			</#list>
		</trim>
	</sql>

	<!--
	方法名称: insert
	调用路径: ${domainPackage}.${className}Entity.insert
	开发信息: 
	处理信息: 保存信息
	-->
	<insert id="insert" parameterType="Object" >
	  INSERT  INTO  ${tableName}   /* ${codeName} */  
					(	
					<#list columnDatas as item>
						<#assign x="${item.columnName?length}" /> 
						<#if item_index==0>
${"                      "}${item.columnName}${"                              "?substring(item.columnName?length)}/* ${item.columnComment} */ 
						<#elseif item_index gt 0>
${"                     "},${item.columnName}${"                              "?substring(item.columnName?length)}/* ${item.columnComment} */ 
						</#if>
					</#list> 		
					)
			values (
					<#list columnDatas as item>
						<#if item_index==0>
${"                      "}${"#"}{${item.domainPropertyName},jdbcType=${item.jdbcType?upper_case}}${"                              "?substring(item.domainPropertyName?length)}/* ${item.columnComment} */ 
						<#elseif item_index gt 0>
${"                     "},${"#"}{${item.domainPropertyName},jdbcType=${item.jdbcType?upper_case}}${"                              "?substring(item.domainPropertyName?length)}/* ${item.columnComment} */ 
						</#if>
					</#list>
					)
	</insert>

	
	<!--
	方法名称: update
	调用路径: ${domainPackage}.${className}Entity.update
	开发信息: 
	处理信息: 修改信息
	-->  
	 <update id="update" parameterType="Object" >
	  UPDATE   ${tableName}  	/* ${codeName} */
	  			<trim   prefix="SET" suffixOverrides="," >
	  				<#list columnDatas as item>
						<#if item.columnKey !='PRI' >
						 <if test="${item.domainPropertyName} != null">
		    		 		/* ${item.columnComment} */ 
	    		 			${item.columnName} = ${"#"}{${item.domainPropertyName},jdbcType=${item.jdbcType?upper_case}},
						 </if>
						</#if>
					</#list>
	  	  		</trim>
				WHERE
	  	 		 		id = ${"#"+"{id}"}		/* 序号 */ 
	 </update>
	
	<!--
	方法名称: get
	调用路径: ${domainPackage}.${className}Entity.get
	开发信息: 
	处理信息: 根据主键查询记录
	-->
	<select id="get" parameterType="Object"  resultMap="${className}Entity">
		   SELECT   
				  <#list columnDatas as item>
					   <#if item_index==0>
${"                   "}${tablesAsName}.${item.columnName}${"                              "?substring(item.columnName?length)}/* ${item.columnComment} */ 
					   <#else>
${"                  "},${tablesAsName}.${item.columnName}${"                              "?substring(item.columnName?length)}/* ${item.columnComment} */ 
						 </#if>
				   </#list>
		   FROM   ${tableName}      AS ${tablesAsName}      /* ${codeName} */ 
		   WHERE
				id = ${"#"+"{id}"}				/* 序号 */ 
	</select>
	
	<!--
	方法名称: delete
	调用路径: ${domainPackage}.${className}Entity.delete
	开发信息: 
	处理信息: 删除记录
	-->
	<delete id="delete" parameterType="Object">
		DELETE 	FROM ${tableName} 	/* ${codeName} */  
		WHERE 
			id = ${"#"+"{id}"}					/* 序号 */ 
	</delete>
	
	<!--
	方法名称: count
	调用路径: ${domainPackage}.${className}Entity.count
	开发信息: 
	处理信息: 列表总数
	-->
	<select id="count" resultType="java.lang.Integer"  parameterType="Object">
		SELECT count(*)  FROM  ${tableName}      AS ${tablesAsName}      /* ${codeName} */ 
		 WHERE 1=1
		    <include refid="wherecontation"/>
	</select>
  	
  	<!--
	方法名称: queryPageList
	调用路径: ${domainPackage}.${className}Entity.queryPageList
	开发信息: 
	处理信息: 列表
	-->
	<select id="queryPageList" parameterType="Object"  resultMap="${className}Entity">
		    SELECT 
				  <#list columnDatas as item>
					   <#if item_index==0>
${"                   "}${tablesAsName}.${item.columnName}${"                              "?substring(item.columnName?length)}/* ${item.columnComment} */ 
					   <#else>
${"                  "},${tablesAsName}.${item.columnName}${"                              "?substring(item.columnName?length)}/* ${item.columnComment} */ 
						 </#if>
				   </#list> 
		FROM   	 ${tableName}      AS ${tablesAsName}      /* ${codeName} */ 
		WHERE 1=1
		   <include refid="wherecontation"/>
		LIMIT  ${"#"+"{startRow}"}  		/* 开始序号 */ 
			  ,${"#"+"{pageSize}"}		/* 每页显示条数 */ 
	</select>
	
	<!--
	方法名称: getBy${foreignKeyUpper}
	调用路径: ${domainPackage}.${className}Entity.getBy${foreignKeyUpper}
	开发信息: 
	处理信息: 
	-->
	<select id="getBy${foreignKeyUpper}" parameterType="Object"  resultMap="${className}Entity">
		    SELECT 
				  <#list columnDatas as item>
					   <#if item_index==0>
${"                   "}${tablesAsName}.${item.columnName}${"                              "?substring(item.columnName?length)}/* ${item.columnComment} */ 
					   <#else>
${"                  "},${tablesAsName}.${item.columnName}${"                              "?substring(item.columnName?length)}/* ${item.columnComment} */ 
						 </#if>
				   </#list> 
		FROM   	 ${tableName}      AS ${tablesAsName}      /* ${codeName} */ 
		WHERE ${foreignKeyTable} = ${"#"+"{value}"}
	</select>
	
	<!--
	方法名称: delBy${foreignKeyUpper}
	调用路径: ${domainPackage}.${className}Entity.delBy${foreignKeyUpper}
	开发信息: 
	处理信息: 
	-->  
	 <update id="delBy${foreignKeyUpper}" parameterType="Object" >
	 	update ${tableName} set DELFLAG = 1, DEL_DT = now() 
	 	where ${foreignKeyTable} = ${"#"+"{value}"}
	 </update>
	 
	 <!--
	方法名称: getCountBy${foreignKeyUpper}
	调用路径: ${domainPackage}.${className}Entity.getCountBy${foreignKeyUpper}
	开发信息: 
	处理信息: 
	-->
	<select id="getCountBy${foreignKeyUpper}" resultType="java.lang.Integer"  parameterType="Object">
		select count(id) from ${tableName} WHERE ${foreignKeyTable} = ${"#"+"{value}"}
	</select>
	
	<!--
	方法名称: deleteBy${foreignKeyUpper}
	调用路径: ${domainPackage}.${className}Entity.deleteBy${foreignKeyUpper}
	开发信息: 
	处理信息: 
	-->
	<delete id="deleteBy${foreignKeyUpper}" parameterType="Object">
		delete from ${tableName} WHERE ${foreignKeyTable} = ${"#"+"{value}"}
	</delete>
	
</mapper>