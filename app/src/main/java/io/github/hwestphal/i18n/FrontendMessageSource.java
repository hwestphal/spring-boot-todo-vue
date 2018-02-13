package io.github.hwestphal.i18n;

import java.util.Properties;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.ResourceLoader;

/**
 * Expose the current locale and all messages as properties.
 */
class FrontendMessageSource extends ReloadableResourceBundleMessageSource {

    private int cacheSeconds;

    @Override
    public void setCacheSeconds(int cacheSeconds) {
        super.setCacheSeconds(cacheSeconds);
        this.cacheSeconds = cacheSeconds;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        // ignore setting of application context specific resource loader to retain compatibility with
        // ResourceBundleMessageSource
    }

    /**
     * Return current locale as IETF BCP 47 code.
     */
    public String getLocale() {
        return LocaleContextHolder.getLocale().toLanguageTag();
    }

    public Properties getMessages() {
        if (cacheSeconds >= 0) {
            clearCacheIncludingAncestors();
        }
        return getMergedProperties(LocaleContextHolder.getLocale()).getProperties();
    }

}
