/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.harmony.umbrella.data.repository;

import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.harmony.umbrella.data.persistence.User;

/**
 * @author wuxii@foxmail.com
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findOneByUserId(Long id);

    User findOneByUsername(String username);

    Collection<User> findListByUsername(String username);

    Page<User> findByUsernameAndUserIdOrUsername(Pageable p, String username1, Long userId, String username2);

}
