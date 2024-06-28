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

package com.yookue.springstarter.messageresource.property;


import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.core.Ordered;
import com.yookue.commonplexus.javaseutil.constant.StringVariantConst;
import com.yookue.springstarter.messageresource.config.MessageResourceAutoConfiguration;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * Properties for message resource
 *
 * @author David Hsing
 * @see org.springframework.boot.autoconfigure.context.MessageSourceProperties
 */
@ConfigurationProperties(prefix = MessageResourceAutoConfiguration.PROPERTIES_PREFIX)
@Getter
@Setter
@ToString
public class MessageResourceProperties implements Serializable {
    /**
     * Indicates whether to enable this starter or not
     * <p>
     * Default is {@code true}
     */
    private Boolean enabled = true;

    /**
     * Message default locale
     */
    private Locale defaultLocale;

    /**
     * Message source attributes
     */
    private final MessageBundle messageBundle = new MessageBundle();

    /**
     * Message source accessor aware attributes
     */
    private final AccessorAware accessorAware = new AccessorAware();

    /**
     * Extra messenger attributes
     */
    private final ExtraMessenger extraMessenger = new ExtraMessenger();


    /**
     * Properties for message resource
     *
     * @author David Hsing
     */
    @Getter
    @Setter
    @ToString
    public static class MessageBundle implements Serializable {
        /**
         * Indicates whether to enable reloadable message source or not
         *
         * @see org.springframework.context.support.ReloadableResourceBundleMessageSource
         */
        private Boolean reloadable;

        /**
         * Message bundles default encoding
         * <p>
         * Default is {@code UTF-8}
         */
        private Charset defaultEncoding = StandardCharsets.UTF_8;

        /**
         * Loaded resource bundle files cache duration
         * <p>
         * When not set, bundles are cached forever
         * <p>
         * If a duration suffix is not specified, seconds will be used
         */
        @DurationUnit(value = ChronoUnit.SECONDS)
        private Duration cacheDuration;

        /**
         * Whether to fall back to the system locale, if no files for a specific locale
         * <p>
         * If this is turned off, the only fallback will be the default file
         * (e.g. "messages.properties" for basename "messages")
         */
        private Boolean fallbackToSystemLocale;

        /**
         * Whether to always apply the MessageFormat rules, parsing even messages without arguments
         */
        private Boolean alwaysUseMessageFormat;

        /**
         * Whether to use the message code as the default message
         * <p>
         * Instead of throwing a {@link org.springframework.context.NoSuchMessageException}
         * <p>
         * Recommended during development only
         */
        private Boolean useCodeAsDefaultMessage;

        /**
         * Whether to add the spring internal message bundles
         * <p>
         * Such as hibernate and spring security, if present
         * <p>
         * Default is {@code true}
         */
        private Boolean addInternalBundles = true;

        /**
         * Mark this scanned resource bundles as primary message resource bean
         * <p>
         * Default is {@code true}
         */
        private Boolean primaryMessageSource = true;

        /**
         * The priority order for marking primary message resource bean, if {@code primaryMessageSource} is {@code true}
         * <p>
         * Default is {@code Ordered.LOWEST_PRECEDENCE - 1000}
         */
        private Integer processorOrder = Ordered.LOWEST_PRECEDENCE - 1000;

        /**
         * Whether to scan the {@code scanResources} recursive
         * <p>
         * Default is {@code true}
         */
        private Boolean scanRecursive = true;

        /**
         * The resource paths to scan for messages, under classpath
         * <p>
         * Especially the path of message resource bundles base names
         */
        private List<String> scanResources = Collections.singletonList(StringVariantConst.LANG);
    }


    /**
     * Properties for message resource accessor aware
     *
     * @author David Hsing
     * @see com.yookue.commonplexus.springutil.context.MessageSourceAccessorAware
     */
    @Getter
    @Setter
    @ToString
    public static class AccessorAware implements Serializable {
        /**
         * Indicates whether to enable this aware or not
         * <p>
         * Default is {@code true}
         */
        private Boolean enabled = true;

        /**
         * The priority order for processing accessor bean
         * <p>
         * Default is {@code Ordered.LOWEST_PRECEDENCE - 1000}
         */
        private Integer processorOrder = Ordered.LOWEST_PRECEDENCE - 1000;
    }


    /**
     * Properties for extra messenger
     *
     * @author David Hsing
     */
    @Getter
    @Setter
    @ToString
    public static class ExtraMessenger implements Serializable {
        /**
         * Indicates whether to enable {@link com.yookue.commonplexus.springutil.message.BeanValidationMessenger} or not
         * <p>
         * Default is {@code true}
         */
        private Boolean beanValidation = true;

        /**
         * Indicates whether to enable {@link com.yookue.commonplexus.springutil.message.FormValidationMessenger} or not
         * <p>
         * Default is {@code true}
         */
        private Boolean formValidation = true;

        /**
         * Indicates whether to enable {@link com.yookue.commonplexus.springutil.message.RestResponseMessenger} or not
         * <p>
         * Default is {@code true}
         */
        private Boolean restResponse = true;
    }
}
