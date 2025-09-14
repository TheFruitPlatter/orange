package com.langwuyue.orange.zookeeper.enums;

import java.util.EnumMap;
import java.util.Map;

import org.apache.curator.framework.api.transaction.OperationType;
import org.apache.curator.framework.api.transaction.TransactionOp;

import com.langwuyue.orange.zookeeper.OperationTypeEnum;

public enum OrangeOperationTypeEnum {
	
	/**
     * {@link TransactionOp#create()}
     */
    CREATE(OperationType.DELETE,OperationTypeEnum.CREATE),

    /**
     * {@link TransactionOp#delete()}
     */
    DELETE(OperationType.DELETE,OperationTypeEnum.DELETE),

    /**
     * {@link TransactionOp#setData()}
     */
    SET_DATA(OperationType.SET_DATA,OperationTypeEnum.SET_DATA),

    /**
     * {@link TransactionOp#check()}
     */
    CHECK(OperationType.CHECK,OperationTypeEnum.CHECK)
	;
	
	private OperationType operationType;
	
	private OperationTypeEnum orangeOperationType;
	
	private static Map<OperationType, OperationTypeEnum> MAP = new EnumMap<>(OperationType.class);
	
	static {
		for(OrangeOperationTypeEnum zNodeType2CreateModeEnum : OrangeOperationTypeEnum.values()) {
			MAP.put(zNodeType2CreateModeEnum.operationType, zNodeType2CreateModeEnum.orangeOperationType);
		}
	}

	
	private OrangeOperationTypeEnum(OperationType operationType, OperationTypeEnum orangeOperationType) {
		this.operationType = operationType;
		this.orangeOperationType = orangeOperationType;
	}


	public OperationType getOperationType() {
		return operationType;
	}


	public OperationTypeEnum getOrangeOperationType() {
		return orangeOperationType;
	}


	public static OperationTypeEnum getBy(OperationType type) {
		return MAP.get(type);
	}
}
