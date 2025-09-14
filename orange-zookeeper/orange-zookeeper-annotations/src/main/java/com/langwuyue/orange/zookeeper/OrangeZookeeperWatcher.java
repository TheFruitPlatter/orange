package com.langwuyue.orange.zookeeper;


public interface OrangeZookeeperWatcher {

	/**
     * Handle event
     *
     * @param event the event
     * @throws Exception any exceptions to log
     */
    public void process(WatchedEvent event) throws Exception;
}
