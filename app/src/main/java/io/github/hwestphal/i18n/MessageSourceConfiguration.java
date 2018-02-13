package io.github.hwestphal.i18n;

import java.nio.charset.Charset;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "spring.messages")
public class MessageSourceConfiguration {

    private String basename = "messages";
    private Charset encoding = Charset.forName("UTF-8");
    private int cacheSeconds = -1;
    private boolean fallbackToSystemLocale = true;
    private boolean alwaysUseMessageFormat = false;

    public void setBasename(String basename) {
        this.basename = basename;
    }

    public void setEncoding(Charset encoding) {
        this.encoding = encoding;
    }

    public void setCacheSeconds(int cacheSeconds) {
        this.cacheSeconds = cacheSeconds;
    }

    public void setFallbackToSystemLocale(boolean fallbackToSystemLocale) {
        this.fallbackToSystemLocale = fallbackToSystemLocale;
    }

    public void setAlwaysUseMessageFormat(boolean alwaysUseMessageFormat) {
        this.alwaysUseMessageFormat = alwaysUseMessageFormat;
    }

    @Bean
    MessageSource messageSource() {
        FrontendMessageSource messageSource = new FrontendMessageSource();
        if (StringUtils.hasText(this.basename)) {
            messageSource
                    .setBasenames(StringUtils.commaDelimitedListToStringArray(StringUtils.trimAllWhitespace(this.basename)));
        }
        if (this.encoding != null) {
            messageSource.setDefaultEncoding(this.encoding.name());
        }
        messageSource.setFallbackToSystemLocale(this.fallbackToSystemLocale);
        messageSource.setCacheSeconds(this.cacheSeconds);
        messageSource.setAlwaysUseMessageFormat(this.alwaysUseMessageFormat);
        return messageSource;
    }

}
