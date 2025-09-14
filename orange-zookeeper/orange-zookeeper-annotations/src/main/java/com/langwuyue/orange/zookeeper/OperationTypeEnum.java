package com.langwuyue.orange.zookeeper;


public enum OperationTypeEnum {

	/**
     * {@link TransactionOp#create()}
     */
    CREATE(true),

    /**
     * {@link TransactionOp#delete()}
     */
    DELETE(true),

    /**
     * {@link TransactionOp#setData()}
     */
    SET_DATA(true),

    /**
     * {@link TransactionOp#check()}
     */
    CHECK(false),
    ;
	
	private boolean write;

	private OperationTypeEnum(boolean write) {
		this.write = write;
	}

	public boolean isWrite() {
		return write;
	}
}
