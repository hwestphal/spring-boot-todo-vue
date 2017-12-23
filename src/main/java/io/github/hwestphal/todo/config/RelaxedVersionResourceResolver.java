package io.github.hwestphal.todo.config;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.ResourceResolverChain;
import org.springframework.web.servlet.resource.VersionResourceResolver;
import org.springframework.web.servlet.resource.VersionStrategy;

/**
 * A version resource resolver which is able to deal with non-existing resources (needed for RequireJS).
 */
class RelaxedVersionResourceResolver extends VersionResourceResolver {

    @Override
    protected String resolveUrlPathInternal(
            String resourceUrlPath,
            List<? extends Resource> locations,
            ResourceResolverChain chain) {

        VersionStrategy versionStrategy = getStrategyForPath(resourceUrlPath);
        if (versionStrategy == null) {
            return null;
        }

        String baseUrl = chain.resolveUrlPath(resourceUrlPath, locations);
        if (baseUrl == null) {
            baseUrl = resourceUrlPath;
        }

        Resource resource = chain.resolveResource(null, baseUrl, locations);
        String version = versionStrategy.getResourceVersion(resource);
        return versionStrategy.addVersion(baseUrl, version);
    }

    /**
     * Prepend fixed version to all path pattern.
     */
    @Override
    public VersionResourceResolver addFixedVersionStrategy(String version, String... pathPatterns) {
        String[] extendedPathPatterns = new String[pathPatterns.length * 2];
        for (int i = 0; i < pathPatterns.length; i++) {
            extendedPathPatterns[i * 2] = pathPatterns[i];
            extendedPathPatterns[i * 2 + 1] = "/" + version + "/" + pathPatterns[i];
        }
        return super.addFixedVersionStrategy(version, extendedPathPatterns);
    }

}
