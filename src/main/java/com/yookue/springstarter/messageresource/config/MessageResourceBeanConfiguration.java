/*
 * Copyright (c) 2016 Yookue Ltd. All rights reserved.
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

package com.yookue.springstarter.messageresource.config;


import javax.annotation.Nonnull;
import javax.validation.Validator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import com.yookue.commonplexus.springutil.message.BeanValidationMessenger;
import com.yookue.commonplexus.springutil.message.FormValidationMessenger;
import com.yookue.commonplexus.springutil.message.RestResponseMessenger;


/**
 * Configuration for extra message resource messengers
 *
 * @author David Hsing
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = MessageResourceAutoConfiguration.PROPERTIES_PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
@AutoConfigureAfter(value = {MessageSourceAutoConfiguration.class, ValidationAutoConfiguration.class, MessageSourceAutoConfiguration.class})
public class MessageResourceBeanConfiguration {
    @Bean
    @ConditionalOnClass(value = Validator.class)
    @ConditionalOnProperty(prefix = MessageResourceAutoConfiguration.PROPERTIES_PREFIX + ".extra-messenger", name = "bean-validation", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean
    public BeanValidationMessenger beanValidationMessenger(@Nonnull @Qualifier(value = MessageResourceAutoConfiguration.MESSAGE_SOURCE) MessageSource source, @Nonnull @Lazy Validator validator) {
        return new BeanValidationMessenger(source, validator);
    }

    @Bean
    @ConditionalOnProperty(prefix = MessageResourceAutoConfiguration.PROPERTIES_PREFIX + ".extra-messenger", name = "form-validation", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean
    public FormValidationMessenger formValidationMessenger(@Nonnull @Qualifier(value = MessageResourceAutoConfiguration.MESSAGE_SOURCE) MessageSource source) {
        return new FormValidationMessenger(source);
    }

    @Bean
    @ConditionalOnProperty(prefix = MessageResourceAutoConfiguration.PROPERTIES_PREFIX + ".extra-messenger", name = "rest-response", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean
    public RestResponseMessenger restResponseMessenger(@Nonnull @Qualifier(value = MessageResourceAutoConfiguration.MESSAGE_SOURCE) MessageSource source, @Nonnull @Lazy Validator validator) {
        return new RestResponseMessenger(source, validator);
    }
}
