package cash.super_.platform.service.parkinglot.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity(name = "marketplace")
public class Marketplace {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String name = "";

    private String url = "";

    @NotNull
    private String codeName = "";

    @NotNull
    private Double appVersion = 1.0;

    private String thumbnail = "";

    public Marketplace() {}

    public Marketplace(Long id, String name, String url, String codeName) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.codeName = codeName;
    }

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