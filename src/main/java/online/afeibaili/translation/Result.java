package online.afeibaili.translation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Result {
    private String errorCode;
    private String query;
    private String isDomainSupport;
    private List<String> translation;
    private Dict dict;
    private Webdict webdict;
    private String l;
    private String tSpeakUrl;
    private String speakUrl;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getIsDomainSupport() {
        return isDomainSupport;
    }

    public void setIsDomainSupport(String isDomainSupport) {
        this.isDomainSupport = isDomainSupport;
    }

    public List<String> getTranslation() {
        return translation;
    }

    public void setTranslation(List<String> translation) {
        this.translation = translation;
    }

    public Dict getDict() {
        return dict;
    }

    public void setDict(Dict dict) {
        this.dict = dict;
    }

    public Webdict getWebdict() {
        return webdict;
    }

    public void setWebdict(Webdict webdict) {
        this.webdict = webdict;
    }

    public String getL() {
        return l;
    }

    public void setL(String l) {
        this.l = l;
    }

    public String gettSpeakUrl() {
        return tSpeakUrl;
    }

    public void settSpeakUrl(String tSpeakUrl) {
        this.tSpeakUrl = tSpeakUrl;
    }

    public String getSpeakUrl() {
        return speakUrl;
    }

    public void setSpeakUrl(String speakUrl) {
        this.speakUrl = speakUrl;
    }

    public static class Dict {
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class Webdict {
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}