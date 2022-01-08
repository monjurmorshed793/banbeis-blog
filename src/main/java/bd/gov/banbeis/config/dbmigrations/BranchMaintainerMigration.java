package bd.gov.banbeis.config.dbmigrations;

import bd.gov.banbeis.domain.Authority;
import bd.gov.banbeis.security.AuthoritiesConstants;
import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;

@ChangeLog(order = "003")
public class BranchMaintainerMigration {

    @ChangeSet(order = "01", author = "morshed", id = "01-addAuthorities")
    public void addAuthority(MongockTemplate mongockTemplate) {
        Authority branchMaintainerAuthority = new Authority();
        branchMaintainerAuthority.setName(AuthoritiesConstants.BRANCH_MAINTAINER);
        mongockTemplate.save(branchMaintainerAuthority);
    }
}
