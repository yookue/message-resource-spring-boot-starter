# Message Resource Spring Boot Starter

Spring Boot application integrates `MessageSource` quickly, to detect resource bundle properties file automatically.

## Quickstart

- Import dependencies

```xml
    <dependency>
        <groupId>com.yookue.springstarter</groupId>
        <artifactId>message-resource-spring-boot-starter</artifactId>
        <version>LATEST</version>
    </dependency>
```

> By default, this starter will auto take effect, you can turn it off by `spring.message-resource.enabled = false`

- Configure Spring Boot `application.yml` with prefix `spring.message-resource` (**Optional**)

```yml
spring:
    message-resource:
        default-locale: en-US
        message-bundle:
            scan-recursive: true
            scan-resources:
                - 'lang'
```
This will create a `MessageSource` bean, by auto scanning the files under the `scan-resources` paths of **classpath**.

- Optional feature: Similar with the Spring `MessageSourceAware` facade, this starter declares a processor with `MessageSourceAccessorAware`, you can implement beans with that, the the Spring will inject a `MessageSourceAccessor`instance, then you can resolve messages. That's an alternative way to `MessageSource`.

## Document

- Github: https://github.com/yookue/message-resource-spring-boot-starter

## Requirement

- jdk 1.8+

## License

This project is under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)

See the `NOTICE.txt` file for required notices and attributions.

## Donation

You like this package? Then [donate to Yookue](https://yookue.com/public/donate) to support the development.

## Website

- Yookue: https://yookue.com
