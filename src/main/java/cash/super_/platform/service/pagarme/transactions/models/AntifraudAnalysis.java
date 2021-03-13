package cash.super_.platform.service.pagarme.transactions.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AntifraudAnalysis {

    @JsonIgnore
    private Long antifraudAnalysisId;

    private Long id;

    private String name;

    private Integer score;

    private Integer cost;

    private String status;

    @JsonProperty(value = "date_created")
    private Date dateCreated;

    @JsonProperty(value = "date_updated")
    private Date dateUpdated;

    public Long getAntifraudAnalysisId() {
        return antifraudAnalysisId;
    }

    public void setAntifraudAnalysisId(Long antifraudAnalysisId) {
        this.antifraudAnalysisId = antifraudAnalysisId;
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

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }
}
