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
package com.langwuyue.orange.redis.timer;

import java.util.function.Consumer;

import com.langwuyue.orange.redis.logger.OrangeRedisLogger;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeTaskLink {
        
	private OrangeRenewTask head;
	
    private OrangeRenewTask tail;
    
    private OrangeRedisLogger logger;
    
    public OrangeTaskLink(OrangeRedisLogger logger) {
		super();
		this.logger = logger;
	}

	public void add(OrangeRenewTask task) {
        assert task.getLink() == null;
        task.setLink(this);
        if (head == null) {
            head = tail = task;
        } else {
            tail.setNext(task);
            task.setPrev(tail);
            tail = task;
        }
    }

    public void expire(Consumer<OrangeRenewTask> consumer) throws Exception {
        OrangeRenewTask task = head;
        while (task != null) {
            OrangeRenewTask next = task.getNext();
            if (task.getRound() <= 0) {
                remove(task);
                if(task.finish()) {
                	consumer.accept(task);
                }
            } else if (task.isRemove()) {
                remove(task);
            } else {
                task.setRound(task.getRound() - 1);
            }
            task = next;
        }
    }

    public OrangeRenewTask remove(OrangeRenewTask task) {
    	this.logger.debug("Removing {}", task);
        OrangeRenewTask next = task.getNext();
        OrangeRenewTask prev = task.getPrev();
        if (task.getPrev() != null) {
            task.getPrev().setNext(next);
        }
        if (task.getNext() != null) {
            task.getNext().setPrev(prev);
        }

        if (task == head) {
            if (task == tail) {
                tail = null;
                head = null;
            } else {
                head = next;
            }
        } else if (task == tail) {
            tail = prev;
        }
        task.setPrev(null);
        task.setNext(null);
        task.setLink(null);
        return next;
    }

}
