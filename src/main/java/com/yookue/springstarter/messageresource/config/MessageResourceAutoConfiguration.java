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


import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractResourceBasedMessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;
import com.yookue.commonplexus.javaseutil.constant.CharVariantConst;
import com.yookue.commonplexus.javaseutil.util.CollectionPlainWraps;
import com.yookue.commonplexus.javaseutil.util.DurationUtilsWraps;
import com.yookue.commonplexus.javaseutil.util.StringUtilsWraps;
import com.yookue.commonplexus.springutil.constant.SpringPropertyConst;
import com.yookue.commonplexus.springutil.processor.MessageSourceAccessorProcessor;
import com.yookue.commonplexus.springutil.util.ResourceUtilsWraps;
import com.yookue.springstarter.messageresource.processor.MessageResourcePriorityProcessor;
import com.yookue.springstarter.messageresource.property.MessageResourceProperties;
import com.yookue.springstarter.messageresource.util.MessageResourceDetectorUtils;


/**
 * Configuration for message resource
 *
 * @author David Hsing
 * @see org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = MessageResourceAutoConfiguration.PROPERTIES_PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
@AutoConfigureBefore(value = MessageSourceAutoConfiguration.class)
@AutoConfigureOrder(value = Ordered.HIGHEST_PRECEDENCE)
@EnableConfigurationProperties(value = MessageResourceProperties.class)
public class MessageResourceAutoConfiguration {
    public static final String PROPERTIES_PREFIX = "spring.message-resource";    // $NON-NLS-1$
    public static final String MESSAGE_SOURCE = "detectiveResourceMessageSource";    // $NON-NLS-1$

    @Bean(name = MESSAGE_SOURCE)
    @ConditionalOnMissingBean(name = MESSAGE_SOURCE)
    public MessageSource messageSource(@Nonnull Environment environment, @Nonnull MessageResourceProperties properties) {
        Set<String> existsNames = null;
        Set<String> configNames = StringUtilsWraps.splitByToSet(environment.getProperty(SpringPropertyConst.MESSAGES_BASENAME), CharVariantConst.COMMA, true);
        if (!CollectionUtils.isEmpty(configNames)) {
            existsNames = configNames.stream().filter(StringUtils::isNotBlank).map(element -> element.replace(CharVariantConst.DOT, CharVariantConst.SLASH)).filter(ResourceUtilsWraps::existsClassPathResource).collect(Collectors.toSet());
        }
        MessageResourceProperties.MessageBundle props = properties.getMessageBundle();
        Set<String> bundleNames = MessageResourceDetectorUtils.detectBaseNames(BooleanUtils.isTrue(props.getAddInternalBundles()), BooleanUtils.isTrue(props.getScanRecursive()), props.getScanResources());
        Set<String> unionNames = CollectionPlainWraps.newLinkedHashSetWithinAll(existsNames, bundleNames);
        if (CollectionUtils.isEmpty(unionNames)) {
            return null;
        }
        AbstractResourceBasedMessageSource result = BooleanUtils.isTrue(props.getReloadable()) ?  new ReloadableResourceBundleMessageSource() : new ResourceBundleMessageSource();
        result.setBasenames(unionNames.toArray(ArrayUtils.EMPTY_STRING_ARRAY));
        Optional.ofNullable(props.getDefaultEncoding()).ifPresent(element -> result.setDefaultEncoding(element.name()));
        Optional.ofNullable(properties.getDefaultLocale()).ifPresent(result::setDefaultLocale);
        DurationUtilsWraps.ifPositive(props.getCacheDuration(), element -> result.setCacheMillis(element.toMillis()));
        result.setFallbackToSystemLocale(BooleanUtils.isTrue(props.getFallbackToSystemLocale()));
        result.setAlwaysUseMessageFormat(BooleanUtils.isTrue(props.getAlwaysUseMessageFormat()));
        result.setUseCodeAsDefaultMessage(BooleanUtils.isTrue(props.getUseCodeAsDefaultMessage()));
        return result;
    }

    @Bean
    @ConditionalOnProperty(prefix = PROPERTIES_PREFIX + ".message-resource", name = "primary-message-resource", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean
    public MessageResourcePriorityProcessor messageSourcePriorityProcessor(@Nonnull MessageResourceProperties properties) {
        MessageResourcePriorityProcessor result = new MessageResourcePriorityProcessor();
        Optional.ofNullable(properties.getMessageBundle().getProcessorOrder()).ifPresent(result::setOrder);
        return result;
    }

    @Bean
    @ConditionalOnProperty(prefix = PROPERTIES_PREFIX + ".accessor-aware", name = "enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean
    public MessageSourceAccessorProcessor messageSourceAccessorProcessor(@Nonnull ConfigurableApplicationContext context, @Nonnull MessageResourceProperties properties) {
        MessageSourceAccessorProcessor result = new MessageSourceAccessorProcessor(context, properties.getDefaultLocale());
        Optional.ofNullable(properties.getAccessorAware().getProcessorOrder()).ifPresent(result::setOrder);
        return result;
    }
}
