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
 * A doubly-linked list implementation for managing OrangeRenewTask objects.
 * 
 * This class provides operations for adding tasks to the list, removing tasks,
 * and processing expired tasks. It maintains head and tail pointers for efficient
 * operations at both ends of the list.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeTaskLink {
        
	/**
	 * The first task in the linked list.
	 */
	private OrangeRenewTask head;
	
	/**
	 * The last task in the linked list.
	 */
    private OrangeRenewTask tail;
    
    /**
     * Logger for recording operations on the task link.
     */
    private OrangeRedisLogger logger;
    
    /**
     * Constructs a new empty task link.
     * 
     * @param logger The logger to use for recording operations on the task link
     */
    public OrangeTaskLink(OrangeRedisLogger logger) {
		super();
		this.logger = logger;
	}

    /**
     * Adds a task to the end of the linked list.
     * 
     * If the list is empty, the task becomes both the head and tail.
     * Otherwise, the task is appended to the end of the list and becomes the new tail.
     * 
     * @param task The task to add to the linked list
     */
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

    /**
     * Processes all tasks in the linked list, handling expired tasks and updating round counts.
     * 
     * This method iterates through the entire linked list from head to tail.
     * For each task:
     * - If the round count is 0 or less, the task is removed and processed by the consumer
     * - If the task is marked for removal, it is removed without processing
     * - Otherwise, the task's round count is decremented by 1
     * 
     * @param consumer The function to apply to each expired task
     * @throws Exception If an error occurs during task processing
     */
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

    /**
     * Removes a task from the linked list and updates the head and tail pointers as needed.
     * 
     * This method handles all cases of removal:
     * - Removing the head task
     * - Removing the tail task
     * - Removing the only task in the list
     * - Removing a task from the middle of the list
     * 
     * After removal, the task's links are cleared (prev, next, and link references set to null).
     * 
     * @param task The task to remove from the linked list
     * @return The next task in the list after the removed task, or null if there is no next task
     */
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