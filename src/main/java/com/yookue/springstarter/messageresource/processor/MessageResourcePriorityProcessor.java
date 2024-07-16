/*
 * Copyright (c) 2021 Yookue Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yookue.springstarter.messageresource.processor;


import jakarta.annotation.Nonnull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.Ordered;
import com.yookue.springstarter.messageresource.config.MessageResourceAutoConfiguration;
import lombok.Getter;
import lombok.Setter;


/**
 * {@link org.springframework.beans.factory.config.BeanFactoryPostProcessor} for registering {@link org.springframework.context.MessageSource} bean
 *
 * @author David Hsing
 */
@Getter
@Setter
public class MessageResourcePriorityProcessor implements BeanFactoryPostProcessor, Ordered {
    private int order = 0;

    @Override
    public void postProcessBeanFactory(@Nonnull ConfigurableListableBeanFactory factory) throws BeansException {
        if (factory.containsBean(MessageResourceAutoConfiguration.MESSAGE_SOURCE)) {
            if (!factory.containsBean(AbstractApplicationContext.MESSAGE_SOURCE_BEAN_NAME)) {
                factory.registerAlias(MessageResourceAutoConfiguration.MESSAGE_SOURCE, AbstractApplicationContext.MESSAGE_SOURCE_BEAN_NAME);
            }
            factory.getBeanDefinition(MessageResourceAutoConfiguration.MESSAGE_SOURCE).setPrimary(true);
        }
    }
}
