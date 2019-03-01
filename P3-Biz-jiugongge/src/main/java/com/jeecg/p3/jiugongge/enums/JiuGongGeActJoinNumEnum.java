package com.jeecg.p3.jiugongge.enums;


public enum JiuGongGeActJoinNumEnum {
	
	join_num_0("0","不限制"),
	join_num_500("500","参与人数小于500"),
	join_num_1000("1000","参与人数小于1000"),
	join_num_2000("2000","参与人数小于2000"),
	join_num_10000("10000","参与人数小于10000");
	
	/**
	 * 编码
	 */
    private String code;
    
    /**
     * 描述
     */
    private String value;

    private JiuGongGeActJoinNumEnum(String code, String value) {
        this.code = code;
        this.value = value;
    }

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public static String toEnum(String code) {
		for(JiuGongGeActJoinNumEnum item : JiuGongGeActJoinNumEnum.values()) {
			if(item.getCode().equals(code)) {
				return item.value;
			}
		}
		return null;
	}

    public String toString() {
        return "{ code: " + code + ", value: " + value +"}";
    }
}
