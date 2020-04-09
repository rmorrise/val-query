package val.query

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Integration
@Rollback
class CatISpec extends Specification {
    @Shared CatBreed breed
    @Shared Cat otherCat

    private void createTestData() {
        breed = new CatBreed(name: 'Test Breed')
        breed.save(failOnError: true)

        otherCat = new Cat(breed: breed, owner: 'dupe', name: 'dupe')
        otherCat.save(failOnError: true)
    }

    private void cleanupTestData() {
        otherCat.delete(failOnError: true)
        breed.delete(failOnError: true, flush: true)
    }

//    @Transactional
//    def setupSpec() {
//        createTestData()
//    }

//    @Transactional
//    def cleanupSpec() {
//        cleanupTestData()
//    }

    @Unroll
    void "Cat"() {
        given:"breed and other cat are defined"
        createTestData()

        expect:"should be validated"
        def result = input.validate()
        input.errors.allErrors.isEmpty() == expected
        result == expected

        // not sure if this is the right place to put cleanup code?
        cleanupTestData()

        where:
        input                                                   || expected
        new Cat()                                               || false
        new Cat(breed: breed, owner: 'foo', name: 'bar')        || true
        new Cat(breed: breed, owner: 'dupe', name: 'dupe')      || false
        new Cat(breed: breed, owner: null, name: 'bar')         || false
        new Cat(breed: breed, owner: 'foo', name: null)         || false
    }
}
