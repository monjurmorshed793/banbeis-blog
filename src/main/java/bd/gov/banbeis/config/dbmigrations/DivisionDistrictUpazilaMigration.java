package bd.gov.banbeis.config.dbmigrations;

import bd.gov.banbeis.domain.Division;
import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;

@ChangeLog(order = "002")
public class DivisionDistrictUpazilaMigration {

    @ChangeSet(order = "01", author = "morshed", id = "01-addDivisions")
    public void addDivisions(MongockTemplate mongockTemplate) throws IOException {
        File divisionFile = new ClassPathResource("data-resource/divisions.txt").getFile();
        List<Document> divisions = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(divisionFile.getPath()));
        String line;
        while ((line = br.readLine()) != null) {
            divisions.add(Document.parse(line));
        }
        mongockTemplate.getCollection("division").insertMany(divisions);
    }

    @ChangeSet(order = "02", author = "morshed", id = "02-addDistricts")
    public void addDistricts(MongockTemplate mongockTemplate) throws IOException {
        File districtFile = new ClassPathResource("data-resource/districts.txt").getFile();
        List<Document> districts = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(districtFile.getPath()));
        String line;
        while ((line = br.readLine()) != null) {
            districts.add(Document.parse(line));
        }
        mongockTemplate.getCollection("district").insertMany(districts);
    }

    @ChangeSet(order = "03", author = "morshed", id = "03-addUpazils")
    public void addUpazillas(MongockTemplate mongockTemplate) throws IOException {
        File upazilaFile = new ClassPathResource("data-resource/upazilas.txt").getFile();
        List<Document> upazilas = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(upazilaFile.getPath()));
        String line;
        while ((line = br.readLine()) != null) {
            upazilas.add(Document.parse(line));
        }
        mongockTemplate.getCollection("upazila").insertMany(upazilas);
    }
}
