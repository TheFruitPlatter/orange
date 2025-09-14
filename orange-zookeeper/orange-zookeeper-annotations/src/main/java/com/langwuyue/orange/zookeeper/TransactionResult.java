package com.langwuyue.orange.zookeeper;

public class TransactionResult {

	private final OperationTypeEnum type;
    private final String forPath;
    private final String resultPath;
    private final Stat resultStat;
    private final int error;

    public TransactionResult(OperationTypeEnum type, String forPath, String resultPath, Stat resultStat)
    {
        this(type, forPath, resultPath, resultStat, 0);
    }

    public TransactionResult(OperationTypeEnum type, String forPath, String resultPath, Stat resultStat, int error)
    {
        this.forPath = forPath;
        this.resultPath = resultPath;
        this.resultStat = resultStat;
        this.type = type;
        this.error = error;
    }

    /**
     * Returns the operation type
     *
     * @return operation type
     */
    public OperationTypeEnum getType()
    {
        return type;
    }

    /**
     * Returns the path that was passed to the operation when added
     *
     * @return operation input path
     */
    public String getForPath()
    {
        return forPath;
    }

    /**
     * Returns the operation generated path or <code>null</code>. i.e. {@link CuratorTransaction#create()}
     * using an EPHEMERAL mode generates the created path plus its sequence number.
     *
     * @return generated path or null
     */
    public String getResultPath()
    {
        return resultPath;
    }

    /**
     * Returns the operation generated stat or <code>null</code>. i.e. {@link CuratorTransaction#setData()}
     * generates a stat object.
     *
     * @return generated stat or null
     */
    public Stat getResultStat()
    {
        return resultStat;
    }

    /**
     * Returns the operation generated error or <code>0</code> i.e. {@link OpResult.ErrorResult#getErr()}
     *
     * @return error or 0
     */
    public int getError()
    {
        return error;
    }
}
