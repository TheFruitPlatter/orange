package com.langwuyue.orange.example.zookeeper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.langwuyue.orange.zookeeper.configuration.OrangeZookeeperClientScan;

@OrangeZookeeperClientScan(basePackages= {"com.langwuyue.orange.example.zookeeper.api"})
@SpringBootApplication
public class ZookeeperExampleApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(ZookeeperExampleApplication.class, args);
	}

}
