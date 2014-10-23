package com.thoughtworks.mingle.api;

import com.dephillipsdesign.logomatic.LogOMatic;
import com.dephillipsdesign.logomatic.Logger;
import com.google.common.base.Joiner;
import com.thoughtworks.mingle.api.hmac.HmacAuth;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MingleInstance {
    private static final String REGEX = "mingle://([^/]+)/identity/(\\w+)\\?key=(.*)";
    private static final Pattern URL_PATTERN = Pattern.compile(REGEX);

    private static final Logger log = LogOMatic.getLogger(MingleInstance.class);
    private final HmacAuth auth;

    private final String projectIdentifier;
    private final String baseUrl;

    public MingleInstance(String baseUrl, String projectIdentifier, String login, String hmacKey) {
        this.baseUrl = baseUrl;
        this.projectIdentifier = projectIdentifier;
        this.auth = new HmacAuth(login, hmacKey);
    }

    public static MingleInstance at(String webUrl) {
        Matcher matcher = URL_PATTERN.matcher(webUrl);
        if (matcher.matches()) {
            MatchResult matchResult = matcher.toMatchResult();

            if (matchResult.groupCount() == 3) {
                String baseUrl = matchResult.group(1);
                String login = matchResult.group(2);
                String hmacKey = matchResult.group(3);
                String projectIdentifier = "android";
                return new MingleInstance(baseUrl, projectIdentifier, login, hmacKey);
            }
        }


       throw new RuntimeException("invalid mingle defined by " + webUrl);

    }

    public HmacAuth getAuth() {
        return this.auth;
    }

    public String getMurmursUrl() {
        String baseApiUrl = "https://" + baseUrl;
        return Joiner.on('/').join(baseApiUrl, "api", "v2", "projects", projectIdentifier, "murmurs.xml");
    }

    public String getAvatarImageUrl(String imageName) {
        String baseWebUrl = "https://" + baseUrl;
        return Joiner.on('/').join(baseWebUrl, "images", "avatars", imageName);
    }

}
