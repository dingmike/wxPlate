package org.codeutil;

import org.jeecgframework.p3.cg.def.FtlDef;
import org.jeecgframework.p3.cg.factory.CodeGenerateFactory;
import org.jeecgframework.p3.core.utils.common.StringUtils;

/**
 * 描述：根据自定义表生成
 * @author：zhoujf
 * @since：
 * @version:1.0
 */
public class CodeToolUtil {

	public static void main(String[] args) {
		 /** 此处修改成你的 表名 和 中文注释***/
         CodeGenerateFactory.codeGenerateByFTL("weixin_subscribe", "关注语",FtlDef.KEY_TYPE_02);

	}
}
