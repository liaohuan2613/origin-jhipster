package tech.deepq.origin.service.dto;

import java.time.Instant;
import java.util.UUID;

public class BondDTO {
    private String id;
    private String marketCode;
    private String category;
    private String code;
    private String name;
    private String aliasString;
    private String relateCompany;
    private String status;
    private String createBy;
    private Instant createDate;
    private String lastModifiedBy;
    private Instant lastModifiedDate;

    public BondDTO() {
        this.id = UUID.randomUUID().toString();
        this.marketCode = "*";
        this.category = "STOCK";
        this.status = "ACTIVE";
        this.createBy = "admin";
        this.lastModifiedBy = "admin";
        this.createDate = Instant.now();
        this.lastModifiedDate = Instant.now();
    }

    public BondDTO(String code, String name, String relateCompany) {
        this.code = code;
        this.name = name;
        this.relateCompany = relateCompany;
        this.id = UUID.randomUUID().toString();
        this.marketCode = "*";
        this.category = "STOCK";
        this.status = "ACTIVE";
        this.createBy = "admin";
        this.lastModifiedBy = "admin";
        this.createDate = Instant.now();
        this.lastModifiedDate = Instant.now();
    }

    public String getId() {
        return id;
    }

    public BondDTO setId(String id) {
        this.id = id;
        return this;
    }

    public String getMarketCode() {
        return marketCode;
    }

    public BondDTO setMarketCode(String marketCode) {
        this.marketCode = marketCode;
        return this;
    }

    public String getCategory() {
        return category;
    }

    public BondDTO setCategory(String category) {
        this.category = category;
        return this;
    }

    public String getCode() {
        return code;
    }

    public BondDTO setCode(String code) {
        this.code = code;
        return this;
    }

    public String getName() {
        return name;
    }

    public BondDTO setName(String name) {
        this.name = name;
        return this;
    }

    public String getAliasString() {
        return aliasString;
    }

    public BondDTO setAliasString(String aliasString) {
        this.aliasString = aliasString;
        return this;
    }

    public String getRelateCompany() {
        return relateCompany;
    }

    public BondDTO setRelateCompany(String relateCompany) {
        this.relateCompany = relateCompany;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public BondDTO setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getCreateBy() {
        return createBy;
    }

    public BondDTO setCreateBy(String createBy) {
        this.createBy = createBy;
        return this;
    }

    public Instant getCreateDate() {
        return createDate;
    }

    public BondDTO setCreateDate(Instant createDate) {
        this.createDate = createDate;
        return this;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public BondDTO setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
        return this;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public BondDTO setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
        return this;
    }

    @Override
    public String toString() {
        return "BondDTO{" +
            "id='" + id + '\'' +
            ", marketCode='" + marketCode + '\'' +
            ", category='" + category + '\'' +
            ", code='" + code + '\'' +
            ", name='" + name + '\'' +
            ", aliasString='" + aliasString + '\'' +
            ", relateCompany='" + relateCompany + '\'' +
            ", status='" + status + '\'' +
            '}';
    }
}
