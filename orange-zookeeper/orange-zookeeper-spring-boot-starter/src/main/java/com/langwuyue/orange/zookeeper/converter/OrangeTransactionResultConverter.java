package com.langwuyue.orange.zookeeper.converter;

import java.util.ArrayList;
import java.util.List;

import org.apache.curator.framework.api.transaction.CuratorTransactionResult;

import com.langwuyue.orange.zookeeper.TransactionResult;
import com.langwuyue.orange.zookeeper.enums.OrangeOperationTypeEnum;

public class OrangeTransactionResultConverter {
	
	private OrangeStatConverter statConverter;
	
	public OrangeTransactionResultConverter(OrangeStatConverter statConverter) {
		this.statConverter = statConverter;
	}

	public List<TransactionResult> convert(List<CuratorTransactionResult> opResults) {
		List<TransactionResult> result = new ArrayList<>();
		for(CuratorTransactionResult opResult : opResults) {
			result.add(convert(opResult));
		}
		return result;
	}

	public TransactionResult convert(CuratorTransactionResult opResult) {
		return new TransactionResult(
			OrangeOperationTypeEnum.getBy(opResult.getType()),
			opResult.getForPath(),
			opResult.getResultPath(),
			this.statConverter.convert(opResult.getResultStat()),
			opResult.getError()
		);
	}

}
