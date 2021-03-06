package com.ontometrics.integrations.configuration;

import com.ontometrics.integrations.events.Issue;
import org.slf4j.Logger;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by rob on 8/19/14.
 * Copyright (c) ontometrics, 2014 All Rights Reserved
 */
public class YouTrackInstance implements IssueTracker {

    private Logger log = getLogger(YouTrackInstance.class);
    private final String baseUrl;
    private final String externalBaseUrl;
    private final String issueBase;

    public YouTrackInstance(Builder builder) {
        baseUrl = builder.baseUrl;
        externalBaseUrl = builder.externalBaseUrl;
        issueBase = getBaseUrl() + "/rest/issue/%s";
    }


    @Override
    public URL getIssueUrl(String issueIdentifier) {
        URL url;
        try {
            url = new URL(String.format("%s/issue/%s", getBaseUrl(), issueIdentifier));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return url;
    }

    @Override
    public URL getExternalIssueUrl(String issueIdentifier) {
        URL url;
        try {
            url = new URL(String.format("%s/issue/%s", getExternalBaseUrl(), issueIdentifier));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return url;
    }

    public String getIssueRestUrl(Issue issue) {
        return String.format(issueBase, issue.getPrefix() + "-" + issue.getId());
    }

    public static class Builder {

        private String baseUrl;
        private String externalBaseUrl;

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder externalBaseUrl(String externalBaseUrl) {
            this.externalBaseUrl = externalBaseUrl;
            return this;
        }

        public YouTrackInstance build(){
            return new YouTrackInstance(this);
            }
    }

    @Override
    public URL getBaseUrl() {
        URL url;
        try {
            url = new URL(baseUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return url;
    }

    @Override
    public URL getExternalBaseUrl() {
        URL url;
        try {
            url = new URL(externalBaseUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return url;
    }

    @Override
    public URL getFeedUrl(String project, Date sinceDate) {
        URL url;
        try {
            url = new URL(String.format("%s/rest/issue/byproject/%s?updatedAfter=%s",
                    getBaseUrl(), project, Long.toString(sinceDate.getTime())));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return url;
    }

    @Override
    public URL getChangesUrl(Issue issue){
        URL url;
        try {
            url = new URL(String.format("%s/rest/issue/%s/changes", getBaseUrl(), issue.getPrefix() + "-" + issue.getId()));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return url;
    }

    @Override
    public URL getAttachmentsUrl(Issue issue) {
        return buildIssueURL(issue, "%s/attachment");
    }



    private URL buildIssueURL(Issue issue, String urlTemplate) {
        URL url = null;
        try {
            String base = getIssueRestUrl(issue);
            url = new URL(urlTemplate.replace("%s", base));
        } catch (MalformedURLException e) {
            log.error("Error building issue URL", e);
        }
        return url;
    }

}
