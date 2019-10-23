package tech.deepq.origin.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import tech.deepq.origin.service.dto.BondDTO;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Component
public class BondScheduleTask {

    private static Logger logger = LoggerFactory.getLogger(BondScheduleTask.class);

    private JdbcTemplate jdbcTemplate;
    private JdbcTemplate secondaryJdbcTemplate;


    public BondScheduleTask(JdbcTemplate jdbcTemplate, @Qualifier("secondaryJdbcTemplate") JdbcTemplate secondaryJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.secondaryJdbcTemplate = secondaryJdbcTemplate;
    }

    @PostConstruct
    @Scheduled(cron = "${sync-tasks.bond-cron}")
    public void syncBondData() {
        Instant thisSyncDate = Instant.now().plus(-2, ChronoUnit.MINUTES);
        String maxSyncDate = "1970-01-01 08:00:00";
        SqlRowSet sqlRowSet = secondaryJdbcTemplate.queryForRowSet("select DATE_FORMAT(max(last_modified_date),'%Y-%m-%d %T') maxSyncDate " +
            " from bond_target");
        try {
            if (sqlRowSet.next()) {
                maxSyncDate = sqlRowSet.getString("maxSyncDate") == null ?
                    "1970-01-01 08:00:00" : sqlRowSet.getString("maxSyncDate");
            }
        } catch (Exception e) {
            logger.error("select max date from bond exist ERROR: ", e);
        }
        Map<String, BondDTO> bondAliasMap = new HashMap<>();
        sqlRowSet = jdbcTemplate.queryForRowSet("select BOND_ID bondId " +
            " from bond where DATE_FORMAT(UPDATE_TIME,'%Y-%m-%d %T') > '" + maxSyncDate + "'");
        while (sqlRowSet.next()) {
            bondAliasMap.putIfAbsent(sqlRowSet.getString("bondId"), new BondDTO());
        }
        sqlRowSet = jdbcTemplate.queryForRowSet("select b.BOND_ID bondId " +
            " from md_security c " +
            " left join bond_ticker b on b.SECURITY_ID = c.SECURITY_ID " +
            " where DATE_FORMAT(b.UPDATE_TIME,'%Y-%m-%d %T') > '" + maxSyncDate + "'");
        while (sqlRowSet.next()) {
            bondAliasMap.putIfAbsent(sqlRowSet.getString("bondId"), new BondDTO());
        }

        sqlRowSet = jdbcTemplate.queryForRowSet("select a.BOND_ID bondId, a.PARTY_ID partyId, c.SEC_SHORT_NAME shortName, c.SEC_FULL_NAME fullName " +
            " from  bond a left join bond_ticker b on a.BOND_ID = b.BOND_ID " +
            " left join md_security c on b.SECURITY_ID = c.SECURITY_ID " +
            " where a.BOND_ID in ('" + String.join("','", bondAliasMap.keySet()) + "') ");
        while (sqlRowSet.next()) {
            BondDTO bondDTO = bondAliasMap.get(sqlRowSet.getString("bondId"));
            if (!StringUtils.hasText(bondDTO.getAliasString())) {
                bondDTO.setCode("BD_" + sqlRowSet.getString("bondId"));
                bondDTO.setRelateCompany("CP_" + sqlRowSet.getString("partyId"));
                bondDTO.setName(sqlRowSet.getString("fullName"));
                bondDTO.setAliasString(sqlRowSet.getString("shortName"));
                bondDTO.setCreateDate(thisSyncDate);
                bondDTO.setLastModifiedDate(thisSyncDate);
            } else {
                bondDTO.setAliasString(bondDTO.getAliasString() + "," + sqlRowSet.getString("shortName"));
            }
        }

        logger.info("bondAliasMap [{}]", bondAliasMap);

        for (BondDTO bondDTO : bondAliasMap.values()) {
            if (checkBond(bondDTO)) {
                updateBond(bondDTO);
            } else {
                insertBond(bondDTO);
            }
        }
    }

    private boolean checkBond(BondDTO bondDTO) {
        return secondaryJdbcTemplate.queryForRowSet("select * from bond_target where code = '" + bondDTO.getCode() + "'").next();
    }

    private void updateBond(BondDTO bondDTO) {
        secondaryJdbcTemplate.update("update bond_target set name = ?, alias_string = ?, " +
            " relate_company = ?, status = ?, last_modified_by = ?, last_modified_date = ? where code = ?", ps -> {
            ps.setString(1, bondDTO.getName());
            ps.setString(2, bondDTO.getAliasString());
            ps.setString(3, bondDTO.getRelateCompany());
            ps.setString(4, bondDTO.getStatus());
            ps.setString(5, bondDTO.getLastModifiedBy());
            ps.setTimestamp(6, Timestamp.from(bondDTO.getLastModifiedDate()));
            ps.setString(7, bondDTO.getCode());
        });
    }

    private void insertBond(BondDTO bondDTO) {
        secondaryJdbcTemplate.update("insert into bond_target(id, market_code, " +
            " category, code, name, alias_string, relate_company, status, created_by, created_date, last_modified_by, " +
            " last_modified_date)  values (?,?,?,?,?,?,?,?,?,?,?,?)", ps -> {
            ps.setString(1, bondDTO.getId());
            ps.setString(2, bondDTO.getMarketCode());
            ps.setString(3, bondDTO.getCategory());
            ps.setString(4, bondDTO.getCode());
            ps.setString(5, bondDTO.getName());
            ps.setString(6, bondDTO.getAliasString());
            ps.setString(7, bondDTO.getRelateCompany());
            ps.setString(8, bondDTO.getStatus());
            ps.setString(9, bondDTO.getCreateBy());
            ps.setTimestamp(10, Timestamp.from(bondDTO.getCreateDate()));
            ps.setString(11, bondDTO.getLastModifiedBy());
            ps.setTimestamp(12, Timestamp.from(bondDTO.getLastModifiedDate()));
        });
    }

}
