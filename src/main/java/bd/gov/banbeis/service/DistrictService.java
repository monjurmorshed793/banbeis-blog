package bd.gov.banbeis.service;

import bd.gov.banbeis.domain.District;
import bd.gov.banbeis.repository.DistrictRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link District}.
 */
@Service
public class DistrictService {

    private final Logger log = LoggerFactory.getLogger(DistrictService.class);

    private final DistrictRepository districtRepository;

    public DistrictService(DistrictRepository districtRepository) {
        this.districtRepository = districtRepository;
    }

    /**
     * Save a district.
     *
     * @param district the entity to save.
     * @return the persisted entity.
     */
    public Mono<District> save(District district) {
        log.debug("Request to save District : {}", district);
        return districtRepository.save(district);
    }

    /**
     * Partially update a district.
     *
     * @param district the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<District> partialUpdate(District district) {
        log.debug("Request to partially update District : {}", district);

        return districtRepository
            .findById(district.getId())
            .map(existingDistrict -> {
                if (district.getDivisionId() != null) {
                    existingDistrict.setDivisionId(district.getDivisionId());
                }
                if (district.getName() != null) {
                    existingDistrict.setName(district.getName());
                }
                if (district.getBnName() != null) {
                    existingDistrict.setBnName(district.getBnName());
                }
                if (district.getLat() != null) {
                    existingDistrict.setLat(district.getLat());
                }
                if (district.getLon() != null) {
                    existingDistrict.setLon(district.getLon());
                }
                if (district.getUrl() != null) {
                    existingDistrict.setUrl(district.getUrl());
                }

                return existingDistrict;
            })
            .flatMap(districtRepository::save);
    }

    /**
     * Get all the districts.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Flux<District> findAll(Pageable pageable) {
        log.debug("Request to get all Districts");
        return districtRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of districts available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return districtRepository.count();
    }

    /**
     * Get one district by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Mono<District> findOne(String id) {
        log.debug("Request to get District : {}", id);
        return districtRepository.findById(id);
    }

    /**
     * Delete the district by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(String id) {
        log.debug("Request to delete District : {}", id);
        return districtRepository.deleteById(id);
    }
}
