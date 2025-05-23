/*
 * Copyright (c) 2025 Liang.Zhong. All rights reserved.
 *
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.langwuyue.orange.example.redis.testcase.transaction;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.langwuyue.orange.example.redis.api.transaction.OrangeRedisTransactionExample1Api;
import com.langwuyue.orange.example.redis.entity.OrangeValueExampleEntity;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
@Service
public class OrangeTransactionService {
	
	private JdbcTemplate jdbcTemplate;
	
	private OrangeRedisTransactionExample1Api example1Api;
	
    public OrangeTransactionService(JdbcTemplate jdbcTemplate,OrangeRedisTransactionExample1Api example1Api) {
        this.jdbcTemplate = jdbcTemplate;
        this.example1Api = example1Api;
    }
    
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
    public void addUserWithTransaction(OrangeValueExampleEntity entity) {
    	// CAS (Compare-And-Swap) cannot be used for inserts in a database transaction.
    	// To ensure uniqueness of the insert operation, we must use a distributed lock first.
    	Long version = example1Api.setValue(entity);
        jdbcTemplate.update("INSERT INTO users (id,name,version) VALUES (?,?,?)", entity.getId(), entity.getText(),version);
        // Redis transaction commits after the database transaction is confirmed successful.
    }
    
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
    public void updateUserWithTransaction(OrangeValueExampleEntity entity) {
    	Long version = example1Api.setValue(entity);
    	int count = jdbcTemplate.update("update users set version = ? , name = ? where id = ? and version < ?", version, entity.getText(),entity.getId(),version);
    	if(count <= 0) {
    		throw new RuntimeException("Update failed!");
    	}
    	// Redis transaction commits after the database transaction is confirmed successful.
    }
}
