package com.langwuyue.orange.zookeeper;

import java.lang.annotation.Annotation;
import java.util.List;

public interface BackgroundEvent {
	
	/**
     * check here first - this value determines the type of event and which methods will have
     * valid values
     *
     * @return event type
     */
    public Class<? extends Annotation> getType();

    /**
     * @return "rc" from async callbacks
     */
    public int getResultCode();

    /**
     * @return the path
     */
    public String getPath();

    /**
     * @return the context object passed to {@link Backgroundable#inBackground(Object)}
     */
    public Object getContext();

    /**
     * @return any stat
     */
    public Stat getStat();

    /**
     * @return any data
     * @throws Exception 
     */
    public Object getData() throws Exception;

    /**
     * @return any name
     */
    public String getName();

    /**
     * @return any children
     */
    public List<String> getChildren();

    /**
     * @return any ACL list or null
     */
    public List<ACL> getACLList();

    /**
     * @return any operation results or null
     */
    public List<TransactionResult> getOpResults();

    /**
     * If {@link #getType()} returns {@link CuratorEventType#WATCHED} this will
     * return the WatchedEvent
     *
     * @return any WatchedEvent
     */
    public WatchedEvent getWatchedEvent();

}
