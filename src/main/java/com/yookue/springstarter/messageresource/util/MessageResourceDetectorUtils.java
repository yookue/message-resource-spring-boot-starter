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

package com.yookue.springstarter.messageresource.util;


import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import jakarta.annotation.Nullable;
import org.springframework.util.CollectionUtils;
import com.yookue.commonplexus.javaseutil.constant.CharVariantConst;
import com.yookue.commonplexus.javaseutil.constant.StringVariantConst;
import com.yookue.commonplexus.javaseutil.util.ArrayUtilsWraps;
import com.yookue.commonplexus.javaseutil.util.CollectionPlainWraps;
import com.yookue.commonplexus.javaseutil.util.RegexUtilsWraps;
import com.yookue.commonplexus.javaseutil.util.StringUtilsWraps;
import com.yookue.commonplexus.springutil.util.ClassUtilsWraps;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;


/**
 * Utilities for detecting {@link org.springframework.context.MessageSource} bundles
 *
 * @author David Hsing
 * @reference "https://stackoverflow.com/questions/44772900/message-localisation-in-spring-boot"
 * @see "org.springframework.security.core.SpringSecurityMessageSource"
 * @see "java.util.spi.AbstractResourceBundleProvider"
 */
@SuppressWarnings({"unused", "JavadocDeclaration", "JavadocLinkAsPlainText"})
public abstract class MessageResourceDetectorUtils {
    private static final String HIBERNATE_VALIDATOR = "org.hibernate.validator.HibernateValidator";    // $NON-NLS-1$
    private static final String HIBERNATE_MESSAGES = "org.hibernate.validator.ValidationMessages";    // $NON-NLS-1$
    private static final String SECURITY_VERSION = "org.springframework.security.core.SpringSecurityCoreVersion";    // $NON-NLS-1$
    private static final String SECURITY_MESSAGES = "org.springframework.security.messages";    // $NON-NLS-1$
    private static final String FILE_PATTERN_2 = "[_\\.]{1}[a-z]{2}\\." + StringVariantConst.PROPERTIES;    // $NON-NLS-1$
    private static final String FILE_PATTERN_4 = "[_\\.]{1}[a-z]{2}[_\\-]{1}[A-Z]{2}\\." + StringVariantConst.PROPERTIES;    // $NON-NLS-1$
    private static final String FILE_PATTERN_6 = "[_\\.]{1}[a-z]{2}[_\\-]{1}[A-Z]{4}\\." + StringVariantConst.PROPERTIES;    // $NON-NLS-1$
    private static final String FILE_PATTERN_7 = "[_\\.]{1}[a-z]{3}[_\\-]{1}[A-Z]{4}\\." + StringVariantConst.PROPERTIES;    // $NON-NLS-1$
    private static final String FILE_PATTERN_8 = "[_\\.]{1}[a-z]{2}[_\\-]{1}[A-Z]{2}[_\\-]{1}[A-Z]{4}\\." + StringVariantConst.PROPERTIES;    // $NON-NLS-1$

    @Nullable
    public static Set<String> detectBaseNames(boolean addInternal, boolean scanRecursive, @Nullable String... scanResources) {
        return detectBaseNames(addInternal, scanRecursive, ArrayUtilsWraps.asList(scanResources));
    }

    @Nullable
    public static Set<String> detectBaseNames(boolean addInternal, boolean scanRecursive, @Nullable List<String> scanResources) {
        if (!addInternal && CollectionUtils.isEmpty(scanResources)) {
            return null;
        }
        Set<String> result = new LinkedHashSet<>();
        if (addInternal && ClassUtilsWraps.isPresent(HIBERNATE_VALIDATOR)) {
            result.add(HIBERNATE_MESSAGES);
        }
        if (addInternal && ClassUtilsWraps.isPresent(SECURITY_VERSION)) {
            result.add(SECURITY_MESSAGES);
        }
        if (!CollectionUtils.isEmpty(scanResources)) {
            String[] paths = org.springframework.util.StringUtils.toStringArray(scanResources);
            ClassGraph graph = new ClassGraph();
            graph = scanRecursive ? graph.acceptPaths(paths) : graph.acceptPathsNonRecursive(paths);
            try (ScanResult scanResult = graph.scan()) {
                List<String> founds = scanResult.getResourcesWithExtension(StringVariantConst.PROPERTIES).getPaths();
                if (!CollectionUtils.isEmpty(result)) {
                    for (String found : founds) {
                        found = RegexUtilsWraps.removeAll(found, FILE_PATTERN_8, FILE_PATTERN_7, FILE_PATTERN_6, FILE_PATTERN_4, FILE_PATTERN_2);
                        found = StringUtilsWraps.substringBeforeLast(found, CharVariantConst.DOT);
                        CollectionPlainWraps.addIfNotBlank(result, found);
                    }
                }
            }
        }
        return CollectionUtils.isEmpty(result) ? null : result;
    }
}
