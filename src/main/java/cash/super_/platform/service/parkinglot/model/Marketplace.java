package cash.super_.platform.service.parkinglot.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "marketplace")
public class Marketplace {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name = "";

    private String url = "";

    private String codeName = "";

    private Double appVersion = 1.0;

    private String thumbnail = "";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCodeName() {
        return codeName;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    public Double getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(Double appVersion) {
        this.appVersion = appVersion;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    @Override
    public String toString() {
        return "Marketplace{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", codeName='" + codeName + '\'' +
                ", appVersion=" + appVersion +
                ", thumbnail='" + thumbnail + '\'' +
                '}';
    }
}