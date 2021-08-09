package cash.super_.platform.service.parkinglot.model;

import cash.super_.platform.utils.StringUtil;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity(name = "marketplace")
@Table(indexes = {
        @Index(name = "marketplace_name_idx", columnList = "name", unique = true),
        @Index(name = "marketplace_url_idx", columnList = "url", unique = true),
        @Index(name = "marketplace_codeName_idx", columnList = "codeName", unique = true)
})
public class Marketplace {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String name = "";

    @NotNull
    private String url = "";

    @NotNull
    private String codeName = "";

    @NotNull
    private Double appVersion = 1.0;

    private String thumbnail = "";

    public Marketplace() {}

    public Marketplace(Long id, String name, String url) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.codeName = String.format("cash.super.%s", StringUtil.stripAccents(name.toLowerCase().trim().replaceAll(" ", "")));
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Marketplace that = (Marketplace) o;
        return Objects.equals(codeName, that.codeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codeName);
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