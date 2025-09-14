package com.langwuyue.orange.zookeeper.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

import com.langwuyue.orange.zookeeper.ZNodeTypeEnum;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ZookeeperZNode {
	
	/**
	 * The path on Zookeeper.
	 * 
	 * @return path
	 */
	String path();
	
	/**
	 * The {@link #zNodeType()} determines how the znode is created on ZooKeeper.
	 * @return ZnodeTypeEnum
	 */
	ZNodeTypeEnum zNodeType();
	
	/**
     * Specify a TTL when {@link #znodeType()} is {@link ZNodeTypeEnum#PERSISTENT_WITH_TTL} or
     * {@link ZNodeTypeEnum#PERSISTENT_SEQUENTIAL_WITH_TTL}. If the znode has not been modified 
     * within the given TTL, it will be deleted once it has no children. The TTL unit is 
     * milliseconds and must be greater than 0 and less than or equal to 12725 days, about 34 years.
     *
     * @return Long TTL
     */
	long ttl() default 0;
	
	/**
	 * Specify time unit of TTL
	 * 
	 * @return TimeUnit TTL unit
	 */
	TimeUnit ttlUnit() default TimeUnit.SECONDS;
}
